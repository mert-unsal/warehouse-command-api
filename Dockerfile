# Multi-stage build for warehouse-command-api
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Enable preview features during compilation if needed
ENV MAVEN_OPTS="--enable-preview"
RUN mvn -q -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
# Default to enabling preview; allow override via JAVA_OPTS
ENV JAVA_OPTS="--enable-preview"
EXPOSE 8082
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
