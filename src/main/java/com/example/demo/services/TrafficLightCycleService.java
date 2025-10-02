package com.example.demo.services;

import com.example.demo.core.CentralState;
import Devices.TrafficLightStatus;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Servicio que controla el ciclo de los semáforos A y B
 * exactamente como en la tabla solicitada.
 */
public class TrafficLightCycleService {

    private final CentralState state;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, Future<?>> runningCycles = new ConcurrentHashMap<>();

    // 🕒 Duraciones (segundos)
    private static final int GREEN_A_TIME = 40;     // 🟢 A verde
    private static final int YELLOW_A_TIME = 4;     // 🟡 A amarillo
    private static final int BOTH_RED_TIME = 3;     // 🔴 Ambos rojo
    private static final int GREEN_B_TIME = 30;     // 🟢 B verde
    private static final int YELLOW_B_TIME = 4;     // 🟡 B amarillo

    public TrafficLightCycleService(CentralState state) {
        this.state = state;
    }

    public void startAll() {
        for (String id : state.tlStates.keySet()) {
            startCycle(id);
        }
    }

    public void stopAll() {
        stop();
    }

    public void startCycle(String semaphoreId) {
        if (runningCycles.containsKey(semaphoreId)) return;

        Future<?> future = executor.submit(() -> {
            while (true) {
                try {
                    CentralState.TLState tl = state.tlStates.get(semaphoreId);
                    if (tl == null) break;

                    // 1️⃣ 🟢 A verde / 🔴 B rojo (40s)
                    tl.a = TrafficLightStatus.GREEN;
                    tl.b = TrafficLightStatus.RED;
                    log(semaphoreId, "🟢 A Verde (40s)");
                    Thread.sleep(GREEN_A_TIME * 1000L);

                    // 2️⃣ 🟡 A amarillo / 🔴 B rojo (4s)
                    tl.a = TrafficLightStatus.YELLOW;
                    tl.b = TrafficLightStatus.RED;
                    log(semaphoreId, "🟡 A Amarillo (4s)");
                    Thread.sleep(YELLOW_A_TIME * 1000L);

                    // 3️⃣ 🔴 Ambos rojo (3s)
                    tl.a = TrafficLightStatus.RED;
                    tl.b = TrafficLightStatus.RED;
                    log(semaphoreId, "🔴 Ambos rojo (3s)");
                    Thread.sleep(BOTH_RED_TIME * 1000L);

                    // 4️⃣ 🔴 A rojo / 🟢 B verde (30s)
                    tl.a = TrafficLightStatus.RED;
                    tl.b = TrafficLightStatus.GREEN;
                    log(semaphoreId, "🟢 B Verde (30s)");
                    Thread.sleep(GREEN_B_TIME * 1000L);

                    // 5️⃣ 🔴 A rojo / 🟡 B amarillo (4s)
                    tl.a = TrafficLightStatus.RED;
                    tl.b = TrafficLightStatus.YELLOW;
                    log(semaphoreId, "🟡 B Amarillo (4s)");
                    Thread.sleep(YELLOW_B_TIME * 1000L);

                    // 6️⃣ 🔴 Ambos rojo (3s)
                    tl.a = TrafficLightStatus.RED;
                    tl.b = TrafficLightStatus.RED;
                    log(semaphoreId, "🔴 Ambos rojo (3s)");
                    Thread.sleep(BOTH_RED_TIME * 1000L);

                    // 🔁 Reinicia el ciclo automáticamente desde el paso 1

                } catch (InterruptedException e) {
                    System.out.println("⏹️ Ciclo detenido en semáforo: " + semaphoreId);
                    break;
                }
            }
        });

        runningCycles.put(semaphoreId, future);
    }

    public void stop() {
        runningCycles.values().forEach(f -> f.cancel(true));
        executor.shutdownNow();
    }

    private void log(String id, String msg) {
        CentralState.TLState s = state.tlStates.get(id);
        System.out.printf("🔄 [%s] %s | A=%s | B=%s%n", id, msg, s.a, s.b);
    }
}
