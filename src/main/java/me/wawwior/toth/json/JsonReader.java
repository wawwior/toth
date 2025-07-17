package me.wawwior.toth.json;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.primitives.DataNumber;
import me.wawwior.toth.util.CatchingFunction;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;

public class JsonReader implements DataReader {

    private final StringReader reader;
    private final Stack<State> stack = new Stack<>();

    public JsonReader(StringReader reader) {
        this.reader = reader;
        stack.push(State.ROOT);
    }

    @Override
    public void enterMap() throws IOException {
        beforeValue();
        expect('{');
        stack.push(State.EMPTY_MAP);
    }

    @Override
    public void leaveMap() throws IOException {
        // TODO: Exception
        if (!stack.peek().isMap()) throw new IOException();
        skipWhitespace();
        expect('}');
        stack.pop();
    }

    @Override
    public void enterList() throws IOException {
        beforeValue();
        expect('[');
        stack.push(State.EMPTY_LIST);
    }

    @Override
    public void leaveList() throws IOException {
        // TODO: Exception
        if (!stack.peek().isList()) throw new IOException();
        skipWhitespace();
        expect(']');
        stack.pop();
    }

    @Override
    public String readKey() throws IOException {
        return "";
    }

    @Override
    public boolean readBoolean() throws IOException {
        beforeValue();
        String value = consumeNonString();
        if (value.equals("true") || value.equals("false")) {
            return value.equals("true");
        }
        // TODO: Exception
        throw new IOException();
    }

    @Override
    public int readInt() throws IOException {
        return readNumber(Integer::parseInt);
    }

    @Override
    public long readLong() throws IOException {
        return readNumber(Long::parseLong);
    }

    @Override
    public float readFloat() throws IOException {
        return readNumber(Float::parseFloat);
    }

    @Override
    public double readDouble() throws IOException {
        return readNumber(Double::parseDouble);
    }

    @Override
    public Number readNumber() throws IOException {
        return readNumber(DataNumber.GenericNumber::new);
    }

    <T> T readNumber(CatchingFunction<String, T, IOException> f) throws IOException {
        beforeValue();
        String value = consumeNonString();
        return f.apply(value);
    }

    @Override
    public String readString() throws IOException {
        return "";
    }

    @Override
    public boolean hasNext() throws IOException {
        // TODO: Exception
        if (stack.peek() == State.KEY) throw new IOException();
        skipWhitespace();
        reader.mark(0);
        char c = (char) reader.read();
        return c == ',';
    }

    @Override
    public DataElement.Type<?> nextType() throws IOException {
        return null;
    }

    void expect(char c) throws IOException {
        if (reader.read() != c) throw new IOException();
    }

    void beforeValue() throws IOException {
        skipWhitespace();
        switch (stack.peek()) {
            case ROOT -> {
                stack.pop();
                stack.push(State.CLOSED);
            }
            case KEY -> {
                expect(':');
                skipWhitespace();
                stack.pop();
            }
            case LIST -> {
                expect(',');
                skipWhitespace();
            }
            case EMPTY_LIST -> {
                stack.pop();
                stack.push(State.LIST);
            }
            // TODO: Exception
            case MAP, EMPTY_MAP, CLOSED -> throw new IOException();
        }
    }

    void beforeKey() throws IOException {
        skipWhitespace();
        switch (stack.peek()) {
            case MAP -> {
                expect(',');
                skipWhitespace();
            }
            case EMPTY_MAP -> {
                stack.pop();
                stack.push(State.MAP);
            }
            // TODO: Exception
            case KEY, LIST, EMPTY_LIST, ROOT, CLOSED -> throw new IOException();
        }
    }

    void skipWhitespace() throws IOException {
        while (true) {
            reader.mark(0);
            int c = reader.read();
            if (" \n\r\t".indexOf(c) == -1) {
                reader.reset();
                break;
            }
        }
    }

    String readInclusiveUntil(String terminators, boolean consuming) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (!consuming) reader.mark(0);
        while (true) {
            int c = reader.read();
            builder.append((char) c);
            if (c == -1 || terminators.indexOf(c) != -1) {
                break;
            }
        }
        if (!consuming) reader.reset();
        return builder.toString();
    }

    String peekUntil(String terminators) throws IOException {
        StringBuilder builder = new StringBuilder();
        reader.mark(0);
        while (true) {
            int c = reader.read();
            if (c == -1 || terminators.indexOf(c) != -1) break;
            builder.append((char) c);
        }
        reader.reset();
        return builder.toString();
    }

    String peekNonString() throws IOException {
        return peekUntil(" \n\r\t,}]");
    }

    String consumeUntil(String terminators) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            reader.mark(0);
            int c = reader.read();
            if (c == -1 || terminators.indexOf(c) != -1) break;
            builder.append((char) c);
        }
        reader.reset();
        return builder.toString();
    }

    String consumeNonString() throws IOException {
        return consumeUntil(" \n\r\t,}]");
    }

    enum State {
        ROOT,
        LIST,
        EMPTY_LIST,
        MAP,
        EMPTY_MAP,
        KEY,
        CLOSED;

        public boolean isMap() {
            return this == MAP || this == EMPTY_MAP;
        }

        public boolean isList() {
            return this == LIST || this == EMPTY_LIST;
        }
    }
}
