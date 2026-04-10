# From backend/: ./mvnw -DskipTests package && docker build -t bikefinder-api .
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/bikefinder-api-*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
