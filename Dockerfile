FROM openjdk:17-jdk-alpine

LABEL maintainer="mirnagmedeiros@gmail.com"

EXPOSE 8080

COPY target/*.jar gcm-sistema-bancario.jar

ENTRYPOINT ["java","-jar","/gcm-sistema-bancario.jar"]
