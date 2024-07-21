FROM openjdk:17-jdk-alpine

LABEL maintainer="mirnagmedeiros@gmail.com"

EXPOSE 8080

COPY ./target/GCM-Sistema-Bancario-0.0.1-SNAPSHOT.jar gcm-sistema-bancario.jar

ENTRYPOINT ["java","-jar","/gcm-sistema-bancario.jar"]
