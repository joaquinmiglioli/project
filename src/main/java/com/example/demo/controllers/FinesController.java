package com.example.demo.controllers;


import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

import com.example.demo.core.AppContext;
import db.FineDAO;
import fines.Fine;

/*
Maneja la API para la pestaña "Fines".
listAll():  Responde a GET /api/fines y devuelve la lista de todas las multas desde la base de datos.
deleteAll(): Responde a DELETE /api/fines y borra todas las multas de la base de datos.
*/


@RestController
@RequestMapping("/api/fines")
public class FinesController {

    private final FineDAO fineDAO;

    // El constructor usa el AppContext para obtener el DAO
    public FinesController(AppContext ctx) {
        this.fineDAO = ctx.fineDAO;
    }

    //Devuelve la lista de multas.
    @GetMapping
    public List<Fine> listAll(@RequestParam(required = false) Integer limit) {
        try {
            // llama a fineDAO.findAll
            return fineDAO.findAll(limit != null ? limit : 1000);

        } catch (Exception e) {
            // Si falla la base de datos, lanza un error de runtime
            throw new RuntimeException("Error ejecutando FineDAO.findAll(...): " + e.getMessage(), e);
        }
    }

   // Borra todas las multas.
    @DeleteMapping
    public Map<String, Object> deleteAll() {
        try {
            //
            fineDAO.deleteAll();
            return Map.of("ok", true, "result", "All fines deleted and sequence reset.");

        } catch (Exception e) {
            // Si falla la base de datos, lanza un error de runtime
            throw new RuntimeException("Error ejecutando borrado: " + e.getMessage(), e);
        }
    }
}
