package io.s7i.temp.domain;

import io.s7i.temp.config.StreamConfig;
import io.s7i.temp.model.TemperatureMeasurement;
import io.s7i.temp.util.TemperatureMeasurementSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("fixed")
public class AnomalyDetector {
    public static final String STATE_TEMP_READINGS = "temp-readings";
    private final TemperatureMeasurementSerde temperatureMeasurementSerde;
    private final StreamConfig streamConfig;
    private final KeyExtractor keyExtractor;

    @Autowired
    void build(StreamsBuilder builder) {
        var stream = builder.stream(streamConfig.getTempMeasurementsTopic(), Consumed.with(Serdes.String(), temperatureMeasurementSerde));
        stream.filter((k, v) -> Objects.nonNull(v))
                .map(keyExtractor::mapKeyValue)
                .process(provideProcessor())
                .to(streamConfig.getTempAnomalyTopic(), Produced.with(Serdes.String(), temperatureMeasurementSerde));
    }

    private ProcessorSupplier<String, TemperatureMeasurement, String, TemperatureMeasurement> provideProcessor() {
        return new ProcessorSupplier<>() {
            @Override
            public Processor<String, TemperatureMeasurement, String, TemperatureMeasurement> get() {
                return new TempReadingProcessor();
            }

            @Override
            public Set<StoreBuilder<?>> stores() {
                var tempReading = Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(STATE_TEMP_READINGS),
                        Serdes.String(), Serdes.Bytes()
                );
                return Set.of(tempReading);
            }
        };
    }
}
