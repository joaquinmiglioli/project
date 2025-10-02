package com.example.demo;

import com.example.demo.core.AppContext;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**

 * Clase principal de Spring Boot.
 * Al recibir ApplicationReadyEvent se crea el AppContext que, en su constructor,
 * inicia FineEmissionService y la simulación de multas.
 */
@SpringBootApplication
public class MapWebApplication implements ApplicationListener<ApplicationReadyEvent> {

    private AppContext ctx;

    public static void main(String[] args) {
        SpringApplication.run(MapWebApplication.class, args);
        System.out.println("➡ Server on http://localhost:8080");
    }

    /**

     * Se ejecuta cuando Spring Boot está listo. Creamos el AppContext aquí
     * para arrancar los servicios de la aplicación (incluye la simulación).
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            System.out.println("Initializing AppContext y starting services...");
            this.ctx = new AppContext(); // constructor ya hace start() y startSimulation()
            System.out.println("AppContext initialized. Fine simulation started.");
        } catch (Exception e) {
            System.err.println("Error initializing AppContext: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**

     * Llamado al cerrar la JVM / contenedor Spring — garantiza detener la simulación
     * y persistir el estado por si correspondiera.
     */
    @PreDestroy
    public void onExit() {
        try {
            if (this.ctx != null) {
                System.out.println("Saving status and stopping simulation before quitting...");
                this.ctx.saveOnExit();
                System.out.println("Status saved. Quitting.");
            }
        } catch (Exception e) {
            System.err.println("Error while saveOnExit: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
