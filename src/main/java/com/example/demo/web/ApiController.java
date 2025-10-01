package com.example.demo.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
public class ApiController {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @GetMapping("/api/devices")
    public JsonNode devices() {
        try (InputStream is = new ClassPathResource("devices.json").getInputStream()) {
            return MAPPER.readTree(is);
        } catch (Exception e) {
            throw new RuntimeException("No pude leer devices.json del classpath", e);
        }
    }
}
