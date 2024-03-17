#!/bin/bash
set -ex

info() {
    echo Kafka Stack Container

    echo KAFKA_LOG_DIR ${KAFKA_LOG_DIR}
}

run_main() {
    info
    start_kraft_kafka&
    sleep 5
    create_topic temp.measurements
    create_topic temp.anomaly
    run_kafdrop
}

create_topic() {
    local TOPIC_NAME=$1
    local KAFKA=localhost:9092

    ${KAFKA_HOME}/bin/kafka-topics.sh --create \
        --if-not-exists \
        --bootstrap-server ${KAFKA} \
        --topic ${TOPIC_NAME} \
        --replication-factor 1 \
        --partitions 1 \
        --config "retention.ms=$((15 * 60 * 1000))"
}


init_log_dir() {
  mkdir -p ${LOGS_DIR}

  echo "Logdir: ${KAFKA_LOG_DIR}"

  if [ ! -e ${KAFKA_LOG_DIR} ]; then
      mkdir -p "${KAFKA_LOG_DIR}"
      CLUSTER_UID=$(${KAFKA_HOME}/bin/kafka-storage.sh random-uuid)
      echo "New Cluster UID: ${CLUSTER_UID}"
      ${KAFKA_HOME}/bin/kafka-storage.sh format -t ${CLUSTER_UID} -c ${KAFKA_CFG}
  fi
}

start_kraft_kafka() {
    echo "Starting Kafka (Kraft) with configuration: ${KAFKA_CFG}"
    init_log_dir

    ${KAFKA_HOME}/bin/kafka-server-start.sh ${KAFKA_CFG}

}

run_kafdrop() {
    java --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
    -Dserver.port=7000 -Dmanagement.server.port=7000 ${KAFDROP_JVM_OPTS}\
    -jar /opt/kafdrop/kafdrop.jar \
    --kafka.brokerConnect=localhost:9092 ${KAFDROP_ARGS} > ${LOGS_DIR}/kafdrop.log
}
run_main
