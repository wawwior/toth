package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.DataMap;

import java.io.IOException;

public final class DataString extends DataElement {

    private final String value;

    public DataString(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static DataString read(DataReader reader) throws IOException {
        return new DataString(reader.readString());
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
