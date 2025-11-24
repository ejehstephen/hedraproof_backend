# Stage 1: Build the application
FROM openjdk:21-jdk-slim as build
RUN apt-get update && apt-get install -y maven
WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jdk-jammy AS runtime
WORKDIR /app

# Copy the built JAR file from the build stage
# Assuming the JAR file is named 'hederaproof-0.0.1-SNAPSHOT.jar' based on common Spring Boot project naming
COPY --from=build /app/target/hederaproof-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
