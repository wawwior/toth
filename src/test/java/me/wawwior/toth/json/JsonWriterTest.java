package me.wawwior.toth.json;

import me.wawwior.toth.util.Pair;
import me.wawwior.toth.util.Streams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
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
     * @param expected the expected result.
     * @return the stream of arguments
     */
    private static Stream<Arguments> testArgs(String expected) {
        return testArgs(expected, expected);
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
    private static Stream<Arguments> testArgs(String compact, String pretty) {
        return Stream.of(
                Arguments.of(JsonWriter.Style.compact(), compact),
                Arguments.of(JsonWriter.Style.pretty("  "), pretty)
        );
    }

    private static <T> Stream<Arguments> valueTestArgs(T value, String expected) {
        return valueTestArgs(value, expected, expected);
    }

    private static <T> Stream<Arguments> multiValueTestArgs(
            List<T> values,
            List<String> expected
    ) {
        return Streams.zip(
                values.stream(),
                expected.stream(),
                (v, e) -> valueTestArgs(v, e, e)
        ).flatMap(s -> s);
    }

    private static <T> Stream<Arguments> valueTestArgs(
            T value,
            String compact,
            String pretty
    ) {
        return Stream.of(
                Arguments.of(value, JsonWriter.Style.compact(), compact),
                Arguments.of(value, JsonWriter.Style.pretty("  "), pretty)
        );
    }

    private static <T> Stream<Arguments> multiValueTestArgs(
            List<T> values,
            List<String> compact,
            List<String> pretty
    ) {
        return Streams.zip(
                values.stream(),
                Streams.zip(compact, pretty, Pair::of),
                (v, pair) -> valueTestArgs(v, pair.a(), pair.b())
        ).flatMap(s -> s);
    }

    /**
     * Utility method for creating tests.
     *
     * @param consumer the write operation
     * @param expected the expected exception
     */
    void throwsTest(ThrowingConsumer<JsonWriter> consumer, String expected) {
        throwsTest(consumer, IllegalArgumentException.class, expected);
    }

    /**
     * Utility method for creating tests.
     *
     * @param consumer the write operation
     * @param expected the expected exception
     */
    <T extends Exception> void throwsTest(ThrowingConsumer<JsonWriter> consumer, Class<T> exceptionType, String expected) {
        StringWriter stringWriter = new StringWriter();
        T exception = assertThrows(
                exceptionType,
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

    @Test
    void value_afterOpenMap() {
        throwsTest(writer -> writer.openMap().value("value"), "State is EMPTY_MAP!");
    }

    @Test
    void value_afterValue_onRoot() {
        throwsTest(writer -> writer.value("value").value("value"), "State is CLOSED!");
    }

    @Test
    void key_afterOpenList() {
        throwsTest(
                writer -> writer.openList().key("key"),
                "State is EMPTY_LIST, expected MAP!"
        );
    }

    @Test
    void key_onRoot() {
        throwsTest(writer -> writer.key("key"), "State is ROOT, expected MAP!");
    }

    @SuppressWarnings("Convert2MethodRef")
    @Test
    void closeMap_onRoot() {
        throwsTest(
                writer -> writer.closeMap(),
                "State is ROOT, expected MAP or EMPTY_MAP!"
        );
    }

    @Test
    void closeMap_afterOpenList() {
        throwsTest(
                writer -> writer.openList().closeMap(),
                "State is EMPTY_LIST, expected MAP or EMPTY_MAP!"
        );
    }

    @Test
    void closeMap_afterKey() {
        throwsTest(
                writer -> writer.openMap().key("key").closeMap(),
                "State is KEY, expected MAP or EMPTY_MAP!"
        );
    }


    @Test
    void closeMap_afterValue_inList() {
        throwsTest(
                writer -> writer.openList().value("value").closeMap(),
                "State is LIST, expected MAP or EMPTY_MAP!"
        );
    }

    @SuppressWarnings("Convert2MethodRef")
    @Test
    void closeList_onRoot() {
        throwsTest(
                writer -> writer.closeList(),
                "State is ROOT, expected LIST or EMPTY_LIST!"
        );
    }

    @Test
    void closeList_afterOpenMap() {
        throwsTest(
                writer -> writer.openMap().closeList(),
                "State is EMPTY_MAP, expected LIST or EMPTY_LIST!"
        );
    }

    @Test
    void closeList_afterKey() {
        throwsTest(
                writer -> writer.openMap().key("key").closeList(),
                "State is KEY, expected LIST or EMPTY_LIST!"
        );
    }

    @Test
    void closeList_afterValue_inMap() {
        throwsTest(
                writer -> writer.openMap().key("key").value("value").closeList(),
                "State is MAP, expected LIST or EMPTY_LIST!"
        );
    }

    @Test
    void value_String_null() {
        throwsTest(
                writer -> writer.openMap().key("key").value(null),
                NullPointerException.class,
                "string should not be null!"
        );
    }

    @Test
    void value_float_Infinity() {
        throwsTest(
                writer -> writer.value(Float.POSITIVE_INFINITY),
                "float with value Infinity is not valid in json!"
        );
    }

    @Test
    void value_float_NaN() {
        throwsTest(
                writer -> writer.value(Float.NaN),
                "float with value NaN is not valid in json!"
        );
    }

    @Test
    void value_double_Infinity() {
        throwsTest(
                writer -> writer.value(Double.POSITIVE_INFINITY),
                "double with value Infinity is not valid in json!"
        );
    }

    @Test
    void value_double_NaN() {
        throwsTest(
                writer -> writer.value(Double.NaN),
                "double with value NaN is not valid in json!"
        );
    }

    @ParameterizedTest
    @MethodSource
    void value_boolean_onRoot(boolean value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(writer -> writer.value(value), style, expected);
    }

    static Stream<Arguments> value_boolean_onRoot() {
        return multiValueTestArgs(
                List.of(true, false),
                //language=JSON
                List.of(
                        """
                        true""",
                        """
                        false"""
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void value_int_onRoot(int value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(writer -> writer.value(value), style, expected);
    }

    static Stream<Arguments> value_int_onRoot() {
        return multiValueTestArgs(
                List.of(0, Integer.MAX_VALUE, Integer.MIN_VALUE),
                //language=JSON
                List.of(
                        """
                        0""",
                        """
                        2147483647""",
                        """
                        -2147483648"""
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void value_long_onRoot(long value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(writer -> writer.value(value), style, expected);
    }

    static Stream<Arguments> value_long_onRoot() {
        return multiValueTestArgs(
                List.of(0L, Long.MAX_VALUE, Long.MIN_VALUE),
                //language=JSON
                List.of(
                        """
                        0""",
                        """
                        9223372036854775807""",
                        """
                        -9223372036854775808"""
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void value_float_onRoot(float value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(writer -> writer.value(value), style, expected);
    }

    static Stream<Arguments> value_float_onRoot() {
        return multiValueTestArgs(
                List.of(0, Float.MAX_VALUE, Float.MIN_VALUE),
                //language=JSON
                List.of(
                        """
                        0.0""",
                        """
                        3.4028235e38""",
                        """
                        1.4e-45"""
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void value_double_onRoot(double value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(writer -> writer.value(value), style, expected);
    }

    static Stream<Arguments> value_double_onRoot() {
        return multiValueTestArgs(
                List.of(0, Double.MAX_VALUE, Double.MIN_VALUE),
                //language=JSON
                List.of(
                        """
                        0.0""",
                        """
                        1.7976931348623157e308""",
                        """
                        4.9e-324"""
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void value_String_onRoot(String value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.value(value),
                style,
                expected
        );
    }

    static Stream<Arguments> value_String_onRoot() {
        return multiValueTestArgs(
                List.of("value", "", "\"\\\b\f\n\r\t"),
                //language=JSON
                List.of(
                        """
                        "value\"""",
                        """
                        "\"""",
                        """
                        "\\"\\\\\\b\\f\\n\\r\\t\""""
                )
        );
    }

    /**
     * Tests writing an empty map.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void closeMap_afterOpenMap(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(writer -> writer.openMap().closeMap(), style, expected);
    }

    static Stream<Arguments> closeMap_afterOpenMap() {
        return testArgs(
                //language=JSON
                """
                {}""");
    }

    /**
     * Tests writing a map with a key-value pair.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_boolean_afterKey_inMap(
            boolean value,
            JsonWriter.Style style,
            String expected
    )
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").value(value).closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_boolean_afterKey_inMap() {
        //language=JSON
        return multiValueTestArgs(
                List.of(true, false),
                List.of(
                        """
                        {"key":true}""",
                        """
                        {"key":false}"""
                ),
                List.of(
                        """
                        {
                          "key": true
                        }""",
                        """
                        {
                          "key": false
                        }"""
                )
        );
    }

    /**
     * Tests writing a map with a key-value pair.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_int_afterKey_inMap(int value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").value(value).closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_int_afterKey_inMap() {
        //language=JSON
        return multiValueTestArgs(
                List.of(0, Integer.MAX_VALUE, Integer.MIN_VALUE),
                List.of(
                        """
                        {"key":0}""",
                        """
                        {"key":2147483647}""",
                        """
                        {"key":-2147483648}"""
                ),
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
                )
        );
    }

    /**
     * Tests writing a map with a key-value pair.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_long_afterKey_inMap(long value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").value(value).closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_long_afterKey_inMap() {
        //language=JSON
        return multiValueTestArgs(
                List.of(0, Long.MAX_VALUE, Long.MIN_VALUE),
                List.of(
                        """
                        {"key":0}""",
                        """
                        {"key":9223372036854775807}""",
                        """
                        {"key":-9223372036854775808}"""
                ),
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
                )
        );
    }

    /**
     * Tests writing a map with a key-value pair.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_float_afterKey_inMap(float value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").value(value).closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_float_afterKey_inMap() {
        //language=JSON
        return multiValueTestArgs(
                List.of(0, Float.MAX_VALUE, Float.MIN_VALUE),
                List.of(
                        """
                        {"key":0.0}""",
                        """
                        {"key":3.4028235e38}""",
                        """
                        {"key":1.4e-45}"""
                ),
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
                )
        );
    }

    /**
     * Tests writing a map with a key-value pair.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_double_afterKey_inMap(
            double value,
            JsonWriter.Style style,
            String expected
    )
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").value(value).closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_double_afterKey_inMap() {
        //language=JSON
        return multiValueTestArgs(
                List.of(0, Double.MAX_VALUE, Double.MIN_VALUE),
                List.of(
                        """
                        {"key":0.0}""",
                        """
                        {"key":1.7976931348623157e308}""",
                        """
                        {"key":4.9e-324}"""
                ),
                List.of(
                        """
                        {
                          "key": 0.0
                        }""",
                        """
                        {
                          "key": 1.7976931348623157e308
                        }""",
                        """
                        {
                          "key": 4.9e-324
                        }"""
                )
        );
    }

    /**
     * Tests writing a map with a key-value pair.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_String_afterKey_inMap(
            String value,
            JsonWriter.Style style,
            String expected
    )
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").value(value).closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_String_afterKey_inMap() {
        return multiValueTestArgs(
                List.of("value", "", "\"\\\b\f\n\r\t"),
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
                )
        );
    }

    /**
     * Tests writing a map with a key-value pair.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void key_afterValue_inMap(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openMap()
                                .key("key")
                                .value("value")
                                .key("key2")
                                .value("value")
                                .closeMap(), style, expected
        );
    }

    static Stream<Arguments> key_afterValue_inMap() {
        return testArgs(
                //language=JSON
                """
                {"key":"value","key2":"value"}""",
                //language=JSON
                """
                {
                  "key": "value",
                  "key2": "value"
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
    void openMap_afterKey_inMap(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").openMap().closeMap().closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> openMap_afterKey_inMap() {
        return testArgs(
                //language=JSON
                """
                {"key":{}}""",
                //language=JSON
                """
                {
                  "key": {}
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
    void openList_afterKey_inMap(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openMap().key("key").openList().closeList().closeMap(),
                style,
                expected
        );
    }

    static Stream<Arguments> openList_afterKey_inMap() {
        return testArgs(
                //language=JSON
                """
                {"key":[]}""",
                //language=JSON
                """
                {
                  "key": []
                }"""
        );
    }

    /**
     * Tests writing an empty list.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void closeList_afterOpenList(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(writer -> writer.openList().closeList(), style, expected);
    }

    static Stream<Arguments> closeList_afterOpenList() {
        return testArgs(
                //language=JSON
                """
                []""");
    }

    // TODO: write Tests for values in lists

    /**
     * Tests writing a list with a values.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_String_afterOpenList(String value, JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openList().value(value).closeList(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_String_afterOpenList() {
        return multiValueTestArgs(
                List.of("value", "", "\"\\\b\f\n\r\t"),
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
                )
        );
    }

    /**
     * Tests writing a list with a nested map.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void openMap_afterOpenList(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openList().openMap().closeMap().closeList(),
                style,
                expected
        );
    }

    static Stream<Arguments> openMap_afterOpenList() {
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

    /**
     * Tests writing a list with a nested map.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void openList_afterOpenList(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openList().openList().closeList().closeList(),
                style,
                expected
        );
    }

    static Stream<Arguments> openList_afterOpenList() {
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

    /**
     * Tests writing a list with a values.
     *
     * @throws IOException If this happens, the test fails.
     */
    @ParameterizedTest
    @MethodSource
    void value_afterValue_inList(JsonWriter.Style style, String expected)
            throws IOException {
        writeTest(
                writer -> writer.openList().value("value").value("value").closeList(),
                style,
                expected
        );
    }

    static Stream<Arguments> value_afterValue_inList() {
        return testArgs(
                //language=JSON
                """
                ["value","value"]""",
                //language=JSON
                """
                [
                  "value",
                  "value"
                ]"""
        );
    }

}