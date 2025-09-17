package com.example.demo.core;

import Devices.DeviceStatus;
import Devices.TrafficLightStatus;

import java.io.Serializable;
import java.util.*;

/**
 * Estado serializable completo de la central.
 * Se persiste en disco al salir y se restaura al iniciar.
 */
public class CentralState implements Serializable {

    // ===== Dispositivos (por ID: "RAD-1", "PK-3", "INT-5", "CAM-2") =====
    // Guardamos solo los datos necesarios para reconstituir la UI/simulaciones.
    public Map<String, DeviceSnapshot> devicesById = new LinkedHashMap<>();

    // Configuraciones de simulación
    public Map<String, Integer> radarLimit = new HashMap<>();
    public Map<String, Integer> parkingToleranceSec = new HashMap<>();

    // Semáforos
    public Map<String, TLMode> tlMode = new HashMap<>();
    public Map<String, TLState> tlStates = new HashMap<>(); // estado A/B y quién es principal

    // Historial de violaciones (log)
    public List<ViolationSnapshot> violations = new ArrayList<>();

    // ===== Tipos auxiliares =====
    public enum TLMode { NORMAL, FLASHING }

    /** Estado por intersección (A y B). */
    public static class TLState implements Serializable {
        public TrafficLightStatus a;
        public TrafficLightStatus b;
        public boolean principalIsA;

        public TLState() {}
        public TLState(TrafficLightStatus a, TrafficLightStatus b, boolean principalIsA) {
            this.a = a; this.b = b; this.principalIsA = principalIsA;
        }
    }

    /** Snapshot mínimo de un dispositivo (tipo + dirección + estado). */
    public static class DeviceSnapshot implements Serializable {
        public String deviceId;     // "RAD-1"
        public String type;         // "Radar" | "ParkingCamera" | "TrafficLight" | "SecurityCamera"
        public String address;
        public DeviceStatus status = DeviceStatus.NORMAL;

        // específicos (opcionales)
        public Integer speedLimit;       // Radar
        public Integer toleranceSec;     // Parking
        public Boolean principalIsA;     // TrafficLightController (config)

        public DeviceSnapshot() {}
        public static DeviceSnapshot radar(String id, String addr, int limit) {
            var d = new DeviceSnapshot();
            d.deviceId = id; d.type = "Radar"; d.address = addr; d.speedLimit = limit;
            return d;
        }
        public static DeviceSnapshot parking(String id, String addr, int tol) {
            var d = new DeviceSnapshot();
            d.deviceId = id; d.type = "ParkingCamera"; d.address = addr; d.toleranceSec = tol;
            return d;
        }
        public static DeviceSnapshot trafficLight(String id, String addr, boolean principalIsA) {
            var d = new DeviceSnapshot();
            d.deviceId = id; d.type = "TrafficLight"; d.address = addr; d.principalIsA = principalIsA;
            return d;
        }
        public static DeviceSnapshot securityCam(String id, String addr) {
            var d = new DeviceSnapshot();
            d.deviceId = id; d.type = "SecurityCamera"; d.address = addr;
            return d;
        }
    }

    /** Violación persistible (equivalente a tu Violation). */
    public static class ViolationSnapshot implements Serializable {
        public long epochSeconds;
        public String deviceId;
        public String plate;
        public String type;     // SPEEDING, ILLEGAL_PARKING, RED_LIGHT, SERVICE_CALL
        public String details;
    }
}
