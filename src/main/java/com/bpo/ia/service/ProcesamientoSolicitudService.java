
package com.bpo.ia.service;

import com.bpo.ia.config.ConfigCompania;
import com.bpo.ia.dto.AiClasificationResponse;
import com.bpo.ia.dto.SolicitudRequest;
import com.bpo.ia.dto.SolicitudResponse;
import com.bpo.ia.integration.PlataformaExternaService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class ProcesamientoSolicitudService {

    private final ValidacionSolicitudService validacionService;
    private final PlataformaExternaService plataformaExternaService;
    private final DocumentoExtractorService documentoExtractorService;
    private final CompaniasConfigService companiasConfigService;
    private final AiClasificationService aiClasificationService;
    private final RespuestaDirectaService respuestaDirectaService;

    public ProcesamientoSolicitudService(
            ValidacionSolicitudService validacionService,
            PlataformaExternaService plataformaExternaService,
            DocumentoExtractorService documentoExtractorService,
            CompaniasConfigService companiasConfigService,
            AiClasificationService aiClasificationService,
            RespuestaDirectaService respuestaDirectaService) {

        this.validacionService = validacionService;
        this.plataformaExternaService = plataformaExternaService;
        this.documentoExtractorService = documentoExtractorService;
        this.companiasConfigService = companiasConfigService;
        this.aiClasificationService = aiClasificationService;
        this.respuestaDirectaService = respuestaDirectaService;
    }

    public SolicitudResponse procesar(SolicitudRequest request) {

        SolicitudResponse response = new SolicitudResponse();

        response.setCompania(request.getCompania());
        response.setSolicitudId(request.getSolicitudId());
        response.setFechaProcesamiento(LocalDateTime.now());

        String descripcion = request.getSolicitudDescripcion();

        // 1. Validar la entrada técnica
        if (!validacionService.esEntradaValida(descripcion)) {
            response.setProximoPaso(
                    "CIERRE_POR_INFORMACION_INSUFICIENTE"
            );
            response.setInformacionFaltante(
                    "La descripción está vacía o no contiene texto suficiente para analizarla."
            );
            response.setJustificacion(
                    "Información incompleta o faltante."
            );
            response.setEstado("CERRADO");
            return response;
        }

        // 2. Consultar la configuración de la compañía
        ConfigCompania config =
                companiasConfigService.obtenerPorNombre(
                        request.getCompania()
                );

        if (config == null) {
            response.setProximoPaso("ERROR_COMPANIA_NO_CONFIGURADA");
            response.setJustificacion(
                    "No existe una configuración registrada para la compañía indicada."
            );
            response.setEstado("ERROR");
            return response;
        }

        // 3. Extraer datos identificativos, si están presentes
        response.setTipoDocumento(
                documentoExtractorService.extraerTipoDocumento(descripcion)
        );

        response.setNumeroDocumento(
                documentoExtractorService.extraerNumeroDocumento(descripcion)
        );

        // 4. Analizar la solicitud mediante IA
        AiClasificationResponse decision;

        try {
            decision = aiClasificationService.clasificar(
                    descripcion,
                    config
            );
        } catch (Exception e) {
            return marcarRevisionManual(
                    response,
                    "No fue posible completar el análisis mediante IA."
            );
        }

        if (decision == null
                || decision.informacionSuficiente() == null) {

            return marcarRevisionManual(
                    response,
                    "La IA no devolvió una evaluación válida de la información."
            );
        }

        // 5. Ejecutar la decisión de validación de información
        if (!decision.informacionSuficiente()) {

            response.setInformacionFaltante(
                    decision.informacionFaltante()
            );

            response.setJustificacion(
                    "Información incompleta o faltante. "
                            + (decision.informacionFaltante() == null
                            ? ""
                            : decision.informacionFaltante())
            );

            response.setProximoPaso(
                    "CIERRE_POR_INFORMACION_INSUFICIENTE"
            );

            response.setEstado("CERRADO");

            return response;
        }

        // 6. Comprobar que la IA haya devuelto los datos necesarios
        if (decision.tipoSolicitud() == null
                || decision.prioridad() == null
                || decision.justificacion() == null
                || decision.proximoPaso() == null) {

            return marcarRevisionManual(
                    response,
                    "La IA no devolvió todos los datos necesarios para procesar la solicitud."
            );
        }

        String tipoSolicitud =
                decision.tipoSolicitud().trim().toUpperCase(Locale.ROOT);

        String prioridad =
                decision.prioridad().trim().toUpperCase(Locale.ROOT);

        String proximoPaso =
                decision.proximoPaso().trim().toUpperCase(Locale.ROOT);

        // 7. Validar categoría permitida
        if (!contieneCategoria(config.getCategorias(), tipoSolicitud)) {
            return marcarRevisionManual(
                    response,
                    "La categoría devuelta por la IA no está permitida para esta compañía."
            );
        }

        // 8. Validar prioridad
        if (!List.of("ALTA", "MEDIA", "BAJA").contains(prioridad)) {
            return marcarRevisionManual(
                    response,
                    "La prioridad devuelta por la IA no es válida."
            );
        }

        response.setTipoSolicitud(tipoSolicitud);
        response.setPrioridad(prioridad);
        response.setJustificacion(decision.justificacion());

        // 9. Reconocer las decisiones que requieren revisión manual
        if ("REVISION_MANUAL".equals(proximoPaso)) {
            return marcarRevisionManual(
                    response,
                    "La IA determinó que la solicitud requiere revisión manual."
            );
        }

        // 10. Comprobar que la categoría tenga una delegación válida
        boolean requiereGestionExterna =
                contieneCategoria(
                        config.getGestionExterna(),
                        tipoSolicitud
                );

        boolean permiteRespuestaDirecta =
                contieneCategoria(
                        config.getRespuestaDirecta(),
                        tipoSolicitud
                );

        if (requiereGestionExterna == permiteRespuestaDirecta) {
            return marcarRevisionManual(
                    response,
                    "La categoría no tiene una delegación única y válida en la configuración de la compañía."
            );
        }

        // 11. Verificar que la recomendación de IA respete la configuración
        if ("GESTION_EXTERNA".equals(proximoPaso)
                && !requiereGestionExterna) {

            return marcarRevisionManual(
                    response,
                    "La IA recomendó gestión externa, pero la categoría no tiene esa delegación."
            );
        }

        if ("RESPUESTA_DIRECTA".equals(proximoPaso)
                && !permiteRespuestaDirecta) {

            return marcarRevisionManual(
                    response,
                    "La IA recomendó respuesta directa, pero la categoría no tiene esa delegación."
            );
        }

        if (!List.of("GESTION_EXTERNA", "RESPUESTA_DIRECTA")
                .contains(proximoPaso)) {

            return marcarRevisionManual(
                    response,
                    "La IA devolvió un siguiente paso no reconocido."
            );
        }

        response.setProximoPaso(proximoPaso);

        // 12. Ejecutar la acción correspondiente
        if ("GESTION_EXTERNA".equals(proximoPaso)) {

            try {
                String idCaso =
                        plataformaExternaService.crearCasoExterno(
                                request.getCompania(),
                                request.getSolicitudId(),
                                tipoSolicitud
                        );

                response.setIdPlataformaExterna(idCaso);
                response.setEstado("PENDIENTE");

            } catch (Exception e) {
                return marcarRevisionManual(
                        response,
                        "No fue posible crear el caso en la plataforma externa."
                );
            }

        } else {

            try {
                String respuestaFinal =
                        respuestaDirectaService.generarRespuesta(
                                config,
                                tipoSolicitud,
                                decision.respuestaSugerida()
                        );

                response.setRespuestaSugerida(respuestaFinal);

                // La respuesta está preparada, pero todavía no se envía.
                response.setEstado("PENDIENTE_RESPUESTA");

            } catch (Exception e) {
                return marcarRevisionManual(
                        response,
                        "No fue posible generar la respuesta directa con la configuración de la compañía."
                );
            }
        }

        return response;
    }

    private boolean contieneCategoria(
            List<String> categorias,
            String categoriaBuscada) {

        return categorias != null
                && categorias.stream().anyMatch(
                categoria -> categoria.equalsIgnoreCase(categoriaBuscada)
        );
    }

    private SolicitudResponse marcarRevisionManual(
            SolicitudResponse response,
            String justificacion) {

        response.setProximoPaso("REVISION_MANUAL");
        response.setJustificacion(justificacion);
        response.setEstado("PENDIENTE_REVISION");

        return response;
    }
}
