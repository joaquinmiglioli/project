package com.example.demo.reports;

import fines.Fine;
import fines.FineType;

import java.util.List;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DTO que encapsula los datos para el Reporte de Multas.
 * Sigue los requisitos del PDF.
 */
public record FinesReport(
        List<FineGroup> groups,
        GlobalStats globalStats
) {

    /**
     * Sub-record para cada grupo de multas (SPEEDING, PARKING, etc.)
     *
     */
    public record FineGroup(
            FineType type,
            List<Fine> fines,
            long count,
            double totalAmount
    ) {}

    /**
     * Sub-record para las estadísticas globales
     *
     */
    public record GlobalStats(
            long totalCount,
            double totalAmount,
            double averageAmount,
            double minAmount,
            double maxAmount
    ) {}

    /**
     * Constructor estático que realiza todos los cálculos.
     */
    public static FinesReport create(List<Fine> allFines) {

        // 1. Agrupar multas por tipo
        Map<FineType, List<Fine>> groupedByType = allFines.stream()
                .collect(Collectors.groupingBy(Fine::getType));

        // 2. Crear los grupos del reporte
        List<FineGroup> groups = groupedByType.entrySet().stream()
                .map(entry -> {
                    FineType type = entry.getKey();
                    List<Fine> finesInGroup = entry.getValue();

                    // Ordenar por importe descendente
                    finesInGroup.sort((f1, f2) -> Double.compare(f2.getAmount(), f1.getAmount()));

                    // Calcular subtotal del grupo
                    double totalAmount = finesInGroup.stream().mapToDouble(Fine::getAmount).sum();

                    return new FineGroup(type, finesInGroup, finesInGroup.size(), totalAmount);
                })
                // Ordenar los grupos por nombre de tipo para consistencia
                .sorted((g1, g2) -> g1.type().name().compareTo(g2.type().name()))
                .toList();

        // 3. Calcular estadísticas globales
        DoubleSummaryStatistics stats = allFines.stream()
                .mapToDouble(Fine::getAmount)
                .summaryStatistics();

        GlobalStats globalStats = new GlobalStats(
                stats.getCount(),
                stats.getSum(),
                stats.getAverage(),
                (stats.getCount() > 0) ? stats.getMin() : 0.0,
                (stats.getCount() > 0) ? stats.getMax() : 0.0
        );

        return new FinesReport(groups, globalStats);
    }
}