
package com.bpo.ia.integration;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PlataformaExternaService {

    public String crearCasoExterno(
            String compania,
            String solicitudId,
            String tipoSolicitud) {

        // Simulación de creación de caso en plataforma externa
        String identificador = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        String idCaso = "EXT-" + identificador;

        System.out.println("----------------------------------");
        System.out.println("SIMULACION PLATAFORMA EXTERNA");
        System.out.println("Compañía: " + compania);
        System.out.println("Solicitud: " + solicitudId);
        System.out.println("Tipo: " + tipoSolicitud);
        System.out.println("Caso creado: " + idCaso);
        System.out.println("----------------------------------");

        return idCaso;
    }
}
