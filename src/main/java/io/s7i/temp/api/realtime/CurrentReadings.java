package io.s7i.temp.api.realtime;

public record CurrentReadings(
        String index,
        TemperatureDetail detail
) {
    public record TemperatureDetail(
            Double avg,
            Double[] data
    ) {

    }
}
