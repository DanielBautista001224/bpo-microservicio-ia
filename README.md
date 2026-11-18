# BPO Microservicio IA

Microservicio desarrollado en Java y Spring Boot para el análisis y procesamiento de solicitudes mediante inteligencia artificial.

## Requisitos

- Java 21.
- Conexión a Internet.
- API Key de Groq.

## Configuración

1. Clonar el repositorio:

en bash ejecutar

git clone URL_DEL_REPOSITORIO
cd bpo-microservicio-ia


2. Crear un archivo `.env` en la raíz del proyecto, en la misma ubicación de `pom.xml`.

3. Agregar las siguientes variables:

```
GROQ_API_KEY=API_KEY
AI_PROVIDER=groq
AI_MODEL=openai/gpt-oss-120b
```

4. Reemplazar `API_KEY` por la clave obtenida desde [Groq Console](https://console.groq.com/keys).

Nota: en el repositorio hay un .env.de_ejemplo, puede usarse ese quitandole la parte de ".de_ejemplo"
## Ejecución

En caso de tener IntelliJ IDEA 
1.Abrir el proyecto en IntelliJ IDEA.

2.Verificar que esté configurado el JDK 21.

3.Ubicar la clase BpoMicroservicioIaApplication.

4.Hacer clic en el botón Run junto al método main o seleccionar Run 'BpoMicroservicioIaApplication'.

en caso de no tener InteliJ IDEA puede desde la carpeta raíz del proyecto, ejecutar:

```powershell
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run
```

Una vez iniciada la aplicación, acceder desde el navegador:

http://localhost:8080

## Uso de la API

*Consultar compañías*

`GET /api/companias`

Devuelve las compañías disponibles para procesar solicitudes.

*Procesar solicitud*

`POST /api/solicitudes/procesar`

Ejemplo del cuerpo de la petición:

{
  "compania": "MENSAJERIA DEL VALLE",
  "solicitud_id": "TEST-001",
  "solicitud_descripcion": "Mi paquete se encuentra perdido y debía llegar hace tres días. Solicito que investiguen qué ocurrió."
}

La respuesta contiene la clasificación, prioridad, justificación y estado del procesamiento.

# #Notas

- Las compañías y sus reglas se configuran en `companias.json`.
- La creación de casos externos es simulada.
- Las respuestas directas se preparan, pero no se envían automáticamente.
- No se deben publicar credenciales ni incluir el archivo `.env` en GitHub.