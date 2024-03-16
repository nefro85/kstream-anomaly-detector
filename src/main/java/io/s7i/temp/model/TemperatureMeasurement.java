package io.s7i.temp.model;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record TemperatureMeasurement(
        double temperature,
        long timestamp,
        String roomId,
        String thermometerId
) implements Serializable {
}
