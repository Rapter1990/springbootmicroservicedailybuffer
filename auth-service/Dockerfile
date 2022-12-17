FROM openjdk:11

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} authservice.jar

ENTRYPOINT ["java", "-jar", "/authservice.jar"]

EXPOSE 7777