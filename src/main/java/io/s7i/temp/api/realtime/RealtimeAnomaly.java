package io.s7i.temp.api.realtime;

import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static io.s7i.temp.domain.AnomaliesPipeline.STORE_ANOMALIES;
import static io.s7i.temp.domain.AnomalyDetector.STATE_TEMP_READINGS;

@RestController
@RequestMapping("realtime")
@RequiredArgsConstructor
@Slf4j
public class RealtimeAnomaly {

    private final StreamsBuilderFactoryBean factoryBean;

    private KafkaStreams kafkaStreams() {
        return factoryBean.getKafkaStreams();
    }

    @GetMapping("anomalies")
    public Flux<TemperatureMeasurement> getAnomalies() {
        ReadOnlyKeyValueStore<String, TemperatureMeasurement> anomalies = kafkaStreams()
                .store(StoreQueryParameters.fromNameAndType(STORE_ANOMALIES, QueryableStoreTypes.keyValueStore()));

        log.info("anomalies size: {}", anomalies.approximateNumEntries());

        return Flux.fromStream(streamOf(anomalies.all()).map(kv -> kv.value));
    }

    @GetMapping("readings")
    public Flux<CurrentReadings> getCurrentReadings() {
        var state = kafkaStreams().store(StoreQueryParameters.fromNameAndType(
                STATE_TEMP_READINGS,
                QueryableStoreTypes.<String, TemperatureReadings>keyValueStore()
        ));

        return Flux.fromStream(streamOf(state.all()).map(this::from));
    }

    private CurrentReadings from(KeyValue<String, TemperatureReadings> kv) {
        var value = kv.value;
        var avg = value.getAggregated().stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0d);

        var detail = new CurrentReadings.TemperatureDetail(
                avg,
                value.getAggregated().toArray(new Double[0])
        );
        return new CurrentReadings(kv.key, detail);
    }

    private <E extends KeyValue<String, V>, V> Stream<E> streamOf(Iterator<E> iterator) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                        iterator,
                        Spliterator.ORDERED),
                false);
    }
}
