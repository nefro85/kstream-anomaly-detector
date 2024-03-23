package io.s7i.temp.domain;

import io.s7i.temp.domain.calculator.AnomalyCalculator;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class TempReadingProcessor implements Processor<String, TemperatureMeasurement, String, TemperatureMeasurement> {
    private final AnomalyCalculator anomalyCalculator;
    private ProcessorContext<String, TemperatureMeasurement> context;
    private KeyValueStore<String, TemperatureReadings> readingsStore;

    @Override
    public void init(ProcessorContext<String, TemperatureMeasurement> context) {
        this.context = context;
        readingsStore = requireNonNull(context.getStateStore(AnomalyDetector.STATE_TEMP_READINGS));
    }

    @Override
    public void process(Record<String, TemperatureMeasurement> record) {
        var key = record.key();
        var tempReadings = fromStateOrNew(key);

        anomalyCalculator.calcAnomaly(record.value(), tempReadings)
                .whenAnomaly(anomaly -> context.forward(record));

        updateState(key, tempReadings);
    }

    private void updateState(String key, TemperatureReadings tempReadings) {
        readingsStore.put(key, tempReadings);
    }

    private TemperatureReadings fromStateOrNew(String key) {
        TemperatureReadings tempReadings = readingsStore.get(key);
        if (tempReadings == null) {
            tempReadings = new TemperatureReadings();
        }
        return tempReadings;
    }
}
