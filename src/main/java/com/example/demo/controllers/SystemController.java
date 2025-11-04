package com.example.demo.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Controlador para acciones administrativas del sistema, como el reseteo.
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    /**
     * Resetea el estado de la aplicación borrando el snapshot persistido.
     * La próxima vez que la aplicación inicie, cargará desde devices.json.
     *
     */
    @PostMapping("/reset")
    public Map<String, Object> resetState() {
        // La ruta está definida en AppContext -> StatePersistenceService
        //
        Path stateFile = Path.of("state.bin");

        try {
            if (Files.exists(stateFile)) {
                Files.delete(stateFile);
                return Map.of(
                        "ok", true,
                        "message", "Reset successful. Please restart the application server to load the default state."
                );
            } else {
                return Map.of(
                        "ok", true,
                        "message", "No saved state found. The app will load the default state on next startup."
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "ok", false,
                    "message", "Error deleting state.bin: " + e.getMessage()
            );
        }
    }
}
