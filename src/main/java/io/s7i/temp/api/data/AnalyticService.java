package io.s7i.temp.api.data;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticService {

    public record ThermometerAggregation(String thermometerId, long count) {

    }

    private final MongoTemplate template;
    private long threshold;

    List<ThermometerAggregation> mostAnomalyDetectedThermometer() {
        var aggr = Aggregation.newAggregation(
                Aggregation.group("thermometerId").count().as("count"),
                Aggregation.match(Criteria.where("count").gt(threshold)),
                Aggregation.project("count").and("thermometerId").previousOperation()
        );

        var r = template.aggregate(aggr, "anomaly", ThermometerAggregation.class);

        return r.getMappedResults();

    }


}
