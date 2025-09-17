package com.example.demo.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

/** Carga devices iniciales desde devices.devices.json (solo cuando no hay snapshot previo). */
public final class BootstrapLoader {
    private BootstrapLoader() {}

    public static void loadFromJson(Path json, CentralState state) {
        try {
            var mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(Files.readString(json));

            // Radars
            for (var n : root.withArray("radars")) {
                String id = n.get("deviceId").asText();
                String addr = n.get("address").asText();
                int limit = n.get("speedLimit").asInt(60);
                state.devicesById.put(id, CentralState.DeviceSnapshot.radar(id, addr, limit));
                state.radarLimit.put(id, limit);
            }
            // Parking cameras
            for (var n : root.withArray("parkingCameras")) {
                String id = n.get("deviceId").asText();
                String addr = n.get("address").asText();
                int tol = n.get("toleranceSec").asInt(120);
                state.devicesById.put(id, CentralState.DeviceSnapshot.parking(id, addr, tol));
                state.parkingToleranceSec.put(id, tol);
            }
            // Traffic lights
            for (var n : root.withArray("trafficLights")) {
                String id = n.get("deviceId").asText();
                String addr = n.get("address").asText();
                boolean principalIsA = n.get("principalIsA").asBoolean(true);
                state.devicesById.put(id, CentralState.DeviceSnapshot.trafficLight(id, addr, principalIsA));
                // Estado inicial A verde / B rojo
                state.tlMode.put(id, CentralState.TLMode.NORMAL);
                state.tlStates.put(id, new CentralState.TLState(
                        Devices.TrafficLightStatus.GREEN,
                        Devices.TrafficLightStatus.RED,
                        principalIsA
                ));
            }
            // Security cams
            for (var n : root.withArray("securityCameras")) {
                String id = n.get("deviceId").asText();
                String addr = n.get("address").asText();
                state.devicesById.put(id, CentralState.DeviceSnapshot.securityCam(id, addr));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error loading devices.devices.json", e);
        }
    }
}