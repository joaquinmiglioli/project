package com.example.demo.controllers;

// Imports de Spring y Java
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

// Imports de TU proyecto
import com.example.demo.core.AppContext;
import db.FineDAO;
import fines.Fine; // <-- El import clave

@RestController
@RequestMapping("/api/fines")
public class FinesController {

    private final FineDAO fineDAO;

    // El constructor usa el AppContext para obtener el DAO
    public FinesController(AppContext ctx) {
        //
        this.fineDAO = ctx.fineDAO;
    }

    /**
     * Devuelve la lista de multas.
     * El tipo de retorno es java.util.List<fines.Fine>
     */
    @GetMapping
    public List<Fine> listAll(@RequestParam(required = false) Integer limit) {
        try {
            // Esta línea llama a fineDAO.findAll, que también devuelve List<fines.Fine>
            //
            return fineDAO.findAll(limit != null ? limit : 1000);

        } catch (Exception e) {
            // Lanza un error de runtime si la base de datos falla
            throw new RuntimeException("Error ejecutando FineDAO.findAll(...): " + e.getMessage(), e);
        }
    }

    /**
     * Borra todas las multas.
     */
    @DeleteMapping
    public Map<String, Object> deleteAll() {
        try {
            //
            fineDAO.deleteAll();
            return Map.of("ok", true, "result", "All fines deleted and sequence reset.");

        } catch (Exception e) {
            // Lanza un error de runtime si la base de datos falla
            throw new RuntimeException("Error ejecutando borrado: " + e.getMessage(), e);
        }
    }
}
