# Build stage
FROM openjdk:17.0.2-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN ./mvnw package -Dmaven.test.skip=true

# Runtime stage
FROM openjdk:17.0.2-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
