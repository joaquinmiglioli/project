package com.example.demo.core;

import com.example.demo.services.*;
import java.nio.file.Path;

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
            this.trafficLightCycleService.startAll();
            System.out.println("✅ Ciclos de semáforos iniciados correctamente.");
        } else {
            System.out.println("⚠️ No se encontraron semáforos para iniciar ciclos.");
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
