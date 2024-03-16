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
    public static final String READINGS = "readings";
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
        var tempReadings = fromStateOrNew();

        tempReadings.putIfNotAnomaly(record.value())
                .ifPresent(anomaly -> context.forward(record));


        updateState(tempReadings);

    }

    private void updateState(TemperatureReadings tempReadings) {
        readingsStore.put(READINGS, kryoSerde.from(tempReadings));
    }

    private TemperatureReadings fromStateOrNew() {
        TemperatureReadings tempReadings;
        var rawReadings = readingsStore.get(READINGS);
        if (rawReadings != null && rawReadings.get().length > 0) {
            tempReadings = kryoSerde.from(rawReadings);
        } else {
            tempReadings = new TemperatureReadings();
        }
        return tempReadings;
    }
}
