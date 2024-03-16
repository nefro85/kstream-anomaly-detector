FROM eclipse-temurin:17

ARG KAFKA_VERSION=3.7.0

ENV SW_KAFKA_GZ="https://www.apache.org/dyn/closer.cgi?action=download&path=/kafka/${KAFKA_VERSION}/kafka_2.12-${KAFKA_VERSION}.tgz" \
    SW_KAFDROP_JAR="https://github.com/obsidiandynamics/kafdrop/releases/download/4.0.1/kafdrop-4.0.1.jar" \
    APP_HOME=/opt/anomaly-detector \
    KAFKA_LOG_DIR=/data/kafka/kafka-logs \
    KAFKA_HOME=/opt/kafka \
    KAFKA_CFG=/opt/anomaly-detector/kafka-server.properties \
    KAFDROP_JVM_OPTS="-Dlogger.kafdrop.level=WARN -Dlogger.kafdrop_service.level=WARN -Xms32M -Xmx64M -XX:-TieredCompilation -XX:+UseStringDeduplication" \
    KAFDROP_ARGS=""

RUN set -ex; \
  echo install kafka; \
  mkdir -p /opt/kafka; \
  wget -nv -O /opt/kafka.gz "$SW_KAFKA_GZ"; \
  tar -xf /opt/kafka.gz -C /opt/kafka --strip-components=1; \
  rm /opt/kafka.gz;

RUN set -ex; \
  echo install kafdrop; \
  mkdir -p /opt/kafdrop; \
  wget -nv -O /opt/kafdrop/kafdrop.jar "$SW_KAFDROP_JAR";

RUN mkdir -p ${APP_HOME}
WORKDIR ${APP_HOME}

COPY entrypoint.sh ${APP_HOME}/entrypoint.sh
COPY kraft-server.proterties ${APP_HOME}/kafka-server.properties
COPY build/libs/app.jar ${APP_HOME}/app.jar

ENTRYPOINT ["sh", "/opt/anomaly-detector/entrypoint.sh"]

EXPOSE 7000
EXPOSE 9092
EXPOSE 8080
EXPOSE 5005
