package io.s7i.temp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("app.anomaly")
public record AnomalyConfig(
        int meanSize,
        double deviationThreshold

) {


}
