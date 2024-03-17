package io.s7i.temp.api.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record Anomaly(

        @Id
        String id,

        double temperature,
        long timestamp,
        String roomId,
        String thermometerId
) {
}
