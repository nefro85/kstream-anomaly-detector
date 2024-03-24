package io.s7i.temp.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnomalyEventListener implements ApplicationListener<AnomalyEvent> {
    private final Sinks.Many<AnomalyEvent> sink;

    @Override
    public void onApplicationEvent(AnomalyEvent event) {
        var result = sink.tryEmitNext(event);

        if (result.isFailure()) {
            log.warn("event emit failure");
        }
    }

    public Flux<AnomalyEvent> asFlux() {
        return sink.asFlux();
    }
}
