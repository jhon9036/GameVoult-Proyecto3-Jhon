# GameVault — Gestión Colaborativa de Videojuegos en la Nube

### Sistema colaborativo de gestión de videojuegos con arquitectura cloud

---

## Equipo de Desarrollo

| Rol | Nombre | Responsabilidades |
|-----|--------|-------------------|
| Product Owner / Backend Developer | Brandon Linares | Sprint 1: API REST, base de datos, Wishlist, CI/CD, Swagger |
| Frontend Developer | Jhon Vargas | Sprint 2: UI, búsqueda, detalle, formularios, wishlist UI, estadísticas; Sprint 3: Docker, tablero Kanban |

---

## Descripción del Proyecto

**GameVault** es una aplicación web para gestionar tu biblioteca de videojuegos personales, desplegada íntegramente en Google Cloud Platform, permite registrar juegos, organizarlos por categoría y plataforma, llevar un seguimiento por estado (PENDIENTE, JUGANDO, TERMINADO, FAVORITO), escribir reseñas con puntuación y mantener una wishlist priorizada. **Cada usuario gestiona su propia biblioteca, wishlist, reseñas y estadísticas de forma privada e independiente** del resto de cuentas.

---

## Arquitectura

| Capa | Tecnología |
|------|-----------|
| Frontend | HTML5, CSS3, JavaScript (vanilla) |
| Backend | Java 21 + Spring Boot 3.2 (API REST) |
| Base de datos | PostgreSQL 15 (Cloud SQL) |
| Infraestructura | GCP — Cloud Run (backend + frontend unificados) |
| CI/CD | GitHub Actions + Cloud Build (deploy automático a main) |
| Contenedorización | Docker multi-stage (Maven build + JRE Alpine) |

Flujo:
```
Usuario → Cloud Run (Spring Boot sirve frontend + /api/*) → Cloud SQL (PostgreSQL)
```

---

## Sprints Completados

### Sprint 1 — Backend + Base de Datos
| Historia | Descripción | Estado |
|----------|-------------|--------|
| HU-01 | CRUD completo de videojuegos (`/api/videojuegos`) | Completado |
| HU-02 | Clasificación por categorías (`/api/categorias`) | Completado |
| HU-03 | Gestión de plataformas (`/api/plataformas`) | Completado |
| HU-04 | Búsqueda y filtros por título, estado, categoría, plataforma | Completado |
| HU-05 | Configuración de base de datos en Cloud SQL | Completado |
| HU-06 | Sistema de reseñas con puntuación (`/api/resenas`) | Completado |
| HU-18 | Wishlist API con prioridad (`/api/wishlist`) | Completado |

Evidencias: [#1 HU-01](https://github.com/blinaresv/GameVoult/issues/1) · [#3 HU-02](https://github.com/blinaresv/GameVoult/issues/3) · [#5 HU-03](https://github.com/blinaresv/GameVoult/issues/5) · [#7 HU-04](https://github.com/blinaresv/GameVoult/issues/7) · [#8 HU-05](https://github.com/blinaresv/GameVoult/issues/8) · [#9 HU-06](https://github.com/blinaresv/GameVoult/issues/9) · [#21 HU-18](https://github.com/blinaresv/GameVoult/issues/21)

### Sprint 2 — Frontend
| Historia | Descripción | Estado |
|----------|-------------|--------|
| HU-07 | Biblioteca visual con tarjetas y badges de estado | Completado |
| HU-08 | Búsqueda en tiempo real y filtros combinados | Completado |
| HU-09 | Página de detalle con reseñas y promedio | Completado |
| HU-10 | Formulario de agregar/editar videojuego | Completado |
| HU-11 | Lista de deseos (Wishlist) con prioridades visuales | Completado |
| HU-12 | Estadísticas del dashboard por estado | Completado |
| HU-19 | Rediseño visual del frontend (CSS/HTML base) | Completado |

Evidencias: [#10 HU-07](https://github.com/blinaresv/GameVoult/issues/10) · [#11 HU-08](https://github.com/blinaresv/GameVoult/issues/11) · [#12 HU-09](https://github.com/blinaresv/GameVoult/issues/12) · [#13 HU-10](https://github.com/blinaresv/GameVoult/issues/13) · [#14 HU-11](https://github.com/blinaresv/GameVoult/issues/14) · [#15 HU-12](https://github.com/blinaresv/GameVoult/issues/15) · [#38 HU-19](https://github.com/blinaresv/GameVoult/issues/38)

### Sprint 3 — Despliegue, Docker y Documentación
| Historia | Descripción | Estado |
|----------|-------------|--------|
| HU-13 | Contenedor Docker unificado (backend + frontend) | Completado |
| HU-14 | CI/CD con Cloud Build y GitHub Actions | Completado |
| HU-15 | Tablero Kanban en GitHub Projects | Completado |
| HU-16 | Documentación completa del proyecto | Completado |
| HU-17 | Manejo de errores y Swagger con HTTPS | Completado |

Evidencias: [#16 HU-13](https://github.com/blinaresv/GameVoult/issues/16) · [#17 HU-14](https://github.com/blinaresv/GameVoult/issues/17) · [#18 HU-15](https://github.com/blinaresv/GameVoult/issues/18) · [#19 HU-16](https://github.com/blinaresv/GameVoult/issues/19) · [#20 HU-17](https://github.com/blinaresv/GameVoult/issues/20)

---

## Métricas del Proyecto

### Resumen 

| Métrica | Valor |
|---------|-------|
| **Velocity promedio** | 21 puntos por sprint |
| **Historias completadas** | 25 / 25 (19 de los Sprints 1–3 + 6 del Proyecto 3) |
| **Bugs encontrados** | 1 (HU-17 — Swagger HTTPS, resuelto en Sprint 3) |
| **PRs mergeados** | 24 |
| **Commits totales** | 86 (2 autores) |
| **Tiempo promedio de resolución** | ~2 horas por historia (mediana: 1h 58min) |
| **Cycle Time mínimo** | 11 minutos (HU-03 Plataformas) |
| **Cycle Time máximo** | 27h 18min (HU-16 Documentación) |




### Lead Time

Lead Time mide el tiempo total desde que una historia entra al backlog hasta que se marca como completada.

| Sprint | Lead Time promedio |
|--------|--------------------|
| Sprint 1 | ~1h 04min |
| Sprint 2 | ~2h 10min |
| Sprint 3 | ~7h 41min |
| **Global** | **~3h 26min** |

> En este proyecto el Lead Time coincide con el Cycle Time porque los issues se crearon y se trabajaron en la misma sesión de trabajo (backlog → in progress → done el mismo día). No hubo tiempo de espera en cola entre la creación del issue y el inicio del desarrollo.

### Cycle Time por Historia

Para los Sprints 1–3 se calcula como la diferencia entre `createdAt` y `closedAt` de cada issue en GitHub. Las 6 historias del Proyecto 3 (HU-20 a HU-25) se entregaron en una fase posterior sin issues con tiempos medidos, por lo que sus valores son **estimaciones** marcadas con `(est.)` y no entran en el promedio/mediana medidos de los Sprints 1–3.

| Historia | Cycle Time |
|----------|-----------|
| HU-01 Gestión de videojuegos | 1h 13min |
| HU-02 Categorías | 0h 40min |
| HU-03 Plataformas | 0h 10min |
| HU-04 Búsqueda y filtros API | 0h 47min |
| HU-05 Cloud SQL | 0h 55min |
| HU-06 Reseñas | 0h 54min |
| HU-07 Biblioteca visual | 1h 19min |
| HU-08 Búsqueda frontend | 1h 32min |
| HU-09 Detalle de videojuego | 1h 58min |
| HU-10 Formulario agregar/editar | 2h 15min |
| HU-11 Wishlist UI | 2h 31min |
| HU-12 Estadísticas dashboard | 3h 13min |
| HU-13 Docker unificado | 5h 39min |
| HU-14 CI/CD GitHub Actions | 3h 55min |
| HU-15 Tablero Kanban | 3h 25min |
| HU-16 Documentación | 27h 18min |
| HU-17 Swagger HTTPS (bug) | 3h 51min |
| HU-18 Wishlist API | 3h 25min |
| HU-19 Rediseño frontend | 0h 21min |
| HU-20 Monitoreo Prometheus + Grafana | 2h 30min (est.) |
| HU-21 Métricas de la API | 1h 45min (est.) |
| HU-22 Autenticación de usuarios | 3h 00min (est.) |
| HU-23 Aislamiento de datos por usuario | 2h 45min (est.) |
| HU-24 Exportación CSV | 1h 10min (est.) |
| HU-25 Documento de seguridad | 1h 30min (est.) |
| **Promedio (Sprints 1–3, medido)** | **3h 26min** |
| **Mediana (Sprints 1–3, medido)** | **1h 58min** |

> La mediana (~2h) es el indicador más representativo: HU-16 (documentación) es un outlier natural al requerir trabajo de redacción extendido.  
> Fuente: [GitHub Insights](https://github.com/blinaresv/GameVoult/pulse) · [Issues cerrados](https://github.com/blinaresv/GameVoult/issues?q=is:closed)

---

## Proyecto 3 - Seguridad, monitoreo y nueva funcionalidad

El Proyecto 3 agrega los siguientes componentes al sistema existente:

| Componente | Implementacion |
|------------|----------------|
| Monitoreo | Prometheus + Grafana con Docker Compose |
| Metricas | Endpoint `/metrics`, contador de requests, latencia y gauge |
| Autenticacion | Registro/login de usuarios + API Key de respaldo para scripts |
| Datos por usuario | Cada cuenta ve y gestiona solo su propia biblioteca, wishlist, resenas y estadisticas |
| Seguridad | Documento [`docs/security.md`](docs/security.md) |
| Nueva funcionalidad | Exportacion de videojuegos a CSV |

### Historias de usuario — Proyecto 3

| Historia | Descripción | Estado |
|----------|-------------|--------|
| HU-20 | Monitoreo con Prometheus + Grafana sobre Docker Compose | Completado |
| HU-21 | Métricas de la API: contador de requests, latencia (percentiles p95/p99) y gauge | Completado |
| HU-22 | Autenticación de usuarios: registro/login con hash PBKDF2 y token firmado | Completado |
| HU-23 | Aislamiento de datos por usuario: cada cuenta gestiona su propia biblioteca, wishlist, reseñas y estadísticas, sin poder acceder a las de otros | Completado |
| HU-24 | Exportación de la biblioteca del usuario a CSV (con filtros) | Completado |
| HU-25 | Documento de seguridad del sistema (`docs/security.md`) | Completado |

> Seguimiento en el [GitHub Project (Kanban)](https://github.com/blinaresv/GameVoult/projects).

#### HU-23 — Aislamiento de datos por usuario (detalle)

**Como** usuario registrado, **quiero** que mis videojuegos, wishlist, reseñas y estadísticas sean privados, **para que** ningún otro usuario vea ni modifique mi información.

Criterios de aceptación:
- Cada videojuego y cada item de wishlist queda asociado al usuario que lo crea (`usuario_id`).
- Las consultas de biblioteca, wishlist, reseñas, estadísticas y exportación CSV devuelven únicamente los datos del usuario autenticado.
- Un usuario no puede leer, editar ni eliminar recursos de otro (responde `404`).
- Los endpoints por usuario requieren token de sesión válido; sin él responden `401`.
- Cubierto por tests de repositorio (`@DataJpaTest`) y de controlador (`@WebMvcTest`).

### Ejecucion del stack completo

Desde la raiz del repositorio:

```bash
docker compose up --build
```

Servicios disponibles:

| Servicio | URL |
|----------|-----|
| Aplicacion GameVault | http://localhost:8080 |
| Metricas Prometheus de la API | http://localhost:8080/metrics |
| Prometheus | http://localhost:9090 |
| Grafana | http://localhost:3000 |

Credenciales de Grafana:

```text
Usuario: admin
Contrasena: admin
```

El dashboard se aprovisiona automaticamente en Grafana con el nombre **GameVault Observabilidad**.

### Generar trafico de prueba

Con el stack corriendo, ejecutar en PowerShell:

```powershell
.\scripts\generate-traffic.ps1
```

El script inicia sesion con un usuario dedicado (lo registra automaticamente si no existe), consulta la biblioteca, las estadisticas y la wishlist con su token, y crea/elimina items temporales de wishlist para generar metricas.

### Login y registro de usuarios

Al abrir la aplicacion se muestra una pantalla de inicio de sesion. Desde esa misma pantalla se puede usar **Crear cuenta** para registrar usuarios nuevos. El sistema crea automaticamente un administrador local para sustentacion:

```text
Usuario: admin
Contrasena: admin123
```

Los usuarios registrados quedan con rol `USER` y el usuario inicial queda con rol `ADMIN`. Las contrasenas se guardan con hash PBKDF2 y el frontend guarda un token firmado temporal que envia en cada peticion a los recursos protegidos con:

```text
Authorization: Bearer <token>
```

### Modelo de autorizacion (datos por usuario)

Cada cuenta gestiona unicamente sus propios datos. Los videojuegos, la wishlist, las resenas y las estadisticas quedan asociados al usuario que los crea y **solo ese usuario** puede verlos o modificarlos. Si Brandon registra juegos y luego inicia sesion Jhon, cada uno vera su propia biblioteca de forma independiente; ningun usuario puede leer ni alterar los datos de otro (responde `404`).

| Tipo de endpoint | Lectura | Escritura |
|------------------|---------|-----------|
| Recursos por usuario — `/api/videojuegos`, `/api/wishlist`, `/api/resenas` (incluye `export/csv` y `estadisticas`) | Requiere sesion de usuario (token Bearer) | Requiere sesion de usuario (token Bearer) |
| Recursos compartidos — `/api/categorias`, `/api/plataformas` | Publica | Token de usuario o `X-API-Key` |

La API Key (`X-API-Key: dev-gamevault-key`) se conserva como respaldo **unicamente** para las escrituras de los recursos compartidos (categorias y plataformas) y para scripts tecnicos; **no da acceso a los datos por usuario**.

Ejemplo de escritura en un recurso compartido con API Key:

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8080/api/categorias" `
  -Headers @{ "X-API-Key" = "dev-gamevault-key"; "Content-Type" = "application/json" } `
  -Body '{"nombre":"Indie"}'
```

Ejemplo de acceso a un recurso por usuario con token Bearer:

```powershell
$login = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/auth/login" `
  -ContentType "application/json" -Body '{"username":"admin","password":"admin123"}'

Invoke-RestMethod -Method Get -Uri "http://localhost:8080/api/videojuegos" `
  -Headers @{ "Authorization" = "Bearer $($login.token)" }
```

Cualquier peticion a un recurso por usuario sin token valido responde `401`.

Si se cambia `API_KEY` en Docker Compose o Cloud Run, el frontend puede usar la nueva clave guardandola en el navegador (solo afecta a los recursos compartidos):

```javascript
localStorage.setItem("gamevaultApiKey", "nueva-clave")
```

### Exportacion CSV

La biblioteca incluye un boton **Exportar CSV** que descarga unicamente los videojuegos del usuario en sesion. El endpoint requiere token Bearer:

```text
GET http://localhost:8080/api/videojuegos/export/csv
Authorization: Bearer <token>
```

Acepta filtros opcionales como `titulo`, `estado`, `categoriaId` y `plataformaId`.

---

## Instalación y Uso

### 1. Clonar el repositorio

```bash
git clone https://github.com/blinaresv/GameVoult.git
```

### 2. Configurar la base de datos

Se debe contar con una instancia de PostgreSQL local o en la nube.
Luego, configurar las credenciales en:

```bash
backend/src/main/resources/application.properties
```

### 3. Ejecutar el backend

```bash
cd backend
mvn clean spring-boot:run
```

Esto levantará el servidor en: http://localhost:8080

### 4. Ejecutar el frontend

Abrir el archivo `frontend/index.html` en cualquier navegador.

### 5. Ejecutar las pruebas

El backend incluye pruebas automatizadas que corren sobre una base de datos H2 en memoria (no requieren PostgreSQL):

```bash
cd backend
./mvnw test        # en Windows: mvnw.cmd test
```

Cobertura actual:
- **Tests de repositorio** (`@DataJpaTest`): validan las consultas `buscarConFiltros` de videojuegos y wishlist sobre H2, incluyendo el **filtrado por usuario dueño** (un usuario no ve los datos de otro).
- **Tests de controlador** (`@WebMvcTest`): validan códigos de estado, validación de entrada, estadísticas y la **autorización por sesión de usuario** (`401` sin token, `201` con token; cada operación queda acotada al usuario autenticado).

> Requiere **Java 21**. El build de la imagen Docker no compila ni ejecuta los tests (`-Dmaven.test.skip=true`): el artefacto de producción se mantiene desacoplado del código de pruebas.

---

## Lecciones Aprendidas

- **Empezar simple en infraestructura**: iniciamos con Firebase Hosting + Cloud Run como dos servicios separados. Mantenerlos sincronizados generó problemas de CORS, doble deploy y complejidad innecesaria. Migrar a un contenedor Docker unificado en el Sprint 3 fue la decisión correcta, aunque idealmente hubiera sido la arquitectura desde el inicio.

- **Hibernate 6 rompe queries JPQL del tutorial**: muchos ejemplos de Spring Boot 3 en internet usan anotaciones `@Query` con JPQL que eran válidas en Hibernate 5. En Hibernate 6 (incluido en Spring Boot 3) el parser es más estricto. Terminamos reemplazando las queries problemáticas con `stream().filter()` hasta identificar la sintaxis correcta.

- **No refactorizar a medias**: al reorganizar los paquetes del backend quedaron dos clases `Application` activas al mismo tiempo, causando `ConflictingBeanDefinitionException` en tiempo de arranque. La lección es completar cualquier refactorización antes de hacer commit o hacer el cambio en una sola operación atómica.

- **GitHub Actions necesita permisos explícitos para Projects V2**: el `GITHUB_TOKEN` por defecto no puede escribir en tableros Projects V2. Dedicamos varios commits a debuggear el workflow hasta entender que requería un Personal Access Token con scope `project` configurado como secret.

- **Los conflictos de merge en archivos grandes son riesgosos**: el conflicto en `index.html` resultó en el contenido de `app.js` sobreescribiendo el HTML, corrompiendo el archivo por completo. Desde entonces aplicamos la práctica de hacer `git pull` antes de empezar cualquier cambio y resolver conflictos archivo por archivo con revisión cuidadosa.



---

## Enlaces Importantes

- [Aplicación en producción](https://gamevoult-289395988346.us-central1.run.app)
- [Swagger UI](https://gamevoult-289395988346.us-central1.run.app/swagger-ui/index.html)
- [Repositorio GitHub](https://github.com/blinaresv/GameVoult)
- [GitHub Project (Kanban)](https://github.com/blinaresv/GameVoult/projects)
- [Documentación de la API](docs/api-documentation.md)
- [Guía de despliegue](docs/deployment-guide.md)

---

## Documentación Técnica Detallada

### 1. Introducción y Contexto del Proyecto

En la actualidad, la cantidad de videojuegos en diferentes plataformas PC, Consolas y celulares hace que sea necesario centralizar la información en un único sistema, desde cualquier lugar.

**GameVault** es un sistema de videojuegos basado en computación en la nube que permite la gestión de un catálogo de videojuegos de manera persistente, segura, y escalable.

El sistema permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) en una base de datos relacional, garantizando la integridad de la información, la disponibilidad del servicio, y la seguridad.

Dado que no es una aplicación tradicional, el sistema ha sido diseñado bajo una arquitectura desacoplada, en la que frontend, backend, y la base de datos actúan como entidades independientes, conectadas por servicios cloud.

---

### 2. Objetivo del Proyecto

**Objetivo General**

Desarrollar una aplicación web desplegada en la nube que permita gestionar videojuegos mediante una arquitectura moderna basada en servicios cloud.

**Objetivos Específicos**

* Diseñar e implementar una API REST utilizando Spring Boot
* Construir una interfaz web funcional para interacción del usuario
* Integrar una base de datos PostgreSQL en la nube
* Desplegar la aplicación utilizando servicios administrados de Google Cloud
* Aplicar buenas prácticas de desarrollo colaborativo con Git

---

### 3. Justificación Técnica

El proyecto fue diseñado con tecnologías que garantizan estabilidad, escalabilidad y facilidad de mantenimiento:

**Backend: Java 21 + Spring Boot**

Se utilizó Java 21 por ser una versión LTS que ofrece mejoras en rendimiento y concurrencia. Spring Boot permite desarrollar APIs REST de forma rápida mediante inyección de dependencias y configuración automática, reduciendo la complejidad del desarrollo.

**Base de Datos: PostgreSQL (Cloud SQL)**

PostgreSQL fue elegido por su cumplimiento de propiedades ACID, lo que garantiza consistencia e integridad de los datos. El uso de Cloud SQL permite delegar la administración del servidor a Google, incluyendo actualizaciones, backups automáticos y alta disponibilidad.

**Contenedorización: Docker**

Se utilizó Docker para empaquetar la aplicación backend junto con todas sus dependencias, asegurando que el sistema funcione de la misma manera en cualquier entorno.

**Ejecución en la nube: Cloud Run**

Cloud Run permite ejecutar contenedores de forma serverless, lo que significa que la aplicación escala automáticamente según la demanda y puede incluso escalar a cero cuando no hay uso.

**Frontend: Spring Boot Static Resources (Cloud Run)**

El frontend (HTML, CSS, JS) se empaqueta dentro del mismo contenedor Docker en la etapa de build, copiándose a `src/main/resources/static/`. Spring Boot lo sirve como contenido estático desde la raíz `/`, mientras que la API REST responde en `/api/*`. Esto elimina la necesidad de un servicio externo de hosting y simplifica el despliegue a una sola URL.

---

### 4. Arquitectura del Sistema

El sistema sigue una arquitectura de tres capas, empaquetadas en un único contenedor Docker desplegado en Cloud Run:

1. **Capa de presentación (Frontend)**  
   Desarrollada en HTML, CSS y JavaScript. Se incluye en el `.jar` de Spring Boot como recurso estático y se sirve desde la raíz `/`.

2. **Capa de lógica de negocio (Backend)**  
   API REST desarrollada con Spring Boot, encargada de procesar solicitudes, validar datos y ejecutar la lógica del sistema. Responde bajo `/api/*`.

3. **Capa de persistencia (Base de datos)**  
   Base de datos PostgreSQL administrada en Cloud SQL, conectada mediante Cloud SQL Socket Factory (sin TCP directo).

**Flujo de comunicación:**

```
Usuario → Cloud Run (Puerto 8080)
             ├── GET /          → Spring Boot sirve index.html (frontend estático)
             ├── GET /api/**    → Controladores REST (lógica de negocio)
             └── Cloud SQL Socket → PostgreSQL (gamelist DB)
```

---

### 5. Stack Tecnológico

* Java 21
* Spring Boot
* PostgreSQL
* HTML5, CSS3, JavaScript
* Maven
* Docker
* Git y GitHub
* Google Cloud Platform

---

### 6. Servicios Cloud Implementados

Durante el desarrollo se utilizaron los siguientes servicios de Google Cloud:

* **Cloud Run:** despliegue del contenedor Docker unificado (frontend + backend + static assets)
* **Cloud SQL:** base de datos PostgreSQL administrada (instancia `gamevoult-db`, región `us-central1`)
* **Artifact Registry:** almacenamiento de imágenes Docker construidas por Cloud Build
* **Cloud Build:** pipeline de CI/CD que construye y despliega automáticamente al hacer push a `main`

> **Nota:** Firebase Hosting se utilizó en una etapa inicial del proyecto para servir el frontend de forma independiente. En el Sprint 3 (HU-13) fue reemplazado por un Dockerfile multi-stage que empaqueta frontend y backend en un solo contenedor, simplificando la arquitectura y reduciendo el número de servicios en producción.

---

### 7. Organización del Repositorio

```bash
/backend    → Código fuente del backend (Spring Boot)
/frontend   → Interfaz web
/database   → Scripts SQL
/docs       → Documentación y evidencias
```

---

### 8. Proceso de Despliegue en la Nube

El despliegue del sistema se realizó utilizando Google Cloud Platform bajo un enfoque de integración continua, permitiendo automatizar la construcción y ejecución del backend a partir del repositorio en GitHub.

**1. Creación de la Base de Datos (Cloud SQL)**

* Se creó la instancia desde la consola de Google Cloud
* Se habilitó el acceso mediante IP pública
* Se definió la base de datos `gamelist`
* Se ejecutaron scripts SQL para la creación de tablas

**2. Configuración del Backend**

Se configuró `application.properties` incluyendo URL de conexión a la base de datos, usuario y contraseña, y configuración de JPA.

**3. Contenerización con Docker**

Se creó un `Dockerfile` multi-stage:

```dockerfile
# Etapa 1: compilar el backend con Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY backend/ ./backend/
COPY frontend/ ./backend/src/main/resources/static/
RUN mvn -f backend/pom.xml clean package -DskipTests

# Etapa 2: imagen final ligera con solo el JRE
FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/backend/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**4. CI/CD con GitHub Actions + Cloud Build**

Se configuró un trigger que al hacer `git push` a `main`:
* Dispara automáticamente el proceso de build
* Reconstruye el contenedor
* Actualiza el servicio en Cloud Run

**5. Despliegue en Cloud Run**

* Puerto: 8080 · Acceso público habilitado · Escalado automático · Ejecución serverless

> En versiones anteriores del proyecto se usó Firebase Hosting para el frontend. Fue eliminado en el Sprint 3 para simplificar el despliegue a una sola URL.

---

### 9. Problemas Encontrados y Soluciones

**Problema 1: Clase `main` duplicada — `ConflictingBeanDefinitionException`**
* Causa: existían dos paquetes con clase `Application`, legacy de una refactorización parcial
* Solución: eliminar el paquete duplicado y especificar `<mainClass>` explícitamente en `pom.xml`

**Problema 2: JPQL incompatible con Hibernate 6 / Spring Boot 3**
* Causa: queries `@Query` con JPQL usaban sintaxis que Hibernate 6 ya no acepta
* Solución: reemplazar las queries por `stream().filter()` en los controladores

**Problema 3: Swagger generaba URLs con `http://` en Cloud Run**
* Causa: Cloud Run termina TLS en el proxy y reenvía al contenedor como HTTP
* Solución: agregar `server.forward-headers-strategy=framework` en `application.properties`

**Problema 4: CORS al tener frontend y backend en dominios diferentes**
* Causa: Firebase Hosting (`web.app`) y Cloud Run en dominios distintos
* Solución: configuración global de CORS en `AppConfig.java` con `allowedOrigins("*")`

**Problema 5: GitHub Actions — `GITHUB_TOKEN` sin permisos para GitHub Projects V2**
* Causa: el token predeterminado de Actions no puede modificar tableros Projects V2
* Solución: crear un Personal Access Token con scope `project` como secret `PROJECT_TOKEN`

**Problema 6: Instancia Cloud SQL renombrada durante el proyecto**
* Causa: la instancia inicial `gamelist-db` fue reconstruida y renombrada a `gamevoult-db`
* Solución: actualizar `INSTANCE_CONNECTION_NAME` en Cloud Run y en el workflow de CI/CD

**Problema 7: Conflicto de merge — `index.html` corrompido**
* Causa: merge entre ramas resultó en el contenido de `app.js` dentro de `index.html`
* Solución: restaurar el archivo desde el historial de git


---

### 10. Evidencia de Funcionamiento

Las capturas del sistema se encuentran en `docs/screenshots/` e incluyen:
* CRUD funcionando · Swagger · Base de datos · Despliegue en Cloud

---

### 11. Documentación Adicional

* API: [docs/api-documentation.md](docs/api-documentation.md)
* Despliegue detallado: [docs/deployment-guide.md](docs/deployment-guide.md)

---

### 12. Conclusiones

El proyecto demuestra la implementación de una arquitectura moderna basada en servicios cloud, logrando escalabilidad, disponibilidad y desacoplamiento entre componentes. Se logró integrar correctamente frontend, backend y base de datos en un entorno real de producción.

---

### 13. Trabajo Futuro

* Migrar el token firmado HMAC actual a JWT estándar con refresh tokens
* Ampliar la cobertura de pruebas (ya hay tests de repositorio y de controlador; faltan reseñas, categorías y plataformas)
* Optimizar consultas con caché
* Mejorar interfaz de usuario
