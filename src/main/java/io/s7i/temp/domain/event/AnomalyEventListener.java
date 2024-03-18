package io.s7i.temp.domain.event;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AnomalyEventListener implements ApplicationListener<AnomalyEvent>, Publisher<AnomalyEvent> {

    private Subscriber<? super AnomalyEvent> subscriber;

    @Override
    public void onApplicationEvent(AnomalyEvent event) {
        if (subscriber != null) {
            subscriber.onNext(event);
        } else {
            log.warn("event received but there was no subscriber");
        }
    }

    @Override
    public void subscribe(Subscriber<? super AnomalyEvent> sub) {
        subscriber = sub;
        log.info("new subscribe: {}", sub);
    }
}
