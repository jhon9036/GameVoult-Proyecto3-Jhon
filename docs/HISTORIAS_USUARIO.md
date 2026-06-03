# 📖 Historias de Usuario — GameVault v2.0
# Formato requerido por el enunciado del Proyecto 2
# Mínimo 2 historias por persona por sprint = 6 historias por persona

# ══════════════════════════════════════════════════════════════
# SPRINT 1 — Backend + Base de Datos
# ══════════════════════════════════════════════════════════════

---

## HU-01 | Gestión de videojuegos
**Rama:** `feature/sprint1-backend-videojuegos`
**Responsable:** @Persona1
**Sprint:** 1 | **Estimación:** 5 puntos | **Tipo:** Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero poder crear, ver, editar y eliminar videojuegos de mi biblioteca
Para llevar un registro organizado de todos los juegos que tengo o he jugado

### Criterios de Aceptación:
- [ ] GET /api/videojuegos retorna lista de todos los juegos con categoría y plataforma
- [ ] POST /api/videojuegos crea un juego con validación de campos obligatorios
- [ ] PUT /api/videojuegos/{id} actualiza los datos de un juego existente
- [ ] DELETE /api/videojuegos/{id} elimina el juego y sus reseñas (cascade)
- [ ] Si el ID no existe, retorna 404 con mensaje descriptivo en JSON
- [ ] El campo `estado` solo acepta: PENDIENTE, JUGANDO, TERMINADO, FAVORITO

---

## HU-02 | Clasificación por categorías
**Rama:** `feature/sprint1-backend-categorias`
**Responsable:** @Persona1
**Sprint:** 1 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero poder organizar mis juegos por categorías (Acción, RPG, Deportes, etc.)
Para encontrar juegos del mismo género fácilmente y filtrar mi biblioteca

### Criterios de Aceptación:
- [ ] CRUD completo de categorías en /api/categorias
- [ ] Al crear un videojuego se puede asignar una categoría existente
- [ ] GET /api/videojuegos?categoriaId={id} filtra juegos por categoría
- [ ] GET /api/videojuegos/{id}/categoria retorna la categoría del juego
- [ ] No se puede crear categoría con nombre duplicado (unique constraint)

---

## HU-03 | Gestión de plataformas
**Rama:** `feature/sprint1-backend-plataformas`
**Responsable:** @Persona1
**Sprint:** 1 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero registrar en qué plataforma (PS5, PC, Xbox, etc.) tengo cada juego
Para saber rápidamente dónde puedo jugarlo y filtrar por consola

### Criterios de Aceptación:
- [ ] CRUD completo de plataformas en /api/plataformas
- [ ] Plataforma tiene campos: nombre y fabricante
- [ ] Al crear/editar videojuego se puede asignar plataforma
- [ ] GET /api/videojuegos?plataformaId={id} filtra por plataforma
- [ ] No se pueden duplicar nombres de plataforma

---

## HU-04 | Búsqueda y filtros en la biblioteca
**Rama:** `feature/sprint1-backend-filtros`
**Responsable:** @Persona1
**Sprint:** 1 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Media

### Como usuario de GameVault
Quiero buscar juegos por nombre y filtrar por estado o categoría
Para encontrar rápidamente un juego específico sin tener que revisar toda la lista

### Criterios de Aceptación:
- [ ] GET /api/videojuegos?titulo=wit encuentra "The Witcher 3" (búsqueda parcial)
- [ ] La búsqueda es insensible a mayúsculas ("WITCHER" = "witcher")
- [ ] Se pueden combinar filtros: ?estado=JUGANDO&categoriaId=3
- [ ] Sin parámetros retorna todos los juegos
- [ ] GET /api/videojuegos/estadisticas retorna conteo por estado

---

## HU-05 | Configuración de base de datos en Cloud SQL
**Rama:** `feature/sprint1-database-setup`
**Responsable:** @Persona1
**Sprint:** 1 | **Estimación:** 5 puntos | **Tipo:** DevOps | **Prioridad:** Alta

### Como equipo de desarrollo
Quiero tener la base de datos PostgreSQL configurada en Google Cloud SQL
Para que la aplicación persista datos de forma segura y accesible en producción

### Criterios de Aceptación:
- [ ] Instancia Cloud SQL creada en GCP (región us-central1)
- [ ] schema.sql ejecutado correctamente con las 5 tablas
- [ ] seed.sql con datos de prueba cargado
- [ ] Conexión desde Spring Boot via Cloud SQL socket factory funcionando
- [ ] Variables de entorno configuradas en Cloud Run (sin credenciales en el código)

---

## HU-06 | Sistema de reseñas de videojuegos
**Rama:** `feature/sprint1-backend-resenas`
**Responsable:** @Persona1
**Sprint:** 1 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Media

### Como usuario de GameVault
Quiero poder escribir reseñas con puntuación para los juegos de mi biblioteca
Para recordar mi opinión sobre cada juego y ver el promedio de valoraciones

### Criterios de Aceptación:
- [ ] POST /api/resenas crea reseña vinculada a un videojuego
- [ ] Puntuación debe estar entre 1 y 10
- [ ] GET /api/resenas/videojuego/{id} retorna todas las reseñas del juego
- [ ] DELETE /api/resenas/{id} elimina una reseña
- [ ] Al eliminar un videojuego, sus reseñas se eliminan automáticamente (cascade)

# ══════════════════════════════════════════════════════════════
# SPRINT 2 — Frontend
# ══════════════════════════════════════════════════════════════

---

## HU-07 | Biblioteca visual de videojuegos
**Rama:** `feature/sprint2-frontend-biblioteca`
**Responsable:** @Persona2
**Sprint:** 2 | **Estimación:** 5 puntos | **Tipo:** Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero ver mi biblioteca de juegos en tarjetas visuales con imagen y estado
Para tener una visión atractiva y rápida de todos mis juegos

### Criterios de Aceptación:
- [ ] Cards con imagen de portada del juego (cuando hay imagenUrl)
- [ ] Badge de color distinto por estado (azul=JUGANDO, naranja=PENDIENTE, etc.)
- [ ] Mostrar categoría y plataforma en la tarjeta
- [ ] Hover effect en las cards
- [ ] Al no haber imagen, mostrar emoji 🎮 como placeholder
- [ ] Diseño responsive que funciona en móvil y escritorio

---

## HU-08 | Búsqueda y filtros en el frontend
**Rama:** `feature/sprint2-frontend-filtros`
**Responsable:** @Persona2
**Sprint:** 2 | **Estimación:** 3 puntos | **Tipo:** Bug + Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero buscar juegos escribiendo parte del nombre y filtrar por estado o categoría
Para encontrar rápidamente un juego sin tener que scrollear toda mi biblioteca

### Criterios de Aceptación:
- [ ] Barra de búsqueda filtra mientras se escribe (oninput)
- [ ] Buscar "wit" muestra "The Witcher 3" (búsqueda parcial, fix del bug anterior)
- [ ] Dropdown de filtro por estado (Todos / Jugando / Pendiente / Terminado / Favorito)
- [ ] Dropdown de filtro por categoría poblado dinámicamente desde la API
- [ ] Filtros se combinan entre sí
- [ ] Al limpiar la búsqueda vuelven todos los juegos

---

## HU-09 | Página de detalle por videojuego
**Rama:** `feature/sprint2-frontend-detalle`
**Responsable:** @Persona2
**Sprint:** 2 | **Estimación:** 5 puntos | **Tipo:** Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero ver una página completa con todos los detalles de un juego y sus reseñas
Para conocer toda la información del juego y leer/agregar opiniones

### Criterios de Aceptación:
- [ ] Clic en una card abre la vista de detalle
- [ ] Muestra: imagen grande, título, año, estado, categoría, plataforma, descripción
- [ ] Muestra puntuación promedio calculada de las reseñas
- [ ] Lista de reseñas con autor, puntuación y comentario
- [ ] Botón para agregar nueva reseña (abre modal)
- [ ] Botones de editar y eliminar el juego
- [ ] Botón "← Volver" regresa a la biblioteca

---

## HU-10 | Formulario de agregar y editar videojuego
**Rama:** `feature/sprint2-frontend-formulario`
**Responsable:** @Persona2
**Sprint:** 2 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero un formulario claro para agregar nuevos juegos o editar los existentes
Para mantener mi biblioteca actualizada con información correcta

### Criterios de Aceptación:
- [ ] Campos: título, año, descripción, URL de imagen, categoría, plataforma, estado
- [ ] Estado se elige con radio buttons visuales (no dropdown)
- [ ] Al editar, el formulario se pre-llena con los datos actuales
- [ ] Validación: título y año son obligatorios
- [ ] Mensaje de error visible si falla la API
- [ ] Al guardar exitosamente, regresa a la biblioteca y recarga los juegos

---

## HU-11 | Lista de deseos (Wishlist)
**Rama:** `feature/sprint2-frontend-wishlist`
**Responsable:** @Persona2
**Sprint:** 2 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Media

### Como usuario de GameVault
Quiero mantener una lista de juegos que quiero comprar en el futuro con prioridad
Para no olvidar los juegos que me interesan y saber cuáles son más urgentes

### Criterios de Aceptación:
- [ ] Sección "Wishlist" accesible desde la navegación
- [ ] Cards con borde de color según prioridad (rojo=Alta, amarillo=Media, verde=Baja)
- [ ] Modal para agregar: título, plataforma, categoría, prioridad, notas
- [ ] Botón para eliminar items de la wishlist
- [ ] Consume los endpoints /api/wishlist

---

## HU-12 | Estadísticas en el dashboard
**Rama:** `feature/sprint2-frontend-stats`
**Responsable:** @Persona2
**Sprint:** 2 | **Estimación:** 2 puntos | **Tipo:** Feature | **Prioridad:** Media

### Como usuario de GameVault
Quiero ver un resumen rápido de cuántos juegos tengo en cada estado
Para tener una visión general de mi biblioteca de un vistazo

### Criterios de Aceptación:
- [ ] Pills/chips en la parte superior mostrando conteo por estado
- [ ] Total de juegos en biblioteca
- [ ] Datos obtenidos de GET /api/videojuegos/estadisticas
- [ ] Se actualiza automáticamente al agregar o eliminar juegos

# ══════════════════════════════════════════════════════════════
# SPRINT 3 — Despliegue, Docker y Documentación
# ══════════════════════════════════════════════════════════════

---

## HU-13 | Contenedor Docker unificado
**Rama:** `feature/sprint3-docker-unificado`
**Responsable:** @Persona2
**Sprint:** 3 | **Estimación:** 5 puntos | **Tipo:** DevOps | **Prioridad:** Alta

### Como equipo de desarrollo
Quiero empaquetar el backend y el frontend en un solo contenedor Docker
Para simplificar el despliegue en Cloud Run y eliminar la dependencia de Firebase Hosting

### Criterios de Aceptación:
- [ ] Dockerfile multi-stage: etapa Maven build + etapa JRE Alpine
- [ ] El frontend se copia a src/main/resources/static/ durante el build
- [ ] Spring Boot sirve el frontend en / y la API en /api/
- [ ] docker build . ejecuta sin errores localmente
- [ ] Una sola URL de Cloud Run sirve toda la aplicación
- [ ] app.js usa URL relativa /api en producción

---

## HU-14 | CI/CD con Cloud Build y GitHub Actions
**Rama:** `feature/sprint3-cicd`
**Responsable:** @Persona1
**Sprint:** 3 | **Estimación:** 5 puntos | **Tipo:** DevOps | **Prioridad:** Alta

### Como equipo de desarrollo
Quiero que cada push a main dispare automáticamente el despliegue en GCP
Para no tener que hacer el deploy manualmente y reducir errores humanos

### Criterios de Aceptación:
- [ ] cloudbuild.yaml actualizado para build de imagen Docker unificada
- [ ] GitHub Actions workflow mueve cards del tablero Kanban automáticamente
- [ ] Al asignar un issue, la card pasa a "In Progress"
- [ ] Al cerrar un PR merged, la card pasa a "Done"
- [ ] Cloud Run actualizado con la nueva imagen en cada deploy
- [ ] Variables de entorno configuradas como secrets en GitHub

---

## HU-15 | Configuración del tablero Kanban en GitHub Projects
**Rama:** `feature/sprint3-github-project`
**Responsable:** @Persona2
**Sprint:** 3 | **Estimación:** 2 puntos | **Tipo:** Documentation | **Prioridad:** Alta

### Como equipo de desarrollo
Quiero tener el GitHub Project configurado con tablero Kanban y campos personalizados
Para gestionar el trabajo de forma ágil y tener trazabilidad de cada tarea

### Criterios de Aceptación:
- [ ] Tablero con columnas: Backlog → Ready → In Progress → Review → Done
- [ ] Campo Sprint: Sprint 1, Sprint 2, Sprint 3
- [ ] Campo Responsable asignado a cada historia
- [ ] Campo Prioridad: Alta, Media, Baja
- [ ] Campo Estimación: 1, 2, 3, 5, 8 (Fibonacci)
- [ ] Campo Tipo: Feature, Bug, Documentation, DevOps
- [ ] Todas las historias de usuario creadas y vinculadas

---

## HU-16 | Documentación completa del proyecto
**Rama:** `feature/sprint3-documentacion`
**Responsable:** @Persona2
**Sprint:** 3 | **Estimación:** 3 puntos | **Tipo:** Documentation | **Prioridad:** Alta

### Como evaluador del proyecto
Quiero encontrar documentación completa en el repositorio
Para entender la arquitectura, cómo instalar el proyecto y los resultados de cada sprint

### Criterios de Aceptación:
- [ ] README.md con: equipo, arquitectura, sprints completados, métricas, instalación
- [ ] docs/api-documentation.md con todos los endpoints y ejemplos
- [ ] docs/deployment-guide.md con pasos de despliegue en GCP
- [ ] docs/screenshots/ con mínimo 5 capturas de la aplicación
- [ ] Retrospectiva del proyecto documentada
- [ ] Métricas: velocity por sprint, historias completadas, bugs encontrados

---

## HU-17 | Manejo de errores y Swagger con HTTPS
**Rama:** `feature/sprint3-backend-fixes`
**Responsable:** @Persona1
**Sprint:** 3 | **Estimación:** 3 puntos | **Tipo:** Bug | **Prioridad:** Alta

### Como desarrollador y usuario de la API
Quiero que los errores devuelvan mensajes claros en JSON y que Swagger funcione con HTTPS
Para depurar problemas fácilmente y documentar la API correctamente en producción

### Criterios de Aceptación:
- [ ] 404 con JSON descriptivo cuando el recurso no existe
- [ ] 400 con mensaje del campo inválido cuando fallan validaciones
- [ ] Swagger accesible en https://[url]/swagger-ui.html (no en http://)
- [ ] server.forward-headers-strategy=framework configurado
- [ ] springdoc-openapi 2.5 en lugar de springfox (compatible con Spring Boot 3)

---

## HU-18 | Lista de deseos en el backend (Wishlist API)
**Rama:** `feature/sprint1-backend-wishlist`
**Responsable:** @Persona1
**Sprint:** 1 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Media

### Como usuario de GameVault
Quiero poder guardar juegos en una lista de deseos con prioridad desde la API
Para que el frontend pueda mostrar y gestionar mi wishlist de forma persistente

### Criterios de Aceptación:
- [ ] CRUD completo en /api/wishlist
- [ ] Campos: titulo, plataforma, categoria, prioridad (ALTA/MEDIA/BAJA), notas
- [ ] GET /api/wishlist?prioridad=ALTA filtra por prioridad
- [ ] GET /api/wishlist?titulo=star busca por título parcial
- [ ] Relaciones con Plataforma y Categoria opcionales

---

## HU-19 | Rediseño visual del frontend (CSS/HTML base)
**Rama:** `feature/sprint2-frontend-redesign`
**Responsable:** @Persona2
**Sprint:** 2 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Alta

### Como usuario de GameVault
Quiero una interfaz moderna con tema oscuro y tipografía editorial
Para tener una experiencia visual atractiva y coherente en toda la aplicación

### Criterios de Aceptación:
- [ ] Fondo de la app cambia a tema oscuro usando `#0d0f14` como color base
- [ ] Se importan las fuentes Bebas Neue (títulos) y DM Sans (cuerpo) desde Google Fonts
- [ ] Se definen variables CSS globales: `--bg`, `--surface`, `--surface2`, `--border`, `--accent`, `--text`, `--muted`
- [ ] Se definen variables CSS por estado: `--JUGANDO: #6c63ff`, `--PENDIENTE: #f59e0b`, `--TERMINADO: #22c55e`, `--FAVORITO: #ff6584`
- [ ] Navbar sticky con logo 🎮, nombre "GameVault" y botones de navegación (Biblioteca, Wishlist, + Agregar)
- [ ] Todos los inputs, selects y textareas tienen estilo oscuro consistente con el tema
- [ ] Botones primarios y secundarios definidos globalmente con variables CSS
- [ ] Diseño responsive: media query `max-width: 700px` ajusta columnas a 1
- [ ] El diseño base aplica a todas las secciones (biblioteca, formulario, modales, detalle)
- [ ] Cards con hover effect: `transform: translateY(-4px)` + sombra al pasar el mouse

## Estimación: 3 puntos
## Sprint: Sprint 2
## Responsable: @Persona2
## Tipo: Feature
## Prioridad: Alta

---

# SPRINT 4 / PROYECTO 3 - Seguridad, monitoreo y nuevas funcionalidades

---

## HU-20 | Monitoreo con Prometheus y Grafana
**Rama:** `codex/proyecto3-gamevoult`
**Responsable:** @Jhon
**Sprint:** 4 | **Estimación:** 5 puntos | **Tipo:** DevOps | **Prioridad:** Alta

### Como equipo de desarrollo
Quiero monitorear la API con Prometheus y Grafana
Para observar throughput, latencia y estado general del sistema durante la sustentación

### Criterios de Aceptación:
- [ ] La API expone métricas en `/metrics`
- [ ] Prometheus recolecta métricas del backend desde Docker Compose
- [ ] Grafana tiene un dashboard con mínimo 3 paneles
- [ ] Los paneles muestran throughput, latencia y una métrica adicional tipo gauge
- [ ] Existe un script para generar tráfico de prueba

---

## HU-21 | Autenticación básica por API Key
**Rama:** `codex/proyecto3-gamevoult`
**Responsable:** @Jhon
**Sprint:** 4 | **Estimación:** 3 puntos | **Tipo:** Seguridad | **Prioridad:** Alta

### Como administrador de GameVault
Quiero proteger las operaciones de escritura con una API Key
Para evitar que usuarios no autorizados creen, modifiquen o eliminen información

### Criterios de Aceptación:
- [ ] `POST`, `PUT`, `PATCH` y `DELETE` bajo `/api/**` requieren `X-API-Key`
- [ ] Los endpoints de lectura siguen siendo públicos
- [ ] Una petición sin API Key válida retorna `401`
- [ ] El frontend envía la API Key en operaciones de escritura

---

## HU-22 | Análisis de seguridad del sistema
**Rama:** `codex/proyecto3-gamevoult`
**Responsable:** @Jhon
**Sprint:** 4 | **Estimación:** 3 puntos | **Tipo:** Documentación | **Prioridad:** Alta

### Como equipo del proyecto
Quiero documentar vulnerabilidades, mitigaciones y respuesta a incidentes
Para demostrar comprensión de los riesgos de seguridad del sistema

### Criterios de Aceptación:
- [ ] Existe el archivo `docs/security.md`
- [ ] Se listan al menos 3 vulnerabilidades del sistema
- [ ] Se describen medidas implementadas y pendientes
- [ ] Se incluye un plan corto de respuesta a incidentes

---

## HU-23 | Exportación de videojuegos a CSV
**Rama:** `codex/proyecto3-gamevoult`
**Responsable:** @Jhon
**Sprint:** 4 | **Estimación:** 3 puntos | **Tipo:** Feature | **Prioridad:** Media

### Como usuario de GameVault
Quiero exportar mi biblioteca de videojuegos en formato CSV
Para guardar un respaldo o analizar los datos fuera de la aplicación

### Criterios de Aceptación:
- [ ] Existe un endpoint `GET /api/videojuegos/export/csv`
- [ ] El CSV contiene id, título, año, estado, categoría, plataforma y descripción
- [ ] El endpoint permite reutilizar filtros de búsqueda
- [ ] El frontend incluye un botón para descargar el CSV
