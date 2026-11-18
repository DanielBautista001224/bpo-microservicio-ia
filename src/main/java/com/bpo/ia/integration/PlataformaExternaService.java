
package com.bpo.ia.integration;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlataformaExternaService {

    public String crearCasoExterno(
            String compania,
            String solicitudId,
            String tipoSolicitud) {

        if (compania == null || compania.isBlank()
                || solicitudId == null || solicitudId.isBlank()
                || tipoSolicitud == null || tipoSolicitud.isBlank()) {

            throw new IllegalArgumentException(
                    "Los datos para crear el caso externo son obligatorios."
            );
        }

        // Simulación de creación de caso en plataforma externa.
        String identificador = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        String idCaso = "EXT-" + identificador;

        System.out.println("----------------------------------");
        System.out.println("SIMULACIÓN DE PLATAFORMA EXTERNA");
        System.out.println("Compañía: " + compania);
        System.out.println("Solicitud: " + solicitudId);
        System.out.println("Tipo: " + tipoSolicitud);
        System.out.println("Caso creado: " + idCaso);
        System.out.println("----------------------------------");

        return idCaso;
    }
}
