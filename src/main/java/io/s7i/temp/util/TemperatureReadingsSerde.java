package io.s7i.temp.util;

import io.s7i.temp.domain.TemperatureReadings;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class TemperatureReadingsSerde implements Serde<TemperatureReadings> {
    private final KryoSerde kryoSerde = new KryoSerde();

    @Override
    public Serializer<TemperatureReadings> serializer() {
        return (topic, data) -> kryoSerde.writeReadings(data);
    }

    @Override
    public Deserializer<TemperatureReadings> deserializer() {
        return (topic, data) -> kryoSerde.readReadings(data);
    }
}
