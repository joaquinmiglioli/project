// com/example/demo/controllers/EventsController.java
/*package com.example.demo.controllers;

import devices.SecurityWarning;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EventsController {

    private final SecurityOpsController securityOps;

    public EventsController(SecurityOpsController securityOps) {
        this.securityOps = securityOps;
    }

    @GetMapping("/events")
    public Map<String, Object> events() {
        // Devolvemos solo notificaciones acá; las multas el front las trae de /api/fines.
        List<SecurityWarning> warnings = securityOps.log();
        return Map.of("fines", List.of(), "warnings", warnings);
    }

    @GetMapping("/security/logs")
    public List<SecurityWarning> logs() {
        return securityOps.log();
    }
}
*/