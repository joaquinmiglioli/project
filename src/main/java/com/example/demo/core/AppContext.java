package com.example.demo.core;

import com.example.demo.services.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Contenedor de servicios + estado compartido, para inyectar en el Controller. */
public final class AppContext {
    public final CentralState state;
    public final StatePersistenceService persistence;

    public final ViolationService violationService;
    public final FineTypeService  fineTypeService;
    public final VehicleService   vehicleService;
    public final DeviceRegistry   deviceRegistry;
    public final FineEmissionService fineEmissionService;

    // servicio de ciclo de semáforos
    public final TrafficLightCycleService trafficLightCycleService;

    public AppContext() {
        // ====== Cargar estado y persistencia ======
        this.persistence = new StatePersistenceService(Path.of("state.bin"));
        this.state = persistence.loadOrBootstrap(Path.of("src/main/resources/devices.json"));

        // ====== Inicialización de servicios ======
        this.violationService = ViolationService.fromSeed(state.violations);
        this.fineTypeService  = new FineTypeService();
        this.vehicleService   = new VehicleService();
        this.deviceRegistry   = new DeviceRegistry();
        this.fineEmissionService = new FineEmissionService(
                violationService, deviceRegistry, vehicleService, fineTypeService
        );

        // ====== Inicialización de ciclo de semáforos ======
        this.trafficLightCycleService = new TrafficLightCycleService(state);

        // Listener que emite PDF automáticamente
        this.fineEmissionService.start();
        // Iniciar simulación de multas (velocidad, parking, semáforo en rojo)
        this.fineEmissionService.startSimulation();

        // ====== Iniciar el ciclo en todos los semáforos cargados ======
        if (!state.tlStates.isEmpty()) {
            // Separar semáforos por calle principal para crear una "onda verde" por cada una.
            // Nota: esta agrupación se basa en la configuración de `devices.json`.
            List<String> independenciaIds = new ArrayList<>();
            for (int i = 1; i <= 27; i++) { independenciaIds.add("Semaphore " + i); }

            List<String> rivadaviaIds = new ArrayList<>();
            for (int i = 28; i <= 31; i++) { rivadaviaIds.add("Semaphore " + i); }

            // Filtrar y ordenar los IDs que realmente existen en el estado actual
            independenciaIds = independenciaIds.stream().filter(state.tlStates::containsKey).collect(Collectors.toList());
            rivadaviaIds = rivadaviaIds.stream().filter(state.tlStates::containsKey).collect(Collectors.toList());

            // Iniciar cascada para cada calle
            if (!independenciaIds.isEmpty()) {
                this.trafficLightCycleService.startCascade(independenciaIds, 5); // 5s de separación
                System.out.println("✅ Independencia cascade started.");
            }
            if (!rivadaviaIds.isEmpty()) {
                this.trafficLightCycleService.startCascade(rivadaviaIds, 5); // 5s de separación
                System.out.println("✅ Rivadavia cascade started.");
            }

            System.out.println("✅ All semaphore cascade cycles started succesfully.");

        } else {
            System.out.println("⚠️ No semaphores were found to start a cycle.");
        }
    }

    /** Llamar al cerrar la app. */
    public void saveOnExit() {
        // Detener simulación de multas
        this.fineEmissionService.stopSimulation();

        // ✅ Detener ciclos de semáforos para evitar hilos vivos al cerrar
        this.trafficLightCycleService.stopAll();

        // Actualizar lista de infracciones antes de persistir
        state.violations = violationService.exportAll();

        // Guardar estado
        persistence.save(state);
    }
}
