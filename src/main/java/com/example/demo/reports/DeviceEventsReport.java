package com.example.demo.reports;

import devices.SecurityWarning;
import fines.Fine;

import java.util.List;

// reporte para filtrar Eventos por Dispositivo

public record DeviceEventsReport(
        String deviceId,
        List<Fine> fines,
        List<SecurityWarning> warnings
) {

    public int getTotalEvents() {
        return fines.size() + warnings.size();
    }
}