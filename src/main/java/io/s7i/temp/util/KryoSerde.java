package io.s7i.temp.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import io.s7i.temp.domain.TemperatureReadings;
import io.s7i.temp.domain.window.Detector;
import io.s7i.temp.model.TemperatureMeasurement;
import org.apache.kafka.common.utils.Bytes;

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
    }

    public TemperatureReadings from(Bytes bytes) {
        return kryo.readObject(new Input(bytes.get()), TemperatureReadings.class);
    }

    public Detector readDetector(byte[] bytes) {
        return kryo.readObject(new Input(bytes), Detector.class);
    }

    public byte[] writeDetector(Detector detector) {
        return writeObject(detector);
    }

    public Bytes from(TemperatureReadings readings) {
        return new Bytes(writeObject(readings));
    }

    private byte[] writeObject(Object readings) {
        var baos = new ByteArrayOutputStream();
        var output = new Output(baos);
        kryo.writeObject(output, readings);
        output.flush();
        return baos.toByteArray();
    }
}
