package com.example.demo.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import devices.DeviceStatus;
import devices.TrafficLightStatus;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/*
  Carga devices iniciales desde devices.json .
  Sigue la estructura con:
   - "semaphoreControllers" (con semA/semB, main, status, lat/lng)
   - "radars", "parkingCameras", "securityCameras"
  También settea tlStates y principalIsA para semáforos.

*/
public final class BootstrapLoader {
    private BootstrapLoader() {}

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

    // lee cada sección del JSON y completa CentralState
    private static void populateFromRoot(JsonNode root, CentralState state) {
        if (root == null || !root.isObject()) {
            throw new RuntimeException("devices.json no es un objeto JSON válido");
        }
    //      Semáforos
        if (root.has("semaphoreControllers") && root.get("semaphoreControllers").isArray()) {
            for (var n : root.withArray("semaphoreControllers")) {
                try {
                    String id   = textOr(n, "id", "deviceId");
                    if (id == null) {
                        System.err.println("TrafficLight sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, "address", "addr", "location");
                    if (addr == null) addr = "SIN DIRECCIÓN";

                    boolean principalIsA = booleanOr(n.path("semA"), "main", true);

                    TrafficLightStatus a = mapLight(textOr(n.path("semA"), "status"));
                    TrafficLightStatus b = mapLight(textOr(n.path("semB"), "status"));

                    if (a == null) a = TrafficLightStatus.GREEN;
                    if (b == null) b = TrafficLightStatus.RED;

                    // Determina el estado del dispositivo

                    DeviceStatus deviceStatus = mapDeviceStatus(textOr(n, "status"));

                    // sobreescribe a failure si ambas luces son VERDES
                    if (a == TrafficLightStatus.GREEN && b == TrafficLightStatus.GREEN) {
                        deviceStatus = DeviceStatus.FAILURE;
                    }

                    // Crear el snapshot con el estado de dispositivo VALIDADO
                    CentralState.DeviceSnapshot snap = CentralState.DeviceSnapshot.trafficLight(id, addr, principalIsA);
                    snap.lat = doubleOr(n, "lat", "latitude", "y");
                    snap.lng = doubleOr(n, "lng", "lon", "long", "longitude", "x");
                    snap.status = deviceStatus;

                    state.devicesById.put(id, snap);

                    // Guarda el estado de las luces (A y B)
                    state.tlMode.put(id, deviceStatus == DeviceStatus.FAILURE
                            ? CentralState.TLMode.FLASHING
                            : CentralState.TLMode.NORMAL);

                    state.tlStates.put(id, new CentralState.TLState(a, b, principalIsA));

                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear un semaphoreController: " + e.getMessage());
                }
            }
        }
        // Radares
        if (root.has("radars") && root.get("radars").isArray()) {
            for (var n : root.withArray("radars")) {
                try {
                    String id   = textOr(n, "id", "deviceId");
                    if (id == null) {
                        System.err.println("Radar sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, "address", "addr", "location");
                    if (addr == null) addr = "SIN DIRECCIÓN";

                    int limit = intOr(n, "speedLimit", "limit", 60); // default 60 si no viene
                    CentralState.DeviceSnapshot snap =
                            CentralState.DeviceSnapshot.radar(id, addr, limit);
                    snap.lat = doubleOr(n, "lat", "latitude", "y");
                    snap.lng = doubleOr(n, "lng", "lon", "long", "longitude", "x");
                    snap.status = mapDeviceStatus(textOr(n, "status"));

                    state.devicesById.put(id, snap);
                    state.radarLimit.put(id, limit);
                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear un radar: " + e.getMessage());
                }
            }
        }

        // Camaras de Estacionamiento
        if (root.has("parkingCameras") && root.get("parkingCameras").isArray()) {
            for (var n : root.withArray("parkingCameras")) {
                try {
                    String id   = textOr(n, "id", "deviceId");
                    if (id == null) {
                        System.err.println("ParkingCamera sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, "address", "addr", "location");
                    if (addr == null) addr = "SIN DIRECCIÓN";

                    int tol = intOr(n, "toleranceSec", "toleranceTime", 120); // tomamos toleranceTime si viene
                    CentralState.DeviceSnapshot snap =
                            CentralState.DeviceSnapshot.parking(id, addr, tol);
                    snap.lat = doubleOr(n, "lat", "latitude", "y");
                    snap.lng = doubleOr(n, "lng", "lon", "long", "longitude", "x");
                    snap.status = mapDeviceStatus(textOr(n, "status"));

                    state.devicesById.put(id, snap);
                    state.parkingToleranceSec.put(id, tol);
                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear una parkingCamera: " + e.getMessage());
                }
            }
        }

        // Camaras de seguridad
        if (root.has("securityCameras") && root.get("securityCameras").isArray()) {
            for (var n : root.withArray("securityCameras")) {
                try {
                    String id   = textOr(n, "id", "deviceId");
                    if (id == null) {
                        System.err.println("SecurityCamera sin id (se ignora): " + n);
                        continue;
                    }
                    String addr = textOr(n, "address", "addr", "location");
                    if (addr == null) addr = "SIN DIRECCIÓN";

                    CentralState.DeviceSnapshot snap =
                            CentralState.DeviceSnapshot.securityCam(id, addr);
                    snap.lat = doubleOr(n, "lat", "latitude", "y");
                    snap.lng = doubleOr(n, "lng", "lon", "long", "longitude", "x");
                    snap.status = mapDeviceStatus(textOr(n, "status"));

                    state.devicesById.put(id, snap);
                } catch (Exception e) {
                    System.err.println("Warning: no pude parsear una securityCamera: " + e.getMessage());
                }
            }
        }
    }

    private static String textOr(JsonNode n, String... keys) {
        if (n == null) return null;
        for (String k : keys) {
            if (k != null && n.has(k) && n.get(k) != null && !n.get(k).isNull()) {
                return n.get(k).asText(null);
            }
        }
        return null;
    }

    private static int intOr(JsonNode n, String k1, String k2, int def) {
        try {
            String t = textOr(n, k1, k2);
            if (t == null) return def;
            return Integer.parseInt(t);
        } catch (Exception e) {
            try {
                if (n.has(k1) && n.get(k1).canConvertToInt()) return n.get(k1).asInt(def);
                if (n.has(k2) && n.get(k2).canConvertToInt()) return n.get(k2).asInt(def);
            } catch (Exception ignored) {}
            return def;
        }
    }

    private static boolean booleanOr(JsonNode n, String key, boolean def) {
        if (n == null) return def;
        if (n.has(key) && n.get(key) != null && !n.get(key).isNull()) return n.get(key).asBoolean(def);
        return def;
    }

    private static Double doubleOr(JsonNode n, String... keys) {
        if (n == null) return null;
        for (String k : keys) {
            if (k != null && n.has(k) && !n.get(k).isNull()) {
                try { return n.get(k).asDouble(); } catch (Exception ignored) {}
            }
        }
        return null;
    }

    private static DeviceStatus mapDeviceStatus(String s) {
        if (s == null) return DeviceStatus.NORMAL;
        return switch (s.toUpperCase()) {

            case "READY", "NORMAL", "OK", "GREEN", "RED", "YELLOW" -> DeviceStatus.NORMAL;

            case "ERROR", "FAIL", "FAILURE", "DOWN" -> DeviceStatus.FAILURE;

            case "INTERMITTENT", "WARN" -> DeviceStatus.INTERMITTENT;

            default -> DeviceStatus.NORMAL;
        };
    }

    private static TrafficLightStatus mapLight(String s) {
        if (s == null) return null;
        return switch (s.toUpperCase()) {
            case "RED"    -> TrafficLightStatus.RED;
            case "YELLOW" -> TrafficLightStatus.YELLOW;
            case "GREEN"  -> TrafficLightStatus.GREEN;
            default       -> null;
        };
    }
}
