package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FineNotificationController {

    // ✅ Campos estáticos para almacenar la última multa generada
    private static String lastFineNumber = null;
    private static String lastPlate = null;
    private static String lastType = null;
    private static String lastPdfPath = null;
    private static String lastHtmlMessage = null;

    /**
     * ✅ Endpoint: devuelve los datos de la última multa generada
     */
    @GetMapping("/api/lastFine")
    public Map<String, String> getLastFine() {
        Map<String, String> m = new HashMap<>();
        m.put("fineNumber", lastFineNumber);
        m.put("plate", lastPlate);
        m.put("type", lastType);
        m.put("pdfPath", lastPdfPath);
        m.put("htmlMessage", lastHtmlMessage);
        return m;
    }

    /**
     * ✅ Endpoint: limpia la última multa (el frontend lo llama después de mostrar el toast)
     */
    @GetMapping("/api/clearFine")
    public void clearLastFine() {
        lastFineNumber = null;
        lastPlate = null;
        lastType = null;
        lastPdfPath = null;
        lastHtmlMessage = null;
    }

    /**
     * ✅ Método llamado desde FineEmissionService cuando se genera una multa
     */
    public static void updateLastFine(String fineNumber, String plate, String type, String pdfPath) {
        lastFineNumber = fineNumber;
        lastPlate = plate;
        lastType = type;
        lastPdfPath = pdfPath;

        // Mensaje HTML que el frontend mostrará en el toast
        lastHtmlMessage = """
            🚨 <b>Nueva multa generada</b><br>
            📄 Número: <b>%s</b><br>
            🚗 Patente: <b>%s</b><br>
            ⚠️ Infracción: <b>%s</b>
        """.formatted(fineNumber, plate, type);
    }
}
