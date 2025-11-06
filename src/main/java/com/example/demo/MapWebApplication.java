package com.example.demo;


import com.example.demo.core.AppContext;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import javafx.application.Platform;


//punto central de la aplicacion de SpringBoot


@SpringBootApplication
public class MapWebApplication {


    private static AppContext ctx;


    public static void main(String[] args) {
        new javafx.embed.swing.JFXPanel();


        SpringApplication.run(MapWebApplication.class, args);
        System.out.println("➡ Server on http://localhost:8080");
    }


    //crea un bean de appContext que coordina la aplicacion
    @Bean
    public AppContext appContext() {
        ctx = new AppContext();
        return ctx;
    }


    //guarda estado y detiene simulaciones
    @PreDestroy
    public void onExit() {
        if (ctx != null) {
            System.out.println("Saving state and stopping services...");
            //
            ctx.saveOnExit();
            System.out.println("✅ Status saved. Closing app.");
        }


        Platform.exit();
    }
}

