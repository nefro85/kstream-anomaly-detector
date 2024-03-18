package io.s7i.temp.domain.calculator;

import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.function.Consumer;

public interface AnomalyCalculator {

    @AllArgsConstructor
    @NoArgsConstructor
    final class AnomalyDetection {
        private static final AnomalyDetection NONE = new AnomalyDetection();
        private TemperatureMeasurement anomaly;

        public static AnomalyDetection empty() {
            return NONE;
        }

        public void whenAnomaly(Consumer<TemperatureMeasurement> action) {
            if (anomaly != null) {
                action.accept(anomaly);
            }
        }
    }

    AnomalyDetection calcAnomaly(TemperatureMeasurement measurement, TemperatureReadings readings);
}
