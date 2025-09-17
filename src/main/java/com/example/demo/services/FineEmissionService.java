package com.example.demo.services;

import Fines.FineType;
import javafx.collections.ListChangeListener;
import com.example.demo.services.PdfGenerator;

import java.nio.file.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FineEmissionService {

    public static record CalcResult(double amount, int points) {}

    private final ViolationService violations;
    private final DeviceRegistry deviceRegistry;   // ← este es el campo correcto
    private final VehicleService vehicleService;
    private final FineTypeService fineTypeService;
    private final AtomicLong seq = new AtomicLong(1);
    private final Path outDir;

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    public FineEmissionService(ViolationService v,
                               DeviceRegistry d,
                               VehicleService vs,
                               FineTypeService fs) {
        this.violations = v;
        this.deviceRegistry = d;
        this.vehicleService = vs;
        this.fineTypeService = fs;
        this.outDir = Paths.get(System.getProperty("user.dir"), "fines");
        try { Files.createDirectories(outDir); } catch (Exception ignore) {}
    }

    /** Conecta el listener: toda violación nueva (salvo SERVICE_CALL) genera multa PDF. */
    public void start() {
        violations.items().addListener((ListChangeListener<? super ViolationService.Violation>) ch -> {
            while (ch.next()) {
                if (ch.wasAdded()) {
                    for (ViolationService.Violation v : ch.getAddedSubList()) onViolation(v);
                }
            }
        });
    }

    private void onViolation(ViolationService.Violation v) {
        if (v.type == ViolationService.Type.SERVICE_CALL) return; // no es multa

        FineType type = switch (v.type) {
            case SPEEDING         -> fineTypeService.getByCode("SPEEDING");
            case ILLEGAL_PARKING  -> fineTypeService.getByCode("ILLEGAL_PARKING");
            case RED_LIGHT        -> fineTypeService.getByCode("RED_LIGHT");
            default               -> fineTypeService.getByCode("UNKNOWN");
        };

        CalcResult calc = calcAmountAndPoints(v, type);
        String fineNumber = String.format("%06d", seq.getAndIncrement());

        // FIX: usar deviceRegistry
        String address = deviceRegistry.addressFor(v.deviceId);
        String owner   = vehicleService.ownerNameFor(v.plate);
        String brand   = vehicleService.brandFor(v.plate);
        String model   = vehicleService.modelFor(v.plate);
        String color   = vehicleService.colorFor(v.plate);

        String barcode = composeBarcode(fineNumber, calc.amount());
        String photo   = vehicleService.randomCarPhotoPathOrNull(); // si no hay, el PDF lo ignora

        Path pdf = PdfGenerator.generateFinePDF(
                outDir, fineNumber, v, type, calc, address, owner, brand, model, color, barcode, photo
        );
        System.out.println("Fine emitted: " + pdf.toAbsolutePath());
    }

    // ====== cálculo de importes/puntos ======

    private static final Pattern SPEED_P =
            Pattern.compile("Speed\\s+(\\d+)\\s*.*?\\(limit\\s+(\\d+)\\)", Pattern.CASE_INSENSITIVE);

    private CalcResult calcAmountAndPoints(ViolationService.Violation v, FineType base) {
        double amount = base.getAmount();
        int points = base.getScoringPoints();

        if (v.type == ViolationService.Type.SPEEDING) {
            Matcher m = SPEED_P.matcher(v.details);
            if (m.find()) {
                int speed = Integer.parseInt(m.group(1));
                int limit = Integer.parseInt(m.group(2));
                if (speed > limit) {
                    double over = (speed - limit) / (double) limit; // ej 0.27 = 27%
                    int blocks = (int) Math.floor(over / 0.10);     // 1 por cada +10%
                    double r = fineTypeService.speedingSurchargePer10Percent(); // ej 0.10 = +10%
                    for (int i = 0; i < blocks; i++) amount *= (1.0 + r);       // acumulativa
                    points += blocks; // +1 punto por cada 10%
                }
            }
        }
        amount = Math.round(amount * 100.0) / 100.0; // 2 decimales
        return new CalcResult(amount, points);
    }

    // ====== “código de barras” numérico (6 dígitos nro + 12 dígitos importe en centavos) ======
    private static String composeBarcode(String sixDigitsNumber, double amount) {
        long cents = Math.round(amount * 100.0);
        String amount12 = String.format("%012d", cents);
        return sixDigitsNumber + amount12;
    }
}