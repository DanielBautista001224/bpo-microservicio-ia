
package com.bpo.ia.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthControler {

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
                "estado", "activo",
                "servicio", "bpo-microservicio-ia"
        );
    }
}
