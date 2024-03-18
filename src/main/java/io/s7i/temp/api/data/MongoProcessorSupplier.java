package io.s7i.temp.api.data;

import io.s7i.temp.domain.event.AnomalyEventPublisher;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoProcessorSupplier implements ProcessorSupplier<String, TemperatureMeasurement, String, TemperatureMeasurement> {

    private final CrudRepository crudRepository;
    private final AnomalyEventPublisher publisher;

    @Override
    public Processor<String, TemperatureMeasurement, String, TemperatureMeasurement> get() {
        return new MongoProcessor(crudRepository, publisher);
    }
}
