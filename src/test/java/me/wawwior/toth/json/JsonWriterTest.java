package me.wawwior.toth.json;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonWriterTest {

    /**
     * Utility method for creating tests.
     *
     * @param consumer the write operation
     * @param style    the {@link JsonWriter.Style}
     * @param expected the expected result
     * @throws IOException If this happens, the test fails.
     */
    private static void writeTest(
            ThrowingConsumer<JsonWriter> consumer,
            JsonWriter.Style style,
            String expected
    ) throws IOException {

        StringWriter stringWriter = new StringWriter();
        consumer.accept(new JsonWriter(stringWriter, style));

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Utility method for creating test arguments.
     * <p>
     * The indent is set to 2 spaces
     *
     * @param compact the expected compact result.
     * @param pretty  the expected pretty result.
     * @return the stream of arguments
     */
    private static Stream<Arguments> writeTestArgs(String compact, String pretty) {
        return Stream.of(
                Arguments.of(JsonWriter.Style.compact(), compact),
                Arguments.of(JsonWriter.Style.pretty("  "), pretty)
        );
    }

    /**
     * Utility method for creating tests.
     *
     * @param consumer the write operation
     * @param expected the expected exception
     */
    void throwsTest(ThrowingConsumer<JsonWriter> consumer, String expected) {
        StringWriter stringWriter = new StringWriter();
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> consumer.accept(new JsonWriter(
                        stringWriter,
                        JsonWriter.Style.compact()
                ))
        );
        assertEquals(expected, exception.getMessage());
    }

    interface ThrowingConsumer<T> {
        void accept(T t) throws IOException;
    }

    /**
     * Tests writing a map to a {@link JsonWriter}.
     */
    @Nested
    class MapTests {

        @Test
        void value_shouldThrowAfterOpenMap() {

            throwsTest(
                    writer -> writer.openMap().value("value"),
                    "State is EMPTY_OBJECT!"
            );
        }

        @Test
        void key_shouldThrowAfterOpenList() {

            throwsTest(
                    writer -> writer.openList().key("key"),
                    "State is EMPTY_ARRAY, expected OBJECT!"
            );

        }

        @SuppressWarnings("Convert2MethodRef")
        @Test
        void closeMap_shouldThrowOnEmptyWriter() {

            throwsTest(
                    writer -> writer.closeMap(),
                    "State is NONE, cannot close object!"
            );
        }

        /**
         * Tests writing an empty map.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void empty(JsonWriter.Style style, String expected) throws IOException {
            writeTest(writer -> writer.openMap().closeMap(), style, expected);
        }

        static Stream<Arguments> empty() {
            return writeTestArgs(
                    //language=JSON
                    """
                    {}""",
                    //language=JSON
                    """
                    {}"""
            );
        }

        /**
         * Tests writing a map with a key-value pair.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void basic(JsonWriter.Style style, String expected) throws IOException {
            writeTest(
                    writer -> writer
                            .openMap()
                            .key("foo")
                            .value("value")
                            .key("bar")
                            .value("value")
                            .closeMap(), style, expected
            );
        }

        static Stream<Arguments> basic() {
            return writeTestArgs(
                    //language=JSON
                    """
                    {"foo":"value","bar":"value"}""",
                    //language=JSON
                    """
                    {
                      "foo": "value",
                      "bar": "value"
                    }"""
            );
        }

        /**
         * Tests writing a map with a nested map.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void nestedMap(JsonWriter.Style style, String expected) throws IOException {
            writeTest(
                    writer -> writer
                            .openMap()
                            .key("foo")
                            .openMap()
                            .key("bar")
                            .value("value")
                            .closeMap()
                            .closeMap(), style, expected
            );
        }

        static Stream<Arguments> nestedMap() {
            return writeTestArgs(
                    //language=JSON
                    """
                    {"foo":{"bar":"value"}}""",
                    //language=JSON
                    """
                    {
                      "foo": {
                        "bar": "value"
                      }
                    }"""
            );
        }

        /**
         * Tests writing a map with a nested list.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void nestedList(JsonWriter.Style style, String expected) throws IOException {
            writeTest(
                    writer -> writer
                            .openMap()
                            .key("foo")
                            .openList()
                            .value("value")
                            .closeList()
                            .closeMap(), style, expected
            );
        }

        static Stream<Arguments> nestedList() {
            return writeTestArgs(
                    //language=JSON
                    """
                    {"foo":["value"]}""",
                    //language=JSON
                    """
                    {
                      "foo": [
                        "value"
                      ]
                    }"""
            );
        }
    }

    @Nested
    class ListTests {

        @SuppressWarnings("Convert2MethodRef")
        @Test
        void closeThrows() {

            throwsTest(
                    writer -> writer.closeList(),
                    "State is NONE, cannot close array!"
            );
        }

        /**
         * Tests writing an empty list.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void empty(JsonWriter.Style style, String expected) throws IOException {
            writeTest(writer -> writer.openList().closeList(), style, expected);
        }

        static Stream<Arguments> empty() {
            return writeTestArgs(
                    //language=JSON
                    """
                    []""",
                    //language=JSON
                    """
                    []"""
            );
        }

        /**
         * Tests writing a list with a values.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void basic(JsonWriter.Style style, String expected) throws IOException {
            writeTest(
                    writer -> writer.openList().value("foo").value("bar").closeList(),
                    style,
                    expected
            );
        }

        static Stream<Arguments> basic() {
            return writeTestArgs(
                    //language=JSON
                    """
                    ["foo","bar"]""",
                    //language=JSON
                    """
                    [
                      "foo",
                      "bar"
                    ]"""
            );
        }

        /**
         * Tests writing a list with a nested map.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void nestedMap(JsonWriter.Style style, String expected) throws IOException {
            writeTest(
                    writer -> writer
                            .openList()
                            .openMap()
                            .key("foo")
                            .value("value")
                            .closeMap()
                            .closeList(), style, expected
            );
        }

        static Stream<Arguments> nestedMap() {
            return writeTestArgs(
                    //language=JSON
                    """
                    [{"foo":"value"}]""",
                    //language=JSON
                    """
                    [
                      {
                        "foo": "value"
                      }
                    ]"""
            );
        }

        /**
         * Tests writing a list with a nested map.
         *
         * @throws IOException If this happens, the test fails.
         */
        @ParameterizedTest
        @MethodSource
        void nestedList(JsonWriter.Style style, String expected) throws IOException {
            writeTest(
                    writer -> writer
                            .openList()
                            .openList()
                            .value("value")
                            .closeList()
                            .closeList(), style, expected
            );
        }

        static Stream<Arguments> nestedList() {
            return writeTestArgs(
                    //language=JSON
                    """
                    [["value"]]""",
                    //language=JSON
                    """
                    [
                      [
                        "value"
                      ]
                    ]"""
            );
        }
    }
}