FROM openjdk:8
COPY target/weatherapp-0.0.1-SNAPSHOT.jar /weather-service.jar
EXPOSE 28082/tcp
ENTRYPOINT ["java", "-jar", "/weather-service.jar"]