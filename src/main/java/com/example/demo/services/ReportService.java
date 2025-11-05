package com.example.demo.services;

import com.example.demo.core.AppContext;
import com.example.demo.core.CentralState;
import com.example.demo.controllers.SecurityOpsController;
import com.example.demo.reports.DeviceEventsReport;
import com.example.demo.reports.DeviceStatusReport;
import com.example.demo.reports.FinesReport;
import com.example.demo.reports.SecurityLogsReport;
import db.FineDAO;
import devices.DeviceStatus;
import devices.SecurityWarning;
import devices.ServiceType;
import fines.Fine;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportService {

    private final AppContext appContext;
    private final FineDAO fineDAO;
    private final SecurityOpsController securityOpsController;
    private final ViolationService violationService;

    public ReportService(AppContext appContext, SecurityOpsController securityOpsController, ViolationService violationService) {
        this.appContext = appContext;
        this.fineDAO = appContext.fineDAO; // Obtenemos el DAO desde AppContext
        this.securityOpsController = securityOpsController;
        this.violationService = violationService;
    }

    /**
     * Reporte 1: Estado de Dispositivos
     */
    public DeviceStatusReport generateDeviceStatusReport(String filterType, String filterStatus) {
        Collection<CentralState.DeviceSnapshot> allDevices = appContext.state.devicesById.values();

        List<CentralState.DeviceSnapshot> filteredDevices = allDevices.stream()
                .filter(d -> {
                    // Filtro por tipo
                    boolean typeMatch = (filterType == null || filterType.isBlank() || filterType.equalsIgnoreCase("ALL"))
                            || d.type.equalsIgnoreCase(filterType);

                    // Filtro por estado
                    boolean statusMatch = (filterStatus == null || filterStatus.isBlank() || filterStatus.equalsIgnoreCase("ALL"));
                    if (!statusMatch) {
                        if (filterStatus.equalsIgnoreCase("NORMAL")) {
                            // "Normal" incluye NORMAL y UNKNOWN (estados no-fallados)
                            statusMatch = (d.status == DeviceStatus.NORMAL || d.status == DeviceStatus.UNKNOWN);
                        } else if (filterStatus.equalsIgnoreCase("FAILURE")) {
                            // "Falla" incluye FAILURE e INTERMITTENT
                            statusMatch = (d.status == DeviceStatus.FAILURE || d.status == DeviceStatus.INTERMITTENT);
                        }
                    }
                    return typeMatch && statusMatch;
                })
                .collect(Collectors.toList());

        return DeviceStatusReport.create(filteredDevices);
    }

    /**
     * Reporte 2: Listado de Multas
     */
    public FinesReport generateFinesReport() {
        try {
            List<Fine> allFines = fineDAO.findAll(9999);
            return FinesReport.create(allFines);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al consultar la base de datos de multas", e);
        }
    }

    /**
     * Reporte 3: Consulta de multas por automóvil
     */
    public List<Fine> generateFinesByPlateReport(String plate) {
        if (plate == null || plate.isBlank()) {
            return List.of();
        }
        try {
            // Usamos el nuevo método del DAO
            return fineDAO.findByPlate(plate);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al consultar multas por patente", e);
        }
    }

    /**
     * Reporte 4: Listado de Avisos de Seguridad
     */
    public SecurityLogsReport generateSecurityLogsReport(String startStr, String endStr) {
        // 1. Obtener todos los logs en memoria
        List<SecurityWarning> allWarnings = securityOpsController.log();

        // 2. Parsear fechas de filtro
        Instant start = parseInstant(startStr, Instant.MIN);
        Instant end = parseInstant(endStr, Instant.MAX);

        // 3. Filtrar por rango de fechas
        List<SecurityWarning> filteredWarnings = allWarnings.stream()
                .filter(w -> !w.getTimestamp().isBefore(start) && !w.getTimestamp().isAfter(end))
                .collect(Collectors.toList());

        // 4. Crear el DTO (hace los cálculos de % y totales)
        return SecurityLogsReport.create(filteredWarnings);
    }

    /**
     * Reporte 5: Eventos por Dispositivo
     */
    public DeviceEventsReport generateDeviceEventsReport(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return new DeviceEventsReport(null, List.of(), List.of());
        }

        // 1. Filtrar multas (desde DB)
        List<Fine> fines;
        try {
            // Obtenemos todas y filtramos en memoria (más simple que modificar el DAO)
            fines = fineDAO.findAll(9999).stream()
                    .filter(f -> deviceId.equals(f.getDeviceId()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            fines = List.of(); // Devolver lista vacía en caso de error
        }

        // 2. Filtrar avisos de seguridad (desde memoria)
        List<SecurityWarning> warnings = securityOpsController.log().stream()
                .filter(w -> deviceId.equals(w.getDeviceId()))
                .collect(Collectors.toList());

        return new DeviceEventsReport(deviceId, fines, warnings);
    }

    // --- Helpers ---

    private Instant parseInstant(String dateTimeStr, Instant defaultVal) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return defaultVal;
        }
        try {
            // Asumimos formato ISO 8601 que viene del input datetime-local, ej: "2025-11-01T14:30"
            // Le agregamos Z para que sea UTC o un offset, ej: "-03:00"
            // Para simplicidad, si no tiene offset, asumimos zona local y convertimos.
            if (dateTimeStr.length() == 16) { // "2025-11-01T14:30"
                // No es ideal, pero funciona para este TP
                return java.time.LocalDateTime.parse(dateTimeStr).toInstant(java.time.ZoneOffset.UTC);
            }
            return Instant.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            System.err.println("Error parseando fecha: " + dateTimeStr + ". Usando default.");
            return defaultVal;
        }
    }

    /**
     * Helper para el frontend: Devuelve todos los IDs de dispositivos.
     */
    public List<String> getAllDeviceIds() {
        return appContext.state.devicesById.keySet().stream().sorted().toList();
    }
}