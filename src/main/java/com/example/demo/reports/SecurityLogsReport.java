package com.example.demo.reports;

import devices.SecurityWarning;
import devices.ServiceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * DTO para el Reporte de Avisos de Seguridad (Reporte 4).
 *
 */
public record SecurityLogsReport(
        List<SecurityWarning> warnings,
        Map<ServiceType, Long> counts,
        Map<ServiceType, Double> percentages,
        long totalCount
) {
    /**
     * Constructor estático que calcula los totales y porcentajes.
     *
     */
    public static SecurityLogsReport create(List<SecurityWarning> filteredWarnings) {
        long totalCount = filteredWarnings.size();

        // 1. Contar los avisos por tipo
        Map<ServiceType, Long> counts = filteredWarnings.stream()
                .collect(Collectors.groupingBy(
                        SecurityWarning::getServiceType,
                        Collectors.counting()
                ));

        // 2. Asegurar que todos los servicios estén presentes (incluso con 0)
        //
        for (ServiceType type : ServiceType.values()) {
            counts.putIfAbsent(type, 0L);
        }

        // 3. Calcular porcentajes
        Map<ServiceType, Double> percentages = counts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (totalCount == 0) ? 0.0 : (100.0 * entry.getValue()) / totalCount
                ));

        return new SecurityLogsReport(filteredWarnings, counts, percentages, totalCount);
    }
}