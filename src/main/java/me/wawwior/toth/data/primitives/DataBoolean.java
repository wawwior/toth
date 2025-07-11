package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public class DataBoolean extends DataElement {

    private final boolean value;

    public DataBoolean(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataBoolean> type() {
        return Type.BOOLEAN_TYPE;
    }
}
