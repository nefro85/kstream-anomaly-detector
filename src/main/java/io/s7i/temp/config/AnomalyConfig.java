package io.s7i.temp.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;


@Configuration
@ConfigurationProperties("app.anomaly")
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyConfig implements Serializable {
    int meanSize;
    double deviationThreshold;
    int avgThreshold;
}
