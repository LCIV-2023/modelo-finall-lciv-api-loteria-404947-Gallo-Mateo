#FROM ubuntu:latest
#LABEL authors="mateo"
#
#ENTRYPOINT ["top", "-b"]

FROM openjdk:17-jdk-alpine
COPY ./target/*.jar loteria-app.jar
ENTRYPOINT ["java","-jar","loteria-app.jar"]