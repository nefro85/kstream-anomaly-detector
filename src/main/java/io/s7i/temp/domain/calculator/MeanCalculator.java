package io.s7i.temp.domain.calculator;

import io.s7i.temp.config.AnomalyConfig;
import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Setter
@Component
@Slf4j
@ConditionalOnProperty(value = "app.anomaly.algName", havingValue = "alg2")
public class MeanCalculator implements AnomalyCalculator, Serializable {
    private AnomalyConfig anomalyConfig;

    @Override
    public AnomalyDetection calcAnomaly(TemperatureMeasurement measurement, TemperatureReadings data) {
        var readings = data.getAggregated();
        var temp = measurement.temperature();

        if (!readings.isEmpty()) {
            double avg = readings.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElseThrow(() -> new IllegalStateException("cannot compute avg"));

            double diff = Math.abs(avg - temp);
            double deviationThreshold = anomalyConfig.getDeviationThreshold();

            if (diff >= deviationThreshold) {
                log.info("[DETECTOR] Detected anomaly, avg: {}, tempValue: {}, diff: {}, limit: {}",
                        avg, temp, diff, deviationThreshold);
                return new AnomalyDetection(measurement);
            }
        }
        readings.push(temp);

        return AnomalyDetection.empty();
    }
}
