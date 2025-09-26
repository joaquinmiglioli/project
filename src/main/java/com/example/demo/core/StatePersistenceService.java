package com.example.demo.core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class StatePersistenceService {
    private final Path file;

    public StatePersistenceService(Path file) { this.file = file; }

    public void save(CentralState state) {
        try (var out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(state);
        } catch (Exception e) {
            throw new RuntimeException("Saving " + file, e);
        }
    }

    public CentralState loadOrBootstrap(Path ignoredDevicesJsonPath) {
        // Si ya existe snapshot => cargar
        if (Files.exists(file)) {
            try (var in = new ObjectInputStream(Files.newInputStream(file))) {
                return (CentralState) in.readObject();
            } catch (Exception e) {
                System.err.println("Fallo leyendo snapshot, rearmando desde JSON… " + e.getMessage());
            }
        }
        // Si no, bootstrap desde /devices.json en el classpath
        CentralState st = new CentralState();
        try (var is = getClass().getResourceAsStream("/devices.json")) {
            if (is == null) throw new IllegalStateException("devices.json no encontrado en classpath");
            var tmp = Files.createTempFile("devices", ".json");
            Files.copy(is, tmp, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            BootstrapLoader.loadFromJson(tmp, st);
        } catch (Exception e) {
            throw new RuntimeException("Error cargando devices.json", e);
        }
        return st;
    }
}
