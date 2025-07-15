package me.wawwior.toth.json;

import me.wawwior.toth.util.CatchingConsumer;
import me.wawwior.toth.util.CatchingFunction;
import me.wawwior.toth.util.Pair;
import me.wawwior.toth.util.Streams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonReaderTest {

    // Tests

    @ParameterizedTest
    @MethodSource
    void read_boolean_onRoot(String input, boolean expected) throws IOException {
        readTest(
                JsonReader::readBoolean,
                input,
                expected
        );
    }

    static Stream<Arguments> read_boolean_onRoot() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        true""",
                        """
                        false"""
                ),
                List.of(
                        true,
                        false
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_int_onRoot(String input, int expected) throws IOException {
        readTest(
                JsonReader::readInteger,
                input,
                expected
        );
    }

    static Stream<Arguments> read_int_onRoot() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        0""",
                        """
                        2147483647""",
                        """
                        -2147483648"""
                ),
                List.of(0, Integer.MAX_VALUE, Integer.MIN_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_long_onRoot(String input, long expected) throws IOException {
        readTest(
                JsonReader::readLong,
                input,
                expected
        );
    }

    static Stream<Arguments> read_long_onRoot() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        0""",
                        """
                        9223372036854775807""",
                        """
                        -9223372036854775808"""
                ),
                List.of(0, Long.MAX_VALUE, Long.MIN_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_float_onRoot(String input, float expected) throws IOException {
        readTest(
                JsonReader::readFloat,
                input,
                expected
        );
    }

    static Stream<Arguments> read_float_onRoot() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        0.0""",
                        """
                        1.7976931348623157e308""",
                        """
                        4.9e-324"""
                ),
                List.of(0, Float.MAX_VALUE, Float.MIN_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_double_onRoot(String input, double expected) throws IOException {
        readTest(
                JsonReader::readDouble,
                input,
                expected
        );
    }

    static Stream<Arguments> read_double_onRoot() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        0.0""",
                        """
                        1.7976931348623157e308""",
                        """
                        4.9e-324"""
                ),
                List.of(0, Double.MAX_VALUE, Double.MIN_VALUE)
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_String_onRoot(String input, String expected) throws IOException {
        readTest(
                JsonReader::readString,
                input,
                expected
        );
    }

    static Stream<Arguments> read_String_onRoot() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        "value\"""",
                        """
                        "\"""",
                        """
                        "\\"\\\\\\b\\f\\n\\r\\t\""""
                ),
                List.of("value", "", "\"\\\b\f\n\r\t")
        );
    }

    @Test
    void leaveMap_afterEnterMap() throws IOException {
        readTest(
                reader -> {
                    reader.enterMap();
                    reader.leaveMap();
                },
                //language=JSON
                """
                {}"""
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_boolean_afterKey_inMap(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                        reader -> {
                            reader.enterMap();
                            return reader.readKey();
                        },
                        reader -> {
                            boolean b = reader.readBoolean();
                            reader.leaveMap();
                            return b;
                        }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_boolean_afterKey_inMap() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        {"key":true}""",
                        """
                        {"key":false}"""
                ),
                //language=JSON
                List.of(
                        """
                        {
                          "key": true
                        }""",
                        """
                        {
                          "key": false
                        }"""
                ),
                List.of(
                        List.of("key", true),
                        List.of("key", false)
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_int_afterKey_inMap(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                        reader -> {
                            reader.enterMap();
                            return reader.readKey();
                        },
                        reader -> {
                            int b = reader.readInteger();
                            reader.leaveMap();
                            return b;
                        }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_int_afterKey_inMap() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        {"key":0}""",
                        """
                        {"key":2147483647}""",
                        """
                        {"key":-2147483648}"""
                ),
                //language=JSON
                List.of(
                        """
                        {
                          "key": 0
                        }""",
                        """
                        {
                          "key": 2147483647
                        }""",
                        """
                        {
                          "key": -2147483648
                        }"""
                ),
                List.of(
                        List.of("key", 0),
                        List.of("key", Integer.MAX_VALUE),
                        List.of("key", Integer.MIN_VALUE)
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_long_afterKey_inMap(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                        reader -> {
                            reader.enterMap();
                            return reader.readKey();
                        },
                        reader -> {
                            long b = reader.readLong();
                            reader.leaveMap();
                            return b;
                        }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_long_afterKey_inMap() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        {"key":0}""",
                        """
                        {"key":9223372036854775807}""",
                        """
                        {"key":-9223372036854775808}"""
                ),
                //language=JSON
                List.of(
                        """
                        {
                          "key": 0
                        }""",
                        """
                        {
                          "key": 9223372036854775807
                        }""",
                        """
                        {
                          "key": -9223372036854775808
                        }"""
                ),
                List.of(
                        List.of("key", 0),
                        List.of("key", Long.MAX_VALUE),
                        List.of("key", Long.MIN_VALUE)
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_float_afterKey_inMap(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                        reader -> {
                            reader.enterMap();
                            return reader.readKey();
                        },
                        reader -> {
                            float b = reader.readFloat();
                            reader.leaveMap();
                            return b;
                        }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_float_afterKey_inMap() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        {"key":0.0}""",
                        """
                        {"key":3.4028235e38}""",
                        """
                        {"key":1.4e-45}"""
                ),
                //language=JSON
                List.of(
                        """
                        {
                          "key": 0.0
                        }""",
                        """
                        {
                          "key": 3.4028235e38
                        }""",
                        """
                        {
                          "key": 1.4e-45
                        }"""
                ),
                List.of(
                        List.of("key", 0),
                        List.of("key", Float.MAX_VALUE),
                        List.of("key", Float.MIN_VALUE)
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_double_afterKey_inMap(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                        reader -> {
                            reader.enterMap();
                            return reader.readKey();
                        },
                        reader -> {
                            double b = reader.readDouble();
                            reader.leaveMap();
                            return b;
                        }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_double_afterKey_inMap() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        {"key":0.0}""",
                        """
                        {"key":3.4028235e38}""",
                        """
                        {"key":1.4e-45}"""
                ),
                //language=JSON
                List.of(
                        """
                        {
                          "key": 0.0
                        }""",
                        """
                        {
                          "key": 3.4028235e38
                        }""",
                        """
                        {
                          "key": 1.4e-45
                        }"""
                ),
                List.of(
                        List.of("key", 0),
                        List.of("key", Double.MAX_VALUE),
                        List.of("key", Double.MIN_VALUE)
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_String_afterKey_inMap(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                        reader -> {
                            reader.enterMap();
                            return reader.readKey();
                        },
                        reader -> {
                            String s = reader.readString();
                            reader.leaveMap();
                            return s;
                        }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_String_afterKey_inMap() {
        return multiListTestArgs(
                //language=JSON
                List.of(
                        """
                        {"key":"value"}""",
                        """
                        {"key":""}""",
                        """
                        {"key":"\\"\\\\\\b\\f\\n\\r\\t"}"""
                ),
                //language=JSON
                List.of(
                        """
                        {
                          "key": "value"
                        }""",
                        """
                        {
                          "key": ""
                        }""",
                        """
                        {
                          "key": "\\"\\\\\\b\\f\\n\\r\\t"
                        }"""
                ),
                List.of(
                        List.of("key", "value"),
                        List.of("key", ""),
                        List.of("key", "\"\\\b\f\n\r\t")
                )
        );
    }

    @SuppressWarnings("Convert2MethodRef")
    @ParameterizedTest
    @MethodSource
    void read_key_afterValue_inMap(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                    reader -> {
                        reader.enterMap();
                        return reader.readKey();
                    },
                    reader -> reader.readString(),
                    reader -> reader.readKey(),
                    reader -> {
                        String s = reader.readString();
                        reader.leaveMap();
                        return s;
                    }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_key_afterValue_inMap() {
        return listTestArgs(
                //language=JSON
                """
                {"key":"value","key2":"value"}""",
                //language=JSON
                """
                {
                  "key": "value",
                  "key2": "value"
                }""",
                List.of("key", "value", "key2", "value")
        );
    }

    @ParameterizedTest
    @MethodSource
    void enterMap_afterKey_inMap(String input, String expected) throws IOException {
        readTest(
                reader -> {
                    reader.enterMap();
                    String key = reader.readKey();
                    reader.enterMap();
                    reader.leaveMap();
                    reader.leaveMap();
                    return key;
                },
                input,
                expected
        );
    }

    static Stream<Arguments> enterMap_afterKey_inMap() {
        return testArgs(
                //language=JSON
                """
                {"key":{}}""",
                //language=JSON
                """
                {
                  "key": {}
                }""",
                "key"
        );
    }

    @ParameterizedTest
    @MethodSource
    void enterList_afterKey_inMap(String input, String expected) throws IOException {
        readTest(
                reader -> {
                    reader.enterMap();
                    String key = reader.readKey();
                    reader.enterList();
                    reader.leaveList();
                    reader.leaveMap();
                    return key;
                },
                input,
                expected
        );
    }

    static Stream<Arguments> enterList_afterKey_inMap() {
        return testArgs(
                //language=JSON
                """
                {"key":[]}""",
                //language=JSON
                """
                {
                  "key": []
                }""",
                "key"
        );
    }


    @Test
    void leaveList_afterEnterList() throws IOException {
        readTest(
                reader -> {
                    reader.enterList();
                    reader.leaveList();
                },
                //language=JSON
                """
                []"""
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_String_afterEnterList(String input, String expected) throws IOException {
        readTest(
                reader -> {
                    reader.enterList();
                    String s = reader.readString();
                    reader.leaveList();
                    return s;
                },
                input,
                expected
        );
    }

    static Stream<Arguments> read_String_afterEnterList() {
        return multiTestArgs(
                //language=JSON
                List.of(
                        """
                        ["value"]""",
                        """
                        [""]""",
                        """
                        ["\\"\\\\\\b\\f\\n\\r\\t"]"""
                ),
                //language=JSON
                List.of(
                        """
                        [
                          "value"
                        ]""",
                        """
                        [
                          ""
                        ]""",
                        """
                        [
                          "\\"\\\\\\b\\f\\n\\r\\t"
                        ]"""
                ),
                List.of("value", "", "\"\\\b\f\n\r\t")
        );
    }

    @ParameterizedTest
    @MethodSource
    void enterMap_afterEnterList(String input) throws IOException {
        readTest(
                reader -> {
                    reader.enterList();
                    reader.enterMap();
                    reader.leaveMap();
                    reader.leaveList();
                },
                input
        );
    }
    
    static Stream<Arguments> enterMap_afterEnterList() {
        return testArgs(
                //language=JSON
                """
                [{}]""",
                //language=JSON
                """
                [
                  {}
                ]"""
        );
    }

    @ParameterizedTest
    @MethodSource
    void enterList_afterEnterList(String input) throws IOException {
        readTest(
                reader -> {
                    reader.enterList();
                    reader.enterList();
                    reader.leaveList();
                    reader.leaveList();
                },
                input
        );
    }

    static Stream<Arguments> enterList_afterEnterList() {
        return testArgs(
                //language=JSON
                """
                [[]]""",
                //language=JSON
                """
                [
                  []
                ]"""
        );
    }

    @ParameterizedTest
    @MethodSource
    void read_afterValue_inList(String input, List<?> expected) throws IOException {
        listReadTest(
                List.of(
                    reader -> {
                        reader.enterList();
                        return reader.readString();
                    },
                    reader -> {
                        String s = reader.readString();
                        reader.leaveList();
                        return s;
                    }
                ),
                input,
                expected
        );
    }

    static Stream<Arguments> read_afterValue_inList() {
        return listTestArgs(
                //language=JSON
                """
                ["value","value"]""",
                //language=JSON
                """
                [
                  "value",
                  "value"
                ]""",
                List.of("value", "value")
        );
    }
    
    // Utility

    static <T> void readTest(
            CatchingFunction<JsonReader, T, IOException> step,
            String input,
            T expected
    ) throws IOException {
        readTest(
                reader -> assertEquals(expected, step.apply(reader)),
                input
        );
    }

    static void listReadTest(
            List<CatchingFunction<JsonReader, ?, IOException>> steps,
            String input,
            List<?> expected
    ) throws IOException {
        readTest(
                Streams.zip(
                        steps,
                        expected,
                        (step, e) ->
                                (CatchingConsumer<JsonReader, IOException>) reader -> assertEquals(e, step.apply(reader))
                ).toList(),
                input
        );
    }

    static void readTest(
            List<CatchingConsumer<JsonReader, IOException>> steps,
            String input
    ) throws IOException {
        StringReader stringReader = new StringReader(input);
        JsonReader reader = new JsonReader(stringReader);
        Streams.catchingForEach(steps, step -> step.accept(reader), IOException.class);
    }

    static void readTest(
            CatchingConsumer<JsonReader, IOException> step,
            String input
    ) throws IOException {
        StringReader stringReader = new StringReader(input);
        JsonReader reader = new JsonReader(stringReader);
        step.accept(reader);
    }
    
    static Stream<Arguments> testArgs(
            String compact,
            String pretty
    ) {
        return Stream.of(
                Arguments.of(compact),
                Arguments.of(pretty)
        );
    }

    static <T> Stream<Arguments> testArgs(
            String compact,
            String pretty,
            T expected
    ) {
        return Stream.of(
                Arguments.of(compact, expected),
                Arguments.of(pretty, expected)
        );
    }

    static Stream<Arguments> multiTestArgs(
            List<String> compact,
            List<String> pretty,
            List<?> expected
    ) {
        return Stream.concat(
                multiTestArgs(compact, expected),
                multiTestArgs(pretty, expected)
        );
    }

    static Stream<Arguments> multiTestArgs(
            List<String> inputs,
            List<?> expected
    ) {
        return Streams.zip(inputs, expected, Arguments::of);
    }

    static Stream<Arguments> listTestArgs(
            String compact,
            String pretty,
            List<?> expected
    ) {
        return Stream.of(
                Arguments.of(compact, expected),
                Arguments.of(pretty, expected)
        );
    }

    static Stream<Arguments> multiListTestArgs(
            List<String> compact,
            List<String> pretty,
            List<List<?>> expected
    ) {
        return Stream.concat(
                multiListTestArgs(compact, expected),
                multiListTestArgs(pretty, expected)
        );
    }

    static Stream<Arguments> multiListTestArgs(
            List<String> inputs,
            List<List<?>> expected
    ) {
        return Streams.zip(inputs, expected, Arguments::of);
    }

    /**
     * Utility method for creating tests.
     *
     * @param consumer the write operation
     * @param expected the expected exception
     */
    void throwsTest(CatchingConsumer<JsonReader, IOException> consumer, String input, String expected) {
        throwsTest(consumer, input, UnsupportedOperationException.class, expected);
    }

    /**
     * Utility method for creating tests.
     *
     * @param consumer the write operation
     * @param expected the expected exception
     */
    <T extends Exception> void throwsTest(CatchingConsumer<JsonReader, IOException> consumer,  String input, Class<T> exceptionType, String expected) {
        StringReader stringReader = new StringReader(input);
        T exception = assertThrows(
                exceptionType,
                () -> consumer.accept(new JsonReader(stringReader))
        );
        assertEquals(expected, exception.getMessage());
    }

}
