package com.example.demo.controllers;

import com.example.demo.core.AppContext;
import cars.CarBrand;
import cars.CarModel;
import db.CarBrandDAO;
import db.CarDAO;
import db.CarModelDAO;
import org.springframework.web.bind.annotation.*;

import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.DatabaseOperationException;
import java.sql.SQLException;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión de Marcas, Modelos y Autos (CRUD).
 */
@RestController
@RequestMapping("/api/cars")
public class CarManagementController {

    private final CarDAO carDAO;
    private final CarBrandDAO carBrandDAO;
    private final CarModelDAO carModelDAO;

    // Inyectamos el AppContext para acceder a los DAOs
    public CarManagementController(AppContext ctx) {
        // Asumimos que expondremos los DAOs en AppContext (siguiente paso)
        this.carDAO = new CarDAO(); // O ctx.getCarDAO() si lo tienes
        this.carBrandDAO = new CarBrandDAO(); // O ctx.getCarBrandDAO()
        this.carModelDAO = new CarModelDAO(); // O ctx.getCarModelDAO()
    }

    /**
     * Devuelve todas las marcas de autos.
     */
    @GetMapping("/brands")
    public List<CarBrand> getBrands() {
        try {
            return carBrandDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Devuelve los modelos para una marca específica.
     */
    @GetMapping("/models")
    public List<CarModel> getModels(@RequestParam Long brandId) {
        try {
            return carModelDAO.findByBrand(brandId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Añade un nuevo auto a la base de datos.
     */
    @PostMapping("/add")
    public Map<String, Object> addCar(
            @RequestParam Long brandId,
            @RequestParam Long modelId,
            @RequestParam String plate,
            @RequestParam String owner,
            @RequestParam String address,
            @RequestParam String colour
    ) {
        try {
            long newCarId = carDAO.insert(brandId, modelId, plate, owner, address, colour);

            return Map.of(
                    "ok", true,
                    "message", "Car added successfully with ID: " + newCarId,
                    "newCarId", newCarId
            );

        } catch (DuplicateResourceException e) {
            // ¡Ahora capturamos la excepción específica!
            // Ya no comparamos strings.
            return Map.of("ok", false, "message", e.getMessage());

        } catch (DatabaseOperationException | SQLException e) {
            // Capturamos otros errores de DB
            e.printStackTrace();
            return Map.of("ok", false, "message", "Error adding car: " + e.getMessage());
        }
    }
}