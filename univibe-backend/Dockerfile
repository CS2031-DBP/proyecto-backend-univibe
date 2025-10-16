# Build stage
FROM eclipse-temurin:21-jdk as build
WORKDIR /app
COPY . .
RUN ./mvnw -q -DskipTests clean package || (apt-get update && apt-get install -y maven && mvn -q -DskipTests clean package)

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/app/target/univibe-app-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
