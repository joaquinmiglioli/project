package com.example.demo.reports;

import com.example.demo.core.CentralState;
import devices.DeviceStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO que encapsula los datos para el Reporte de Estado de Dispositivos.
 * Sigue los requisitos del PDF.
 */
public record DeviceStatusReport(
        String reportTimestamp,
        List<CentralState.DeviceSnapshot> devices,
        Map<String, Long> countByType,
        long totalDevices,
        long totalNormal,
        long totalFailure,
        double pctNormal,
        double pctFailure
) {
    /**
     * Constructor estático para facilitar la creación desde el servicio.
     */
    public static DeviceStatusReport create(List<CentralState.DeviceSnapshot> devices) {
        String reportTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long totalDevices = devices.size();

        // Conteo por tipo
        Map<String, Long> countByType = devices.stream()
                .collect(Collectors.groupingBy(
                        d -> d.type,
                        Collectors.counting()
                ));

        // Estadísticas de funcionamiento
        long totalNormal = devices.stream()
                .filter(d -> d.status == DeviceStatus.NORMAL || d.status == DeviceStatus.UNKNOWN) // Consideramos UNKNOWN como "no fallado"
                .count();

        // Fallado incluye FAILURE e INTERMITTENT
        long totalFailure = devices.stream()
                .filter(d -> d.status == DeviceStatus.FAILURE || d.status == DeviceStatus.INTERMITTENT)
                .count();

        // Aseguramos que la suma sea el total (por si aparecen nuevos estados)
        // O recalculamos totalNormal para ser más explícitos
        totalFailure = totalDevices - totalNormal;


        double pctNormal = (totalDevices == 0) ? 0 : (100.0 * totalNormal) / totalDevices;
        double pctFailure = (totalDevices == 0) ? 0 : (100.0 * totalFailure) / totalDevices;

        return new DeviceStatusReport(
                reportTimestamp,
                devices,
                countByType,
                totalDevices,
                totalNormal,
                totalFailure,
                pctNormal,
                pctFailure
        );
    }
}