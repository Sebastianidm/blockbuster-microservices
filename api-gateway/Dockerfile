# ==============================================================================
# Dockerfile para api-gateway (Hito 1)
# ==============================================================================

# Etapa 1: Build y compilación del JAR
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Copiar pom.xml y descargar dependencias para aprovechar la caché de Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar el código fuente y compilar el paquete omitiendo pruebas unitarias
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Imagen final optimizada para ejecución
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el .jar compilado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto estándar del Gateway
EXPOSE 8080

# Comando de arranque del API Gateway
ENTRYPOINT ["java", "-jar", "app.jar"]
