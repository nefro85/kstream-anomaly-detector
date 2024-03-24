package io.s7i.temp.domain.event;

import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnomalyEventPublisher {
    private final ApplicationEventPublisher publisher;

    public void publishAnomaly(TemperatureMeasurement measurement) {
        var event = new AnomalyEvent(this, measurement);
        publisher.publishEvent(event);
    }
}