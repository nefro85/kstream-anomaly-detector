package io.s7i.temp.util;

import io.s7i.temp.domain.window.Detector;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

public class DetectorSerde implements Serde<Detector> {

    private final KryoSerde kryoSerde = new KryoSerde();

    @Override
    public Serializer<Detector> serializer() {
        return ((topic, data) -> kryoSerde.writeDetector(data));
    }

    @Override
    public Deserializer<Detector> deserializer() {
        return (topic, data) -> kryoSerde.readDetector(data);
    }
}
