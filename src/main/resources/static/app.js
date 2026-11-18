
const form = document.getElementById("solicitudForm");
const companiaSelect = document.getElementById("compania");
const submitButton = document.getElementById("submitButton");
const resultSection = document.getElementById("resultSection");
const resultContent = document.getElementById("resultContent");
const estadoBadge = document.getElementById("estadoBadge");

// Cargar compañías configuradas en el backend
async function cargarCompanias() {
    try {
        const response = await fetch("/api/companias");

        if (!response.ok) {
            throw new Error("No fue posible consultar las compañías.");
        }

        const companias = await response.json();

        companiaSelect.innerHTML =
            '<option value="">Selecciona una compañía</option>';

        companias.forEach(compania => {
            const option = document.createElement("option");
            option.value = compania;
            option.textContent = compania;
            companiaSelect.appendChild(option);
        });

    } catch (error) {
        companiaSelect.innerHTML =
            '<option value="">Error al cargar compañías</option>';
        console.error(error);
    }
}

// Procesar solicitud
form.addEventListener("submit", async function (event) {
    event.preventDefault();

    const datos = {
        compania: companiaSelect.value,
        solicitud_id: document.getElementById("solicitudId").value.trim(),
        solicitud_descripcion: document.getElementById("descripcion").value.trim()
    };

    submitButton.disabled = true;
    submitButton.innerHTML = "<span>Procesando solicitud...</span>";
    resultSection.classList.add("hidden");

    try {
        const response = await fetch("/api/solicitudes/procesar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(datos)
        });

        const resultado = await response.json();

        if (!response.ok) {
            const mensaje = Array.isArray(resultado)
                ? resultado.map(error => error.defaultMessage || error.message).join(". ")
                : resultado.message || "No fue posible procesar la solicitud.";

            throw new Error(mensaje);
        }

        mostrarResultado(resultado);

    } catch (error) {
        mostrarError(error.message);
    } finally {
        submitButton.disabled = false;
        submitButton.innerHTML =
            '<span>Procesar solicitud</span><span aria-hidden="true">→</span>';
    }
});

// Mostrar resultado del procesamiento
function mostrarResultado(data) {
    resultSection.classList.remove("hidden");

    estadoBadge.textContent = data.estado || "SIN ESTADO";
    estadoBadge.className = "badge";

    const prioridad = data.prioridad || "NO DEFINIDA";
    const prioridadClass = ["ALTA", "MEDIA", "BAJA"].includes(prioridad)
        ? `priority-${prioridad}`
        : "";

    const campos = [
        ["Compañía", data.compania],
        ["Identificador", data.solicitudId],
        ["Categoría", data.tipoSolicitud],
        ["Prioridad", prioridad, prioridadClass],
        ["Tipo de documento", data.tipoDocumento],
        ["Número de documento", data.numeroDocumento],
        ["Siguiente paso", data.proximoPaso],
        ["Caso externo", data.idPlataformaExterna],
        ["Fecha de procesamiento", data.fechaProcesamiento]
    ];

    resultContent.innerHTML = "";

    const grid = document.createElement("div");
    grid.className = "result-grid";

    campos.forEach(([label, value, cssClass]) => {
        if (value === null || value === undefined || value === "") {
            return;
        }

        const item = document.createElement("div");
        item.className = "result-item";

        const small = document.createElement("small");
        small.textContent = label;

        const strong = document.createElement("strong");
        strong.textContent = value;

        if (cssClass) {
            strong.className = cssClass;
        }

        item.appendChild(small);
        item.appendChild(strong);
        grid.appendChild(item);
    });

    // Mostrar justificación del análisis
    const justification = document.createElement("div");
    justification.className = "result-item full";

    const justificationLabel = document.createElement("small");
    justificationLabel.textContent = "Justificación del análisis";

    const justificationText = document.createElement("strong");
    justificationText.textContent =
        data.justificacion || "No se generó una justificación.";

    justification.appendChild(justificationLabel);
    justification.appendChild(justificationText);
    grid.appendChild(justification);

    // Mostrar respuesta sugerida cuando exista
    if (data.respuestaSugerida) {
        const respuesta = document.createElement("div");
        respuesta.className = "result-item full";

        const respuestaLabel = document.createElement("small");
        respuestaLabel.textContent = "Respuesta sugerida al cliente";

        const respuestaTexto = document.createElement("p");
        respuestaTexto.textContent = data.respuestaSugerida;

        respuesta.appendChild(respuestaLabel);
        respuesta.appendChild(respuestaTexto);
        grid.appendChild(respuesta);
    }

    resultContent.appendChild(grid);

    resultSection.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

// Mostrar errores
function mostrarError(mensaje) {
    resultSection.classList.remove("hidden");
    estadoBadge.textContent = "ERROR";
    estadoBadge.className = "badge";

    resultContent.textContent = mensaje;
    resultSection.scrollIntoView({
        behavior: "smooth",
        block: "start"
    });
}

cargarCompanias();
