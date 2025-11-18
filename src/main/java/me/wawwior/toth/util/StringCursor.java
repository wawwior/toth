package me.wawwior.toth.util;

import java.util.Optional;

public class StringCursor {

    private final String string;
    private int position = 0;

    public StringCursor(String string) {
        this.string = string;
    }

    public StringCursor peek() {
        StringCursor cursor = new StringCursor(this.string);
        cursor.position = this.position;
        return cursor;
    }

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

    public Optional<Character> readChar() {
        try {
            position++;
            return Optional.of(string.charAt(position - 1));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }
}
