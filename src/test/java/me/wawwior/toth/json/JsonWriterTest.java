package me.wawwior.toth.json;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonWriterTest {

    /**
     * Utility method for creating tests.
     *
     * @param consumer the write operation
     * @param expected the expected result
     * @throws IOException If this happens, the test fails.
     */
    void compactTest(ThrowingConsumer<JsonWriter> consumer, String expected)
            throws IOException {

        StringWriter stringWriter = new StringWriter();
        consumer.accept(new JsonWriter(stringWriter, JsonWriter.Style.compact()));

        assertEquals(expected, stringWriter.toString());
    }

    /**
     * Utility method for creating tests.
     * <p>
     * The indent is set to 2 spaces
     *
     * @param consumer the write operation
     * @param expected the expected result
     * @throws IOException If this happens, the test fails.
     */
    void prettyTest(ThrowingConsumer<JsonWriter> consumer, String expected)
            throws IOException {

        StringWriter stringWriter = new StringWriter();
        consumer.accept(new JsonWriter(stringWriter, JsonWriter.Style.pretty("  ")));

        assertEquals(expected, stringWriter.toString());
    }

    interface ThrowingConsumer<T> {
        void accept(T t) throws IOException;
    }

    /**
     * Tests writing an object to a {@link JsonWriter}...
     */
    @Nested
    class ObjectTests {

        /**
         * ...with {@link JsonWriter.Style#compact()}.
         */
        @Nested
        class Compact {

            /**
             * Tests writing an empty json object.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void empty() throws IOException {
                compactTest(
                        writer -> writer.openObject().closeObject(),
                        //language=JSON
                        """
                        {}"""
                );
            }

            /**
             * Tests writing a json object with a key-value pair.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void basic() throws IOException {
                compactTest(
                        writer -> writer
                                .openObject()
                                .key("key")
                                .value("value")
                                .closeObject(),
                        //language=JSON
                        """
                        {"key":"value"}"""
                );
            }

            /**
             * Tests writing a json object with a nested object.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void nestedObject() throws IOException {
                compactTest(
                        writer -> writer
                                .openObject()
                                .key("foo")
                                .openObject()
                                .key("bar")
                                .value("baz")
                                .closeObject()
                                .closeObject(),
                        //language=JSON
                        """
                        {"foo":{"bar":"baz"}}"""
                );
            }

            /**
             * Tests writing a json object with a nested array.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void nestedArray() throws IOException {
                compactTest(
                        writer -> writer
                                .openObject()
                                .key("key")
                                .openArray()
                                .value("value")
                                .closeArray()
                                .closeObject(),
                        //language=JSON
                        """
                        {"key":["value"]}"""
                );
            }

        }

        /**
         * ...with {@link JsonWriter.Style#pretty(String)}.
         */
        @Nested
        class Pretty {

            /**
             * Tests writing an empty json object.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void empty() throws IOException {
                prettyTest(
                        writer -> writer.openObject().closeObject(),
                        //language=JSON
                        """
                        {}"""
                );
            }

            /**
             * Tests writing a json object with a key-value pair.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void basic() throws IOException {
                prettyTest(
                        writer -> writer
                                .openObject()
                                .key("key")
                                .value("value")
                                .closeObject(),
                        //language=JSON
                        """
                        {
                          "key": "value"
                        }"""
                );
            }

            /**
             * Tests writing a json object with a nested object.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void nestedObject() throws IOException {
                prettyTest(
                        writer -> writer
                                .openObject()
                                .key("foo")
                                .openObject()
                                .key("bar")
                                .value("baz")
                                .closeObject()
                                .closeObject(),
                        //language=JSON
                        """
                        {
                          "foo": {
                            "bar": "baz"
                          }
                        }"""
                );
            }

            /**
             * Tests writing a json object with a nested array.
             *
             * @throws IOException If this happens, the test fails.
             */
            @Test
            void nestedArray() throws IOException {
                prettyTest(
                        writer -> writer
                                .openObject()
                                .key("key")
                                .openArray()
                                .value("value")
                                .closeArray()
                                .closeObject(),
                        //language=JSON
                        """
                        {
                          "key": [
                            "value"
                          ]
                        }"""
                );
            }

        }

    }

    @Nested
    class ArrayTests {

        @Nested
        class Compact {

            /**
             * Tests writing an empty json array.
             *
             * @throws IOException If this happens, the test fails
             */
            @Test
            void empty() throws IOException {
                compactTest(
                        writer -> writer.openArray().closeArray(),
                        //language=JSON
                        """
                        []"""
                );
            }

            /**
             * Tests writing a json array with a value.
             *
             * @throws IOException If this happens, the test fails
             */
            @Test
            void basic() throws IOException {
                compactTest(
                        writer -> writer.openArray().value("value").closeArray(),
                        //language=JSON
                        """
                        ["value"]"""
                );
            }

            @Test
            void nestedObject() throws IOException {
                compactTest(
                        writer -> writer
                                .openArray()
                                .openObject()
                                .key("key")
                                .value("value")
                                .closeObject()
                                .closeArray(),
                        //language=JSON
                        """
                        [{"key":"value"}]"""
                );
            }

            @Test
            void nestedArray() throws IOException {
                compactTest(
                        writer -> writer
                                .openArray()
                                .openArray()
                                .value("value")
                                .closeArray()
                                .closeArray(),
                        //language=JSON
                        """
                        [["value"]]"""
                );
            }

        }


        @Nested
        class Pretty {

            /**
             * Tests writing a json array with a value.
             *
             * @throws IOException If this happens, the test fails
             */
            @Test
            void empty() throws IOException {
                prettyTest(
                        writer -> writer.openArray().closeArray(),
                        //language=JSON
                        """
                        []"""
                );
            }

            /**
             * Tests writing a json array with a value.
             *
             * @throws IOException If this happens, the test fails
             */
            @Test
            void basic() throws IOException {
                prettyTest(
                        writer -> writer.openArray().value("value").closeArray(),
                        //language=JSON
                        """
                        [
                          "value"
                        ]"""
                );
            }

            @Test
            void nestedObject() throws IOException {
                prettyTest(
                        writer -> writer
                                .openArray()
                                .openObject()
                                .key("key")
                                .value("value")
                                .closeObject()
                                .closeArray(),
                        //language=JSON
                        """
                        [
                          {
                            "key": "value"
                          }
                        ]"""
                );
            }

            @Test
            void nestedArray() throws IOException {
                prettyTest(
                        writer -> writer
                                .openArray()
                                .openArray()
                                .value("value")
                                .closeArray()
                                .closeArray(),
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
}