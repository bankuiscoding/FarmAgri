FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN mvn -B -DskipTests dependency:go-offline
COPY src src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S farmagri && adduser -S farmagri -G farmagri
COPY --from=build /app/target/*.jar app.jar
COPY application.properties application.properties
RUN mkdir -p /app/uploads && chown -R farmagri:farmagri /app
USER farmagri
EXPOSE 8085
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 CMD wget -qO- http://localhost:8085/actuator/health || exit 1
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
