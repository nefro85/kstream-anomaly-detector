package io.s7i.temp.domain.calculator;

import io.s7i.temp.config.AnomalyConfig;
import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static io.s7i.temp.TempApplication.ALG_CFG;
import static io.s7i.temp.TempApplication.ALG_NAME_B;
import static java.util.Objects.requireNonNull;

@Component
@Slf4j
@ConditionalOnProperty(value = ALG_CFG, havingValue =  ALG_NAME_B)
public class MeanCalculator implements AnomalyCalculator, Serializable {
    private AnomalyConfig anomalyConfig;

    @Autowired
    public void setAnomalyConfig(AnomalyConfig anomalyConfig) {
        this.anomalyConfig = anomalyConfig.toBuilder().build();//avoid generate class code for correct serialization
    }

    public AnomalyConfig getAnomalyConfig() {
        return anomalyConfig;
    }

    @Override
    public AnomalyDetection calcAnomaly(TemperatureMeasurement measurement, TemperatureReadings data) {
        requireNonNull(anomalyConfig, "config required");

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
