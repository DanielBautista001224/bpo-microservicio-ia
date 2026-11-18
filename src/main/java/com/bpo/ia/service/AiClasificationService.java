
package com.bpo.ia.service;

import com.bpo.ia.config.ConfigCompania;
import com.bpo.ia.dto.AiClasificationResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiClasificationService {

    private final ChatClient chatClient;

    public AiClasificationService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public AiClasificationResponse clasificar(
            String descripcion,
            ConfigCompania config) {

        String categorias = String.join(", ", config.getCategorias());

        String prioridades = """
                ALTA: %s
                MEDIA: %s
                BAJA: %s
                """.formatted(
                String.join(", ", config.getPalabrasPrioridadAlta()),
                String.join(", ", config.getPalabrasPrioridadMedia()),
                String.join(", ", config.getPalabrasPrioridadBaja())
        );

        String gestionExterna = String.join(
                ", ", config.getGestionExterna()
        );

        String respuestaDirecta = String.join(
                ", ", config.getRespuestaDirecta()
        );

        String prompt = """
                Eres un analista especializado en la gestión de solicitudes
                de servicio al cliente de una empresa BPO.

                Tu responsabilidad es analizar las solicitudes y tomar
                decisiones operativas siguiendo las reglas de cada compañía.

                No inventes información ni supongas hechos que el cliente
                no haya proporcionado.

                INFORMACIÓN DE LA COMPAÑÍA

                Compañía: %s

                CATEGORÍAS OPERATIVAS PERMITIDAS:
                %s

                REGLAS DE PRIORIDAD:
                %s

                CASOS QUE REQUIEREN GESTIÓN EXTERNA:
                %s

                CASOS QUE PERMITEN RESPUESTA DIRECTA:
                %s

                SOLICITUD DEL CLIENTE

                %s

                INSTRUCCIONES DE ANÁLISIS

                PASO 1. VALIDAR INFORMACIÓN

                Comprueba que la descripción permita comprender:
                - Qué ocurrió o qué solicita el cliente.
                - Cuándo ocurrió o cuál es el contexto temporal,
                  cuando sea relevante para gestionar el caso.
                - Qué necesita el cliente o qué solución espera.

                No exijas una fecha exacta si existe un contexto temporal
                suficiente para comprender la situación.

                Si la información es insuficiente:
                - informacionSuficiente = false.
                - Explica qué información hace falta.
                - tipoSolicitud = null.
                - prioridad = null.
                - proximoPaso = CIERRE_POR_INFORMACION_INSUFICIENTE.
                - No continúes con los siguientes pasos.

                PASO 2. IDENTIFICAR TIPO DE SOLICITUD

                Si la información es suficiente, selecciona una única
                categoría de la lista permitida.

                No inventes categorías ni utilices categorías externas
                a la configuración de la compañía.

                PASO 3. ASIGNAR PRIORIDAD

                Asigna ALTA, MEDIA o BAJA.

                Utiliza las reglas de prioridad de la compañía y analiza
                el contexto, la afectación y la urgencia descritos.

                Las palabras indicadas en las reglas son referencias,
                no condiciones obligatorias ni sustituyen el análisis
                del contexto completo.

                PASO 4. JUSTIFICAR

                Redacta una justificación breve y concreta que explique
                por qué asignaste esa prioridad.

                Utiliza información real de la solicitud.
                No incluyas hechos inventados.

                PASO 5. DEFINIR SIGUIENTE PASO

                Consulta las delegaciones de la compañía:

                - Si la categoría requiere gestión externa,
                  proximoPaso = GESTION_EXTERNA.
                - Si la categoría permite respuesta directa,
                  proximoPaso = RESPUESTA_DIRECTA.

                Si una categoría aparece en ambas listas o no aparece
                en ninguna, no inventes una decisión:
                indica que requiere revisión manual.

                Si el siguiente paso es RESPUESTA_DIRECTA,
                redacta una respuesta breve, profesional y coherente
                con la solicitud en respuestaSugerida.

                Si el siguiente paso es GESTION_EXTERNA,
                respuestaSugerida debe ser null.

                FORMATO DE RESPUESTA

                Devuelve exclusivamente un objeto JSON válido con
                los siguientes campos:

                {
                  "informacionSuficiente": true,
                  "informacionFaltante": "",
                  "tipoSolicitud": "CATEGORIA",
                  "prioridad": "MEDIA",
                  "justificacion": "Justificación basada en la solicitud",
                  "proximoPaso": "RESPUESTA_DIRECTA",
                  "respuestaSugerida": "Respuesta para el cliente"
                }

                Los únicos valores permitidos para proximoPaso son:
                CIERRE_POR_INFORMACION_INSUFICIENTE,
                GESTION_EXTERNA,
                RESPUESTA_DIRECTA,
                REVISION_MANUAL.

                Si la información es insuficiente, utiliza el formato
                correspondiente con informacionSuficiente=false,
                tipoSolicitud=null y prioridad=null.

                Si requiere revisión manual, no inventes una acción
                ni una clasificación no permitida.
                """.formatted(
                config.getNombre(),
                categorias,
                prioridades,
                gestionExterna,
                respuestaDirecta,
                descripcion
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .entity(AiClasificationResponse.class);
    }
}
