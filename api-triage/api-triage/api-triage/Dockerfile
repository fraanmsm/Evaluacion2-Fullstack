FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=remote", "-jar", "app.jar"]