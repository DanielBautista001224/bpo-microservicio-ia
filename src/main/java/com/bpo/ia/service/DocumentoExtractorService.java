
package com.bpo.ia.service;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DocumentoExtractorService {

    private static final Pattern CEDULA_PATTERN = Pattern.compile(
            "(?i)(?:c[eé]dula|documento)"
                    + "\\D{0,25}(\\d{5,15})"
    );

    public String extraerTipoDocumento(String descripcion) {
        if (descripcion == null) {
            return null;
        }

        String texto = descripcion.toLowerCase();

        if (texto.contains("cédula") || texto.contains("cedula")) {
            return "CEDULA";
        }

        if (texto.contains("pasaporte")) {
            return "PASAPORTE";
        }

        return null;
    }

    public String extraerNumeroDocumento(String descripcion) {
        if (descripcion == null) {
            return null;
        }

        Matcher matcher = CEDULA_PATTERN.matcher(descripcion);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
