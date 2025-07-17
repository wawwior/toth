package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.util.Suppliers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Supplier;

public class DataNumber extends DataElement {

    private final Number value;

    public DataNumber(Number number) {
        this.value = number;
    }

    public static DataNumber read(DataReader reader) throws IOException {
        return new DataNumber(reader.readNumber());
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.value(value);
    }

    @Override
    public Type<DataNumber> type() {
        return Type.NUMBER_TYPE;
    }

    public static class GenericNumber extends Number {

        private final String value;

        private final Supplier<BigDecimal> bigDecimal;

        public GenericNumber(String value) {
            this.value = value;
            this.bigDecimal = Suppliers.memoize(() -> new BigDecimal(value));
        }

        @Override
        public int intValue() {
            return Integer.parseInt(value);
        }

        @Override
        public long longValue() {
            return Long.parseLong(value);
        }

        @Override
        public float floatValue() {
            return Float.parseFloat(value);
        }

        @Override
        public double doubleValue() {
            return Double.parseDouble(value);
        }

        @Override
        public final boolean equals(Object o) {
            if (!(o instanceof Number that)) return false;

            BigDecimal bigDecimal = new BigDecimal(that.toString());

            return bigDecimal.compareTo(this.bigDecimal.get()) == 0;

        }

        @Override
        public String toString() {
            return value;
        }
    }

}
