package me.wawwior.toth.json;

import me.wawwior.toth.DataWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

public class JsonWriter implements DataWriter {

    private static final String[] ESCAPES = new String[93];

    static {
        ESCAPES['\"'] = "\\\"";
        ESCAPES['\\'] = "\\\\";

        ESCAPES['\b'] = "\\b";
        ESCAPES['\f'] = "\\f";
        ESCAPES['\n'] = "\\n";
        ESCAPES['\r'] = "\\r";
        ESCAPES['\t'] = "\\t";
    }

    private final Writer writer;
    private final JsonWriter.Style style;
    private final String comma;
    private final String colon;

    private final Stack<State> stack = new Stack<>();
    private int indent = 0;

    /**
     * Constructs a new {@link JsonWriter} writing to the backing {@link Writer}.
     *
     * @param writer backing {@link Writer}
     * @param style  style settings
     */
    public JsonWriter(Writer writer, Style style) {
        this.writer = writer;
        this.style = style;
        if (style.spaces()) {
            colon = ": ";
        } else {
            colon = ":";
        }
        if (style.newline().isEmpty() && style.spaces()) {
            comma = ", ";
        } else {
            comma = ",";
        }
        stack.push(State.ROOT);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public DataWriter openMap() throws IOException {
        beforeValue();
        stack.push(State.EMPTY_MAP);
        writer.write("{");
        indent++;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public DataWriter closeMap() throws IOException {
        if (!stack.peek().isObject())
            throw new IllegalArgumentException("State is " + stack.peek() + ", expected " + State.MAP + " or " + State.EMPTY_MAP + "!");
        indent--;
        if (stack.peek() == State.MAP) {
            writeNewline();
            writeIndent();
        }
        stack.pop();
        writer.write("}");
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public DataWriter openList() throws IOException {
        beforeValue();
        stack.push(State.EMPTY_LIST);
        writer.write("[");
        indent++;
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public DataWriter closeList() throws IOException {
        if (!stack.peek().isArray())
            throw new IllegalArgumentException("State is " + stack.peek() + ", expected " + State.LIST + " or " + State.EMPTY_LIST + "!");
        indent--;
        if (stack.peek() == State.LIST) {
            writeNewline();
            writeIndent();
        }
        stack.pop();
        writer.write("]");
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param key {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public DataWriter key(String key) throws IOException {
        beforeKey();
        stack.push(State.KEY);
        return string(key);
    }

    /**
     * {@inheritDoc}
     *
     * @param b {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public DataWriter value(boolean b) throws IOException {
        beforeValue();
        writer.write(String.valueOf(b));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param i {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public DataWriter value(int i) throws IOException {
        beforeValue();
        writer.write(String.valueOf(i));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param l {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public DataWriter value(long l) throws IOException {
        beforeValue();
        writer.write(String.valueOf(l));
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param f {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public DataWriter value(float f) throws IOException {
        beforeValue();
        boolean valid = Float.isFinite(f) && !Float.isNaN(f);
        if (!valid) {
            throw new IllegalArgumentException("float with value " + f + " is not valid in json!");
        }
        writer.write(String.valueOf(f).toLowerCase());
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param d {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    @Override
    public DataWriter value(double d) throws IOException {
        beforeValue();
        boolean valid = Double.isFinite(d) && !Double.isNaN(d);
        if (!valid) {
            throw new IllegalArgumentException("double with value " + d + " is not valid in json!");
        }
        writer.write(String.valueOf(d).toLowerCase());
        return this;
    }

    @Override
    public DataWriter value(Number number) throws IOException {
        try {
            long l = number.longValue();
            return value(l);
        } catch (NumberFormatException e) {
            double d = number.doubleValue();
            return value(d);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param string {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public DataWriter value(String string) throws IOException {
        if (string == null) return nullValue();
        beforeValue();
        return string(string);
    }

    @Override
    public DataWriter nullValue() throws IOException {
        beforeValue();
        writer.write("null");
        return this;
    }

    private JsonWriter string(String string) throws IOException {
        writer.write("\"");
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            if (c < 93) {
                String replacement = ESCAPES[c];
                if (replacement == null) {
                    writer.write(c);
                } else {
                    writer.write(replacement);
                }
            } else {
                writer.write(c);
            }
        }
        writer.write("\"");
        return this;
    }

    private void beforeValue() throws IOException {
        switch (stack.peek()) {
            case ROOT -> {
                updateScope();
            }
            case LIST -> {
                writer.write(comma);
                writeNewline();
                writeIndent();
            }
            case EMPTY_LIST -> {
                writeNewline();
                writeIndent();
                updateScope();
            }
            case KEY -> {
                stack.pop();
                writer.write(colon);
            }
            case MAP, EMPTY_MAP, CLOSED->
                    throw new IllegalArgumentException("State is " + stack.peek() + "!");
        }
    }

    private void beforeKey() throws IOException {
        switch (stack.peek()) {
            case MAP -> {
                writer.write(comma);
                writeNewline();
                writeIndent();
            }
            case EMPTY_MAP -> {
                writeNewline();
                writeIndent();
                updateScope();
            }
            case KEY, LIST, EMPTY_LIST, ROOT, CLOSED ->
                    throw new IllegalArgumentException("State is " + stack.peek() + ", expected " + State.MAP + "!");
        }
    }

    private void updateScope() {
        if (stack.peek().isArray()) {
            stack.pop();
            stack.push(State.LIST);
            return;
        }
        if (stack.peek().isObject()) {
            stack.pop();
            stack.push(State.MAP);
        }
        if (stack.peek() == State.ROOT) {
            stack.pop();
            stack.push(State.CLOSED);
        }
    }

    private void writeNewline() throws IOException {
        writer.write(style.newline());
    }

    private void writeIndent() throws IOException {
        if (!style.newline().isEmpty()) writer.write(style.indent().repeat(indent));
    }

    /**
     * Configuration Class for {@link JsonWriter} behaviour
     */
    public interface Style {

        /**
         * Helper method to obtain a {@link Style} with newlines, spaces, and indent.
         *
         * @param indent the indent on new lines
         * @return a configured {@link Style}
         */
        static Style pretty(String indent) {
            return of(indent, "\n", true);
        }

        /**
         * Helper method to obtain a {@link Style} without newlines, spaces or indent.
         *
         * @return a configured {@link Style}
         */
        static Style compact() {
            return of("", "", false);
        }

        static Style of(String indent, String newline, boolean spaces) {

            StringBuilder builder = new StringBuilder();
            builder.append("Style[");
            if (!indent.isEmpty()) builder.append("indent,");
            if (!newline.isEmpty()) builder.append("newline,");
            if (spaces) builder.append("spaces,");
            if (!indent.isEmpty() || !newline.isEmpty() || spaces)
                builder.setLength(builder.length() - 1);
            builder.append("]");
            String name = builder.toString();

            return new Style() {
                @Override
                public String indent() {
                    return indent;
                }

                @Override
                public String newline() {
                    return newline;
                }

                @Override
                public boolean spaces() {
                    return spaces;
                }

                @Override
                public String toString() {
                    return name;
                }
            };
        }

        String indent();

        String newline();

        boolean spaces();

    }

    enum State {
        ROOT,
        LIST,
        EMPTY_LIST,
        MAP,
        EMPTY_MAP,
        KEY,
        CLOSED;

        public boolean isObject() {
            return this == MAP || this == EMPTY_MAP;
        }

        public boolean isArray() {
            return this == LIST || this == EMPTY_LIST;
        }
    }
}
