FROM openjdk:17-slim
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
COPY data.db /data.db
COPY templates /templates
EXPOSE 80
ENTRYPOINT ["java","-jar","/app.jar"]