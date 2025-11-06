package com.example.demo.reports;

import com.example.demo.core.CentralState;
import devices.DeviceStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//  encapsula los datos para el filtro de Estado de Dispositivos.

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

    public static DeviceStatusReport create(List<CentralState.DeviceSnapshot> devices) {
        String reportTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        long totalDevices = devices.size();

        Map<String, Long> countByType = devices.stream()
                .collect(Collectors.groupingBy(
                        d -> d.type,
                        Collectors.counting()
                ));

        long totalNormal = devices.stream()
                .filter(d -> d.status == DeviceStatus.NORMAL || d.status == DeviceStatus.UNKNOWN) // Consideramos UNKNOWN como "no fallado"
                .count();


        long totalFailure = devices.stream()
                .filter(d -> d.status == DeviceStatus.FAILURE || d.status == DeviceStatus.INTERMITTENT)
                .count();

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