package io.s7i.temp.domain;

import io.s7i.temp.model.TemperatureMeasurement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Optional;

@Data
@AllArgsConstructor
@Slf4j
public class TemperatureReadings implements Serializable {

    public interface AnomalyDetection {
        void onAnomalyDetection(TemperatureMeasurement measurement);
    }

    private static final int AVG_THRESHOLD = 9;
    private static final double DV_LIMIT = 5d;

    public TemperatureReadings() {
        this(new LinkedList<>());
    }

    private LinkedList<Double> readings;

    public Optional<TemperatureMeasurement> putIfNotAnomaly(TemperatureMeasurement value) {
        var temp = value.temperature();

        if (readings.size() < AVG_THRESHOLD) {
            readings.push(temp);
        } else {
            double avg = readings.stream()
                    .mapToDouble(Double::doubleValue)
                    .average().orElseThrow();
            if (Math.abs(avg - temp) >= DV_LIMIT) {
                log.info("[DETECTOR] Detected anomaly, avg: {}, tempValue: {},diff: {}, limit: {}",
                        avg, temp, Math.abs(avg - temp), DV_LIMIT);
                return Optional.of(value);
            } else {
                readings.pollFirst();
                readings.push(temp);
            }
        }
        return Optional.empty();
    }
}
