#FROM openjdk:17-jdk-slim
#ARG JAR_FILE=target/*.jar
#COPY ./target/patient-service-0.0.1-SNAPSHOT.jar app.jar
#ENTRYPOINT ["java", "-jar", "/app.jar"]


# === Stage 1: Build the Spring Boot application ===
FROM maven:3.9.5-eclipse-temurin-17 AS builder

# Set working directory
WORKDIR /app

# Copy the pom.xml and download dependencies (cached layer)
COPY pom.xml .

# Pre-download dependencies to leverage Docker cache
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# === Stage 2: Run the Spring Boot application ===
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port (e.g., 8080)
EXPOSE 4000

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]