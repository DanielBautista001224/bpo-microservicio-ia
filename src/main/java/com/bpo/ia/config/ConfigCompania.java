
package com.bpo.ia.config;

import java.util.List;
import java.util.Map;

public class ConfigCompania {

    private String nombre;

    private List<String> categorias;

    private List<String> palabrasPrioridadAlta;
    private List<String> palabrasPrioridadMedia;
    private List<String> palabrasPrioridadBaja;

    private List<String> gestionExterna;
    private List<String> respuestaDirecta;

    private Map<String, List<String>> palabrasCategoria;

    private Map<String, String> plantillasRespuesta;

    public ConfigCompania() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<String> categorias) {
        this.categorias = categorias;
    }

    public List<String> getPalabrasPrioridadAlta() {
        return palabrasPrioridadAlta;
    }

    public void setPalabrasPrioridadAlta(List<String> palabrasPrioridadAlta) {
        this.palabrasPrioridadAlta = palabrasPrioridadAlta;
    }

    public List<String> getPalabrasPrioridadMedia() {
        return palabrasPrioridadMedia;
    }

    public void setPalabrasPrioridadMedia(List<String> palabrasPrioridadMedia) {
        this.palabrasPrioridadMedia = palabrasPrioridadMedia;
    }

    public List<String> getPalabrasPrioridadBaja() {
        return palabrasPrioridadBaja;
    }

    public void setPalabrasPrioridadBaja(List<String> palabrasPrioridadBaja) {
        this.palabrasPrioridadBaja = palabrasPrioridadBaja;
    }

    public List<String> getGestionExterna() {
        return gestionExterna;
    }

    public void setGestionExterna(List<String> gestionExterna) {
        this.gestionExterna = gestionExterna;
    }

    public List<String> getRespuestaDirecta() {
        return respuestaDirecta;
    }

    public void setRespuestaDirecta(List<String> respuestaDirecta) {
        this.respuestaDirecta = respuestaDirecta;
    }

    public Map<String, List<String>> getPalabrasCategoria() {
        return palabrasCategoria;
    }

    public void setPalabrasCategoria(
            Map<String, List<String>> palabrasCategoria) {
        this.palabrasCategoria = palabrasCategoria;
    }

    public Map<String, String> getPlantillasRespuesta() {
        return plantillasRespuesta;
    }

    public void setPlantillasRespuesta(
            Map<String, String> plantillasRespuesta) {
        this.plantillasRespuesta = plantillasRespuesta;
    }
}
