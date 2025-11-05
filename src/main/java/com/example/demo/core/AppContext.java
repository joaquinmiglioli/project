package com.example.demo.core;

import com.example.demo.runtime.DeviceCatalog;
import com.example.demo.runtime.DeviceFactory;
import com.example.demo.runtime.SnapshotSync;

import com.example.demo.services.TrafficLightCycleService;
import com.example.demo.services.ViolationService;
import com.example.demo.core.ViolationSimulator;

import cars.CarService;
import fines.FineIssuer;
import fines.SimpleFineIssuer;

import db.CarDAO;
import db.FineDAO;

import java.nio.file.Path;

public final class AppContext {

    // ===== Persistencia de estado (sin cambios) =====
    public final StatePersistenceService persistence;
    public final CentralState state;

    // ===== Runtime de dispositivos =====
    public final DeviceCatalog deviceCatalog;
    public final SnapshotSync  snapshotSync;

    // ===== Dominio AUTOS & MULTAS (DB) =====
    public final CarService carService;
    public final FineDAO fineDAO;
    public final FineIssuer fineIssuer;

    // ===== Violaciones y Semáforos =====
    public final ViolationService violationService;
    public final TrafficLightCycleService trafficLightCycleService;

    public final ViolationCoordinator violationCoordinator;

    public final ViolationSimulator violationSimulator;

    // (transient para que no se guarde en state.bin)
    // (volatile para que sea visible entre hilos)
    private transient volatile boolean allowSaveOnExit = true;


    public AppContext() {
        // 1) Estado
        this.persistence = new StatePersistenceService(Path.of("state.bin"));
        this.state       = persistence.loadOrBootstrap(Path.of("src/main/resources/devices.json"));

        // 2) Objetos de dispositivos desde snapshot
        this.deviceCatalog = DeviceFactory.buildFrom(state.devicesById);
        this.snapshotSync  = new SnapshotSync(state);

        // 3) Servicios de dominio (DB)
        var carDAO       = new CarDAO();
        this.carService  = new CarService(carDAO);
        this.fineDAO     = new FineDAO();
        this.fineIssuer  = new SimpleFineIssuer(carService, fineDAO);

        // 4) Violaciones + ciclo semáforos
        this.violationService         = ViolationService.fromSeed(state.violations);
        this.trafficLightCycleService = new TrafficLightCycleService(state);

        // 1) Apago cualquier ciclo previo (defensivo)
        this.trafficLightCycleService.stopAll();

        // 2) Grupo Independencia: Semáforos 1..27
        java.util.List<String> indep = java.util.stream.IntStream.rangeClosed(1, 27)
                .mapToObj(i -> "Semaphore " + i)
                .toList();

        // 3) Grupo Rivadavia: Semáforos 28..31
        java.util.List<String> riv = java.util.stream.IntStream.rangeClosed(28, 31)
                .mapToObj(i -> "Semaphore " + i)
                .toList();
        // 4) // Ambos grupos comienzan en ROJO y lanzan onda con offset de 1s
        trafficLightCycleService.startCascade("Independencia", indep, 2);
        trafficLightCycleService.startCascade("Rivadavia", riv, 2);

        this.violationSimulator = new ViolationSimulator(state, violationService);
        this.violationSimulator.start();

        // 5) Coordinador violaciones → multas (comienza a escuchar)
        this.violationCoordinator = new ViolationCoordinator(violationService, fineIssuer);
        this.violationCoordinator.start();
    }

    /** Deshabilita el guardado al salir (usado por el Reset) */
    public void disableSaveOnExit() {
        this.allowSaveOnExit = false;
    }

    public void saveOnExit() {
        // Añadimos esta comprobación al inicio
        if (!allowSaveOnExit) {
            System.out.println("🛑 Skipping save on exit (Reset requested).");
            this.trafficLightCycleService.stopAll(); // Detenemos los ciclos
            return; // NO GUARDAR
        }

        // Si allowSaveOnExit es true, hace lo de siempre:
        this.trafficLightCycleService.stopAll();
        state.violations = violationService.exportAll();
        persistence.save(state);
    }
}
