package io.s7i.temp.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(value = "app.anomaly.runtimeEvents", havingValue = "on")
public @interface RuntimeAnomalyEvents {
}
