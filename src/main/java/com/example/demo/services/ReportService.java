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


//construye los reports


@Service
public class ReportService {


    private final AppContext appContext;
    private final FineDAO fineDAO;
    private final SecurityOpsController securityOpsController;
    private final ViolationService violationService;


    public ReportService(AppContext appContext, SecurityOpsController securityOpsController, ViolationService violationService) {
        this.appContext = appContext;
        this.fineDAO = appContext.fineDAO;
        this.securityOpsController = securityOpsController;
        this.violationService = violationService;
    }


    //estado de dispositivos
    public DeviceStatusReport generateDeviceStatusReport(String filterType, String filterStatus) {
        Collection<CentralState.DeviceSnapshot> allDevices = appContext.state.devicesById.values();


        List<CentralState.DeviceSnapshot> filteredDevices = allDevices.stream()
                .filter(d -> {
                    // filtra por tipo
                    boolean typeMatch = (filterType == null || filterType.isBlank() || filterType.equalsIgnoreCase("ALL"))
                            || d.type.equalsIgnoreCase(filterType);


                    // filtra por estado
                    boolean statusMatch = (filterStatus == null || filterStatus.isBlank() || filterStatus.equalsIgnoreCase("ALL"));
                    if (!statusMatch) {
                        if (filterStatus.equalsIgnoreCase("NORMAL")) {
                            statusMatch = (d.status == DeviceStatus.NORMAL || d.status == DeviceStatus.UNKNOWN);
                        } else if (filterStatus.equalsIgnoreCase("FAILURE")) {
                            statusMatch = (d.status == DeviceStatus.FAILURE || d.status == DeviceStatus.INTERMITTENT);  //failure e intermittent
                        }
                    }
                    return typeMatch && statusMatch;
                })
                .collect(Collectors.toList());


        return DeviceStatusReport.create(filteredDevices);
    }


    //lsitado de multas
    public FinesReport generateFinesReport() {
        try {
            List<Fine> allFines = fineDAO.findAll(9999);
            return FinesReport.create(allFines);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error consulting Database", e);
        }
    }


    public List<Fine> generateFinesByPlateReport(String plate) {
        if (plate == null || plate.isBlank()) {
            return List.of();
        }
        try {
            return fineDAO.findByPlate(plate);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error consulting fines", e);
        }
    }


    //avisos de seguridad
    public SecurityLogsReport generateSecurityLogsReport(String startStr, String endStr) {


        List<SecurityWarning> allWarnings = securityOpsController.log();


        Instant start = parseInstant(startStr, Instant.MIN);
        Instant end = parseInstant(endStr, Instant.MAX);


        List<SecurityWarning> filteredWarnings = allWarnings.stream()
                .filter(w -> !w.getTimestamp().isBefore(start) && !w.getTimestamp().isAfter(end))
                .collect(Collectors.toList());


        return SecurityLogsReport.create(filteredWarnings);
    }


    //eventos por dispositivo
    public DeviceEventsReport generateDeviceEventsReport(String deviceId) {
        if (deviceId == null || deviceId.isBlank()) {
            return new DeviceEventsReport(null, List.of(), List.of());
        }


        List<Fine> fines;
        try {
            fines = fineDAO.findAll(9999).stream()
                    .filter(f -> deviceId.equals(f.getDeviceId()))
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            e.printStackTrace();
            fines = List.of();
        }


        List<SecurityWarning> warnings = securityOpsController.log().stream()
                .filter(w -> deviceId.equals(w.getDeviceId()))
                .collect(Collectors.toList());


        return new DeviceEventsReport(deviceId, fines, warnings);
    }


    private Instant parseInstant(String dateTimeStr, Instant defaultVal) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return defaultVal;
        }
        try {
            if (dateTimeStr.length() == 16) {
                return java.time.LocalDateTime.parse(dateTimeStr).toInstant(java.time.ZoneOffset.UTC);
            }
            return Instant.parse(dateTimeStr);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + dateTimeStr + ". Using default.");
            return defaultVal;
        }
    }


    public List<String> getAllDeviceIds() {
        return appContext.state.devicesById.keySet().stream().sorted().toList();
    }
}
