package io.s7i.temp.domain;

import io.s7i.temp.config.StreamConfig;
import io.s7i.temp.domain.window.TempTimestampExtractor;
import io.s7i.temp.model.TemperatureMeasurement;
import io.s7i.temp.util.TemperatureMeasurementSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;
import java.util.stream.DoubleStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnomalyDetectorTest implements FloatAssert {


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

        var keyExtractor = new KeyExtractor(streamConfig);

        var sut = new AnomalyDetector(temSerde, streamConfig, keyExtractor); //SYSTEM UNDER TEST

        var streamsBuilder = new StreamsBuilder();
        sut.build(streamsBuilder);

        var topology = streamsBuilder.build();

        Properties props = new Properties();
        var stringSerde = Serdes.String();

        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, stringSerde.getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, temSerde.getClass().getName());
        props.setProperty(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TempTimestampExtractor.class.getName());

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
        //given
        DoubleStream.of(
                        20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 27.1, 23.1
                )
                .mapToObj(temp -> TemperatureMeasurement.builder()
                        .temperature(temp)
                        .roomId("room-1")
                        .thermometerId("thermometer-01")
                        .timestamp(System.currentTimeMillis())
                        .build())
                .forEach(inputTopic::pipeInput); //when


        //then
        var anomaly = outputTopic.readValue();

        assertNotNull(anomaly);
        assertTrue(floatEquals(27.1, anomaly.temperature()));
    }

    @Test
    void should_detect_nothing() {
        //given
        DoubleStream.of(
                        20.1, 21.2, 20.3, 19.1, 20.1, 19.2, 20.1, 18.1, 19.4, 20.1, 22.1, 23.1
                )
                .mapToObj(temp -> TemperatureMeasurement.builder()
                        .temperature(temp)
                        .roomId("room-1")
                        .thermometerId("thermometer-01")
                        .timestamp(System.currentTimeMillis())
                        .build())
                .forEach(inputTopic::pipeInput); //when


        //then
        assertTrue(outputTopic.isEmpty());
    }

}