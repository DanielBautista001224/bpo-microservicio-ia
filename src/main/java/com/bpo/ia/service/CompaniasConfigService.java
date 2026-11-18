
package com.bpo.ia.service;

import com.bpo.ia.config.ConfigCompania;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CompaniasConfigService {

    private final ObjectMapper objectMapper;

    private List<ConfigCompania> companias = new ArrayList<>();

    public CompaniasConfigService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void cargarConfiguraciones() {

        try (InputStream archivo =
                     new ClassPathResource("companias.json").getInputStream()) {

            JsonNode raiz = objectMapper.readTree(archivo);

            JsonNode listaCompanias = raiz.get("companias");

            if (listaCompanias == null || !listaCompanias.isArray()) {
                throw new IllegalStateException(
                        "El archivo companias.json no contiene una lista válida de compañías."
                );
            }

            List<ConfigCompania> configuraciones = new ArrayList<>();

            for (JsonNode nodo : listaCompanias) {

                ConfigCompania compania =
                        objectMapper.treeToValue(nodo, ConfigCompania.class);

                if (compania.getNombre() == null ||
                        compania.getNombre().isBlank()) {
                    throw new IllegalStateException(
                            "Se encontró una compañía sin nombre."
                    );
                }

                configuraciones.add(compania);
            }

            companias = configuraciones;

            System.out.println(
                    "Configuraciones de compañías cargadas: " + companias.size()
            );

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No fue posible cargar companias.json", e
            );
        }
    }

    public ConfigCompania obtenerPorNombre(String nombre) {

        if (nombre == null) {
            return null;
        }

        return companias.stream()
                .filter(compania ->
                        compania.getNombre().equalsIgnoreCase(nombre.trim()))
                .findFirst()
                .orElse(null);
    }

    public List<ConfigCompania> obtenerTodas() {
        return List.copyOf(companias);
    }

}
