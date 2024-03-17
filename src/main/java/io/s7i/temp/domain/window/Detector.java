package io.s7i.temp.domain.window;

import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Detector implements Serializable {


    private final TemperatureReadings temperatureReadings = new TemperatureReadings();
    @Getter
    private final List<TemperatureMeasurement> anomalies = new ArrayList<>();

    Detector aggregate(TemperatureMeasurement measurement) {
        temperatureReadings.putIfNotAnomaly(measurement).ifPresent(anomalies::add);
        return this;
    }

}
