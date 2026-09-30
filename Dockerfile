# Stage 1: Build Spring Boot JAR with Maven & Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy backend pom and source code
COPY "backend-spring boot/pom.xml" ./
COPY "backend-spring boot/src" ./src

# Build production JAR skipping test suite
RUN mvn clean package -DskipTests

# Stage 2: Production JRE runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=5454
EXPOSE 5454

ENTRYPOINT ["java", "-jar", "app.jar"]
