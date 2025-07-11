package me.wawwior.toth.data.primitives;

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

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataDouble> type() {
        return Type.DOUBLE_TYPE;
    }
}
