package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public class DataString extends DataElement {

    private final String value;

    public DataString(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataString> type() {
        return Type.STRING_TYPE;
    }
}
