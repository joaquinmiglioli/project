package com.example.demo.controllers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import devices.SecurityWarning;
import devices.ServiceType;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/security")
public class SecurityOpsController {

    /** Log de notificaciones (en memoria, thread-safe). */
    private final List<SecurityWarning> notifications = new CopyOnWriteArrayList<>();

    /** Opcional: lista de tipos válidos (por si el front quiere poblar UI). */
    @GetMapping("/services")
    public List<String> services() {
        return Arrays.stream(ServiceType.values()).map(Enum::name).toList();
    }

    /** Lista rutas públicas de imágenes (preferencia /static/images/security). */
    @GetMapping("/images")
    public List<String> listImages() throws IOException {
        var preferred = new ClassPathResource("static/images/security");
        if (preferred.exists()) {
            try (var s = Files.list(preferred.getFile().toPath())) {
                return s.filter(Files::isRegularFile)
                        .map(p -> "/images/security/" + p.getFileName())
                        .filter(SecurityOpsController::isImage)
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        var fallback = new ClassPathResource("static/images");
        if (fallback.exists()) {
            try (var s = Files.list(fallback.getFile().toPath())) {
                return s.filter(Files::isRegularFile)
                        .map(p -> "/images/" + p.getFileName())
                        .filter(SecurityOpsController::isImage)
                        .sorted()
                        .collect(Collectors.toList());
            }
        }
        return List.of();
    }

    private static boolean isImage(String path) {
        String u = path.toLowerCase(Locale.ROOT);
        return u.endsWith(".jpg") || u.endsWith(".jpeg") || u.endsWith(".png") || u.endsWith(".webp");
    }

    /** Crea y guarda un SecurityWarning. */
    @PostMapping("/notify")
    public Map<String, Object> notifyService(
            @RequestParam String cameraId,
            @RequestParam String service,            // POLICE | SAME | FIRE | FALSE_ALARM
            @RequestParam(required = false) String imagePath,
            @RequestParam(required = false) String note
    ) {
        ServiceType type = parseService(service);

        SecurityWarning sw = new SecurityWarning();
        sw.setDeviceId(cameraId);
        sw.setServiceType(type);
        sw.setImagePath(imagePath);
        sw.setNote(note != null ? note.trim() : null);
        sw.setTimestamp(Instant.now());

        notifications.add(sw);

        return Map.of("ok", true, "event", sw);
    }

    /** Para inspección/depuración del historial. */
    @GetMapping("/log")
    public List<SecurityWarning> log() {
        // En orden cronológico (más nuevos al final, como se insertan)
        return notifications;
    }

    private ServiceType parseService(String s) {
        if (s == null) return ServiceType.POLICE;
        try {
            return ServiceType.valueOf(s.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return ServiceType.POLICE;
        }
    }
}
