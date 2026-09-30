# Stage 1: Build Spring Boot JAR with Maven & Java 17
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Limit Maven memory to fit Render 512MB free tier
ENV MAVEN_OPTS="-Xms128m -Xmx384m"

# Copy backend pom and source code using JSON array format for paths with spaces
COPY ["backend-spring boot/pom.xml", "./pom.xml"]
COPY ["backend-spring boot/src", "./src"]

# Build production JAR skipping test suite
RUN mvn clean package -DskipTests

# Stage 2: Production JRE runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV PORT=5454
ENV JAVA_OPTS="-Xms128m -Xmx384m"
EXPOSE 5454

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
