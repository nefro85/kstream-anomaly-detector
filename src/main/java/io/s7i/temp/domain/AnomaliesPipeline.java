package io.s7i.temp.domain;

import io.s7i.temp.api.data.MongoProcessorSupplier;
import io.s7i.temp.util.TemperatureMeasurementSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@RequiredArgsConstructor
@Slf4j
public class AnomaliesPipeline {
    private final TemperatureMeasurementSerde temperatureMeasurementSerde;
    private final MongoProcessorSupplier processorSupplier;
    @Value("${app.tempAnomalyTopic}")
    String tempAnomalyTopic;


    @Autowired
    void build(StreamsBuilder builder) {
        var table = builder.stream(tempAnomalyTopic, Consumed.with(Serdes.String(), temperatureMeasurementSerde))
                .map((key, value) -> KeyValue.pair(UUID.randomUUID().toString(), value))
                .peek(((key, value) -> log.info("reading-anomaly: {}", value)))
                .process(processorSupplier)
                .toTable(Named.as("anomalies"), Materialized.as("anomalies"));

        log.info("queryable name: {}", table.queryableStoreName());
    }
}
