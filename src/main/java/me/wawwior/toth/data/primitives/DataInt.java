package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public class DataInt extends DataElement {

    private final int value;

    public DataInt(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataInt> type() {
        return Type.INT_TYPE;
    }
}
