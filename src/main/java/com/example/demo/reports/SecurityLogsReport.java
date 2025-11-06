package com.example.demo.reports;

import devices.SecurityWarning;
import devices.ServiceType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

//encapsula los datos para el filtro de Avisos de Seguridad

public record SecurityLogsReport(
        List<SecurityWarning> warnings,
        Map<ServiceType, Long> counts,
        Map<ServiceType, Double> percentages,
        long totalCount
) {
    public static SecurityLogsReport create(List<SecurityWarning> filteredWarnings) {
        long totalCount = filteredWarnings.size();

        Map<ServiceType, Long> counts = filteredWarnings.stream()
                .collect(Collectors.groupingBy(
                        SecurityWarning::getServiceType,
                        Collectors.counting()
                ));


        for (ServiceType type : ServiceType.values()) {
            counts.putIfAbsent(type, 0L);
        }

        // calcula porcentajes
        Map<ServiceType, Double> percentages = counts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> (totalCount == 0) ? 0.0 : (100.0 * entry.getValue()) / totalCount
                ));

        return new SecurityLogsReport(filteredWarnings, counts, percentages, totalCount);
    }
}