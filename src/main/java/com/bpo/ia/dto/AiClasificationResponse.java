

package com.bpo.ia.dto;

public record AiClasificationResponse(
        Boolean informacionSuficiente,
        String informacionFaltante,
        String tipoSolicitud,
        String prioridad,
        String justificacion,
        String proximoPaso,
        String respuestaSugerida
) {}
