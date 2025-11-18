package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.DataMap;
import me.wawwior.toth.json.JsonReader;
import me.wawwior.toth.json.JsonWriter;
import me.wawwior.toth.util.StringCursor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CodecTest {

    @Test
    void test() throws IOException {

        record TestClass(int i, List<String> strings, String nullString) {}

        Codec<TestClass> testCodec = Codec.group(
                Codec.INT_CODEC.fieldOf("int").bind(TestClass::i),
                Codec.STRING_CODEC.listOf().fieldOf("strings").bind(TestClass::strings),
                Codec.STRING_CODEC.nullable().fieldOf("null_string").bind(TestClass::nullString)
        ).build(TestClass::new);

        TestClass testObject = new TestClass(10, List.of("Hello", "JSON!"), null);

        Result<DataElement, String> element = testCodec.encode(testObject);

        assertTrue(element.isPresent());

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter, JsonWriter.Style.pretty("  "));

        element.value().write(jsonWriter);

        System.out.println(stringWriter);

        StringCursor cursor = new StringCursor(stringWriter.toString());
        JsonReader reader = new JsonReader(cursor);

        DataMap map = DataMap.read(reader);

        Result<TestClass, String> result = testCodec.decode(map);

        System.out.println(result.error());

        assertTrue(result.isPresent());
        TestClass testObject2 = result.value();
        assertEquals(testObject.i, testObject2.i);
        assertIterableEquals(testObject.strings, testObject2.strings);
    }

    @Test
    void readNull() throws IOException {

        DataMap map = new DataMap();
        map.put("string", Codec.STRING_CODEC.encode(null).value());

        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter, JsonWriter.Style.pretty("  "));

        map.write(jsonWriter);

        System.out.println(stringWriter);

        StringCursor stringCursor = new StringCursor(stringWriter.toString());
        JsonReader jsonReader = new JsonReader(stringCursor);

        DataMap readMap = DataMap.read(jsonReader);
        Optional<DataElement> element = readMap.get("string");
        assertTrue(element.isPresent());
        assertEquals(DataElement.Type.NULL_TYPE, element.get().type());

    }

}