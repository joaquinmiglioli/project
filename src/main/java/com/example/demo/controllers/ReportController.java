package com.example.demo.controllers;

import com.example.demo.reports.DeviceEventsReport;
import com.example.demo.reports.DeviceStatusReport;
import com.example.demo.reports.FinesReport;
import com.example.demo.reports.SecurityLogsReport;
import com.example.demo.services.ReportService;
import fines.Fine;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/*Expone los 5 reportes como endpoints para el frontend.
Decisión de diseño: Este controlador es un intermediario, no hace ningún trabajo.
 Simplemente recibe la petición y llama al ReportService para que haga el procesamiento.
 */

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    // Spring inyecta el servicio que creamos
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Endpoint para el Reporte 1: Estado de Dispositivos
    @GetMapping("/device-status")
    public DeviceStatusReport getDeviceStatusReport(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {

        return reportService.generateDeviceStatusReport(type, status);
    }

    //Endpoint para el Reporte 2: Listado de Multas
    @GetMapping("/fines")
    public FinesReport getFinesReport() {
        return reportService.generateFinesReport();
    }

    //Endpoint para el Reporte 3: Multas por Patente
    @GetMapping("/fines-by-plate")
    public List<Fine> getFinesByPlateReport(@RequestParam String plate) {
        return reportService.generateFinesByPlateReport(plate);
    }

   // Endpoint para el Reporte 4: Avisos de Seguridad

    @GetMapping("/security-logs")
    public SecurityLogsReport getSecurityLogsReport(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        return reportService.generateSecurityLogsReport(start, end);
    }

    // Endpoint para el Reporte 5: Eventos por Dispositivo
    @GetMapping("/device-events")
    public DeviceEventsReport getDeviceEventsReport(@RequestParam String deviceId) {
        return reportService.generateDeviceEventsReport(deviceId);
    }

    // Endpoint auxiliar para poblar el dropdown del Reporte 5
    @GetMapping("/device-ids")
    public List<String> getAllDeviceIds() {
        return reportService.getAllDeviceIds();
    }
}