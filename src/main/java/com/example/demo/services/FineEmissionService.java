package com.example.demo.services;

import Fines.FineType;
import com.example.demo.controllers.FineNotificationController;
import javafx.collections.ListChangeListener;

import java.io.IOException;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FineEmissionService {

    public static record CalcResult(double amount, int points) {}

    private final ViolationService violations;
    private final DeviceRegistry deviceRegistry;
    private final VehicleService vehicleService;
    private final FineTypeService fineTypeService;
    private final AtomicLong seq = new AtomicLong(1);
    private final Path outDir;

    private ScheduledExecutorService simulationExecutor;
    private final Random random = new Random();
    private List<String> radarIds;
    private List<String> parkingCamIds;
    private List<String> trafficLightIds;

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

    public void start() {
        violations.items().addListener((ListChangeListener<? super ViolationService.Violation>) ch -> {
            while (ch.next()) {
                if (ch.wasAdded()) {
                    for (ViolationService.Violation v : ch.getAddedSubList()) onViolation(v);
                }
            }
        });
    }

    /** Se ejecuta automáticamente cuando ocurre una violación. */
    private void onViolation(ViolationService.Violation v) {
        if (v.type == ViolationService.Type.SERVICE_CALL) return;

        FineType type = switch (v.type) {
            case SPEEDING        -> fineTypeService.getByCode("SPEEDING");
            case ILLEGAL_PARKING -> fineTypeService.getByCode("ILLEGAL_PARKING");
            case RED_LIGHT       -> fineTypeService.getByCode("RED_LIGHT");
            default              -> fineTypeService.getByCode("UNKNOWN");
        };

        CalcResult calc = calcAmountAndPoints(v, type);
        String fineNumber = String.format("%06d", seq.getAndIncrement());

        String address = deviceRegistry.addressFor(v.deviceId);
        String owner   = vehicleService.ownerNameFor(v.plate);
        String brand   = vehicleService.brandFor(v.plate);
        String model   = vehicleService.modelFor(v.plate);
        String color   = vehicleService.colorFor(v.plate);
        String barcode = composeBarcode(fineNumber, calc.amount());

        // Foto: carpeta específica de multas
        String photo = chooseRandomAnomalyPhoto();

        Path pdf = PdfGenerator.generateFinePDF(
                outDir, fineNumber, v, type, calc, address, owner, brand, model, color, barcode, photo
        );
        System.out.println("✅ Fine emitted: " + pdf.toAbsolutePath());

        // Notificar a frontend
        FineNotificationController.updateLastFine(
                fineNumber, v.plate, type.getDescription(), pdf.toAbsolutePath().toString()
        );
    }

    /** Selecciona una imagen desde /static/images/fines y devuelve SOLO el filename. */
    private String chooseRandomAnomalyPhoto() {
        Path imagesDir = Paths.get("src/main/resources/static/images/fines");
        try {
            List<Path> photos = Files.list(imagesDir)
                    .filter(p -> {
                        String n = p.getFileName().toString().toLowerCase();
                        return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
                    })
                    .collect(Collectors.toList());

            if (!photos.isEmpty()) {
                Path rnd = photos.get(random.nextInt(photos.size()));
                System.out.println("📸 Selected photo: " + rnd);
                return rnd.getFileName().toString(); // SOLO nombre
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "fallback.png";
    }

    /** Simulación automática de multas (acepta IDs internos y "humanos"). */
    public void startSimulation() {
        if (simulationExecutor != null) return;

        // NOTA: getAllDeviceIds() devuelve SOLO internos.
        // Para tolerar IDs "humanos" en la simulación, simplemente aceptamos ambos prefijos.
        this.radarIds = deviceRegistry.getAllDeviceIds().stream()
                .filter(id -> id.startsWith("RAD-") || id.startsWith("Radar "))
                .collect(Collectors.toList());

        this.parkingCamIds = deviceRegistry.getAllDeviceIds().stream()
                .filter(id -> id.startsWith("PK-") || id.startsWith("Parking Camera "))
                .collect(Collectors.toList());

        this.trafficLightIds = deviceRegistry.getAllDeviceIds().stream()
                .filter(id -> id.startsWith("INT-") || id.startsWith("Semaphore "))
                .collect(Collectors.toList());

        if (radarIds.isEmpty() && parkingCamIds.isEmpty() && trafficLightIds.isEmpty()) {
            System.out.println("No devices found to start simulation.");
            return;
        }

        simulationExecutor = Executors.newSingleThreadScheduledExecutor();

        Runnable simulationTask = new Runnable() {
            @Override
            public void run() {
                try {
                    int fineType = random.nextInt(3);
                    switch (fineType) {
                        case 0 -> {
                            if (!radarIds.isEmpty()) {
                                String radarId = radarIds.get(random.nextInt(radarIds.size()));
                                String plate = vehicleService.randomPlateOrGenerate();
                                int limit = 80;
                                int speed = limit + random.nextInt(61);
                                violations.recordSpeeding(radarId, plate, speed, limit);
                                System.out.println("SIM: Generated SPEEDING fine for plate " + plate);
                            }
                        }
                        case 1 -> {
                            if (!parkingCamIds.isEmpty()) {
                                String camId = parkingCamIds.get(random.nextInt(parkingCamIds.size()));
                                String plate = vehicleService.randomPlateOrGenerate();
                                int tolerance = 300;
                                int stayTime = tolerance + random.nextInt(301);
                                violations.recordIllegalParking(camId, plate, stayTime, tolerance);
                                System.out.println("SIM: Generated ILLEGAL_PARKING fine for plate " + plate);
                            }
                        }
                        case 2 -> {
                            if (!trafficLightIds.isEmpty()) {
                                String intersectionId = trafficLightIds.get(random.nextInt(trafficLightIds.size()));
                                String plate = vehicleService.randomPlateOrGenerate();
                                String[] directions = {"N-S", "E-W"};
                                String direction = directions[random.nextInt(directions.length)];
                                violations.recordRedLight(intersectionId, plate, direction);
                                System.out.println("SIM: Generated RED_LIGHT fine for plate " + plate);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    long delay = 10 + random.nextInt(36);
                    simulationExecutor.schedule(this, delay, TimeUnit.SECONDS);
                }
            }
        };

        System.out.println("Fine simulation started.");
        simulationExecutor.schedule(simulationTask, 5, TimeUnit.SECONDS);
    }

    public void stopSimulation() {
        if (simulationExecutor != null) {
            simulationExecutor.shutdown();
            System.out.println("Fine simulation stopped.");
        }
    }

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
                    double over = (speed - limit) / (double) limit;
                    int blocks = (int) Math.floor(over / 0.10);
                    double r = fineTypeService.speedingSurchargePer10Percent();
                    for (int i = 0; i < blocks; i++) amount *= (1.0 + r);
                    points += blocks;
                }
            }
        }
        amount = Math.round(amount * 100.0) / 100.0;
        return new CalcResult(amount, points);
    }

    private static String composeBarcode(String sixDigitsNumber, double amount) {
        long cents = Math.round(amount * 100.0);
        String amount12 = String.format("%012d", cents);
        return sixDigitsNumber + amount12;
    }
}
