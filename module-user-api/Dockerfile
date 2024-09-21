FROM openjdk:17
ARG JAR_FILE=build/libs/module-user-api.jar
COPY ${JAR_FILE} app.jar
ENV TZ=Asia/Seoul
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "-Djasypt.encryptor.password=${JASYPT_PASSWORD}", "/app.jar"]