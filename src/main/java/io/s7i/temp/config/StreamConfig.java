package io.s7i.temp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties("app.stream")
@Data
public class StreamConfig {
    String tempMeasurementsTopic;

    String tempAnomalyTopic;

    String keyType;

    private Duration windowSize;
}
