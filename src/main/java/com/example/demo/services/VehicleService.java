package com.example.demo.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class VehicleService {
    private final Map<String, Vehicle> byPlate = new HashMap<>();
    private final Random rnd = new Random();

    public static final class Vehicle {
        public final String plate, owner, brand, model, color;
        Vehicle(String plate, String owner, String brand, String model, String color) {
            this.plate = plate; this.owner = owner; this.brand = brand; this.model = model; this.color = color;
        }
    }

    public String randomPlateOrGenerate() {
        String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String p = "" + letters.charAt(rnd.nextInt(26)) + letters.charAt(rnd.nextInt(26)) + letters.charAt(rnd.nextInt(26))
                + rnd.nextInt(10) + rnd.nextInt(10) + rnd.nextInt(10);
        ensure(p);
        return p;
    }

    private void ensure(String plate) {
        byPlate.computeIfAbsent(plate, pl -> new Vehicle(pl, rndOwner(), rndBrand(), rndModel(), rndColor()));
    }

    public String ownerNameFor(String plate) { ensure(plate); return byPlate.get(plate).owner; }
    public String brandFor(String plate)     { ensure(plate); return byPlate.get(plate).brand; }
    public String modelFor(String plate)     { ensure(plate); return byPlate.get(plate).model; }
    public String colorFor(String plate)     { ensure(plate); return byPlate.get(plate).color; }

    private final String[] finePhotos = {
            "/com/example/demo/Images/Fines/FinesPhoto1.jpeg",
            "/com/example/demo/Images/Fines/FinesPhoto2.jpg",
            "/com/example/demo/Images/Fines/FinesPhoto3.jpg"
    };
    /** Busca una foto en /com/example/demo/Images/Cars; si no hay, devuelve null. */
    public String randomCarPhotoPathOrNull() {
        if (finePhotos.length == 0) return null;
        return finePhotos[rnd.nextInt(finePhotos.length)];

    }

    private String rndOwner() { String[] s = {"John Doe","Jane Smith","Luis Martínez","Ana Silva"}; return s[rnd.nextInt(s.length)]; }
    private String rndBrand() { String[] s = {"Toyota","Ford","Chevrolet","Volkswagen","Renault"};  return s[rnd.nextInt(s.length)]; }
    private String rndModel() { String[] s = {"Corolla","Fiesta","Cruze","Golf","Clio"};            return s[rnd.nextInt(s.length)]; }
    private String rndColor() { String[] s = {"White","Black","Red","Blue","Silver"};               return s[rnd.nextInt(s.length)]; }
}