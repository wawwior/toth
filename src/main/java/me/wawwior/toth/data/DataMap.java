package me.wawwior.toth.data;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;

import java.io.IOException;
import java.util.*;

public final class DataMap extends DataElement {

    private final Map<String, DataElement> elements = new LinkedHashMap<>();

    public Optional<DataElement> get(String key) {
        return Optional.ofNullable(elements.get(key));
    }

    public <T extends DataElement> Optional<T> getAs(String key, Type<T> type) {
        return Optional.ofNullable(elements.get(key)).flatMap(element -> {
            try {
                return Optional.of(type.cast(element));
            } catch (IllegalArgumentException ignored) {
                return Optional.empty();
            }
        });
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
        while (reader.hasNext()) {
            String key = reader.readKey();
            Type<?> type = reader.nextType();
            map.put(key, type.read(reader));
        }
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
