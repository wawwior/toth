package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public class DataFloat extends DataElement {

    private final float value;

    public DataFloat(float value) {
        this.value = value;
    }

    public float value() {
        return value;
    }

    public static DataFloat read(DataReader reader) throws IOException {
        return new DataFloat(reader.readInt());
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataFloat> type() {
        return Type.FLOAT_TYPE;
    }
}
