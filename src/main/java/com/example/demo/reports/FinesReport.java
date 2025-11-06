package com.example.demo.reports;

import fines.Fine;
import fines.FineType;

import java.util.List;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.stream.Collectors;

//encapsula los datos para el filtro de Multas

public record FinesReport(
        List<FineGroup> groups,
        GlobalStats globalStats
) {

    public record FineGroup(
            FineType type,
            List<Fine> fines,
            long count,
            double totalAmount
    ) {}

    public record GlobalStats(
            long totalCount,
            double totalAmount,
            double averageAmount,
            double minAmount,
            double maxAmount
    ) {}

    public static FinesReport create(List<Fine> allFines) {

        Map<FineType, List<Fine>> groupedByType = allFines.stream()
                .collect(Collectors.groupingBy(Fine::getType));

        List<FineGroup> groups = groupedByType.entrySet().stream()
                .map(entry -> {
                    FineType type = entry.getKey();
                    List<Fine> finesInGroup = entry.getValue();

                    finesInGroup.sort((f1, f2) -> Double.compare(f2.getAmount(), f1.getAmount()));

                    double totalAmount = finesInGroup.stream().mapToDouble(Fine::getAmount).sum();

                    return new FineGroup(type, finesInGroup, finesInGroup.size(), totalAmount);
                })
                .sorted((g1, g2) -> g1.type().name().compareTo(g2.type().name()))
                .toList();

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