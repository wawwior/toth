package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public class DataLong extends DataElement {

    private final long value;

    public DataLong(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    public static DataLong read(DataReader reader) throws IOException {
        return new DataLong(reader.readInt());
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataLong> type() {
        return Type.LONG_TYPE;
    }
}
