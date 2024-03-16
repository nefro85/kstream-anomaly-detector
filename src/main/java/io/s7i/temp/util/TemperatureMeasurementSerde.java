package io.s7i.temp.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

@Slf4j
public class TemperatureMeasurementSerde implements Serde<TemperatureMeasurement> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Serializer<TemperatureMeasurement> serializer() {
        return this::serialize;
    }

    @Override
    public Deserializer<TemperatureMeasurement> deserializer() {
        return this::deserialize;
    }

    @SneakyThrows
    private byte[] serialize(String topic, TemperatureMeasurement measurement) {
        return objectMapper.writeValueAsBytes(measurement);
    }


    private TemperatureMeasurement deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, TemperatureMeasurement.class);
        } catch (IOException e) {
            log.error("corrupted temperature measurement", e);
            return null;
        }
    }
}
