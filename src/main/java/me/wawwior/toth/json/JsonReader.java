package me.wawwior.toth.json;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.primitives.DataNumber;
import me.wawwior.toth.util.CatchingFunction;
import me.wawwior.toth.util.StringCursor;

import java.io.IOException;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonReader implements DataReader {

    private final StringCursor cursor;
    private final Stack<JsonLocation> stack = new Stack<>();

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

    public JsonReader(StringCursor cursor) {
        this.cursor = cursor;
        stack.push(JsonLocation.ROOT);
    }

    @Override
    public void enterMap() throws IOException {
        beforeValue(cursor);
        expect('{', cursor);
        stack.push(JsonLocation.EMPTY_MAP);
    }

    @Override
    public void leaveMap() throws IOException {
        if (!stack.peek().isMap()) throw new IOException("State is " + stack.peek() + ", expected MAP or EMPTY_MAP!");
        skipWhitespace(cursor);
        expect('}', cursor);
        stack.pop();
    }

    @Override
    public void enterList() throws IOException {
        beforeValue(cursor);
        expect('[', cursor);
        stack.push(JsonLocation.EMPTY_LIST);
    }

    @Override
    public void leaveList() throws IOException {
        if (!stack.peek().isList()) throw new IOException("State is " + stack.peek() + ", expected LIST or EMPTY_LIST!");
        skipWhitespace(cursor);
        expect(']', cursor);
        stack.pop();
    }

    @Override
    public String readKey() throws IOException {
        beforeKey(cursor);
        stack.push(JsonLocation.KEY);
        return readQuotedValue(cursor);
    }

    @Override
    public void expectNull() throws IOException {
        beforeValue(cursor);
        String value = readUnquotedValue(cursor);
        if (!value.equals("null")) throw new IOException("Expected null, found \"" + value + "\"!");
    }

    @Override
    public boolean readBoolean() throws IOException {
        beforeValue(cursor);
        String value = readUnquotedValue(cursor);
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
        beforeValue(cursor);
        String value = readUnquotedValue(cursor);
        return f.apply(value);
    }

    @Override
    public String readString() throws IOException {
        beforeValue(cursor);
        return readQuotedValue(cursor);
    }

    @Override
    public boolean hasNext() throws IOException {
        if (stack.peek() == JsonLocation.KEY) throw new IOException("Cannot determine next while state is KEY!");
        skipWhitespace(cursor);
        return cursor.peek().readChar().filter(character -> "}]".indexOf(character) == -1).isPresent();
    }

    @Override
    public DataElement.Type<?> nextType() throws IOException {

        StringCursor peeking = cursor.peek();

        skipWhitespace(peeking);

        switch (stack.peek()) {
            case KEY -> expect(':', peeking);
            case LIST -> expect(',', peeking);
            case MAP, EMPTY_MAP, CLOSED -> throw new IOException("Cannot get next type when state is " + stack.peek() + "!");
            default -> {
            }
        }

        skipWhitespace(peeking);

        String bracket = peeking.peek().readUntil("[{", true);
        if (bracket.equals("[")) {
            return DataElement.Type.LIST_TYPE;
        }
        if (bracket.equals("{")) {
            return DataElement.Type.MAP_TYPE;
        }

        String unquotedValue = readUnquotedValue(peeking.peek());
        if (unquotedValue.equals("true") || unquotedValue.equals("false")) {
            return DataElement.Type.BOOLEAN_TYPE;
        }
        if (unquotedValue.equals("null")) {
            return DataElement.Type.NULL_TYPE;
        }

        Matcher numberMatcher = numberPattern.matcher(unquotedValue);
        if (numberMatcher.matches()) {
            return DataElement.Type.NUMBER_TYPE;
        }

        // for validation
        String ignored = readQuotedValue(peeking);

        return DataElement.Type.STRING_TYPE;
    }

    private void expect(char c, StringCursor cursor) throws IOException {
        Optional<Character> optional = cursor.readChar();
        if (optional.isEmpty()) {
            throw new IOException("Expected '" + c + "', found end of reader!");
        }
        char actual = optional.get();
        if (actual != c) {
            throw new IOException("Expected '" + c + "', found '" + actual + "'!");
        }
    }

    private void beforeValue(StringCursor cursor) throws IOException {
        skipWhitespace(cursor);
        switch (this.stack.peek()) {
            case ROOT -> {
                this.stack.pop();
                this.stack.push(JsonLocation.CLOSED);
            }
            case KEY -> {
                expect(':', cursor);
                skipWhitespace(cursor);
                this.stack.pop();
            }
            case LIST -> {
                expect(',', cursor);
                skipWhitespace(cursor);
            }
            case EMPTY_LIST -> {
                this.stack.pop();
                this.stack.push(JsonLocation.LIST);
            }
            case MAP, EMPTY_MAP, CLOSED -> throw new IOException("Cannot read value when state is " + this.stack.peek() + "!");
        }
    }

    private void beforeKey(StringCursor cursor) throws IOException {
        skipWhitespace(cursor);
        switch (this.stack.peek()) {
            case MAP -> {
                expect(',', cursor);
                skipWhitespace(cursor);
            }
            case EMPTY_MAP -> {
                this.stack.pop();
                this.stack.push(JsonLocation.MAP);
            }
            case KEY, LIST, EMPTY_LIST, ROOT, CLOSED -> throw new IOException("Cannot read key when state is " + this.stack.peek() + "!");
        }
    }

    /**
     * Consumes reader until non-whitespace is encountered.
     * skipWhitespace is idempotent.
     * skipWhitespace should be non-interfering, as in no other method should rely on skipWhitespace not being called.
     */
    private void skipWhitespace(StringCursor cursor) throws IOException {
        while (cursor.peek().readChar().filter(c -> " \n\r\t".indexOf(c) == -1).isEmpty()) {
            cursor.readChar();
        }
    }

    private String readUnquotedValue(StringCursor cursor) throws IOException {
        return cursor.readUntil(" \n\r\t,}]", false);
    }

    private String readQuotedValue(StringCursor cursor) throws IOException {
        StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        String quote = cursor.readUntil("\"'", true);
        if (quote.length() > 1) {
            throw new IOException("Could not find quotation mark!");
        }
        char quote_char = quote.charAt(0);
        while (true) {
            char c = cursor.readChar().orElseThrow(() -> new IOException("Expected quotation mark, found EOF!"));
            if (!escaped) {
                if (c == quote_char) break;
                if (c == '\\') {
                    escaped = true;
                    continue;
                }
                builder.append(c);
            } else {
                escaped = false;
                if (c > 116) throw new IOException("char '" + c + "' cannot be escaped!");
                char replacement = ESCAPES[c];
                if (replacement == 0) throw new IOException("char '" + c + "' cannot be escaped!");
                builder.append(replacement);
            }
        }
        return builder.toString();
    }
}
