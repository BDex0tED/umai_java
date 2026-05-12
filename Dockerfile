FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

COPY src ./src

RUN ./mvnw clean package -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Add ca-certificates-java to sync OS certs with Java's internal keystore
RUN apt-get update && \
    apt-get install -y ca-certificates ca-certificates-java && \
    update-ca-certificates && \
    rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-Xmx300m", "-Xss512k", "-jar", "app.jar"]