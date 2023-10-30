FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} cuota-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/cuota-service-0.0.1-SNAPSHOT.jar"]