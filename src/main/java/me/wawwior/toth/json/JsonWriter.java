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
        stack.push(State.NONE);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public JsonWriter openMap() throws IOException {
        beforeValue();
        stack.push(State.EMPTY_OBJECT);
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
    public JsonWriter closeMap() throws IOException {
        if (!stack.peek().isObject())
            throw new IllegalArgumentException("State is " + stack.peek() + ", cannot close object!");
        indent--;
        if (stack.peek() == State.OBJECT) {
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
    public JsonWriter openList() throws IOException {
        beforeValue();
        stack.push(State.EMPTY_ARRAY);
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
    public JsonWriter closeList() throws IOException {
        if (!stack.peek().isArray())
            throw new IllegalArgumentException("State is " + stack.peek() + ", cannot close array!");
        indent--;
        if (stack.peek() == State.ARRAY) {
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
    public JsonWriter key(String key) throws IOException {
        beforeName();
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
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @param string {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     */
    public JsonWriter value(String string) throws IOException {
        beforeValue();
        return string(string);
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
            case ARRAY -> {
                writer.write(comma);
                writeNewline();
                writeIndent();
            }
            case EMPTY_ARRAY -> {
                writeNewline();
                writeIndent();
                updateScope();
            }
            case KEY -> {
                stack.pop();
                writer.write(colon);
            }
            case OBJECT, EMPTY_OBJECT ->
                    throw new IllegalArgumentException("State is " + stack.peek() + "!");
        }
    }

    private void beforeName() throws IOException {
        switch (stack.peek()) {
            case OBJECT -> {
                writer.write(comma);
                writeNewline();
                writeIndent();
            }
            case EMPTY_OBJECT -> {
                writeNewline();
                writeIndent();
                updateScope();
            }
            case KEY, ARRAY, EMPTY_ARRAY ->
                    throw new IllegalArgumentException("State is " + stack.peek() + ", expected " + State.OBJECT + "!");
        }
    }

    private void updateScope() {
        if (stack.peek().isArray()) {
            stack.pop();
            stack.push(State.ARRAY);
            return;
        }
        if (stack.peek().isObject()) {
            stack.pop();
            stack.push(State.OBJECT);
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

    public enum State {
        NONE,
        ARRAY,
        EMPTY_ARRAY,
        OBJECT,
        EMPTY_OBJECT,
        KEY;

        public boolean isObject() {
            return this == OBJECT || this == EMPTY_OBJECT;
        }

        public boolean isArray() {
            return this == ARRAY || this == EMPTY_ARRAY;
        }
    }
}
