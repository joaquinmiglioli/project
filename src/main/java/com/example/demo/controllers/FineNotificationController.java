package com.example.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/*Implementa un sistema simple de "polling" para notificar al frontend sobre nuevas multas.
updateLastFine(): Es llamado por el Backend (desde ViolationCoordinator) cuando se genera una multa. Guarda los datos de la multa en variables estáticas.
getLastFine(): Es llamado por el Frontend (cada 5 seg.) para preguntar "¿Hay una multa nueva?". Si la hay, devuelve los datos y luego clearLastFine() los borra.
*/

@RestController
public class FineNotificationController {

    //Campos estáticos para almacenar la última multa generada
    private static String lastFineNumber = null;
    private static String lastPlate = null;
    private static String lastType = null;
    private static String lastPdfPath = null;
    private static String lastHtmlMessage = null;

   // Endpoint: devuelve los datos de la última multa generada
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

    // Endpoint: limpia la última multa (el frontend lo llama después de mostrar el toast)
    @GetMapping("/api/clearFine")
    public void clearLastFine() {
        lastFineNumber = null;
        lastPlate = null;
        lastType = null;
        lastPdfPath = null;
        lastHtmlMessage = null;
    }

   // Metodo llamado desde FineEmissionService cuando se genera una multa
    public static void updateLastFine(String fineNumber, String plate, String type, String pdfPath) {
        lastFineNumber = fineNumber;
        lastPlate = plate;
        lastType = type;
        lastPdfPath = pdfPath;

        // Mensaje HTML que el frontend va a mostrar en el toast
        lastHtmlMessage = """
            🚨 <b>New fine generated</b><br>
            📄 Number: <b>%s</b><br>
            🚗 Plate: <b>%s</b><br>
            ⚠️ Violation: <b>%s</b>
        """.formatted(fineNumber, plate, type);
    }
}
