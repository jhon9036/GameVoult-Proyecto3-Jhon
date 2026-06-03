# Analisis de seguridad - Proyecto 3

## Vulnerabilidades identificadas

### 1. Endpoints de escritura sin autenticacion
Antes del Proyecto 3, los endpoints `POST`, `PUT` y `DELETE` de videojuegos, categorias, plataformas, resenas y wishlist podian ejecutarse sin ningun mecanismo de autenticacion. Esto permitia que cualquier persona con acceso a la API pudiera crear, modificar o eliminar datos.

**Medida implementada:** se agrego autenticacion basica por API Key mediante la cabecera `X-API-Key`. Las operaciones de lectura siguen publicas, pero los metodos de escritura sobre `/api/**` ahora requieren una clave valida.

**Medida pendiente:** reemplazar la API Key compartida por autenticacion de usuarios con roles, por ejemplo JWT o sesiones, para identificar quien realiza cada cambio.

### 2. Credenciales y secretos expuestos o reutilizados
El proyecto depende de credenciales de base de datos y ahora tambien de una API Key. Si estos valores se dejan fijos en el codigo o se comparten publicamente, un atacante podria conectarse a servicios internos o modificar datos.

**Medida implementada:** la API Key se lee desde la variable de entorno `API_KEY`, con un valor de demostracion para ejecucion local. La base de datos tambien se configura por variables de entorno en Docker Compose y Cloud Run.

**Medida pendiente:** usar un gestor de secretos como Google Secret Manager, rotar la API Key periodicamente y evitar usar la clave de demostracion en produccion.

### 3. Trafico local sin HTTPS
Durante la ejecucion local con Docker Compose, la aplicacion se expone por HTTP. Esto es aceptable para sustentacion local, pero en una red no confiable podria permitir captura de cabeceras, incluyendo la API Key.

**Medida implementada:** se documenta que Docker Compose es para entorno local. En produccion, Cloud Run expone la aplicacion por HTTPS.

**Medida pendiente:** si el stack local se publica fuera del equipo, agregar un proxy con TLS o restringir el acceso por red privada.

### 4. CORS demasiado permisivo
La configuracion actual permite origenes amplios para facilitar pruebas del frontend. Esto puede abrir la puerta a llamadas desde sitios externos si se combina con claves expuestas en el navegador.

**Medida implementada:** las operaciones de escritura quedan protegidas por API Key, reduciendo el impacto de llamadas no autorizadas.

**Medida pendiente:** restringir CORS a dominios especificos del frontend en produccion.

## Medidas implementadas en este proyecto

- Filtro `ApiKeyAuthFilter` para proteger metodos `POST`, `PUT`, `PATCH` y `DELETE` sobre `/api/**`.
- Endpoint `/metrics` con Actuator y Micrometer para exponer metricas a Prometheus.
- Metricas propias: contador de requests, latencia de requests y gauge de videojuegos registrados.
- Docker Compose con PostgreSQL, backend, Prometheus y Grafana.
- Dashboard de Grafana aprovisionado con paneles de throughput, latencia y gauge.
- Script `scripts/generate-traffic.ps1` para generar trafico de prueba.

## Plan de respuesta a incidentes

Si el equipo detecta acceso no autorizado o datos comprometidos, primero debe revocar y cambiar la API Key, detener temporalmente las operaciones de escritura y revisar logs/metrica de requests para identificar endpoints afectados. Luego se deben restaurar datos desde respaldo si hubo eliminaciones o modificaciones, documentar el incidente, avisar a los usuarios afectados y cerrar la causa raiz antes de volver a habilitar el acceso normal.
