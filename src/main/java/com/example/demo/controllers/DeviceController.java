package com.example.demo.controllers;

import com.example.demo.core.AppContext;
import com.example.demo.core.CentralState;
import devices.Device;
import devices.DeviceStatus;
import devices.TrafficLightStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final AppContext ctx;

    public DeviceController(AppContext ctx) { this.ctx = ctx; }

    /**
     * JSON agrupado para el front:
     * - radars:          { id, lat, lng, status, limit, speedLimit }
     * - parkingCameras:  { id, lat, lng, status, toleranceTime, toleranceSec }
     * - cameras:         { id, lat, lng, status }
     * - semaphores:      { id, lat, lng, status, a, b, principalIsA }
     */
    @GetMapping
    public Map<String, Object> grouped() {
        Map<String, Object> out = new LinkedHashMap<>();
        Collection<CentralState.DeviceSnapshot> all = ctx.state.devicesById.values();

        // ---------- RADARS (incluimos limit/speedLimit) ----------
        var radars = all.stream()
                .filter(s -> "Radar".equalsIgnoreCase(s.type))
                .map(s -> {
                    Integer limit = s.speedLimit != null ? s.speedLimit
                            : ctx.state.radarLimit.getOrDefault(s.deviceId, 60);
                    return Map.<String,Object>of(
                            "id", s.deviceId,
                            "lat", s.lat,
                            "lng", s.lng,
                            "status", uiStatus(s.status),
                            "limit", limit,         // 👈 nombre que suelen usar los popups
                            "speedLimit", limit     // 👈 alias por si el front espera este
                    );
                })
                .collect(Collectors.toList());
        out.put("radars", radars);

        // ---------- PARKING CAMERAS (toleranceTime/toleranceSec) ----------
        var parking = all.stream()
                .filter(s -> "ParkingCamera".equalsIgnoreCase(s.type))
                .map(s -> {
                    Integer tol = s.toleranceSec != null ? s.toleranceSec
                            : ctx.state.parkingToleranceSec.getOrDefault(s.deviceId, 120);
                    return Map.<String,Object>of(
                            "id", s.deviceId,
                            "lat", s.lat,
                            "lng", s.lng,
                            "status", uiStatus(s.status),
                            "toleranceTime", tol,   // 👈 coincide con tu JSON original
                            "toleranceSec",  tol    // 👈 alias, por las dudas
                    );
                })
                .collect(Collectors.toList());
        out.put("parkingCameras", parking);

        // ---------- SECURITY CAMERAS ----------
        var cams = all.stream()
                .filter(s -> "SecurityCamera".equalsIgnoreCase(s.type))
                .map(s -> Map.of(
                        "id", s.deviceId,
                        "lat", s.lat,
                        "lng", s.lng,
                        "status", uiStatus(s.status)
                ))
                .collect(Collectors.toList());
        out.put("cameras", cams);

        // ---------- SEMAPHORES (TrafficLight en state → “semaphores”) ----------
        var sems = all.stream()
                .filter(s -> "TrafficLight".equalsIgnoreCase(s.type))
                .map(s -> {
                    CentralState.TLState st = ctx.state.tlStates.get(s.deviceId);
                    TrafficLightStatus a = st != null ? st.a : TrafficLightStatus.RED;
                    TrafficLightStatus b = st != null ? st.b : TrafficLightStatus.RED;
                    boolean principalIsA = st != null && st.principalIsA;
                    return Map.<String,Object>of(
                            "id", s.deviceId,
                            "lat", s.lat,
                            "lng", s.lng,
                            "status", uiStatus(s.status),
                            "a", a.name(),
                            "b", b.name(),
                            "principalIsA", principalIsA
                    );
                })
                .collect(Collectors.toList());
        out.put("semaphores", sems);

        return out;
    }

    /** Lista plana para depuración. */
    @GetMapping("/raw")
    public Collection<CentralState.DeviceSnapshot> raw() {
        return ctx.state.devicesById.values();
    }

    /** POST de estado (sigue disponible). */
    @PostMapping("/{id}/status")
    public Map<String, String> setStatus(@PathVariable String id, @RequestParam String action) {
        return applyStatusAction(id, action);
    }

    // ✅ Endpoints GET rápidos para enganchar desde el popup (menos fricción en el front)
    @GetMapping("/{id}/repair")
    public Map<String, String> repair(@PathVariable String id) {
        return applyStatusAction(id, "REPAIR");
    }

    @GetMapping("/{id}/fail")
    public Map<String, String> fail(@PathVariable String id) {
        return applyStatusAction(id, "FAIL");
    }

    @GetMapping("/{id}/intermittent")
    public Map<String, String> intermittent(@PathVariable String id) {
        return applyStatusAction(id, "INTERMITTENT");
    }

    // ---------- helpers ----------
    private Map<String, String> applyStatusAction(String id, String action) {
        Device d = ctx.deviceCatalog.get(id);
        if (d == null) throw new IllegalArgumentException("Device not found: " + id);

        switch (action.toUpperCase()) {
            case "FAIL"         -> d.fail();
            case "REPAIR"       -> d.repair();
            case "INTERMITTENT" -> d.intermittent();
            default -> throw new IllegalArgumentException("Unknown action: " + action);
        }
        // reflejamos al snapshot (front/persistencia)
        ctx.snapshotSync.pushToSnapshot(d);
        return Map.of("id", id, "status", d.getStatus().name());
    }

    private static String uiStatus(DeviceStatus st) {
        if (st == null) return "READY";
        return switch (st) {
            case NORMAL       -> "READY";
            case FAILURE      -> "ERROR";
            case INTERMITTENT -> "YELLOW";
            default           -> "READY";
        };
    }

}