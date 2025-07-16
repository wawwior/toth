package me.wawwior.toth;

import me.wawwior.toth.data.DataElement;

import java.io.IOException;

public interface DataReader {

    void enterMap() throws IOException;

    void leaveMap() throws IOException;

    void enterList() throws IOException;

    void leaveList() throws IOException;

    String readKey() throws IOException;

    boolean readBoolean() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    Number readNumber() throws IOException;

    String readString() throws IOException;

    /**
     * This should return true if there is a next element <i>within</i> the given scope.
     * <p>
     * For example, at the end of a map, this will return {@code false}, even if there are more elements after the map.
     * @return If there are more elements in scope.
     * @throws IOException Propagated from a {@link java.io.Reader}
     */
    boolean hasNext() throws IOException;

    /**
     * Returns the {@link me.wawwior.toth.data.DataElement.Type} of the next element without consuming it.
     * @return The type of the next element.
     * @throws IOException If the next token is not an element.
     */
    DataElement.Type<?> nextType() throws IOException;
}
