package com.example.demo.runtime;

import com.example.demo.core.CentralState;
import devices.*;

import java.util.Map;
import java.util.Objects;

/**
 * Sincroniza estado simple entre objetos `devices.*` y snapshots de `CentralState`.
 * Mantiene `CentralState` como fuente de verdad para endpoints y ciclo.
 */
public class SnapshotSync {
    private final CentralState state;

    public SnapshotSync(CentralState state) {
        this.state = Objects.requireNonNull(state);
    }

    /** Lleva datos simples del snapshot → objeto (por si recargás JSON o restaurás state.bin). */
    public void pullFromSnapshot(Device d) {
        if (d == null) return;
        CentralState.DeviceSnapshot s = state.devicesById.get(d.getDeviceId());
        if (s == null) return;

        d.setAddress(s.address);
        d.setStatus(s.status);

        if (d instanceof Radar r && s.speedLimit != null) {
            r.setSpeedLimit(s.speedLimit);
        } else if (d instanceof ParkingCamera p && s.toleranceSec != null) {
            p.setToleranceSec(s.toleranceSec);
        }
        // TrafficLightController: el ciclo y A/B están en tlStates (no se tocan acá)
    }

    /** Vuelca cambios simples del objeto → snapshot (para que front/ciclo/persistencia lo vean). */
    public void pushToSnapshot(Device d) {
        if (d == null) return;
        Map<String, CentralState.DeviceSnapshot> m = state.devicesById;
        CentralState.DeviceSnapshot s = m.get(d.getDeviceId());
        if (s == null) {
            s = new CentralState.DeviceSnapshot();
            s.deviceId = d.getDeviceId();
            s.type = inferType(d);
            m.put(s.deviceId, s);
        }
        s.address = d.getAddress();
        s.status  = d.getStatus();

        if (d instanceof Radar r) {
            s.speedLimit = r.getSpeedLimit();
        } else if (d instanceof ParkingCamera p) {
            s.toleranceSec = p.getToleranceSec();
        }
        // semáforos: si necesitaras reflejar algo, lo haríamos explícito aquí
    }

    private static String inferType(Device d) {
        if (d instanceof Radar) return "Radar";
        if (d instanceof ParkingCamera) return "ParkingCamera";
        if (d instanceof SecurityCamera) return "SecurityCamera";
        if (d instanceof TrafficLightController) return "TrafficLight";
        return "Unknown";
    }
}