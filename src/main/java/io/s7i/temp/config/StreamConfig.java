package io.s7i.temp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.stream")
@Data
public class StreamConfig {
    String tempMeasurementsTopic;

    String tempAnomalyTopic;

    String keyType;
}
