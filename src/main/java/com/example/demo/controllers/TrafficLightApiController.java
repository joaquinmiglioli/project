package com.example.demo.controllers;

import com.example.demo.core.AppContext;
import com.example.demo.core.CentralState;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/*Un  controlador muy específico y optimizado.
getTrafficLights(): Responde a GET /api/trafficlights. Es consultado constantemente (cada 800ms) por el frontend para obtener el estado actual de las luces (A y B) de todos los semáforos, permitiendo la animación de parpadeo y cambio de color en el mapa.
*/

@RestController
public class TrafficLightApiController {

    private final AppContext ctx;

    public TrafficLightApiController(AppContext ctx) {
        this.ctx = ctx;
    }

    // Devuelve el estado actual de todos los semáforos.
    // Este endpoint es utilizado por el frontend para actualizar el mapa.
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
