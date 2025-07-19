package me.wawwior.toth.json;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.primitives.DataNumber;
import me.wawwior.toth.util.CatchingFunction;

import java.io.IOException;
import java.io.StringReader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonReader implements DataReader {

    private final StringReader reader;
    private final Stack<State> stack = new Stack<>();

    static final Pattern numberPattern = Pattern.compile("-?([1-9]\\d*|0)(.\\d+)?([Ee][+-]?\\d+)?");

    private static final char[] ESCAPES = new char[117];

    static {
        ESCAPES['"'] = '"';
        ESCAPES['\\'] = '\\';

        ESCAPES['b'] = '\b';
        ESCAPES['f'] = '\f';
        ESCAPES['n'] = '\n';
        ESCAPES['r'] = '\r';
        ESCAPES['t'] = '\t';
    }

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
        if (!stack.peek().isMap()) throw new IOException("State is " + stack.peek() + ", expected MAP or EMPTY_MAP!");
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
        if (!stack.peek().isList()) throw new IOException("State is " + stack.peek() + ", expected LIST or EMPTY_LIST!");
        skipWhitespace();
        expect(']');
        stack.pop();
    }

    @Override
    public String readKey() throws IOException {
        beforeKey();
        stack.push(State.KEY);
        return consumeString();
    }

    @Override
    public boolean readBoolean() throws IOException {
        beforeValue();
        String value = consumeNonString();
        if (value.equals("true") || value.equals("false")) {
            return value.equals("true");
        }
        throw new IOException("Expected boolean, found \"" + value + "\"!");
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
        beforeValue();
        return consumeString();
    }

    @Override
    public boolean hasNext() throws IOException {
        if (stack.peek() == State.KEY) throw new IOException("Cannot determine next while state is KEY!");
        skipWhitespace();
        reader.mark(0);
        char c = (char) reader.read();
        reader.reset();
        return c == ',';
    }

    @Override
    public DataElement.Type<?> nextType() throws IOException {
        skipWhitespace();

        String bracket = peekUntilInclude("[{");
        if (bracket.equals("[")) {
            return DataElement.Type.LIST_TYPE;
        }
        if (bracket.equals("{")) {
            return DataElement.Type.MAP_TYPE;
        }

        String nonStringValue = peekNonString();
        if (nonStringValue.equals("true") || nonStringValue.equals("false")) {
            return DataElement.Type.BOOLEAN_TYPE;
        }
        if (nonStringValue.equals("null")) {
            return DataElement.Type.NULL_TYPE;
        }

        Matcher numberMatcher = numberPattern.matcher(nonStringValue);
        if (numberMatcher.matches()) {
            return DataElement.Type.NUMBER_TYPE;
        }

        String ignored = peekString();
        return DataElement.Type.STRING_TYPE;
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
            case MAP, EMPTY_MAP, CLOSED -> throw new IOException("Cannot read value when state is " + stack.peek() + "!");
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
            case KEY, LIST, EMPTY_LIST, ROOT, CLOSED -> throw new IOException("Cannot read key when state is " + stack.peek() + "!");
        }
    }

    /**
     * Consumes reader until non-whitespace is encountered.
     * skipWhitespace^n for n > 0 is equal to skipWhitespace.
     * skipWhitespace should be non-interfering, as in no other method should rely on skipWhitespace not being called.
     * @throws IOException propagated from the reader.
     */
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

    String peekUntilInclude(String terminators) throws IOException {
        StringBuilder builder = new StringBuilder();
        reader.mark(0);
        while (true) {
            int c = reader.read();
            if (c == -1) break;
            builder.append((char) c);
            if (terminators.indexOf(c) != -1) break;
        }
        reader.reset();
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

    String peekString() throws IOException {
        StringBuilder builder = new StringBuilder();
        reader.mark(0);
        boolean escaped = false;
        String quote = peekUntilInclude("\"'");
        if (quote.length() > 1) {
            throw new IOException("Could not find quotation mark!");
        }
        char quote_char = quote.charAt(0);
        long ignored = reader.skip(1);
        while (true) {
            int c = reader.read();
            if (c == -1) throw new IOException("Expected quotation mark, found EOF!");
            if (!escaped) {
                if (c == quote_char) break;
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                builder.append((char) c);
            } else {
                escaped = false;
                if (c > 116) throw new IOException("char '" + (char) c + "' cannot be escaped!");
                char replacement = ESCAPES[c];
                if (replacement == 0) throw new IOException("char '" + (char) c + "' cannot be escaped!");
                builder.append(replacement);
            }
        }
        reader.reset();
        return builder.toString();
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

    String consumeUntilInclude(String terminators) throws IOException {
        StringBuilder builder = new StringBuilder();
        while (true) {
            int c = reader.read();
            if (c == -1) break;
            builder.append((char) c);
            if (terminators.indexOf(c) != -1) break;
        }
        return builder.toString();
    }

    String consumeNonString() throws IOException {
        return consumeUntil(" \n\r\t,}]");
    }

    String consumeString() throws IOException {
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        String quote = consumeUntilInclude("\"'");
        if (quote.length() > 1) {
            throw new IOException("Could not find quotation mark!");
        }
        char quote_char = quote.charAt(0);
        while (true) {
            int c = reader.read();
            if (c == -1) throw new IOException("Expected quotation mark, found EOF!");
            if (!escaped) {
                if (c == quote_char) break;
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                builder.append((char) c);
            } else {
                escaped = false;
                if (c > 116) throw new IOException("char '" + (char) c + "' cannot be escaped!");
                char replacement = ESCAPES[c];
                if (replacement == 0) throw new IOException("char '" + (char) c + "' cannot be escaped!");
                builder.append(replacement);
            }
        }
        return builder.toString();
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
