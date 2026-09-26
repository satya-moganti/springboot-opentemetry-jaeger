FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY . .

RUN if [ -f "./gradlew" ]; then \
        chmod +x ./gradlew && ./gradlew bootJar -x test --no-daemon -q; \
    else \
        chmod +x ./mvnw && ./mvnw clean package -DskipTests -q; \
    fi && \
    find . \( -path '*/build/libs/*.jar' -o -path '*/target/*.jar' \) ! -name '*plain*' | head -1 | xargs -I{} cp {} app.jar

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/app.jar ./
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "app.jar"]
