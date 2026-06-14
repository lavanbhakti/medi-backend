FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "target/mediai-0.0.1-SNAPSHOT.jar"]