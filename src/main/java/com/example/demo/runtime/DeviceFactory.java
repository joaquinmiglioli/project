package com.example.demo.runtime;

import com.example.demo.core.CentralState;
import devices.*;

import java.util.Map;

public final class DeviceFactory {
    private DeviceFactory() {}

    public static DeviceCatalog buildFrom(Map<String, CentralState.DeviceSnapshot> snaps) {
        DeviceCatalog cat = new DeviceCatalog();
        if (snaps == null) return cat;

        for (CentralState.DeviceSnapshot s : snaps.values()) {
            Device d = switch (s.type) {
                case "Radar" -> {
                    Radar r = new Radar();
                    r.setDeviceId(s.deviceId);
                    r.setAddress(s.address);
                    r.setStatus(s.status);
                    if (s.speedLimit != null) r.setSpeedLimit(s.speedLimit);
                    yield r;
                }
                case "ParkingCamera" -> {
                    ParkingCamera p = new ParkingCamera();
                    p.setDeviceId(s.deviceId);
                    p.setAddress(s.address);
                    p.setStatus(s.status);
                    if (s.toleranceSec != null) p.setToleranceSec(s.toleranceSec);
                    yield p;
                }
                case "SecurityCamera" -> {
                    SecurityCamera c = new SecurityCamera();
                    c.setDeviceId(s.deviceId);
                    c.setAddress(s.address);
                    c.setStatus(s.status);
                    yield c;
                }
                case "TrafficLight" -> {
                    // Modelo rico si lo querés usar; el ciclo real seguirá en CentralState/Service
                    TrafficLightController t = new TrafficLightController();
                    t.setDeviceId(s.deviceId);
                    t.setAddress(s.address);
                    t.setStatus(s.status);
                    yield t;
                }
                default -> null;
            };
            if (d != null) cat.put(d);
        }
        return cat;
    }
}