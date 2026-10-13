FROM openjdk:17
LABEL authors="itcast"

ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app
COPY wms-iot-simulate.jar app.jar

EXPOSE 18084

ENTRYPOINT ["java", "-jar", "/app/app.jar"]