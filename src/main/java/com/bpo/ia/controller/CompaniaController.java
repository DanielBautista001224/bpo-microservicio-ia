
package com.bpo.ia.controller;

import com.bpo.ia.config.ConfigCompania;
import com.bpo.ia.service.CompaniasConfigService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companias")
public class CompaniaController {

    private final CompaniasConfigService companiasConfigService;

    public CompaniaController(CompaniasConfigService companiasConfigService) {
        this.companiasConfigService = companiasConfigService;
    }

    @GetMapping
    public List<String> listarCompanias() {
        return companiasConfigService.obtenerTodas()
                .stream()
                .map(ConfigCompania::getNombre)
                .toList();
    }
}
