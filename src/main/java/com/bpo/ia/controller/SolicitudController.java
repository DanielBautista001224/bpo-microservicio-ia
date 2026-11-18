
package com.bpo.ia.controller;

import com.bpo.ia.dto.SolicitudRequest;
import com.bpo.ia.dto.SolicitudResponse;
import com.bpo.ia.service.ProcesamientoSolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final ProcesamientoSolicitudService procesamientoService;

    public SolicitudController(
            ProcesamientoSolicitudService procesamientoService) {
        this.procesamientoService = procesamientoService;
    }

    @PostMapping("/procesar")
    public ResponseEntity<SolicitudResponse> procesarSolicitud(
            @Valid @RequestBody SolicitudRequest request) {

        SolicitudResponse response =
                procesamientoService.procesar(request);

        return ResponseEntity.ok(response);
    }
}
