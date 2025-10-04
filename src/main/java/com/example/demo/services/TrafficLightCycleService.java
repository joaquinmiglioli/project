package com.example.demo.services;

import com.example.demo.core.CentralState;
import Devices.TrafficLightStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Function;

@Service
public class TrafficLightCycleService {

    // ===== Duraciones realistas (segundos) =====
    private static final int T_GREEN_A = 40; // A verde
    private static final int T_GREEN_B = 30; // B verde
    private static final int T_YELLOW  = 4;  // ámbar
    private static final int T_ALL_RED = 2;  // despeje (ambos rojo)

    private final CentralState state;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() / 2),
            r -> { var t = new Thread(r, "tl-scheduler"); t.setDaemon(true); return t; });

    private final ConcurrentMap<String, ScheduledFuture<?>> futures = new ConcurrentHashMap<>();

    public TrafficLightCycleService(CentralState state) {
        this.state = Objects.requireNonNull(state, "state");
    }

    /** Programa todas las intersecciones con delay escalonado 0s,1s,2s,... */
    public void startAll() {
        int delay = 0;
        for (Map.Entry<String, CentralState.TLState> e : state.tlStates.entrySet()) {
            final String id = e.getKey();
            final CentralState.TLState tl = e.getValue();
            final int delaySec = delay; // capturo para la lambda
            futures.computeIfAbsent(id, __ -> startOneWithInitialDelay(id, tl, delaySec));
            delay++;
        }
    }

    public void stopAll() {
        futures.values().forEach(f -> f.cancel(true));
        futures.clear();
        scheduler.shutdownNow();
        System.out.println("⏹️ Ciclos detenidos.");
    }

    public void stopOne(String semaphoreId) {
        var f = futures.remove(semaphoreId);
        if (f != null) f.cancel(true);
    }

    // ===== Programación por intersección =====

    private ScheduledFuture<?> startOneWithInitialDelay(String id, CentralState.TLState tl, int delaySec) {
        final boolean principalIsA = tl.principalIsA;

        Phase start = deduceStartPhase(tl);
        var clock = new PhaseClock(start);

        System.out.printf("▶ Ciclo %s  initialDelay=%ds  principalIsA=%s  startPhase=%s%n",
                id, delaySec, principalIsA, start);

        // Primera emisión ocurre al primer tick tras el delay (para que se note el escalonamiento).
        return scheduler.scheduleAtFixedRate(
                new TickTask(id, principalIsA, clock, this::emitTri),
                delaySec, 1, TimeUnit.SECONDS
        );
    }

    private Phase deduceStartPhase(CentralState.TLState s) {
        if (s == null) return Phase.A_GREEN__B_RED;
        if (s.a == TrafficLightStatus.GREEN  && s.b == TrafficLightStatus.RED)    return Phase.A_GREEN__B_RED;
        if (s.a == TrafficLightStatus.YELLOW && s.b == TrafficLightStatus.RED)    return Phase.A_YELLOW__B_RED;
        if (s.a == TrafficLightStatus.RED    && s.b == TrafficLightStatus.GREEN)  return Phase.A_RED__B_GREEN;
        if (s.a == TrafficLightStatus.RED    && s.b == TrafficLightStatus.YELLOW) return Phase.A_RED__B_YELLOW;
        return Phase.BOTH_RED_1;
    }

    /** Emite al CentralState respetando principalIsA y corrige cualquier incoherencia. */
    private void emit(String id, Phase phase, boolean principalIsA) {
        TrafficLightStatus a = phase.aStatus();
        TrafficLightStatus b = phase.bStatus();
        if (!principalIsA) { var tmp = a; a = b; b = tmp; }

        // Invariantes de seguridad:
        if (a == TrafficLightStatus.GREEN && b == TrafficLightStatus.GREEN) {
            b = TrafficLightStatus.RED; // nunca ambos verdes
        }
        if (a == TrafficLightStatus.YELLOW && b != TrafficLightStatus.RED) {
            b = TrafficLightStatus.RED; // ámbar sólo con la otra vía en rojo
        }
        if (b == TrafficLightStatus.YELLOW && a != TrafficLightStatus.RED) {
            a = TrafficLightStatus.RED;
        }

        var st = state.tlStates.get(id);
        if (st != null) { st.a = a; st.b = b; }
    }
    private void emitTri(String id, Phase phase, Boolean principalIsA) {
        emit(id, phase, principalIsA.booleanValue());
    }

    // ===== 6 fases coherentes con despeje =====
    private enum Phase {
        A_GREEN__B_RED  (T_GREEN_A, TrafficLightStatus.GREEN,  TrafficLightStatus.RED),
        A_YELLOW__B_RED (T_YELLOW,  TrafficLightStatus.YELLOW, TrafficLightStatus.RED),
        BOTH_RED_1      (T_ALL_RED, TrafficLightStatus.RED,    TrafficLightStatus.RED),
        A_RED__B_GREEN  (T_GREEN_B, TrafficLightStatus.RED,    TrafficLightStatus.GREEN),
        A_RED__B_YELLOW (T_YELLOW,  TrafficLightStatus.RED,    TrafficLightStatus.YELLOW),
        BOTH_RED_2      (T_ALL_RED, TrafficLightStatus.RED,    TrafficLightStatus.RED);

        private final int seconds;
        private final TrafficLightStatus a, b;
        Phase(int s, TrafficLightStatus a, TrafficLightStatus b) { this.seconds = s; this.a = a; this.b = b; }
        int seconds() { return seconds; }
        TrafficLightStatus aStatus() { return a; }
        TrafficLightStatus bStatus() { return b; }
        Phase next() {
            Phase[] all = values();
            return all[(ordinal() + 1) % all.length];
        }
    }

    /** Reloj por intersección: avanza 1Hz y rota fases según sus duraciones. */
    private static final class PhaseClock {
        private Phase phase;
        private int second; // [0 .. phase.seconds-1]

        PhaseClock(Phase start) { this.phase = start; this.second = 0; }

        void tick() {
            second++;
            if (second >= phase.seconds()) {
                phase = phase.next();
                second = 0;
            }
        }

        Phase currentPhase() { return phase; }
    }

    /** Tarea 1Hz: avanza el reloj y emite estado. */
    private static final class TickTask implements Runnable {
        private final String id;
        private final boolean principalIsA;
        private final PhaseClock clock;
        private final TriConsumer<String, Phase, Boolean> emitter;

        TickTask(String id, boolean principalIsA, PhaseClock clock,
                 TriConsumer<String, Phase, Boolean> serviceEmit) {
            this.id = id;
            this.principalIsA = principalIsA;
            this.clock = clock;
            this.emitter = serviceEmit;
        }

        @Override public void run() {
            try {
                clock.tick(); // avanza 1s
                emitter.accept(id, clock.currentPhase(), principalIsA);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    @FunctionalInterface private interface TriConsumer<A,B,C> { void accept(A a, B b, C c); }
}
