# 1. Use multi-stage build to reduce final image size
FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw package -DskipTests -B

# 2. Final lightweight image with just the JAR
FROM openjdk:17-jdk-slim
WORKDIR /app

# 3. Copy only the built JAR from builder stage
COPY --from=builder /app/target/Foodie-*.jar /app/app.jar

# 4. Add a non-root user for security
RUN useradd -m myuser && chown -R myuser:myuser /app
USER myuser

# 5. Explicitly set the port (Cloud Run requirement)
ENV PORT=8080
EXPOSE $PORT

# 6. Add health check (recommended for Cloud Run)
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:$PORT/actuator/health || exit 1

# 7. Use shell form to allow env variable expansion
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -Dserver.address=0.0.0.0 -jar /app/app.jar"]