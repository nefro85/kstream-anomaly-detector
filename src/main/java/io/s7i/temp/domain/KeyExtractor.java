package io.s7i.temp.domain;

import io.s7i.temp.config.StreamConfig;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.KeyValue;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeyExtractor {

    private final StreamConfig streamConfig;

    public KeyValue<String, TemperatureMeasurement> mapKeyValue(String key, TemperatureMeasurement value) {
        return KeyValue.pair(extractKey(value), value);
    }

    private String extractKey(TemperatureMeasurement value) {
        switch (streamConfig.getKeyType()) {
            case "room" -> {
                return value.roomId();
            }
            case "dev", "thermometer" -> {
                return value.thermometerId();
            }
            case "both" -> {
                return value.roomId() + "_" + value.thermometerId();
            }
            default -> {
                return "static";
            }
        }
    }
}
