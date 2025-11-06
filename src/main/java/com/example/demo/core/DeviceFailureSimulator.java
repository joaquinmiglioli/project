package com.example.demo.core;

import com.example.demo.runtime.DeviceCatalog;
import com.example.demo.runtime.SnapshotSync;
import devices.Device;
import devices.DeviceStatus;
// import devices.IMaintenanceContext; // <-- Ya no lo necesita

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
Un hilo (thread) que corre en segundo plano y simula fallos de dispositivos.
tick(): Cada cierto tiempo, elige un dispositivo al azar que esté "NORMAL",
llama a su método .fail() o .intermittent(), y luego usa SnapshotSync para actualizar el CentralState y que el frontend vea el cambio.
 */
public class DeviceFailureSimulator {

    private final DeviceCatalog deviceCatalog;
    private final SnapshotSync snapshotSync;
    private final Random r = new Random();
    private ScheduledExecutorService exec;


    public DeviceFailureSimulator(DeviceCatalog deviceCatalog, SnapshotSync snapshotSync) {
        this.deviceCatalog = deviceCatalog;
        this.snapshotSync = snapshotSync;
    }

    // Inicia el hilo de simulación de fallos

    public void start() {
        if (exec != null) return;
        exec = Executors.newSingleThreadScheduledExecutor(rn -> {
            var t = new Thread(rn, "fail-sim");
            t.setDaemon(true);
            return t;
        });
        exec.schedule(this::tick, 30, TimeUnit.SECONDS);
    }


    public void stop() {
        if (exec != null) exec.shutdownNow();
    }

    // Logica de "tick": buscar un dispositivo y hacerlo fallar.

    private void tick() {
        try {
            List<Device> candidates = deviceCatalog.all().stream()
                    .filter(d -> d.getStatus() == DeviceStatus.NORMAL)
                    .toList();

            if (!candidates.isEmpty()) {
                Device deviceToFail = candidates.get(r.nextInt(candidates.size()));
                String deviceId = deviceToFail.getDeviceId();

                if (r.nextDouble() < 0.20) { // 20% chance de intermitente
                    deviceToFail.intermittent();
                    System.out.println("💥 (SIM) Device set to INTERMITTENT: " + deviceId);
                } else { // 80% chance de fallo
                    deviceToFail.fail();
                    System.out.println("❌ (SIM) Device set to FAILURE: " + deviceId);
                }

                snapshotSync.pushToSnapshot(deviceToFail);
            }

        } catch (Throwable ignore) {
        } finally {
            if (exec != null && !exec.isShutdown()) {
                // 5. Programar el próximo fallo aleatorio
                exec.schedule(this::tick, 30 + r.nextInt(60), TimeUnit.SECONDS);
            }
        }
    }
}
