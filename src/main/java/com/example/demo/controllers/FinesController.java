// com/example/demo/controllers/FinesController.java
package com.example.demo.controllers;

import org.springframework.web.bind.annotation.*;

import java.lang.reflect.*;
import java.sql.Connection;
import java.util.*;

@RestController
@RequestMapping("/api/fines")
public class FinesController {

    private final Object fineDAO; // instancia real de tu db.FineDAO

    public FinesController() {
        this.fineDAO = initDAO();
    }

    private static Object initDAO() {
        List<String> candidates = List.of("db.FineDAO", "fines.FineDAO");
        Class<?> daoClass = null;
        for (String name : candidates) {
            try {
                daoClass = Class.forName(name);
                break;
            } catch (ClassNotFoundException ignore) {}
        }
        if (daoClass == null) {
            throw new IllegalStateException("No se encontró clase FineDAO (probé db.FineDAO y fines.FineDAO).");
        }

        // 1) getInstance()
        try {
            Method gi = daoClass.getMethod("getInstance");
            return gi.invoke(null);
        } catch (NoSuchMethodException ignored) {
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Error invocando FineDAO.getInstance(): " + e.getMessage(), e);
        }

        // 2) ctor() sin args
        try {
            Constructor<?> c = daoClass.getDeclaredConstructor();
            c.setAccessible(true);
            return c.newInstance();
        } catch (NoSuchMethodException ignored) {
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("No pude crear FineDAO(): " + e.getMessage(), e);
        }

        // Si llegamos acá, no hay forma simple de construirlo
        throw new IllegalStateException("No se pudo instanciar FineDAO (sin getInstance() ni ctor vacío).");
    }

    @SuppressWarnings("unchecked")
    @GetMapping
    public List<?> listAll(@RequestParam(required = false) Integer limit) {
        try {
            // 1) findAll()
            try {
                Method m = fineDAO.getClass().getMethod("findAll");
                return (List<?>) m.invoke(fineDAO);
            } catch (NoSuchMethodException ignore) {}

            // 2) findAll(int limit)
            if (limit == null) limit = Integer.MAX_VALUE;
            try {
                Method m = fineDAO.getClass().getMethod("findAll", int.class);
                return (List<?>) m.invoke(fineDAO, limit);
            } catch (NoSuchMethodException ignore) {}

            // 3) findAll(Connection)
            try {
                Method m = fineDAO.getClass().getMethod("findAll", Connection.class);
                return (List<?>) m.invoke(fineDAO, new Object[]{null});
            } catch (NoSuchMethodException ignore) {}

            // 4) findAll(int, int)  (offset, limit)
            try {
                Method m = fineDAO.getClass().getMethod("findAll", int.class, int.class);
                return (List<?>) m.invoke(fineDAO, 0, limit);
            } catch (NoSuchMethodException ignore) {}

            // 5) findAll(String)
            try {
                Method m = fineDAO.getClass().getMethod("findAll", String.class);
                return (List<?>) m.invoke(fineDAO, (Object) null);
            } catch (NoSuchMethodException ignore) {}

            throw new IllegalStateException("No se encontró una firma usable de findAll(...) en FineDAO");
        } catch (InvocationTargetException ite) {
            throw new RuntimeException("Error ejecutando FineDAO.findAll(...): " + ite.getTargetException(), ite);
        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando FineDAO.findAll(...): " + e.getMessage(), e);
        }
    }

    @DeleteMapping
    public Map<String, Object> deleteAll() {
        try {
            // 1) deleteAll()
            try {
                Method m = fineDAO.getClass().getMethod("deleteAll");
                Object res = m.invoke(fineDAO);
                return Map.of("ok", true, "result", String.valueOf(res));
            } catch (NoSuchMethodException ignore) {}

            // 2) deleteAll(Connection)
            try {
                Method m = fineDAO.getClass().getMethod("deleteAll", Connection.class);
                Object res = m.invoke(fineDAO, new Object[]{null});
                return Map.of("ok", true, "result", String.valueOf(res));
            } catch (NoSuchMethodException ignore) {}

            // 3) clearAll(), truncate(), purge() (nombres usuales)
            for (String name : List.of("clearAll", "truncate", "purge")) {
                try {
                    Method m = fineDAO.getClass().getMethod(name);
                    Object res = m.invoke(fineDAO);
                    return Map.of("ok", true, "result", String.valueOf(res));
                } catch (NoSuchMethodException ignore) {}
            }

            throw new IllegalStateException("No se encontró método para borrar todas las multas en FineDAO");
        } catch (InvocationTargetException ite) {
            throw new RuntimeException("Error ejecutando borrado: " + ite.getTargetException(), ite);
        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando borrado: " + e.getMessage(), e);
        }
    }
}
