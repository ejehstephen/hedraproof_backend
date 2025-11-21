# Build stage
FROM openjdk:17-jdk-slim-bullseye AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN chmod +x mvnw
RUN ./mvnw package -Dmaven.test.skip=true

# Runtime stage
FROM openjdk:17-slim-bullseye
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
