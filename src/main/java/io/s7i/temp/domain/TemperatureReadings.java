package io.s7i.temp.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.LinkedList;

@Data
@AllArgsConstructor
@Slf4j
public class TemperatureReadings implements Serializable {
    public TemperatureReadings() {
        this(new LinkedList<>());
    }

    private LinkedList<Double> aggregated;
}
