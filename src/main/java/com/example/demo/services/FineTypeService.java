package com.example.demo.services;

import Fines.FineType;
import Fines.Speeding;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FineTypeService {
    private final Map<String, FineType> byCode = new HashMap<>();

    public FineTypeService() {
        byCode.put("SPEEDING", new Speeding("SPEEDING", "Speeding", 120.0, 3, 0.10));  // +10% cada +10%
        byCode.put("ILLEGAL_PARKING", new FineType("ILLEGAL_PARKING", "Illegal parking", 80.0, 2));
        byCode.put("RED_LIGHT", new FineType("RED_LIGHT", "Red light running", 200.0, 5));
        byCode.put("UNKNOWN", new FineType("UNKNOWN", "Unknown", 0.0, 0));
    }

    public FineType getByCode(String code) {
        return byCode.getOrDefault(code, byCode.get("UNKNOWN"));
    }

    public double speedingSurchargePer10Percent() {
        var ft = byCode.get("SPEEDING");
        return (ft instanceof Speeding sp) ? sp.getTenPercentExcessSurcharge() : 0.10;
    }

    public void setSpeedingSurchargePer10Percent(double v) {
        var ft = byCode.get("SPEEDING");
        if (ft instanceof Speeding sp) sp.setTenPercentExcessSurcharge(v);
    }
}