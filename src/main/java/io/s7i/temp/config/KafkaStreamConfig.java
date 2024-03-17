package io.s7i.temp.config;

import io.s7i.temp.domain.window.TempTimestampExtractor;
import io.s7i.temp.util.TemperatureMeasurementSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.util.HashMap;

import static org.apache.kafka.streams.StreamsConfig.*;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamConfig {

    @Value("${app.brokers}")
    private String brokers;

    @Value("${app.stateDir}")
    private String stateDir;
    @Value("${app.id}")
    private String appId;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    KafkaStreamsConfiguration streamsConfig() {
        var props = new HashMap<String, Object>();
        props.put(APPLICATION_ID_CONFIG, appId);
        props.put(BOOTSTRAP_SERVERS_CONFIG, brokers);
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, TemperatureMeasurementSerde.class.getName());
        props.put(STATE_DIR_CONFIG, stateDir);
        props.put(DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TempTimestampExtractor.class.getName());

        return new KafkaStreamsConfiguration(props);
    }
}