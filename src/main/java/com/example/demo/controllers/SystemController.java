package com.example.demo.controllers;

import com.example.demo.core.AppContext;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/*Expone funciones administrativas.
resetState():  Responde a POST /api/system/reset. Borra el archivo state.bin, le indica a AppContext que no guarde al salir, y reinicia la aplicación. Esto permite volver al estado inicial de devices.json.
*/

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final AppContext appContext;
    private final ApplicationContext springContext;

    // Spring se encarga de inyectar los beans automáticamente en el constructor
    public SystemController(AppContext appContext, ApplicationContext springContext) {
        this.appContext = appContext;
        this.springContext = springContext;
    }

    @PostMapping("/reset")
    public Map<String, Object> resetState() {
        //
        Path stateFile = Path.of("state.bin");
        String message;

        try {
            // 1. BORRAR EL ARCHIVO (si existe)
            if (Files.exists(stateFile)) {
                Files.delete(stateFile);
                message = "Reset successful. Application will now shut down.";
            } else {
                message = "No saved state found. Application will now shut down.";
            }

            // 2. DESHABILITAR EL GUARDADO AL SALIR
            // (Llama al metodo que creamos en AppContext)

            appContext.disableSaveOnExit();

            // Hilo para apagar la app 1 seg después de enviar la respuesta OK
            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException e) { /* ignorar */ }

                // 3. CERRAR SPRING GRACEFULLY (sin System.exit)
                // Esto ejecutará el @PreDestroy, pero como pusimos el flag,
                // el metodo saveOnExit() se saltará el guardado.
                //
                SpringApplication.exit(springContext, () -> 0);
            }).start();

            return Map.of("ok", true, "message", message);

        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "ok", false,
                    "message", "Error deleting state.bin: " + e.getMessage()
            );
        }
    }
}