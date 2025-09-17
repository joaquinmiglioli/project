package com.example.demo.core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Guarda/carga CentralState en un binario simple (serialización Java). */
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

    public CentralState loadOrBootstrap(Path devicesJson) {
        if (Files.exists(file)) {
            try (var in = new ObjectInputStream(Files.newInputStream(file))) {
                return (CentralState) in.readObject();
            } catch (Exception e) {
                System.err.println("Fallo leyendo snapshot, rearmando desde JSON… " + e.getMessage());
            }
        }
        CentralState st = new CentralState();
        BootstrapLoader.loadFromJson(devicesJson, st);
        return st;
    }
}