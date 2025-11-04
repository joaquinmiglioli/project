package com.example.demo;

import com.example.demo.core.AppContext;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Clase principal de Spring Boot.
 * Se encarga de levantar el servidor y crear el único AppContext de la app.
 */
@SpringBootApplication
public class MapWebApplication {

    private static AppContext ctx;

    public static void main(String[] args) {

        // Esto inicializa el Toolkit de JavaFX "en modo headless"
        // para que Spring Boot pueda usar clases como ObservableList.
        new javafx.embed.swing.JFXPanel();

        SpringApplication.run(MapWebApplication.class, args);
        System.out.println("➡ Server on http://localhost:8080");
    }

    /**
     * Creamos y exponemos el único AppContext como bean para que Spring lo use en controladores y servicios.
     */
    @Bean
    public AppContext appContext() {
        ctx = new AppContext(); // ← Solo se crea acá, una sola vez
        return ctx;
    }

    /**
     * Al cerrar, guardamos el estado y detenemos simulaciones.
     */
    @PreDestroy
    public void onExit() {
        if (ctx != null) {
            System.out.println("Saving state and stopping services...");
            ctx.saveOnExit();
            System.out.println("✅ Status saved. Closing app.");
        }
    }
}
