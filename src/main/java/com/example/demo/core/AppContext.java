package com.example.demo.core;

import com.example.demo.runtime.DeviceCatalog;
import com.example.demo.runtime.DeviceFactory;
import com.example.demo.runtime.SnapshotSync;

import com.example.demo.services.TrafficLightCycleService;
import com.example.demo.services.ViolationService;
import com.example.demo.core.ViolationSimulator;
import com.example.demo.core.DeviceFailureSimulator;

import cars.CarService;
import fines.FineIssuer;
import fines.SimpleFineIssuer;

import db.CarDAO;
import db.FineDAO;
import devices.IMaintenanceContext;

import java.nio.file.Path;


/*
Constructor: Inicializa todos los servicios y componentes principales: carga el estado (StatePersistenceService),
crea los objetos de dispositivo (DeviceFactory), inicializa los DAOs de la base de datos, y arranca los simuladores
(ViolationSimulator, DeviceFailureSimulator) y el ciclo de semáforos (TrafficLightCycleService).
Implementa IMaintenanceContext: Actúa como el "contexto" para el polimorfismo de mantenimiento, proveyendo los métodos
pauseTrafficLight y resumeTrafficLight que los dispositivos pueden llamar.
*/

public final class AppContext implements IMaintenanceContext {

    public final StatePersistenceService persistence;
    public final CentralState state;
    public final DeviceCatalog deviceCatalog;
    public final SnapshotSync  snapshotSync;
    public final CarService carService;
    public final FineDAO fineDAO;
    public final FineIssuer fineIssuer;
    public final ViolationService violationService;
    public final TrafficLightCycleService trafficLightCycleService;
    public final ViolationCoordinator violationCoordinator;
    public final ViolationSimulator violationSimulator;
    public final DeviceFailureSimulator deviceFailureSimulator;

    private transient volatile boolean allowSaveOnExit = true;


    public AppContext() {

        // 1) Estado
        this.persistence = new StatePersistenceService(Path.of("state.bin"));
        this.state       = persistence.loadOrBootstrap(Path.of("src/main/resources/devices.json"));
        // 2) Objetos de dispositivos
        this.deviceCatalog = DeviceFactory.buildFrom(state.devicesById);
        this.snapshotSync  = new SnapshotSync(state);
        // 3) (DB)
        var carDAO       = new CarDAO();
        this.carService  = new CarService(carDAO);
        this.fineDAO     = new FineDAO();
        this.fineIssuer  = new SimpleFineIssuer(carService, fineDAO);
        // 4) Violaciones + ciclo semáforos
        this.violationService         = ViolationService.fromSeed(state.violations);
        this.trafficLightCycleService = new TrafficLightCycleService(state);
        this.trafficLightCycleService.stopAll();
        //  (grupos indep y riv )
        java.util.List<String> indep = java.util.stream.IntStream.rangeClosed(1, 27).mapToObj(i -> "Semaphore " + i).toList();
        java.util.List<String> riv = java.util.stream.IntStream.rangeClosed(28, 31).mapToObj(i -> "Semaphore " + i).toList();
        trafficLightCycleService.startCascade("Independencia", indep, 2);
        trafficLightCycleService.startCascade("Rivadavia", riv, 2);

        this.violationSimulator = new ViolationSimulator(state, violationService);
        this.violationSimulator.start();

        // 5) Coordinador violaciones
        this.violationCoordinator = new ViolationCoordinator(violationService, fineIssuer);
        this.violationCoordinator.start();

        this.deviceFailureSimulator = new DeviceFailureSimulator(
                this.deviceCatalog,
                this.snapshotSync
        );
        this.deviceFailureSimulator.start();
    }

    /* Deshabilita el guardado al salir (usado por el Reset) */
    public void disableSaveOnExit() {
        this.allowSaveOnExit = false;
    }

    /* saveOnExit (sin cambios) */
    public void saveOnExit() {
        if (!allowSaveOnExit) {
            System.out.println("🛑 Skipping save on exit (Reset requested).");
            this.trafficLightCycleService.stopAll();
            this.violationSimulator.stop();
            this.deviceFailureSimulator.stop();
            return;
        }

        this.trafficLightCycleService.stopAll();
        this.violationSimulator.stop();
        this.deviceFailureSimulator.stop();
        state.violations = violationService.exportAll();
        persistence.save(state);
    }

    // Implementación de IMaintenanceContext

    @Override
    public void pauseTrafficLight(String deviceId) {
        if (this.trafficLightCycleService != null) {
            // Delega la llamada al servicio real
            this.trafficLightCycleService.stopOne(deviceId);
        }
    }

    @Override
    public void resumeTrafficLight(String deviceId) {
        if (this.trafficLightCycleService != null) {
            // Delega la llamada al servicio real
            this.trafficLightCycleService.resumeOne(deviceId);
        }
    }
}
