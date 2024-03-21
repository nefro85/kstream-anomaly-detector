package io.s7i.temp.domain.window;


import io.s7i.temp.config.StreamConfig;
import io.s7i.temp.domain.KeyExtractor;
import io.s7i.temp.domain.calculator.AnomalyCalculator;
import io.s7i.temp.util.DetectorSerde;
import io.s7i.temp.util.TemperatureMeasurementSerde;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.anomaly.algName", havingValue = "alg2")
@Slf4j
public class AnomalyDetectorWindowed {
    private final TemperatureMeasurementSerde temperatureMeasurementSerde;
    private final StreamConfig streamConfig;
    private final KeyExtractor keyExtractor;
    private final AnomalyCalculator anomalyCalculator;

    @Autowired
    void build(StreamsBuilder builder) {
        var windowSize = streamConfig.getWindowSize();
        log.info("building stream with window size: {}", windowSize);

        var stream = builder.stream(streamConfig.getTempMeasurementsTopic(), Consumed.with(Serdes.String(), temperatureMeasurementSerde));
        stream.filter((k, v) -> Objects.nonNull(v))
                .map(keyExtractor::mapKeyValue)
                .groupByKey()
                .windowedBy(TimeWindows.ofSizeWithNoGrace(windowSize))
                .aggregate(
                        () -> new Detector(anomalyCalculator),
                        (key, value, detector) -> detector.aggregate(value),
                        Materialized.with(Serdes.String(), new DetectorSerde())
                ).toStream()
                .process(DetectedAnomalyProcessor::new)
                .peek(((key, value) -> log.info("publishing anomaly: {}", value)))
                .to(streamConfig.getTempAnomalyTopic(), Produced.with(Serdes.String(), temperatureMeasurementSerde));
    }
}
