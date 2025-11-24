package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.DataMap;
import me.wawwior.toth.json.JsonReader;
import me.wawwior.toth.json.JsonWriter;
import me.wawwior.toth.util.StringCursor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CodecTest {

    @Test
    void test() throws IOException {

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