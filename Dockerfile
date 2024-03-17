FROM eclipse-temurin:17

ENV APP_HOME=/opt/anomaly-detector


RUN mkdir -p ${APP_HOME}
WORKDIR ${APP_HOME}

COPY entrypoint.sh ${APP_HOME}/entrypoint.sh
COPY build/libs/app.jar ${APP_HOME}/app.jar

ENTRYPOINT ["sh", "/opt/anomaly-detector/entrypoint.sh"]

EXPOSE 8080
EXPOSE 5005
