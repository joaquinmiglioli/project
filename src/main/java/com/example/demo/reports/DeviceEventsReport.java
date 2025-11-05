package com.example.demo.reports;

import devices.SecurityWarning;
import fines.Fine;

import java.util.List;

/**
 * DTO para el Reporte de Eventos por Dispositivo (Reporte 5).
 *
 */
public record DeviceEventsReport(
        String deviceId,
        List<Fine> fines,
        List<SecurityWarning> warnings
) {

    public int getTotalEvents() {
        return fines.size() + warnings.size();
    }
}