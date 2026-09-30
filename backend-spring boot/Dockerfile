# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Limit Maven memory for Render 512MB free tier
ENV MAVEN_OPTS="-Xms128m -Xmx384m"

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV PORT=5454
ENV JAVA_OPTS="-Xms128m -Xmx384m"
EXPOSE 5454

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
