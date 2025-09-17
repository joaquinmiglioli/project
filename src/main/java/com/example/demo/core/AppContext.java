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
    public final DeviceRegistry   deviceRegistry; // opcional (podemos sacar más adelante)
    public final FineEmissionService fineEmissionService;

    public AppContext() {
        this.persistence = new StatePersistenceService(Path.of("state.bin"));
        this.state = persistence.loadOrBootstrap(Path.of("src/main/resources/devices.json"));

        // Servicios
        this.violationService = ViolationService.fromSeed(state.violations);
        this.fineTypeService  = new FineTypeService();
        this.vehicleService   = new VehicleService();
        this.deviceRegistry   = new DeviceRegistry(); // ya lo usaba tu PDF Generator
        this.fineEmissionService = new FineEmissionService(violationService, deviceRegistry, vehicleService, fineTypeService);

        // Listener que emite PDF automáticamente:
        this.fineEmissionService.start();
        // Iniciar simulación de multas (velocidad, parking, semáforo en rojo)
        this.fineEmissionService.startSimulation();
    }

    /** Llamar al cerrar la app. */
    public void saveOnExit() {
        // Detener la simulación
        this.fineEmissionService.stopSimulation();
        // Refrescar el array persistible con lo que esté en el servicio
        state.violations = violationService.exportAll();
        // Guardar snapshot
        persistence.save(state);
    }
}