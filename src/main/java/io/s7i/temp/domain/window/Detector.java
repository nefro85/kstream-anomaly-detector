package io.s7i.temp.domain.window;

import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.domain.calculator.AnomalyCalculator;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class Detector implements Serializable {
    public Detector() {

    }

    public Detector(AnomalyCalculator calculator) {
        this.calculator = calculator;
    }

    private AnomalyCalculator calculator;
    private final TemperatureReadings temperatureReadings = new TemperatureReadings();
    @Getter
    private final List<TemperatureMeasurement> anomalies = new ArrayList<>();

    Detector aggregate(TemperatureMeasurement measurement) {
        requireNonNull(calculator, "missing calculator");
        calculator.calcAnomaly(measurement, temperatureReadings).whenAnomaly(anomalies::add);
        return this;
    }

}
