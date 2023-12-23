FROM ecplipse-terminus:17-jdk-alpine
RUN apk add curl
VOLUME /tmp
EXPOSE 8080
ADD target/api-gastos.jar api-gastos.jar
ENTRYPOINT ["java","-jar","/api-gastos.jar"]
LABEL authors="Tiago"