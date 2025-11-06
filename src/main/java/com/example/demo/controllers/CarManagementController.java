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
import java.util.regex.Pattern;

/*
Maneja las peticiones de la pestaña "Settings" para el CRUD (Crear, Leer, Actualizar, Borrar) de autos.
Expone endpoints como /api/cars/brands (para listar marcas) y /api/cars/add (para agregar un auto nuevo).
Cumple el requisito de que los autos se gestionen desde el sistema.
*/

@RestController
@RequestMapping("/api/cars")
public class CarManagementController {

    private final CarDAO carDAO;
    private final CarBrandDAO carBrandDAO;
    private final CarModelDAO carModelDAO;

    // REGEX PARA VALIDAR PATENTES
    private static final Pattern PLATE_REGEX = Pattern.compile("^([A-Z]{3}\\d{3}|[A-Z]{2}\\d{3}[A-Z]{2})$");

    // Inyectamos el AppContext para acceder a los DAOs
    public CarManagementController(AppContext ctx) {

        this.carDAO = new CarDAO(); //
        this.carBrandDAO = new CarBrandDAO();
        this.carModelDAO = new CarModelDAO();
    }


    //Devuelve todas las marcas de autos.
    @GetMapping("/brands")
    public List<CarBrand> getBrands() {
        try {
            return carBrandDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

   //Devuelve los modelos para una marca específica.
    @GetMapping("/models")
    public List<CarModel> getModels(@RequestParam Long brandId) {
        try {
            return carModelDAO.findByBrand(brandId);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

   //NUEVO ENDPOINT: Añade una nueva MARCA
    @PostMapping("/brands/add")
    public Map<String, Object> addBrand(@RequestParam String name) {
        try {
            String trimmedName = name.trim();
            if (trimmedName.isEmpty()) {
                return Map.of("ok", false, "message", "Brand name cannot be empty.");
            }

            // Verificar duplicados
            if (carBrandDAO.findByName(trimmedName).isPresent()) {
                throw new DuplicateResourceException("CarBrand", "name", trimmedName);
            }

            carBrandDAO.insert(trimmedName);
            return Map.of("ok", true, "message", "Brand '" + trimmedName + "' added successfully.");

        } catch (DuplicateResourceException e) {
            return Map.of("ok", false, "message", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("ok", false, "message", "Error adding brand: " + e.getMessage());
        }
    }

    //NUEVO ENDPOINT: Añade un nuevo MODELO
    @PostMapping("/models/add")
    public Map<String, Object> addModel(@RequestParam Long brandId, @RequestParam String name) {
        try {
            String trimmedName = name.trim();
            if (trimmedName.isEmpty()) {
                return Map.of("ok", false, "message", "Model name cannot be empty.");
            }

            // Verificar duplicados
            if (carModelDAO.findByBrandAndName(brandId, trimmedName).isPresent()) {
                throw new DuplicateResourceException("CarModel", "name", trimmedName);
            }

            carModelDAO.insert(brandId, trimmedName);
            return Map.of("ok", true, "message", "Model '" + trimmedName + "' added successfully.");

        } catch (DuplicateResourceException e) {
            return Map.of("ok", false, "message", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("ok", false, "message", "Error adding model: " + e.getMessage());
        }
    }


   // Añade un nuevo auto a la base de datos.
    @PostMapping("/add")
    public Map<String, Object> addCar(
            @RequestParam Long brandId,
            @RequestParam Long modelId,
            @RequestParam String plate,
            @RequestParam String owner,
            @RequestParam String address,
            @RequestParam String colour
    ) {
        //VALIDACIÓN DE PATENTE
        String upperPlate = plate.trim().toUpperCase();
        if (!PLATE_REGEX.matcher(upperPlate).matches()) {
            return Map.of(
                    "ok", false,
                    "message", "Error: Invalid plate format. Must be AAA123 or AA123BB."
            );
        }

        try {
            long newCarId = carDAO.insert(brandId, modelId, upperPlate, owner, address, colour);

            return Map.of(
                    "ok", true,
                    "message", "Car added successfully with ID: " + newCarId,
                    "newCarId", newCarId
            );

        } catch (DuplicateResourceException e) {
            //capturamos la excepcion que creamos
            // Ya no comparamos strings.
            return Map.of("ok", false, "message", e.getMessage());

        } catch (DatabaseOperationException | SQLException e) {
            // Capturamos otros errores de DB
            e.printStackTrace();
            return Map.of("ok", false, "message", "Error adding car: " + e.getMessage());
        }
    }
}