package com.example.demo.services;


import com.example.demo.core.CentralState;
import devices.TrafficLightStatus;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


//maneja el ciclo de los semafotos


@Service
public class TrafficLightCycleService {


    //duracion de cada ciclo en segundos
    private static final int T_GREEN_A = 40;
    private static final int T_GREEN_B = 30;
    private static final int T_YELLOW  = 4;
    private static final int T_ALL_RED = 2;


    //duracion de cada ciclo en milisegundos
    private static final long A_MS   = T_GREEN_A * 1000L;
    private static final long B_MS   = T_GREEN_B * 1000L;
    private static final long Y_MS   = T_YELLOW  * 1000L;
    private static final long AR_MS  = T_ALL_RED * 1000L;
    private static final long CYCLE_MS = A_MS + Y_MS + AR_MS + B_MS + Y_MS + AR_MS;


    private static final long T1 = A_MS;
    private static final long T2 = T1 + Y_MS;
    private static final long T3 = T2 + AR_MS;
    private static final long T4 = T3 + B_MS;
    private static final long T5 = T4 + Y_MS;


    private final CentralState state;


    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor(r -> {
                var t = new Thread(r, "tl-cascade");
                t.setDaemon(true);
                return t;
            });


    private final Map<String, GroupRunner> groups = new ConcurrentHashMap<>();


    private final Set<String> paused = ConcurrentHashMap.newKeySet();


    public TrafficLightCycleService(CentralState state) {
        this.state = Objects.requireNonNull(state, "state");
    }


    public synchronized void stopAll() {
        groups.values().forEach(GroupRunner::stop);
        groups.clear();
        System.out.println("⏹️ Stopped cascades.");
    }


    public synchronized void stopGroup(String name) {
        Optional.ofNullable(groups.remove(name)).ifPresent(GroupRunner::stop);
    }


    public void stopOne(String semaphoreId) {
        paused.add(semaphoreId);
    }


    public void resumeOne(String semaphoreId) {
        paused.remove(semaphoreId);
    }


    public synchronized void startCascade(String name, List<String> orderedIds, int stepSec) {
        stopGroup(name);


        for (String id : orderedIds) {
            var st = state.tlStates.get(id);
            if (st == null) continue;
            st.principalIsA = true;
            st.a = TrafficLightStatus.RED;
            st.b = TrafficLightStatus.RED;
        }


        var runner = new GroupRunner(name, new ArrayList<>(orderedIds), Math.max(1, stepSec));
        runner.start();
        groups.put(name, runner);


        System.out.printf("⏩ Cascade '%s' started. ids=%d, step=%ds%n", name, orderedIds.size(), stepSec);
    }


    //duracion total del ciclo
    public static int cycleLengthSec() {
        return (int)(CYCLE_MS / 1000L);
    }


    private final class GroupRunner implements Runnable {
        final String name;
        final List<String> ids;
        final int stepSec;
        final long startNanos;
        ScheduledFuture<?> future;


        GroupRunner(String name, List<String> ids, int stepSec) {
            this.name = name;
            this.ids = ids;
            this.stepSec = stepSec;
            this.startNanos = System.nanoTime();
        }


        void start() {
            this.future = scheduler.scheduleAtFixedRate(this, 0, 200, TimeUnit.MILLISECONDS);
        }


        void stop() {
            if (future != null) future.cancel(true);
        }


        @Override public void run() {
            try {
                long elapsedMs = (System.nanoTime() - startNanos) / 1_000_000L;
                for (int i = 0; i < ids.size(); i++) {
                    String id = ids.get(i);


                    if (paused.contains(id)) continue;


                    var st = state.tlStates.get(id);
                    if (st == null) continue;


                    long eff = elapsedMs - (i * (long)stepSec * 1000L);
                    TrafficLightStatus a,b;


                    if (eff < 0) {
                        a = TrafficLightStatus.RED; b = TrafficLightStatus.RED;
                    } else {
                        long t = eff % CYCLE_MS;
                        if      (t < T1) { a = TrafficLightStatus.GREEN;  b = TrafficLightStatus.RED;    }
                        else if (t < T2) { a = TrafficLightStatus.YELLOW; b = TrafficLightStatus.RED;    }
                        else if (t < T3) { a = TrafficLightStatus.RED;    b = TrafficLightStatus.RED;    }
                        else if (t < T4) { a = TrafficLightStatus.RED;    b = TrafficLightStatus.GREEN;  }
                        else if (t < T5) { a = TrafficLightStatus.RED;    b = TrafficLightStatus.YELLOW; }
                        else             { a = TrafficLightStatus.RED;    b = TrafficLightStatus.RED;    }
                    }


                    if (a == TrafficLightStatus.GREEN && b == TrafficLightStatus.GREEN) b = TrafficLightStatus.RED;
                    if (a == TrafficLightStatus.YELLOW && b != TrafficLightStatus.RED)  b = TrafficLightStatus.RED;
                    if (b == TrafficLightStatus.YELLOW && a != TrafficLightStatus.RED)  a = TrafficLightStatus.RED;


                    st.principalIsA = true; // en cascada A es la prioritaria
                    st.a = a; st.b = b;
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }


    public List<String> runningGroups() {
        return groups.keySet().stream().sorted().collect(Collectors.toList());
    }
}
