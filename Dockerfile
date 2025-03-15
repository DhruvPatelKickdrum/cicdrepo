# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
# Stage 2: Run
FROM amazoncorretto:17
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app`.jar"]