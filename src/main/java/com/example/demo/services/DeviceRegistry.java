// com/example/demo/services/DeviceRegistry.java
package com.example.demo.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** Registro sencillo de direcciones por dispositivo (RAD-1, PK-3, INT-5, CAM-2, etc.). */
public class DeviceRegistry {
    private final Map<String, String> addressById = new HashMap<>();

    public DeviceRegistry() {
        // --- Security Cameras ---
        addressById.put("CAM-1", "Av. Ejemplo 100");
        addressById.put("CAM-2", "Av. Ejemplo 200");
        addressById.put("CAM-3", "Av. Ejemplo 300");
        addressById.put("CAM-4", "Av. Ejemplo 400");
        addressById.put("CAM-5", "Av. Ejemplo 500");

        // --- Radars ---
        addressById.put("RAD-1", "Ruta 1 km 12");
        addressById.put("RAD-2", "Ruta 2 km 5");
        addressById.put("RAD-3", "Ruta 3 km 33");
        addressById.put("RAD-4", "Ruta 4 km 7");

        // --- Parking Cameras ---
        addressById.put("PK-1", "Calle Parque 101");
        addressById.put("PK-2", "Calle Parque 202");
        addressById.put("PK-3", "Calle Parque 303");
        addressById.put("PK-4", "Calle Parque 404");

        // --- Traffic Lights (INT-1..INT-22) ---
        addressById.put("INT-1",  "Esq. A y B 1");
        addressById.put("INT-2",  "Esq. A y B 2");
        addressById.put("INT-3",  "Esq. A y B 3");
        addressById.put("INT-4",  "Esq. A y B 4");
        addressById.put("INT-5",  "Esq. A y B 5");
        addressById.put("INT-6",  "Esq. A y B 6");
        addressById.put("INT-7",  "Esq. A y B 7");
        addressById.put("INT-8",  "Esq. A y B 8");
        addressById.put("INT-9",  "Esq. A y B 9");
        addressById.put("INT-10", "Esq. A y B 10");
        addressById.put("INT-11", "Esq. A y B 11");
        addressById.put("INT-12", "Esq. A y B 12");
        addressById.put("INT-13", "Esq. A y B 13");
        addressById.put("INT-14", "Esq. A y B 14");
        addressById.put("INT-15", "Esq. A y B 15");
        addressById.put("INT-16", "Esq. A y B 16");
        addressById.put("INT-17", "Esq. A y B 17");
        addressById.put("INT-18", "Esq. A y B 18");
        addressById.put("INT-19", "Esq. A y B 19");
        addressById.put("INT-20", "Esq. A y B 20");
        addressById.put("INT-21", "Esq. A y B 21");
        addressById.put("INT-22", "Esq. A y B 22");
    }

    /** Cambiar o cargar direcciones en runtime (si querés). */
    public void setAddress(String deviceId, String address) {
        addressById.put(deviceId, address);
    }

    /** Siempre devuelve algo: si no está configurado, devuelve un texto explícito. */
    public String addressFor(String deviceId) {
        return addressById.getOrDefault(deviceId, "SIN DIRECCIÓN (“" + deviceId + "”)");
    }

    /** Devuelve todos los IDs de dispositivos registrados. */
    public Set<String> getAllDeviceIds() {
        return addressById.keySet();
    }
}