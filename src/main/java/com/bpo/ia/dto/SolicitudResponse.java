
package com.bpo.ia.dto;

import java.time.LocalDateTime;

public class SolicitudResponse {

    private String compania;
    private String solicitudId;
    private LocalDateTime fechaProcesamiento;
    private String tipoSolicitud;
    private String prioridad;
    private String tipoDocumento;
    private String numeroDocumento;
    private String idPlataformaExterna;
    private String proximoPaso;
    private String justificacion;
    private String estado;
    private String informacionFaltante;
    private String respuestaSugerida;

    public SolicitudResponse() {
    }

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

    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }

    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getIdPlataformaExterna() {
        return idPlataformaExterna;
    }

    public void setIdPlataformaExterna(String idPlataformaExterna) {
        this.idPlataformaExterna = idPlataformaExterna;
    }

    public String getProximoPaso() {
        return proximoPaso;
    }

    public void setProximoPaso(String proximoPaso) {
        this.proximoPaso = proximoPaso;
    }

    public String getJustificacion() {
        return justificacion;
    }

    public void setJustificacion(String justificacion) {
        this.justificacion = justificacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getInformacionFaltante() {
        return informacionFaltante;
    }

    public void setInformacionFaltante(String informacionFaltante) {
        this.informacionFaltante = informacionFaltante;
    }

    public String getRespuestaSugerida() {
        return respuestaSugerida;
    }

    public void setRespuestaSugerida(String respuestaSugerida) {
        this.respuestaSugerida = respuestaSugerida;
    }
}
