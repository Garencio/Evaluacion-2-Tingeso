FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} examen-service-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/examen-service-0.0.1-SNAPSHOT.jar"]