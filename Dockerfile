# Use a slim OpenJDK 21 image as the base
FROM openjdk:21-jdk-slim as build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files (pom.xml and src)
COPY pom.xml .
COPY src ./src

# Build the application using Maven
# The -Dmaven.test.skip=true flag skips running tests during the build
RUN ./mvnw package -Dmaven.test.skip=true

# Use a smaller base image for the final runtime
FROM openjdk:21-jre-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
# Assuming the JAR file is named 'hedral-proof-0.0.1-SNAPSHOT.jar' based on common Spring Boot project naming
COPY --from=build /app/target/*.jar app.jar

# Expose the port that Spring Boot listens on (default is 8080)
EXPOSE 8080

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]