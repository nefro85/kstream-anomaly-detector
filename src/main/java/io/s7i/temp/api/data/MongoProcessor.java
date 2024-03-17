package io.s7i.temp.api.data;

import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;


@RequiredArgsConstructor
@Slf4j
public class MongoProcessor implements Processor<String, TemperatureMeasurement, String, TemperatureMeasurement> {

    ProcessorContext<String, TemperatureMeasurement> context;

    private final CrudRepository crudRepository;

    @Override
    public void init(ProcessorContext<String, TemperatureMeasurement> context) {
        this.context = context;
    }

    @Override
    public void process(Record<String, TemperatureMeasurement> record) {
        var measurement = record.value();
        var temp = measurement.temperature();
        var ts = measurement.timestamp();
        var roomId = measurement.roomId();
        var thermometer = measurement.thermometerId();

        var anomaly = new Anomaly(null, temp, ts, roomId, thermometer);
        crudRepository.insert(anomaly);
        context.forward(record);
    }
}
