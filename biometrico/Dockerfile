# Etapa 1: Construcción (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Optimización: Descargar dependencias primero (Aprovecha el caché de Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución (Runtime)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Seguridad: Crear un usuario de sistema para no correr como root
RUN addgroup --system javauser && adduser --system --ingroup javauser javauser

# Copiar solo el JAR resultante
COPY --from=build /app/target/*.jar app.jar

# Permisos para el usuario no-root
RUN chown javauser:javauser app.jar
USER javauser

# Puerto estándar
EXPOSE 8080

# Parámetros de rendimiento para la JVM en contenedores
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]