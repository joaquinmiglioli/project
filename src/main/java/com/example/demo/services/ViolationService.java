package com.example.demo.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ViolationService {

    public enum Type { SPEEDING, ILLEGAL_PARKING, RED_LIGHT, SERVICE_CALL }

    public static final class Violation implements Serializable {
        public final Instant ts;
        public final String deviceId;
        public final String plate;
        public final String details;
        public final Type type;

        public Violation(Instant ts, String deviceId, String plate, Type type, String details){
            this.ts = ts;
            this.deviceId = deviceId;
            this.plate = plate;
            this.type = type;
            this.details = details;
        }

        public Instant getTs()        {
            return ts;
        }
        public String  getDeviceId()  {
            return deviceId;
        }
        public String  getPlate()     {
            return plate;
        }
        public String  getDetails()   {
            return details;
        }
        public Type    getType()      {
            return type;
        }
    }

    private final ObservableList<Violation> items = FXCollections.observableArrayList();
    public ObservableList<Violation> items() {
        return items;
    }

    public ViolationService() {}

    /** Construye el servicio partiendo de una lista persistida (seed). */
    public static ViolationService fromSeed(List<com.example.demo.core.CentralState.ViolationSnapshot> seed) {
        ViolationService vs = new ViolationService();
        if (seed != null) {
            for (var s : seed) {
                vs.items.add(new Violation(
                        Instant.ofEpochSecond(s.epochSeconds),
                        s.deviceId,
                        s.plate,
                        Type.valueOf(s.type),
                        s.details
                ));
            }
        }
        return vs;
    }

    /** Exporta el contenido actual para persistir en CentralState. */
    public List<com.example.demo.core.CentralState.ViolationSnapshot> exportAll() {
        List<com.example.demo.core.CentralState.ViolationSnapshot> out = new ArrayList<>();
        for (var v : items) {
            var s = new com.example.demo.core.CentralState.ViolationSnapshot();
            s.epochSeconds = v.ts.getEpochSecond();
            s.deviceId = v.deviceId;
            s.plate = v.plate;
            s.type = v.type.name();
            s.details = v.details;
            out.add(s);
        }
        return out;
    }

    // helpers
    public void recordSpeeding(String dev, String plate, int speed, int limit) {
        items.add(new Violation(Instant.now(), dev, plate, Type.SPEEDING,
                "Speed " + speed + " (limit " + limit + ")"));
    }
    public void recordIllegalParking(String dev, String plate, int stay, int tol) {
        items.add(new Violation(Instant.now(), dev, plate, Type.ILLEGAL_PARKING,
                "Stay " + stay + "s (tol " + tol + "s)"));
    }
    public void recordRedLight(String dev, String plate, String dir) {
        items.add(new Violation(Instant.now(), dev, plate, Type.RED_LIGHT, dir));
    }
    public void recordServiceCall(String camId, String service) {
        items.add(new Violation(Instant.now(), camId, "-", Type.SERVICE_CALL, service));
    }

    // agregados simples (para Reports)
    public Map<String, Long> countByDevice() {
        return items.stream().collect(Collectors.groupingBy(v -> v.deviceId,
                LinkedHashMap::new, Collectors.counting()));
    }
    public Map<String, Long> countByCategory(Function<String,String> categoryFn) {
        return items.stream().collect(Collectors.groupingBy(v -> categoryFn.apply(v.deviceId),
                LinkedHashMap::new, Collectors.counting()));
    }
}