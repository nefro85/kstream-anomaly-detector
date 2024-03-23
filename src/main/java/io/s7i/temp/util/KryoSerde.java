package io.s7i.temp.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.s7i.temp.config.AnomalyConfig;
import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.domain.calculator.MeanCalculator;
import io.s7i.temp.domain.window.Detector;
import io.s7i.temp.model.TemperatureMeasurement;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

public class KryoSerde {
    private final Kryo kryo = new Kryo();

    public KryoSerde() {
        kryo.register(TemperatureReadings.class);
        kryo.register(LinkedList.class);
        kryo.register(Detector.class);
        kryo.register(ArrayList.class);
        kryo.register(TemperatureMeasurement.class);
        kryo.register(MeanCalculator.class);
        kryo.register(AnomalyConfig.class);
    }

    public Detector readDetector(byte[] bytes) {
        return readObject(Detector.class, bytes);
    }

    public byte[] writeDetector(Detector detector) {
        return writeObject(detector);
    }

    public byte[] writeReadings(TemperatureReadings data) {
        return writeObject(data);
    }

    public TemperatureReadings readReadings(byte[] bytes) {
        return readObject(TemperatureReadings.class, bytes);
    }

    private byte[] writeObject(Object readings) {
        var baos = new ByteArrayOutputStream();
        var output = new Output(baos);
        kryo.writeObject(output, readings);
        output.flush();
        return baos.toByteArray();
    }

    private <T> T readObject(Class<T> type, byte[] data) {
        return kryo.readObject(new Input(data), type);
    }
}
