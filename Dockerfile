# Use a slim OpenJDK 17 image as the base
FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw package -DskipTests

# Runtime image
FROM openjdk:17-jre-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
