# Etapa 1: compilar con Maven
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY LetHimCook/pom.xml .
RUN mvn dependency:go-offline -q
COPY LetHimCook/src ./src
RUN mvn package -DskipTests -q

# Etapa 2: ejecutar el JAR en JRE ligero
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/LetHimCook-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9999
ENTRYPOINT ["java", "-jar", "app.jar"]
