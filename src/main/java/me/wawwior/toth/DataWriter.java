package me.wawwior.toth;

import java.io.IOException;

public interface DataWriter {

    /**
     * Starts a new map on the writer.
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter openMap() throws IOException;

    /**
     * Ends an opened map on the writer.
     * @return this
     * @throws IOException If there is no map to close or the writer is waiting for a value.
     */
    DataWriter closeMap() throws IOException;

    /**
     * Starts a new list on the writer.
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter openList() throws IOException;

    /**
     * Ends an opened list on the writer.
     * @return this
     * @throws IOException If there is no list to close.
     */
    DataWriter closeList() throws IOException;

    /**
     * Writes a key into an opened map on the writer.
     * @param key map key
     * @return this
     * @throws IOException If there is no opened map or the writer is waiting for a value.
     */
    DataWriter key(String key) throws IOException;

    /**
     * Writes a boolean value on the writer.
     * @param b value
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter value(boolean b) throws IOException;

    /**
     * Writes an integer value on the writer.
     * @param i value
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter value(int i) throws IOException;

    /**
     * Writes a long value on the writer.
     * @param l value
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter value(long l) throws IOException;

    /**
     * Writes a float value on the writer.
     * @param f value
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter value(float f) throws IOException;

    /**
     * Writes a double value on the writer.
     * @param d value
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter value(double d) throws IOException;

    /**
     * Writes a string value on the writer.
     * @param string value
     * @return this
     * @throws IOException If the writer isn't expecting a value.
     */
    DataWriter value(String string) throws IOException;

}
