package io.s7i.temp.config;

import io.s7i.temp.util.TemperatureMeasurementSerde;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfiguration {
    @Bean
    TemperatureMeasurementSerde temperatureMeasurementSerde() {
        return new TemperatureMeasurementSerde();
    }

}
