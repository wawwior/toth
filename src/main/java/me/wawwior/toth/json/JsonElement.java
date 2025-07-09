package me.wawwior.toth.json;

import java.io.IOException;

public interface JsonElement {

    void write(JsonWriter writer) throws IOException;

}
