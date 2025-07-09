package me.wawwior.toth.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

class JsonWriterTest {

    /**
     * Compact style {@link JsonWriter} for testing.
     * @param writer the backing {@link StringWriter}
     * @return a JsonWriter
     */
    JsonWriter compact(StringWriter writer) {
        return new JsonWriter(writer, JsonWriter.Style.compact());
    }

    /**
     * Pretty style {@link JsonWriter} for testing.
     * <p>
     * The indent is set as 2 spaces.
     * @param writer the backing {@link StringWriter}
     * @return a JsonWriter with 2 space indent
     */
    JsonWriter pretty(StringWriter writer) {
        return new JsonWriter(writer, JsonWriter.Style.pretty("  "));
    }

    /**
     * Tests writing an empty json object with compact style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void writeEmpty_object_compact() throws IOException {
        StringWriter stringWriter = new StringWriter();
        compact(stringWriter)
                .openObject()
                .closeObject();

        //language=JSON
        String expected = """
                {}""";

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Tests writing a json object with a key-value pair with compact style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void write_object_compact() throws IOException {
        StringWriter stringWriter = new StringWriter();
        compact(stringWriter)
                .openObject()
                .name("key")
                .value("value")
                .closeObject();

        //language=JSON
        String expected = """
                {"key":"value"}""";

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Tests writing an empty json object with pretty style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void writeEmpty_object_pretty() throws IOException {
        StringWriter stringWriter = new StringWriter();
        pretty(stringWriter)
                .openObject()
                .closeObject();

        //language=JSON
        String expected = """
                {}""";

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Tests writing a json object with a key-value pair with pretty style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void write_object_pretty() throws IOException {

        StringWriter stringWriter = new StringWriter();
        pretty(stringWriter)
                .openObject()
                .name("key")
                .value("value")
                .closeObject();

        //language=JSON
        String expected = """
                {
                  "key": "value"
                }""";

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Tests writing an empty json array with compact style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void writeEmpty_array_compact() throws IOException {
        StringWriter stringWriter = new StringWriter();
        compact(stringWriter)
                .openArray()
                .closeArray();

        //language=JSON
        String expected = """
                []""";

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Tests writing a json array with a key-value pair with compact style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void write_array_compact() throws IOException {
        StringWriter stringWriter = new StringWriter();
        compact(stringWriter)
                .openArray()
                .value("value")
                .closeArray();

        //language=JSON
        String expected = """
                ["value"]""";

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Tests writing an empty json array with pretty style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void writeEmpty_array_pretty() throws IOException {
        StringWriter stringWriter = new StringWriter();
        pretty(stringWriter)
                .openArray()
                .closeArray();

        //language=JSON
        String expected = """
                []""";

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Tests writing a json array with a key-value pair with pretty style.
     * @throws IOException If this happens, the test fails
     */
    @Test
    void write_array_pretty() throws IOException {

        StringWriter stringWriter = new StringWriter();
        pretty(stringWriter)
                .openArray()
                .value("value")
                .closeArray();


        //language=JSON
        String expected = """
                [
                  "value"
                ]""";

        assertEquals(expected, stringWriter.toString());
    }

}