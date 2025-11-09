package com.example.demo.services;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.stereotype.Service;
import fines.FineType;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

//mantiene la lista de violaciones


@Service
public class ViolationService {




    public static final class Violation implements Serializable {
        public final Instant ts;
        public final String deviceId;
        public final String plate;
        public final String details;
        public final FineType type;


        public Violation(Instant ts, String deviceId, String plate, FineType type, String details){
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
        public FineType getType(){return type;}
    }


    private final ObservableList<Violation> items = FXCollections.observableArrayList();
    public ObservableList<Violation> items() {
        return items;
    }


    public ViolationService() {}


    public static ViolationService fromSeed(List<com.example.demo.core.CentralState.ViolationSnapshot> seed) {
        ViolationService vs = new ViolationService();
        if (seed != null) {
            for (var s : seed) {
                vs.items.add(new Violation(
                        Instant.ofEpochSecond(s.epochSeconds),
                        s.deviceId,
                        s.plate,
                        FineType.valueOf(s.type),
                        s.details
                ));
            }
        }
        return vs;
    }


    //exporta el contenido a CentralState
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


    public void recordSpeeding(String dev, String plate, int speed, int limit) {
        items.add(new Violation(Instant.now(), dev, plate, FineType.SPEEDING,
                "Speed " + speed + " (limit " + limit + ")"));
    }
    public void recordIllegalParking(String dev, String plate, int stay, int tol) {
        items.add(new Violation(Instant.now(), dev, plate, FineType.PARKING,
                "Stay " + stay + "s (tol " + tol + "s)"));
    }
    public void recordRedLight(String dev, String plate, String dir) {
        items.add(new Violation(Instant.now(), dev, plate, FineType.RED_LIGHT, dir));
    }


}
