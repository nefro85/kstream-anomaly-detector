package io.s7i.temp.domain.window;

import io.s7i.temp.model.TemperatureMeasurement;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

public class DetectedAnomalyProcessor implements Processor<Windowed<String>, Detector, String, TemperatureMeasurement> {

    private ProcessorContext<String, TemperatureMeasurement> context;

    @Override
    public void init(ProcessorContext<String, TemperatureMeasurement> context) {
        this.context = context;
    }

    @Override
    public void process(Record<Windowed<String>, Detector> record) {
        for (var anomaly : record.value().getAnomalies()) {
            context.forward(new Record<>(null, anomaly, System.currentTimeMillis()));
        }
    }
}
