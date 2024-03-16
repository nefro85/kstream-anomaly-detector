package io.s7i.temp.domain.window;

import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.model.TemperatureMeasurement;
import io.s7i.temp.util.KryoSerde;
import lombok.Getter;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Detector implements Serializable {

    public static Serde<Detector> DetectorSerde() {
        return new DetectorSerde();
    }

    public static class DetectorSerde implements Serde<Detector> {

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

    private final TemperatureReadings temperatureReadings = new TemperatureReadings();
    @Getter
    private final List<TemperatureMeasurement> anomalies = new ArrayList<>();

    Detector aggregate(TemperatureMeasurement measurement) {
        temperatureReadings.putIfNotAnomaly(measurement).ifPresent(anomalies::add);
        return this;
    }

}
