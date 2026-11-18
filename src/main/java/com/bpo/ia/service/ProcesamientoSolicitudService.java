
package com.bpo.ia.service;

import com.bpo.ia.config.ConfigCompania;
import com.bpo.ia.dto.SolicitudRequest;
import com.bpo.ia.dto.SolicitudResponse;
import com.bpo.ia.integration.PlataformaExternaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ProcesamientoSolicitudService {

    private final ValidacionSolicitudService validacionService;
    private final PlataformaExternaService plataformaExternaService;
    private final DocumentoExtractorService documentoExtractorService;
    private final CompaniasConfigService companiasConfigService;

    public ProcesamientoSolicitudService(
            ValidacionSolicitudService validacionService,
            PlataformaExternaService plataformaExternaService,
            DocumentoExtractorService documentoExtractorService,
            CompaniasConfigService companiasConfigService) {

        this.validacionService = validacionService;
        this.plataformaExternaService = plataformaExternaService;
        this.documentoExtractorService = documentoExtractorService;
        this.companiasConfigService = companiasConfigService;
    }

    public SolicitudResponse procesar(SolicitudRequest request) {

        SolicitudResponse response = new SolicitudResponse();

        response.setCompania(request.getCompania());
        response.setSolicitudId(request.getSolicitudId());
        response.setFechaProcesamiento(LocalDateTime.now());

        String descripcion = request.getSolicitudDescripcion();

        // Extracción de información del documento
        response.setTipoDocumento(
                documentoExtractorService.extraerTipoDocumento(descripcion));

        response.setNumeroDocumento(
                documentoExtractorService.extraerNumeroDocumento(descripcion));

        // 1. Validar información mínima
        if (!validacionService.tieneInformacionMinima(descripcion)) {
            response.setProximoPaso("CIERRE_POR_INFORMACION_INSUFICIENTE");
            response.setJustificacion(
                    "La descripción no contiene información suficiente para continuar.");
            response.setEstado("CERRADO");
            return response;
        }

        // 2. Consultar configuración de la compañía
        ConfigCompania config =
                companiasConfigService.obtenerPorNombre(request.getCompania());

        if (config == null) {
            response.setProximoPaso("ERROR_COMPANIA_NO_CONFIGURADA");
            response.setJustificacion(
                    "No existe una configuración registrada para la compañía indicada.");
            response.setEstado("ERROR");
            return response;
        }

        String texto = descripcion.toLowerCase(Locale.ROOT);

        // 3. Clasificar solicitud según las reglas de la compañía
        String tipoSolicitud = clasificarSolicitud(texto, config);

        if (tipoSolicitud == null) {
            response.setProximoPaso("REVISION_MANUAL");
            response.setJustificacion(
                    "No fue posible identificar una categoría con las reglas configuradas.");
            response.setEstado("PENDIENTE_REVISION");
            return response;
        }

        response.setTipoSolicitud(tipoSolicitud);

        // 4. Determinar prioridad
        String prioridad = determinarPrioridad(texto, config);
        response.setPrioridad(prioridad);

        // 5. Determinar siguiente paso según delegación
        String proximoPaso;

        if ("ALTA".equals(prioridad)
                || config.getGestionExterna().contains(tipoSolicitud)) {

            proximoPaso = "GESTION_EXTERNA";

        } else if (config.getRespuestaDirecta().contains(tipoSolicitud)) {

            proximoPaso = "RESPUESTA_DIRECTA";

        } else {
            proximoPaso = "REVISION_MANUAL";
        }

        response.setProximoPaso(proximoPaso);

        response.setJustificacion(
                "Solicitud clasificada como " + tipoSolicitud
                        + " con prioridad " + prioridad
                        + " según las reglas configuradas para "
                        + config.getNombre() + "."
        );

        // 6. Ejecutar acción correspondiente
        if ("GESTION_EXTERNA".equals(proximoPaso)) {

            String idCaso = plataformaExternaService.crearCasoExterno(
                    request.getCompania(),
                    request.getSolicitudId(),
                    tipoSolicitud
            );

            response.setIdPlataformaExterna(idCaso);
            response.setEstado("PENDIENTE");

        } else if ("RESPUESTA_DIRECTA".equals(proximoPaso)) {

            response.setEstado("PENDIENTE_RESPUESTA");

        } else {
            response.setEstado("PENDIENTE_REVISION");
        }

        return response;
    }

    private String clasificarSolicitud(
            String texto, ConfigCompania config) {

        Map<String, List<String>> categorias =
                config.getPalabrasCategoria();

        if (categorias == null) {
            return null;
        }

        for (Map.Entry<String, List<String>> categoria : categorias.entrySet()) {

            for (String palabra : categoria.getValue()) {

                if (texto.contains(palabra.toLowerCase(Locale.ROOT))) {
                    return categoria.getKey();
                }
            }
        }

        return null;
    }

    private String determinarPrioridad(
            String texto, ConfigCompania config) {

        if (contienePalabra(texto, config.getPalabrasPrioridadAlta())) {
            return "ALTA";
        }

        if (contienePalabra(texto, config.getPalabrasPrioridadMedia())) {
            return "MEDIA";
        }

        return "BAJA";
    }

    private boolean contienePalabra(
            String texto, List<String> palabras) {

        if (palabras == null) {
            return false;
        }

        return palabras.stream()
                .anyMatch(palabra ->
                        texto.contains(palabra.toLowerCase(Locale.ROOT)));
    }
}
