package com.example.demo.core;

import devices.DeviceStatus;
import devices.TrafficLightStatus;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/*
 Contiene todo el estado de la aplicación (la lista de dispositivos, el estado de cada semáforo, el historial de violaciones).
           Decisión de diseño: Esta clase es Serializable, lo que permite que StatePersistenceService la guarde y cargue de golpe en un archivo binario (state.bin).
*/
    @Component
        public class CentralState implements Serializable {

    //Dispositivos (por ID: "RAD-1", "PK-3", "INT-5", "CAM-2")
    public Map<String, DeviceSnapshot> devicesById = new LinkedHashMap<>();

    // Configuraciones de simulación
    public Map<String, Integer> radarLimit = new HashMap<>();
    public Map<String, Integer> parkingToleranceSec = new HashMap<>();

    // Semáforos
    public Map<String, TLMode> tlMode = new HashMap<>();
    public Map<String, TLState> tlStates = new HashMap<>();

    // Historial de violaciones
    public List<ViolationSnapshot> violations = new ArrayList<>();

    public enum TLMode {NORMAL, FLASHING}

    public static class TLState implements Serializable {
        public TrafficLightStatus a;
        public TrafficLightStatus b;
        public boolean principalIsA;

        public TLState() {
        }

        public TLState(TrafficLightStatus a, TrafficLightStatus b, boolean principalIsA) {
            this.a = a;
            this.b = b;
            this.principalIsA = principalIsA;
        }
    }

    public static class DeviceSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        public String deviceId;
        public String type;
        public String address;
        public DeviceStatus status = DeviceStatus.NORMAL;

        public Integer speedLimit;
        public Integer toleranceSec;
        public Boolean principalIsA;

        public Double lat;
        public Double lng;

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

    public static class ViolationSnapshot implements Serializable {
        public long epochSeconds;
        public String deviceId;
        public String plate;
        public String type;
        public String details;
    }
}