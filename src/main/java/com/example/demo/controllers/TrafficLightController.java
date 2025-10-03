package com.example.demo.controllers;

import com.example.demo.core.AppContext;
import com.example.demo.core.CentralState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class TrafficLightController {

    private final AppContext ctx;

    public TrafficLightController(AppContext ctx) {  // ← inyecta el MISMO bean
        this.ctx = ctx;
    }

    @GetMapping("/api/trafficlights")
    public List<Map<String, String>> getTrafficLights() {
        List<Map<String, String>> out = new ArrayList<>();
        for (Map.Entry<String, CentralState.TLState> e : ctx.state.tlStates.entrySet()) {
            Map<String, String> m = new HashMap<>();
            m.put("id", e.getKey());
            m.put("a", e.getValue().a.name());
            m.put("b", e.getValue().b.name());
            out.add(m);
        }
        return out;
    }
}
