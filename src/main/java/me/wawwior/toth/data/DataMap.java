package me.wawwior.toth.data;

import com.google.gson.stream.JsonReader;
import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataMap extends DataElement {

    private final Map<String, DataElement> elements = new HashMap<>();

    public DataElement get(String key) {
        return elements.get(key);
    }

    public void put(String key, DataElement element) {
        elements.put(key, element);
    }

    public void remove(String key) {
        elements.remove(key);
    }

    public static DataMap read(DataReader reader) throws IOException {
        DataMap map = new DataMap();
        reader.enterMap();
        while (reader.hasNext()) map.put(reader.readKey(), reader.nextType().read(reader));
        reader.leaveMap();
        return map;
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.openMap();
        for (Map.Entry<String, DataElement> entry : elements.entrySet()) {
            writer.key(entry.getKey());
            entry.getValue().write(writer);
        }
        writer.closeMap();
    }

    @Override
    public Type<DataMap> type() {
        return Type.MAP_TYPE;
    }
}
