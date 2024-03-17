package io.s7i.temp.domain;

public interface FloatAssert {
    double EPSILON = 0.0000000000001;

    default boolean floatEquals(double expected, double value) {
        return Math.abs(value - expected) < EPSILON;
    }
}
