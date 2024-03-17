package io.s7i.temp.domain.window;

import io.s7i.temp.model.TemperatureMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.streams.processor.TimestampExtractor;

@Slf4j
public class TempTimestampExtractor implements TimestampExtractor {
    @Override
    public long extract(ConsumerRecord<Object, Object> record, long partitionTime) {
        var val = record.value();

        if (val instanceof TemperatureMeasurement m) {
            return m.timestamp();
        } else {
            log.warn("cannot extract timestamp form value: {}", val);
        }

        return partitionTime;
    }
}
