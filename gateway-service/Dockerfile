# Use Java 21 runtime
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
# Expose application port
EXPOSE 8080
# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]