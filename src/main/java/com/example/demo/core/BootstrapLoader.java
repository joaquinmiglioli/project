package com.example.demo.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**

 * Carga devices iniciales desde devices.json (Path o InputStream).
 * Versión robusta: admite varias variantes del JSON (keys "deviceId" o "id",
 * "speedLimit" o "limit", "toleranceSec" o "toleranceTime", etc.)
 */
public final class BootstrapLoader {
    private BootstrapLoader() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void loadFromJson(Path json, CentralState state) {
        try {
            JsonNode root = MAPPER.readTree(Files.readString(json));
            populateFromRoot(root, state);
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo " + json, e);
        }
    }

    public static void loadFromJson(InputStream is, CentralState state) {
        try {
            JsonNode root = MAPPER.readTree(is);
            populateFromRoot(root, state);
        } catch (Exception e) {
            throw new RuntimeException("Error leyendo devices.json desde classpath", e);
        }
    }

    private static void populateFromRoot(JsonNode root, CentralState state) {
        if (root == null || !root.isObject()) {
            throw new RuntimeException("devices.json no es un objeto JSON válido");
        }

        // ---- radars ----
        if (root.has("radars") && root.get("radars").isArray()) {
            for (var n : root.withArray("radars")) {
                try {
                    String id = textOr(n, "deviceId", "id");
                    if (id == null) {
                        System.err.println("Radar sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, "address", "addr", "location", null);
                    int limit = intOr(n, "speedLimit", "limit", 60);
                    state.devicesById.put(id, CentralState.DeviceSnapshot.radar(id, addr == null ? "SIN DIRECCIÓN" : addr, limit));
                    state.radarLimit.put(id, limit);
                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear un radar: " + e.getMessage());
                }
            }
        }

        // ---- parking cameras ----
        if (root.has("parkingCameras") && root.get("parkingCameras").isArray()) {
            for (var n : root.withArray("parkingCameras")) {
                try {
                    String id = textOr(n, "deviceId", "id");
                    if (id == null) {
                        System.err.println("ParkingCamera sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, null, "address", "addr", "location");
                    int tol = intOr(n, "toleranceSec", "toleranceTime", 120);
                    state.devicesById.put(id, CentralState.DeviceSnapshot.parking(id, addr == null ? "SIN DIRECCIÓN" : addr, tol));
                    state.parkingToleranceSec.put(id, tol);
                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear una parkingCamera: " + e.getMessage());
                }
            }
        }

        // ---- traffic lights / semaphoreControllers ----
        // tu JSON puede llamarlo "trafficLights" o "semaphoreControllers"
        String tlArrayName = root.has("trafficLights") ? "trafficLights" : (root.has("semaphoreControllers") ? "semaphoreControllers" : null);
        if (tlArrayName != null && root.get(tlArrayName).isArray()) {
            for (var n : root.withArray(tlArrayName)) {
                try {
                    String id = textOr(n, "deviceId", "id");
                    if (id == null) {
                        System.err.println("TrafficLight/semáforo sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, "address", "addr", null);
                    boolean principalIsA = booleanOr(n, "principalIsA", true);
                    state.devicesById.put(id, CentralState.DeviceSnapshot.trafficLight(id, addr == null ? "SIN DIRECCIÓN" : addr, principalIsA));
                    state.tlMode.put(id, CentralState.TLMode.NORMAL);
                    state.tlStates.put(id, new CentralState.TLState(
                            Devices.TrafficLightStatus.GREEN,
                            Devices.TrafficLightStatus.RED,
                            principalIsA
                    ));
                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear un trafficLight/semaphoreController: " + e.getMessage());
                }
            }
        }

        // ---- security cameras ----
        if (root.has("securityCameras") && root.get("securityCameras").isArray()) {
            for (var n : root.withArray("securityCameras")) {
                try {
                    String id = textOr(n, "deviceId", "id");
                    if (id == null) {
                        System.err.println("SecurityCamera sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, "address", "addr", null);
                    state.devicesById.put(id, CentralState.DeviceSnapshot.securityCam(id, addr == null ? "SIN DIRECCIÓN" : addr));
                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear una securityCamera: " + e.getMessage());
                }
            }
        }

    }

    // ----- helpers -----
    private static String textOr(JsonNode n, String defaultVal, String... keys) {
        if (n == null) return defaultVal;
        for (String k : keys) {
            if (k != null && n.has(k) && n.get(k) != null && !n.get(k).isNull()) {
                return n.get(k).asText(null);
            }
        }
        return defaultVal;
    }

    private static String textOr(JsonNode n, String key1, String key2) {
        return textOr(n, null, key1, key2);
    }

    private static int intOr(JsonNode n, String key1, String key2, int defaultVal) {
        try {
            String t = textOr(n, null, key1, key2);
            if (t == null) return defaultVal;
            return Integer.parseInt(t);
        } catch (Exception e) {
            try {
                if (n.has(key1) && n.get(key1).canConvertToInt()) return n.get(key1).asInt(defaultVal);
                if (n.has(key2) && n.get(key2).canConvertToInt()) return n.get(key2).asInt(defaultVal);
            } catch (Exception ignored) {
            }
            return defaultVal;
        }
    }

    private static boolean booleanOr(JsonNode n, String key, boolean defaultVal) {
        if (n == null) return defaultVal;
        if (n.has(key) && n.get(key) != null && !n.get(key).isNull()) return n.get(key).asBoolean(defaultVal);
        return defaultVal;
    }
}
