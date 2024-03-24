package io.s7i.temp.config;

import io.s7i.temp.domain.event.AnomalyEvent;
import io.s7i.temp.util.TemperatureMeasurementSerde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class CoreConfiguration {
    @Bean
    TemperatureMeasurementSerde temperatureMeasurementSerde() {
        return new TemperatureMeasurementSerde();
    }

    @Bean
    Sinks.Many<AnomalyEvent> sink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }
}
