# ===== Stage 1: Build =====
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiar el wrapper y pom principal
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Copiar el backend completo
COPY univibe-backend ./univibe-backend

# Compilar usando el POM del backend
RUN ./mvnw -q -f univibe-backend/pom.xml -DskipTests clean package

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiar solo el JAR final del módulo app
COPY --from=build /app/univibe-backend/app/target/univibe-app-*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]

