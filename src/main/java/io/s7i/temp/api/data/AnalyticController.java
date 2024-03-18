package io.s7i.temp.api.data;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AnalyticController {

    private final AnalyticService service;

    @GetMapping("listMostAnomalyDetectedThermometer")
    List<AnalyticService.ThermometerAggregation> listMost() {
        return service.mostAnomalyDetectedThermometer();
    }
}
