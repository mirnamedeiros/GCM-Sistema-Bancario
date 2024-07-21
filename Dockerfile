FROM openjdk:17-jdk-alpine

LABEL maintainer="mirnagmedeiros@gmail.com"

EXPOSE 8080

RUN ls -la

COPY ./target/*.jar gcm-sistema-bancario.jar

RUN ls -la

ENTRYPOINT ["java","-jar","/gcm-sistema-bancario.jar"]
