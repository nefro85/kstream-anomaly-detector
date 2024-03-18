package io.s7i.temp.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;


@Configuration
@ConfigurationProperties("app.anomaly")
@Data
public class AnomalyConfig implements Serializable {
    int meanSize;
    double deviationThreshold;
    int avgThreshold;
}
