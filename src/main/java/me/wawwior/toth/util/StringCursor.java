package me.wawwior.toth.util;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

public sealed interface StringCursor {

    StringCursor peek();

    String readUntil(String terminators, boolean include) throws IOException;

    Optional<Character> readChar() throws IOException;

    static StringCursor of(String string) {
        return of(string, 0);
    }

    static StringCursor of(String string, int position) {
        return new DefaultCursor(string, position);
    }

    static StringCursor of(Reader reader) {
        return new ReaderCursor(reader, new StringBuilder(), 0);
    }

    final class DefaultCursor implements StringCursor {

        private final String string;
        private int position;

        private DefaultCursor(String string, int position) {
            this.string = string;
            this.position = position;
        }

        @Override
        public StringCursor peek() {
            return new DefaultCursor(string, position);
        }

        @Override
        public String readUntil(String terminators, boolean include) {
            StringBuilder builder = new StringBuilder();
            while (true) {
                try {
                    char c = string.charAt(position);
                    boolean terminate = terminators.indexOf(c) >= 0;
                    if (terminate && !include) break;
                    position++;
                    builder.append(c);
                    if (terminate) break;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            return builder.toString();
        }

        @Override
        public Optional<Character> readChar() {
            try {
                position++;
                return Optional.of(string.charAt(position - 1));
            } catch (IndexOutOfBoundsException e) {
                return Optional.empty();
            }
        }
    }

    final class ReaderCursor implements StringCursor {

        private final Reader reader;
        private final StringBuilder buffer;
        private int position;

        private ReaderCursor(Reader reader, StringBuilder buffer, int position) {
            this.reader = reader;
            this.buffer = buffer;
            this.position = position;
        }

        @Override
        public StringCursor peek() {
            return new ReaderCursor(reader, buffer, position);
        }

        @Override
        public String readUntil(String terminators, boolean include) throws IOException {
            StringBuilder builder = new StringBuilder();
            while (true) {
                try {
                    char c = buffer.charAt(position);
                    boolean terminate = terminators.indexOf(c) >= 0;
                    if (terminate && !include) break;
                    next();
                    builder.append(c);
                    if (terminate) break;
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
            return builder.toString();
        }

        @Override
        public Optional<Character> readChar() throws IOException {
            try {
                next();
                return Optional.of(buffer.charAt(position - 1));
            } catch (IndexOutOfBoundsException e) {
                return Optional.empty();
            }
        }

        private void next() throws IOException {
            position++;
            if (buffer.length() <= position) {
                int i = reader.read();
                if (i != -1) buffer.append((char) i);
            }
        }
    }
}
