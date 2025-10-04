package com.example.demo.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class DeviceRegistry {
    private final Map<String, String> addressById = new HashMap<>();

    public DeviceRegistry() {
        // --- Security Cameras (internos) ---
        addressById.put("CAM-1", "Av. Ejemplo 100");
        addressById.put("CAM-2", "Av. Ejemplo 200");
        addressById.put("CAM-3", "Av. Ejemplo 300");
        addressById.put("CAM-4", "Av. Ejemplo 400");
        addressById.put("CAM-5", "Av. Ejemplo 500");

        // --- Radars (internos) ---
        addressById.put("RAD-1", "Ruta 1 km 12");
        addressById.put("RAD-2", "Ruta 2 km 5");
        addressById.put("RAD-3", "Ruta 3 km 33");
        addressById.put("RAD-4", "Ruta 4 km 7");

        // --- Parking Cameras (internos) ---
        addressById.put("PK-1", "Calle Parque 101");
        addressById.put("PK-2", "Calle Parque 202");
        addressById.put("PK-3", "Calle Parque 303");
        addressById.put("PK-4", "Calle Parque 404");

        // --- Traffic Lights (internos INT-1..INT-22) ---
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

    // Patrones "humanos" → internos
    private static final Pattern P_SEM = Pattern.compile("^\\s*Semaphore\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern P_RAD = Pattern.compile("^\\s*Radar\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern P_PK  = Pattern.compile("^\\s*Parking\\s+Camera\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);
    private static final Pattern P_CAM = Pattern.compile("^\\s*Camera\\s+(\\d+)\\s*$", Pattern.CASE_INSENSITIVE);

    /** Normaliza IDs "humanos" del JSON a IDs internos (INT-, RAD-, PK-, CAM-). */
    private String normalizeToInternal(String deviceId) {
        if (deviceId == null) return null;
        String s = deviceId.trim();

        Matcher m;
        m = P_SEM.matcher(s); if (m.find()) return "INT-" + m.group(1);
        m = P_RAD.matcher(s); if (m.find()) return "RAD-" + m.group(1);
        m = P_PK.matcher(s);  if (m.find()) return "PK-" + m.group(1);
        m = P_CAM.matcher(s); if (m.find()) return "CAM-" + m.group(1);

        // Si no matchea, devolvemos tal cual (ya interno o desconocido)
        return s;
    }

    /** Permite configurar/cambiar direcciones en runtime usando IDs internos. */
    public void setAddress(String deviceId, String address) {
        addressById.put(deviceId, address);
    }

    /**
     * Devuelve la dirección para un deviceId. Soporta:
     * - IDs internos (búsqueda directa)
     * - IDs "humanos" (se normaliza a interno y se busca)
     */
    public String addressFor(String deviceId) {
        if (deviceId == null) return "SIN DIRECCIÓN (“null”)";

        // 1) Intento directo (si ya vienen internos)
        String addr = addressById.get(deviceId);
        if (addr != null) return addr;

        // 2) Intento normalizando "humanos" → internos
        String norm = normalizeToInternal(deviceId);
        addr = addressById.get(norm);
        return addr != null ? addr : "SIN DIRECCIÓN (“" + deviceId + "”)";
    }

    /** Devuelve todos los IDs internos conocidos. */
    public Set<String> getAllDeviceIds() {
        return addressById.keySet();
    }
}
