# ============================================================
# Dockerfile UNIFICADO — GameVault
# Spring Boot sirve el frontend como recursos estáticos en /
# y la API en /api/
# ============================================================

# ── Etapa 1: Build con Maven ─────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar pom.xml primero para cachear dependencias Maven
COPY backend/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

# Copiar código fuente del backend
COPY backend/src ./src

# Copiar el frontend dentro de los recursos estáticos de Spring Boot
# Spring Boot lo servirá en / por convención de classpath:/static/
COPY frontend/ ./src/main/resources/static/

# Compilar el JAR sin compilar ni ejecutar los tests.
# La imagen de producción no debe depender del código de pruebas;
# los tests se ejecutan aparte con `mvn test` (ver README / CI).
RUN mvn clean package -Dmaven.test.skip=true

# ── Etapa 2: Imagen de runtime liviana ───────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar solo el JAR compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto estándar de Cloud Run
EXPOSE 8080

# Valores por defecto (se sobreescriben con variables de Cloud Run)
ENV DB_NAME=gamelist
ENV DB_USER=postgres
ENV DB_PASSWORD=changeme
ENV INSTANCE_CONNECTION_NAME=project:region:instance

ENTRYPOINT ["java", "-jar", "app.jar"]
