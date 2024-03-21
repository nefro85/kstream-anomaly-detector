package io.s7i.temp.domain.window;

import io.s7i.temp.config.AnomalyConfig;
import io.s7i.temp.config.StreamConfig;
import io.s7i.temp.domain.KeyExtractor;
import io.s7i.temp.domain.calculator.MeanCalculator;
import io.s7i.temp.model.TemperatureMeasurement;
import io.s7i.temp.util.TemperatureMeasurementSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnomalyDetectorWindowedTest {


    private TopologyTestDriver testDriver;
    private TestInputTopic<String, TemperatureMeasurement> inputTopic;
    private TestOutputTopic<String, TemperatureMeasurement> outputTopic;


    @BeforeEach
    void setupTest() {
        //setup
        var temSerde = new TemperatureMeasurementSerde();
        var streamConfig = new StreamConfig();
        streamConfig.setKeyType("room");
        streamConfig.setTempMeasurementsTopic("temp");
        streamConfig.setTempAnomalyTopic("anomaly");
        streamConfig.setWindowSize(Duration.parse("PT10S"));

        var keyExtractor = new KeyExtractor(streamConfig);

        var anomalyConfig = new AnomalyConfig();
        anomalyConfig.setDeviationThreshold(5d);

        var calc = new MeanCalculator();
        calc.setAnomalyConfig(anomalyConfig);

        var sut = new AnomalyDetectorWindowed(temSerde, streamConfig, keyExtractor, calc); //SYSTEM UNDER TEST

        var streamsBuilder = new StreamsBuilder();
        sut.build(streamsBuilder);

        var topology = streamsBuilder.build();

        Properties props = new Properties();
        var stringSerde = Serdes.String();

        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, stringSerde.getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, temSerde.getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TempTimestampExtractor.class.getName());
        //props.setProperty(StreamsConfig.STATE_DIR_CONFIG, "C:\\temp\\kafka-streams");

        testDriver = new TopologyTestDriver(topology, props);
        inputTopic = testDriver.createInputTopic(streamConfig.getTempMeasurementsTopic(), stringSerde.serializer(), temSerde.serializer());
        outputTopic = testDriver.createOutputTopic(streamConfig.getTempAnomalyTopic(), stringSerde.deserializer(), temSerde.deserializer());
    }

    @AfterEach
    void cleanSetup() {
        testDriver.close();
    }

    @Test
    void should_detect_anomaly() {
        String[][] rawData = { // incorrect data ?
                {"19.1", "1684945005"},
                {"19.2", "1684945006"},
                {"19.5", "1684945007"},
                {"19.7", "1684945008"},
                {"19.3", "1684945009"},
                {"24.1", "1684945010"},
                {"18.2", "1684945011"},
                {"19.1", "1684945012"},
                {"19.2", "1684945013"},
                {"23.4", "1684945015"}
        };

        var measurements = Arrays.stream(rawData)
                .map(d -> TemperatureMeasurement.builder()
                        .temperature(Double.parseDouble(d[0]))
                        .timestamp(Long.parseLong(d[1]))
                        .roomId("room-A")
                        .thermometerId("dev-A")
                        .build()
                ).toList();

        measurements.forEach(inputTopic::pipeInput);
        var anomalies = outputTopic.readKeyValuesToList();

        assertTrue(anomalies.isEmpty());
    }

    @Test
    void should_detect_anomaly_2() {
        String[][] rawData = {
                {"19.1", "1684945005"},
                {"19.2", "1684945006"},
                {"19.5", "1684945007"},
                {"19.7", "1684945008"},
                {"19.3", "1684945009"},
                {"27.1", "1684945010"},
                {"18.2", "1684945011"},
                {"19.1", "1684945012"},
                {"19.2", "1684945013"},
                {"26.4", "1684945015"},
                {"19.2", "1684945013"},
                {"19.2", "1684945013"},
                {"35.2", "1684945013"},
                {"19.2", "1684945013"},
        };

        var measurements = Arrays.stream(rawData)
                .map(d -> TemperatureMeasurement.builder()
                        .temperature(Double.parseDouble(d[0]))
                        .timestamp(System.currentTimeMillis())
                        .roomId("room-A")
                        .thermometerId("dev-A")
                        .build()
                ).toList();

        measurements.forEach(inputTopic::pipeInput);
        var anomalies = outputTopic.readKeyValuesToList();

        assertFalse(anomalies.isEmpty());
    }
}