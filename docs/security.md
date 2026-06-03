# Analisis de seguridad - Proyecto 3

## Vulnerabilidades identificadas

### 1. Endpoints de escritura sin autenticacion
Antes del Proyecto 3, los endpoints `POST`, `PUT` y `DELETE` de videojuegos, categorias, plataformas, resenas y wishlist podian ejecutarse sin ningun mecanismo de autenticacion. Esto permitia que cualquier persona con acceso a la API pudiera crear, modificar o eliminar datos.

**Medida implementada:** se agrego login y registro de usuarios mediante `/api/auth/login` y `/api/auth/register`. El frontend muestra una pantalla de inicio de sesion, permite crear cuenta y envia `Authorization: Bearer <token>` en operaciones de escritura. Tambien se conserva `X-API-Key` como mecanismo de respaldo para scripts de prueba. Las operaciones de lectura siguen publicas, pero los metodos de escritura sobre `/api/**` requieren token o clave valida.

**Medida pendiente:** migrar el token firmado local a JWT formal o sesiones revocables, agregar expiracion configurable por usuario y definir permisos distintos para `ADMIN` y `USER` por endpoint.

### 2. Credenciales y secretos expuestos o reutilizados
El proyecto depende de credenciales de base de datos, usuario administrador inicial, clave de firma de tokens y API Key de respaldo. Si estos valores se dejan fijos en el codigo o se comparten publicamente, un atacante podria conectarse a servicios internos o modificar datos.

**Medida implementada:** la API Key, el usuario administrador inicial, la contrasena inicial y la clave de firma de tokens se leen desde variables de entorno, con valores de demostracion para ejecucion local. Las contrasenas de usuarios registrados se guardan con hash PBKDF2. La base de datos tambien se configura por variables de entorno en Docker Compose y Cloud Run.

**Medida pendiente:** usar un gestor de secretos como Google Secret Manager, rotar claves periodicamente y evitar credenciales de demostracion en produccion.

### 3. Trafico local sin HTTPS
Durante la ejecucion local con Docker Compose, la aplicacion se expone por HTTP. Esto es aceptable para sustentacion local, pero en una red no confiable podria permitir captura de cabeceras, incluyendo la API Key.

**Medida implementada:** se documenta que Docker Compose es para entorno local. En produccion, Cloud Run expone la aplicacion por HTTPS.

**Medida pendiente:** si el stack local se publica fuera del equipo, agregar un proxy con TLS o restringir el acceso por red privada.

### 4. CORS demasiado permisivo
La configuracion actual permite origenes amplios para facilitar pruebas del frontend. Esto puede abrir la puerta a llamadas desde sitios externos si se combina con claves expuestas en el navegador.

**Medida implementada:** las operaciones de escritura quedan protegidas por token de usuario o API Key, reduciendo el impacto de llamadas no autorizadas.

**Medida pendiente:** restringir CORS a dominios especificos del frontend en produccion.

## Medidas implementadas en este proyecto

- Login de usuarios con `/api/auth/login` y registro con `/api/auth/register`.
- Tabla `usuario_app` con roles `ADMIN` y `USER`, contrasenas protegidas con PBKDF2 y tokens firmados con expiracion.
- Filtro `ApiKeyAuthFilter` para proteger metodos `POST`, `PUT`, `PATCH` y `DELETE` sobre `/api/**` usando token Bearer o API Key.
- Endpoint `/metrics` con Actuator y Micrometer para exponer metricas a Prometheus.
- Metricas propias: contador de requests, latencia de requests y gauge de videojuegos registrados.
- Docker Compose con PostgreSQL, backend, Prometheus y Grafana.
- Dashboard de Grafana aprovisionado con paneles de throughput, latencia y gauge.
- Script `scripts/generate-traffic.ps1` para generar trafico de prueba.

## Plan de respuesta a incidentes

Si el equipo detecta acceso no autorizado o datos comprometidos, primero debe revocar y cambiar la API Key, detener temporalmente las operaciones de escritura y revisar logs/metrica de requests para identificar endpoints afectados. Luego se deben restaurar datos desde respaldo si hubo eliminaciones o modificaciones, documentar el incidente, avisar a los usuarios afectados y cerrar la causa raiz antes de volver a habilitar el acceso normal.
