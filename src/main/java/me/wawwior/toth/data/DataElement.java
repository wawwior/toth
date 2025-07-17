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

    public static class Type<T extends DataElement> {

        public static final Type<DataNull> NULL_TYPE = new Type<>(reader -> DataNull.INSTANCE);

        public static final Type<DataMap> MAP_TYPE = new Type<>(DataMap::read);
        public static final Type<DataList> LIST_TYPE = new Type<>(DataList::read);

        public static final Type<DataBoolean> BOOLEAN_TYPE = new Type<>(DataBoolean::read);
        public static final Type<DataNumber> NUMBER_TYPE = new Type<>(DataNumber::read);
        public static final Type<DataString> STRING_TYPE = new Type<>(DataString::read);

        private final CatchingFunction<DataReader, T, IOException> fromReader;

        private Type(CatchingFunction<DataReader, T, IOException> fromReader) {
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

    }

}
