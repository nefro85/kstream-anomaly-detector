package io.s7i.temp.domain;

import io.s7i.temp.model.TemperatureMeasurement;
import io.s7i.temp.util.KryoSerde;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

import static java.util.Objects.requireNonNull;

public class TempReadingProcessor implements Processor<String, TemperatureMeasurement, String, TemperatureMeasurement> {
    private final KryoSerde kryoSerde = new KryoSerde();
    private ProcessorContext<String, TemperatureMeasurement> context;
    private KeyValueStore<String, Bytes> readingsStore;

    @Override
    public void init(ProcessorContext<String, TemperatureMeasurement> context) {
        this.context = context;
        readingsStore = requireNonNull(context.getStateStore(AnomalyDetector.STATE_TEMP_READINGS));
    }

    @Override
    public void process(Record<String, TemperatureMeasurement> record) {
        var key = record.key();
        var tempReadings = fromStateOrNew(key);

        tempReadings.putIfNotAnomaly(record.value())
                .ifPresent(anomaly -> context.forward(record));
        updateState(key, tempReadings);
    }

    private void updateState(String key, TemperatureReadings tempReadings) {
        readingsStore.put(key, kryoSerde.from(tempReadings));
    }

    private TemperatureReadings fromStateOrNew(String key) {
        TemperatureReadings tempReadings;
        var rawReadings = readingsStore.get(key);
        if (rawReadings != null && rawReadings.get().length > 0) {
            tempReadings = kryoSerde.from(rawReadings);
        } else {
            tempReadings = new TemperatureReadings();
        }
        return tempReadings;
    }
}
