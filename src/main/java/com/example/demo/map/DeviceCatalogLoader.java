package com.example.demo.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Files;
import java.nio.file.Path;

public class DeviceCatalogLoader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static DevicesSchema load(Path p) {
        try {
            return MAPPER.readValue(Files.readString(p), DevicesSchema.class); }
        catch (Exception e) {
            throw new RuntimeException("Error leyendo devices JSON: " + p, e); }
    }
}
