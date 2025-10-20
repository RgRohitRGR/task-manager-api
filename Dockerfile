# Stage 1: Use a Maven image to compile the source code
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src/
RUN mvn clean install -DskipTests

# Stage 2: Create the final, minimal runtime image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the generated JAR from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Application runs on port 8080
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


