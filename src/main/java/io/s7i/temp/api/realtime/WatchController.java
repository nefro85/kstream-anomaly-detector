package io.s7i.temp.api.realtime;

import io.s7i.temp.config.RuntimeAnomalyEvents;
import io.s7i.temp.domain.event.AnomalyEvent;
import io.s7i.temp.domain.event.AnomalyEventListener;
import io.s7i.temp.model.TemperatureMeasurement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RuntimeAnomalyEvents
public class WatchController {

    private final AnomalyEventListener listener;

    @GetMapping(value = "/watch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<TemperatureMeasurement> watchEvents() {
        return Flux.from(listener.asFlux()).map(AnomalyEvent::getAnomaly).log();
    }

    @GetMapping(value = "/tick", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> tick() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(sequence -> "tick=" + sequence);
    }
}
