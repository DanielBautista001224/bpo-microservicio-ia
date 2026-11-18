package com.bpo.ia.service;

import org.springframework.stereotype.Service;

@Service
public class ValidacionSolicitudService {

    public boolean esEntradaValida(String descripcion) {
        return descripcion != null
                && !descripcion.isBlank()
                && descripcion.trim().length() >= 5;
    }
}