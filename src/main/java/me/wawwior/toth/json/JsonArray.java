package me.wawwior.toth.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonArray implements JsonElement {

    private List<JsonElement> elements = new ArrayList<>();

    public JsonElement get(int index) {
        return elements.get(index);
    }

    public void set(int index, JsonElement element) {
        elements.set(index, element);
    }

    public void add(JsonElement element) {
        elements.add(element);
    }

    public void remove(int index) {
        elements.remove(index);
    }

    @Override
    public void write(JsonWriter writer) throws IOException {
        writer.openArray();
        for (JsonElement element : elements) {
            element.write(writer);
        }
        writer.closeArray();
    }
}
