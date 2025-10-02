package com.example.demo.core;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/** Guarda/carga CentralState en un binario simple (serialización Java).

 * Si no hay snapshot, intenta cargar devices.json desde el Path; si no existe,
 * intenta cargar devices.json desde el classpath (resources).
 */
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

    public CentralState loadOrBootstrap(Path devicesJsonPath) {
        // 1) si existe el snapshot binario, lo leemos
        if (Files.exists(file)) {
            try (var in = new ObjectInputStream(Files.newInputStream(file))) {
                return (CentralState) in.readObject();
            } catch (Exception e) {
                System.err.println("Fallo leyendo snapshot, rearmando desde JSON… " + e.getMessage());
            }
        }
        // 2) No hay snapshot: construiremos el estado a partir del JSON
        CentralState st = new CentralState();

        // 2.a) si devicesJsonPath existe en disco, usarlo
        try {
            if (devicesJsonPath != null && Files.exists(devicesJsonPath)) {
                BootstrapLoader.loadFromJson(devicesJsonPath, st);
                return st;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando " + devicesJsonPath, e);
        }

        // 2.b) fallback: intentar cargar devices.json desde classpath (resources)
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("devices.json")) {
            if (is != null) {
                BootstrapLoader.loadFromJson(is, st);
                return st;
            } else {
                throw new IllegalStateException("devices.json no encontrado en classpath ni en " + devicesJsonPath);
            }
        } catch (IllegalStateException ise) {
            throw ise;
        } catch (Exception e) {
            throw new RuntimeException("Error cargando devices.json desde classpath", e);
        }

    }
}
