
package com.bpo.ia.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ValidacionSolicitudService {

    public boolean tieneInformacionMinima(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            return false;
        }

        String texto = descripcion.toLowerCase(Locale.ROOT);

        boolean contieneProblema =
                texto.contains("problema") ||
                        texto.contains("falla") ||
                        texto.contains("fallas") ||
                        texto.contains("error") ||
                        texto.contains("daño") ||
                        texto.contains("daños") ||
                        texto.contains("solicito") ||
                        texto.contains("solicitud") ||
                        texto.contains("requiero") ||
                        texto.contains("necesito") ||
                        texto.contains("requerimiento");

        boolean contieneContexto =
                texto.length() >= 30;

        return contieneProblema && contieneContexto;
    }
}
