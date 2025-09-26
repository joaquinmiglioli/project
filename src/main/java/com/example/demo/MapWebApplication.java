package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MapWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(MapWebApplication.class, args);
        System.out.println("➡ Servidor en http://localhost:8080");
    }
}
