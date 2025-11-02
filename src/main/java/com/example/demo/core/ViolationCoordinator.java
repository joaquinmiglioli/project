// com/example/demo/core/ViolationCoordinator.java
package com.example.demo.core;

import com.example.demo.controllers.FineNotificationController;
import com.example.demo.services.PdfGenerator;
import com.example.demo.services.ViolationService;
import fines.Fine;
import fines.FineIssuer;
import fines.FineType;
import javafx.collections.ListChangeListener;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViolationCoordinator {

    private final ViolationService violations;
    private final FineIssuer issuer;

    public ViolationCoordinator(ViolationService violations, FineIssuer issuer) {
        this.violations = violations;
        this.issuer = issuer;
    }

    public void start() {
        violations.items().addListener((ListChangeListener<? super ViolationService.Violation>) ch -> {
            while (ch.next()) {
                if (ch.wasAdded()) {
                    for (ViolationService.Violation v : ch.getAddedSubList()) {
                        try { onViolation(v); } catch (Throwable t) { t.printStackTrace(); }
                    }
                }
            }
        });
    }

    private void onViolation(ViolationService.Violation v) {
        if (v.type == ViolationService.Type.SERVICE_CALL) return;

        FineType type = switch (v.type) {
            case SPEEDING        -> FineType.SPEEDING;
            case ILLEGAL_PARKING -> FineType.PARKING;
            case RED_LIGHT       -> FineType.RED_LIGHT;
            default              -> FineType.RED_LIGHT;
        };

        Map<String,Object> meta = new HashMap<>();
        // Parseo de detalles si aplica
        switch (v.type) {
            case SPEEDING -> { // "Speed 123 (limit 80)"
                Matcher m = Pattern.compile("Speed\\s+(\\d+)\\s*.*?\\(limit\\s+(\\d+)\\)", Pattern.CASE_INSENSITIVE).matcher(v.details);
                if (m.find()) {
                    meta.put("speed", Integer.parseInt(m.group(1)));
                    meta.put("limit", Integer.parseInt(m.group(2)));
                }
            }
            case ILLEGAL_PARKING -> { // "Stay 620s (tol 300s)"
                Matcher m = Pattern.compile("Stay\\s+(\\d+)s\\s*.*?\\(tol\\s+(\\d+)s\\)", Pattern.CASE_INSENSITIVE).matcher(v.details);
                if (m.find()) {
                    meta.put("parkedSec", Integer.parseInt(m.group(1)));
                    meta.put("toleranceSec", Integer.parseInt(m.group(2)));
                }
            }
            default -> {}
        }

        // Elegimos una evidencia aleatoria (sólo filename)
        String photoFile = devices.Photo.randomFinePhotoFilename();

        // 1) Emisión (calcula, inserta en DB y setea id/barcode dentro del objeto Fine)
        Fine fine = issuer.issue(type, v.deviceId, photoFile, meta);

        // 2) Generar PDF directo desde Fine
        Path outDir = Path.of(System.getProperty("user.dir"), "fines");
        try { Files.createDirectories(outDir); } catch (Exception ignore) {}
        Path pdf = PdfGenerator.generateFinePDF(outDir, fine);

        // 3) Notificar a frontend (toast)
        String fineNumber = String.format("%06d", fine.getFineId());
        FineNotificationController.updateLastFine(
                fineNumber,
                fine.getCar() != null ? fine.getCar().getPlate() : "-",
                fine.getType().name(),
                pdf.toAbsolutePath().toString()
        );
    }
}
