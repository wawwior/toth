package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public class DataNull extends DataElement {

    public static DataNull INSTANCE = new DataNull();

    private DataNull() {}

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.nullValue();
    }

    @Override
    public Type<DataNull> type() {
        return Type.NULL_TYPE;
    }
}
