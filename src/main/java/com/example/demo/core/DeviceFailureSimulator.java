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

/**
 * Simula fallos aleatorios en los dispositivos, como requiere el PDF.
 * Se ejecuta en un hilo separado.
 */
public class DeviceFailureSimulator {

    private final DeviceCatalog deviceCatalog;
    // private final IMaintenanceContext maintenanceContext; // <-- ELIMINADO
    private final SnapshotSync snapshotSync;
    private final Random r = new Random();
    private ScheduledExecutorService exec;

    /**
     * @param deviceCatalog El catálogo de dispositivos (POJOs)
     * @param snapshotSync El servicio para hacer visibles los cambios al frontend
     */
    // ✅ MODIFICADO: Constructor más simple
    public DeviceFailureSimulator(DeviceCatalog deviceCatalog, SnapshotSync snapshotSync) {
        this.deviceCatalog = deviceCatalog;
        this.snapshotSync = snapshotSync;
    }

    /**
     * Inicia el hilo de simulación de fallos.
     */
    public void start() {
        if (exec != null) return;
        exec = Executors.newSingleThreadScheduledExecutor(rn -> {
            var t = new Thread(rn, "fail-sim"); // Nombre del hilo: "fail-sim"
            t.setDaemon(true);
            return t;
        });
        exec.schedule(this::tick, 30, TimeUnit.SECONDS);
    }

    /**
     * Detiene el simulador.
     */
    public void stop() {
        if (exec != null) exec.shutdownNow();
    }

    /**
     * Lógica de un "tick": buscar un dispositivo y hacerlo fallar.
     */
    private void tick() {
        try {
            // 1. Buscar dispositivos que estén 'NORMAL' (candidatos a fallar)
            List<Device> candidates = deviceCatalog.all().stream()
                    .filter(d -> d.getStatus() == DeviceStatus.NORMAL)
                    .toList();

            if (!candidates.isEmpty()) {
                // 2. Elegir uno al azar
                Device deviceToFail = candidates.get(r.nextInt(candidates.size()));
                String deviceId = deviceToFail.getDeviceId();

                // 3. Aplicar el fallo (polimórficamente)
                if (r.nextDouble() < 0.20) { // 20% chance de intermitente
                    // ✅ MODIFICADO: Llama a la versión simple (sin contexto)
                    deviceToFail.intermittent();
                    System.out.println("💥 (SIM) Device set to INTERMITTENT: " + deviceId);
                } else { // 80% chance de fallo total
                    // ✅ MODIFICADO: Llama a la versión simple (sin contexto)
                    deviceToFail.fail();
                    System.out.println("❌ (SIM) Device set to FAILURE: " + deviceId);
                }

                // 4. Sincronizar el estado del POJO al Snapshot
                snapshotSync.pushToSnapshot(deviceToFail);
            }

        } catch (Throwable ignore) {
            // Ignorar errores (defensivo)
        } finally {
            if (exec != null && !exec.isShutdown()) {
                // 5. Programar el próximo fallo aleatorio
                exec.schedule(this::tick, 30 + r.nextInt(60), TimeUnit.SECONDS);
            }
        }
    }
}
