package me.wawwior.toth.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

public class JsonWriter {

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
     * Starts a new json object.
     *
     * @return this
     * @throws IOException if the current state does not accept a value.
     */
    public JsonWriter openObject() throws IOException {
        beforeValue();
        stack.push(State.EMPTY_OBJECT);
        writer.write("{");
        indent++;
        return this;
    }

    /**
     * Ends an open json object.
     *
     * @return this
     * @throws IOException if the current state is not an object.
     */
    public JsonWriter closeObject() throws IOException {
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
     * Starts a new json array.
     *
     * @return this
     * @throws IOException if the current state does not accept a value.
     */
    public JsonWriter openArray() throws IOException {
        beforeValue();
        stack.push(State.EMPTY_ARRAY);
        writer.write("[");
        indent++;
        return this;
    }

    /**
     * Ends an open json object.
     *
     * @return this
     * @throws IOException if the current state is not an array.
     */
    public JsonWriter closeArray() throws IOException {
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
     * Writes a value
     *
     * @param string the value
     * @return this
     * @throws IOException if the current state is an object
     */
    public JsonWriter value(String string) throws IOException {
        beforeValue();
        return string(string);
    }

    /**
     * Writes a key
     *
     * @param string the key
     * @return this
     * @throws IOException if the current state is not an object
     */
    public JsonWriter key(String string) throws IOException {
        beforeName();
        stack.push(State.KEY);
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
     * Flushes the backing {@link Writer}
     *
     * @throws IOException propagated from the backing {@link Writer}
     */
    public void flush() throws IOException {
        writer.flush();
    }

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
