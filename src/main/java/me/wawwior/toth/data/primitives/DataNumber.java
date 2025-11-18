package me.wawwior.toth.data.primitives;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;
import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.util.Suppliers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.function.Supplier;

public final class DataNumber extends DataElement {

    private final Number value;

    public DataNumber(Number number) {
        this.value = number;
    }

    public static DataNumber read(DataReader reader) throws IOException {
        return new DataNumber(reader.readNumber());
    }

    public int asInt() {
        return value.intValue();
    }

    public long asLong() {
        return value.longValue();
    }

    public float asFloat() {
        return value.floatValue();
    }

    public double asDouble() {
        return value.doubleValue();
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
        private final Supplier<Integer> intValue;
        private final Supplier<Long> longValue;
        private final Supplier<Float> floatValue;
        private final Supplier<Double> doubleValue;

        public GenericNumber(String value) {
            this.value = value;
            this.bigDecimal = Suppliers.memoize(() -> new BigDecimal(value));
            this.intValue = Suppliers.memoize(() -> Integer.parseInt(value));
            this.longValue = Suppliers.memoize(() -> Long.parseLong(value));
            this.floatValue = Suppliers.memoize(() -> Float.parseFloat(value));
            this.doubleValue = Suppliers.memoize(() -> Double.parseDouble(value));
        }

        @Override
        public int intValue() {
            return intValue.get();
        }

        @Override
        public long longValue() {
            return longValue.get();
        }

        @Override
        public float floatValue() {
            return floatValue.get();
        }

        @Override
        public double doubleValue() {
            return doubleValue.get();
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
