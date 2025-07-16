package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public class DataDouble extends DataElement {

    private final double value;

    public DataDouble(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }

    public static DataDouble read(DataReader reader) throws IOException {
        return new DataDouble(reader.readInt());
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataDouble> type() {
        return Type.DOUBLE_TYPE;
    }
}
