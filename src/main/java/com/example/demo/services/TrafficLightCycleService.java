package com.example.demo.services;

import com.example.demo.core.CentralState;
import Devices.TrafficLightStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

@Service
public class TrafficLightCycleService {

    // ===== Duraciones realistas (segundos) =====
    private static final int T_GREEN_A = 40; // A verde (arteria principal)
    private static final int T_GREEN_B = 30; // B verde (lateral)
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

    // =====================================================================================
    // API pública
    // =====================================================================================

    /** Programa todas las intersecciones con delay escalonado 0s,1s,2s,... (modo normal). */
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

    /** Cancela todas las tareas (no apaga el scheduler para poder reiniciar). */
    public void stopAll() {
        futures.values().forEach(f -> f.cancel(true));
        futures.clear();
        System.out.println("⏹️ Ciclos detenidos.");
    }

    /** Cancela una sola intersección. */
    public void stopOne(String semaphoreId) {
        var f = futures.remove(semaphoreId);
        if (f != null) f.cancel(true);
    }

    /**
     * MODO CASCADA: todos arrancan ROJO y luego, de abajo hacia arriba (orderedIds),
     * cada uno inicia su ciclo 1s después del anterior (configurable con stepSec).
     *
     * @param orderedIds ids en orden de “abajo → arriba” (el primero inicia primero)
     * @param stepSec    separación entre inicios (por defecto 1s)
     */
    public void startCascade(List<String> orderedIds, int stepSec) {
        // Detener solo los semáforos de esta cascada para no interferir con otras
        for (String id : orderedIds) {
            stopOne(id);
        }

        // 1) Ponerlos en rojo ya mismo (visualmente: todo rojo al inicio).
        for (String id : orderedIds) {
            var tl = state.tlStates.get(id);
            if (tl == null) continue;
            // La vía A será la prioritaria para la onda
            tl.principalIsA = true;
            emit(id, Phase.BOTH_RED_1, true);
        }

        // 2) Programar el inicio escalonado: idx*stepSec
        final long cascadeStartNanos = System.nanoTime();
        for (int i = 0; i < orderedIds.size(); i++) {
            String id = orderedIds.get(i);
            var tl = state.tlStates.get(id);
            if (tl == null) continue;
            int delay = Math.max(0, i * Math.max(1, stepSec));
            futures.computeIfAbsent(id, __ -> startOneCascade(id, tl, cascadeStartNanos, delay));
            System.out.printf("⏩ Cascade id=%s startDelay=%ds%n", id, delay);
        }
    }

    // =====================================================================================
    // Programación por intersección (modo normal, con initialDelay y emisión inmediata)
    // =====================================================================================

    private ScheduledFuture<?> startOneWithInitialDelay(String id, CentralState.TLState tl, int delaySec) {
        final boolean principalIsA = tl.principalIsA;
        Phase start = deduceStartPhase(tl);
        var clock = new PhaseClock(start);

        System.out.printf("▶ Ciclo %s  initialDelay=%ds  principalIsA=%s  startPhase=%s%n",
                id, delaySec, principalIsA, start);

        // Emito inmediatamente el estado inicial (para front con polling)
        emit(id, start, principalIsA);

        // Emite sólo cuando cambia la fase
        Runnable task = new Runnable() {
            Phase last = start;
            @Override public void run() {
                try {
                    clock.tick();
                    Phase now = clock.currentPhase();
                    if (now != last) {
                        emit(id, now, principalIsA);
                        last = now;
                    }
                } catch (Throwable t) { t.printStackTrace(); }
            }
        };
        return scheduler.scheduleAtFixedRate(task, delaySec, 1, TimeUnit.SECONDS);
    }

    // =====================================================================================
    // Programación por intersección (modo cascada): temporización absoluta
    // =====================================================================================

    private ScheduledFuture<?> startOneCascade(String id, CentralState.TLState tl, long cascadeStartNanos, int offsetSec) {
        final boolean principalIsA = true; // en cascada usamos A como vía prioritaria

        Runnable task = new Runnable() {
            Phase lastEmittedPhase = null;
            final int totalCycleSec = cycleLengthSec();

            @Override
            public void run() {
                try {
                    long elapsedNanos = System.nanoTime() - cascadeStartNanos;
                    long elapsedSec = TimeUnit.NANOSECONDS.toSeconds(elapsedNanos);
                    long effectiveElapsedSec = elapsedSec - offsetSec;

                    Phase now;
                    if (effectiveElapsedSec < 0) {
                        now = Phase.BOTH_RED_1;
                    } else {
                        long secInCycle = effectiveElapsedSec % totalCycleSec;
                        if (secInCycle < T_GREEN_A) {
                            now = Phase.A_GREEN__B_RED;
                        } else if (secInCycle < T_GREEN_A + T_YELLOW) {
                            now = Phase.A_YELLOW__B_RED;
                        } else if (secInCycle < T_GREEN_A + T_YELLOW + T_ALL_RED) {
                            now = Phase.BOTH_RED_1;
                        } else if (secInCycle < T_GREEN_A + T_YELLOW + T_ALL_RED + T_GREEN_B) {
                            now = Phase.A_RED__B_GREEN;
                        } else if (secInCycle < T_GREEN_A + T_YELLOW + T_ALL_RED + T_GREEN_B + T_YELLOW) {
                            now = Phase.A_RED__B_YELLOW;
                        } else {
                            now = Phase.BOTH_RED_2;
                        }
                    }

                    if (now != lastEmittedPhase) {
                        emit(id, now, principalIsA);
                        lastEmittedPhase = now;
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };

        // Chequear el estado 4 veces por segundo para que sea bien responsivo.
        return scheduler.scheduleAtFixedRate(task, 0, 250, TimeUnit.MILLISECONDS);
    }


    // =====================================================================================
    // Core: fases, reloj y utilidades
    // =====================================================================================

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
        if (st != null) {
            st.a = a; st.b = b;
            // hook SSE opcional: hub.broadcastEstado(id, st);
        }
    }

    // 6 fases coherentes con despeje
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
        Phase next() { Phase[] all = values(); return all[(ordinal() + 1) % all.length]; }
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

    // ===== Utilidad opcional: duración de ciclo total =====
    public static int cycleLengthSec() {
        return T_GREEN_A + T_YELLOW + T_ALL_RED + T_GREEN_B + T_YELLOW + T_ALL_RED;
    }
}
