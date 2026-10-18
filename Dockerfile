FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw -q dependency:go-offline
COPY src src
RUN ./mvnw -q package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --no-create-home app
COPY --from=build /app/target/backend-*.jar app.jar
USER app
EXPOSE 8081
# rede Docker da VPS nao tem IPv6; sem isso o R2 (que resolve IPv6 primeiro) falha com "Network is unreachable"
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-Djava.net.preferIPv4Stack=true", "-jar", "app.jar"]
