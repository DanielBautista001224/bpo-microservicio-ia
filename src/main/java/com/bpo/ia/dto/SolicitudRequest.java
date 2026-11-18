
package com.bpo.ia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class SolicitudRequest {

    @NotBlank(message = "La compañía es obligatoria")
    private String compania;

    @NotBlank(message = "El identificador de la solicitud es obligatorio")
    @JsonProperty("solicitud_id")
    private String solicitudId;

    @NotBlank(message = "La descripción de la solicitud es obligatoria")
    @JsonProperty("solicitud_descripcion")
    private String solicitudDescripcion;

    public String getCompania() {
        return compania;
    }

    public void setCompania(String compania) {
        this.compania = compania;
    }

    public String getSolicitudId() {
        return solicitudId;
    }

    public void setSolicitudId(String solicitudId) {
        this.solicitudId = solicitudId;
    }

    public String getSolicitudDescripcion() {
        return solicitudDescripcion;
    }

    public void setSolicitudDescripcion(String solicitudDescripcion) {
        this.solicitudDescripcion = solicitudDescripcion;
    }
}
