FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY target/*.jar app.jar
COPY ./src/main/java/com/example/tripadvisor/trained /trained
ENTRYPOINT ["java","-jar","/app.jar"]
EXPOSE 8080