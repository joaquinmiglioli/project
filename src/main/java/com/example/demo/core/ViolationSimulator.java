package com.example.demo.core;

import com.example.demo.services.ViolationService;

import java.util.*;
import java.util.concurrent.*;

public class ViolationSimulator {

    private final CentralState state;
    private final ViolationService vs;
    private ScheduledExecutorService exec;
    private final Random r = new Random();

    public ViolationSimulator(CentralState state, ViolationService vs) {
        this.state = state; this.vs = vs;
    }

    public void start() {
        if (exec != null) return;
        exec = Executors.newSingleThreadScheduledExecutor(rn -> { var t = new Thread(rn, "viol-sim"); t.setDaemon(true); return t; });
        exec.schedule(this::tick, 4, TimeUnit.SECONDS);
    }

    public void stop() { if (exec != null) exec.shutdownNow(); }

    private void tick() {
        try {
            // elegimos tipo 0..2 (sin SecurityCamera)
            int t = r.nextInt(3);
            List<CentralState.DeviceSnapshot> pool = state.devicesById.values().stream()
                    .filter(s -> s.status != devices.DeviceStatus.FAILURE) // si está fallado, no “funciona”
                    .filter(s -> switch (t) {
                        case 0 -> "Radar".equals(s.type);
                        case 1 -> "ParkingCamera".equals(s.type);
                        default -> "TrafficLight".equals(s.type);
                    })
                    .toList();

            if (!pool.isEmpty()) {
                CentralState.DeviceSnapshot d = pool.get(r.nextInt(pool.size()));
                switch (t) {
                    case 0 -> { // Radar
                        int limit = Optional.ofNullable(d.speedLimit).orElse(80);
                        int speed = limit + 10 + r.nextInt(51);
                        vs.recordSpeeding(d.deviceId, "XXX000", speed, limit);
                    }
                    case 1 -> { // Parking
                        int tol = Optional.ofNullable(d.toleranceSec).orElse(300);
                        int stay = tol + 60 + r.nextInt(600);
                        vs.recordIllegalParking(d.deviceId, "YYY111", stay, tol);
                    }
                    default -> { // Red light
                        String dir = r.nextBoolean() ? "N-S" : "E-W";
                        vs.recordRedLight(d.deviceId, "ZZZ222", dir);
                    }
                }
            }
        } catch (Throwable ignore) {
        } finally {
            if (exec != null && !exec.isShutdown()) {
                exec.schedule(this::tick, 8 + r.nextInt(25), TimeUnit.SECONDS);
            }
        }
    }
}
