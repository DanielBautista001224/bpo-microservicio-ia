
package com.bpo.ia.service;

import com.bpo.ia.config.ConfigCompania;
import org.springframework.stereotype.Service;

@Service
public class RespuestaDirectaService {

    public String generarRespuesta(
            ConfigCompania config,
            String tipoSolicitud,
            String respuestaSugerida) {

        if (config == null || tipoSolicitud == null) {
            throw new IllegalArgumentException(
                    "La configuración y la categoría son obligatorias."
            );
        }

        // Consultar la plantilla definida para la compañía.
        String plantilla = config.getPlantillasRespuesta() == null
                ? null
                : config.getPlantillasRespuesta().get(tipoSolicitud);

        if (plantilla == null || plantilla.isBlank()) {
            throw new IllegalStateException(
                    "No existe una plantilla de respuesta para la categoría: "
                            + tipoSolicitud
            );
        }

        // Validar que la categoría permita respuesta directa.
        boolean permiteRespuesta = config.getRespuestaDirecta() != null
                && config.getRespuestaDirecta().stream()
                .anyMatch(categoria ->
                        categoria.equalsIgnoreCase(tipoSolicitud));

        if (!permiteRespuesta) {
            throw new IllegalStateException(
                    "La categoría no permite respuesta directa."
            );
        }

        // Combinar la plantilla institucional con la propuesta de IA.
        if (respuestaSugerida == null || respuestaSugerida.isBlank()) {
            return plantilla;
        }

        return plantilla.trim() + "\n\n" + respuestaSugerida.trim();
    }
}
