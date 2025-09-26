package com.example.demo;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@RestController
public class DevicesController {

    @GetMapping(value = "/api/devices", produces = MediaType.APPLICATION_JSON_VALUE)
    public String devices() throws Exception {
        try (InputStream is = new ClassPathResource("devices.json").getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
    }
}
