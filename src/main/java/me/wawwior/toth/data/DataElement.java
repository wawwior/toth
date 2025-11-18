package me.wawwior.toth.data;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.primitives.*;
import me.wawwior.toth.util.CatchingFunction;

import java.io.IOException;

public abstract class DataElement {

    public abstract void write(DataWriter writer) throws IOException;

    public abstract Type<?> type();

    public final <T extends DataElement> T as(Type<T> type) {
        return type.cast(this);
    }

    public static DataElement read(DataReader reader) throws IOException {
        return reader.nextType().read(reader);
    }

    public static class Type<T extends DataElement> {

        public static final Type<DataNull> NULL_TYPE = new Type<>("null_type", reader -> {
            reader.expectNull();
            return DataNull.INSTANCE;
        });

        public static final Type<DataMap> MAP_TYPE = new Type<>("map_type", DataMap::read);
        public static final Type<DataList> LIST_TYPE = new Type<>("list_type", DataList::read);

        public static final Type<DataBoolean> BOOLEAN_TYPE = new Type<>("boolean_type", DataBoolean::read);
        public static final Type<DataNumber> NUMBER_TYPE = new Type<>("number_type", DataNumber::read);
        public static final Type<DataString> STRING_TYPE = new Type<>("string_type", DataString::read);

        private final String name;
        private final CatchingFunction<DataReader, T, IOException> fromReader;

        private Type(String name, CatchingFunction<DataReader, T, IOException> fromReader) {
            this.name = name;
            this.fromReader = fromReader;
        }

        @SuppressWarnings("unchecked")
        public T cast(DataElement element) throws IllegalArgumentException {
            if (element.type() != this) {
                throw new IllegalArgumentException();
            }
            return (T) element;
        }

        public T read(DataReader reader) throws IOException {
            return fromReader.apply(reader);
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
