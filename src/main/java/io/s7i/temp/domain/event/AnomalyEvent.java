package io.s7i.temp.domain.event;

import io.s7i.temp.model.TemperatureMeasurement;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AnomalyEvent extends ApplicationEvent {
    private final TemperatureMeasurement anomaly;

    public AnomalyEvent(Object source, TemperatureMeasurement anomaly) {
        super(source);
        this.anomaly = anomaly;
    }
}
