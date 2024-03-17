package io.s7i.temp.api.realtime;

import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/realtime/anomalies")
@RequiredArgsConstructor
@Slf4j
public class RealtimeAnomaly {

    private final StreamsBuilderFactoryBean factoryBean;

    private KafkaStreams kafkaStreams() {
        return factoryBean.getKafkaStreams();
    }

    @GetMapping
    public List<TemperatureMeasurement> getAnomalies() {
        ReadOnlyKeyValueStore<String, TemperatureMeasurement> anomalies = kafkaStreams()
                .store(StoreQueryParameters.fromNameAndType("anomalies", QueryableStoreTypes.keyValueStore()));

        log.info("anomalies size: {}", anomalies.approximateNumEntries());

        var result = new ArrayList<TemperatureMeasurement>();
        for (KeyValueIterator<String, TemperatureMeasurement> it = anomalies.all(); it.hasNext(); ) {
            var a = it.next();

            result.add(a.value);
        }

        return result;
    }

}
