package me.wawwior.toth.data;

import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.primitives.*;

import java.io.IOException;

public abstract class DataElement {

    public abstract void write(DataWriter writer) throws IOException;

    public abstract Type<?> type();

    public final <T extends DataElement> T as(Type<T> type) {
        return type.cast(this);
    }

    public static class Type<T extends DataElement> {

        public static final Type<DataMap> MAP_TYPE = new Type<>();
        public static final Type<DataList> LIST_TYPE = new Type<>();

        public static final Type<DataBoolean> BOOLEAN_TYPE = new Type<>();
        public static final Type<DataInt> INT_TYPE = new Type<>();
        public static final Type<DataLong> LONG_TYPE = new Type<>();
        public static final Type<DataFloat> FLOAT_TYPE = new Type<>();
        public static final Type<DataDouble> DOUBLE_TYPE = new Type<>();
        public static final Type<DataString> STRING_TYPE = new Type<>();

        private Type() {}

        @SuppressWarnings("unchecked")
        public T cast(DataElement element) throws IllegalArgumentException {
            if (element.type() != this) {
                throw new IllegalArgumentException();
            }
            return (T) element;
        }

    }

}
