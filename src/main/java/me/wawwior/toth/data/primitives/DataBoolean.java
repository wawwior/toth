package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.DataMap;

import java.io.IOException;

public final class DataBoolean extends DataElement {

    private final boolean value;

    public DataBoolean(boolean value) {
        this.value = value;
    }

    public boolean value() {
        return value;
    }

    public static DataBoolean read(DataReader reader) throws IOException {
        return new DataBoolean(reader.readBoolean());
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
