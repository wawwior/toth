package me.wawwior.toth.data;

import me.wawwior.toth.DataReader;
import me.wawwior.toth.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DataList extends DataElement implements Iterable<DataElement> {

    private final List<DataElement> elements = new ArrayList<>();

    public Optional<DataElement> get(int index) {
        try {
            return Optional.of(elements.get(index));
        } catch (IndexOutOfBoundsException ignored) {
            return Optional.empty();
        }
    }

    public void set(int index, DataElement element) {
        elements.set(index, element);
    }

    public void add(DataElement element) {
        elements.add(element);
    }

    public void remove(int index) {
        elements.remove(index);
    }

    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<DataElement> iterator() {
        return elements.iterator();
    }

    public static DataList read(DataReader reader) throws IOException {
        DataList list = new DataList();
        reader.enterList();
        while (reader.hasNext()) list.add(reader.nextType().read(reader));
        reader.leaveList();
        return list;
    }

    @Override
    public void write(DataWriter writer) throws IOException {
        writer.openList();
        for (DataElement element : elements) {
            element.write(writer);
        }
        writer.closeList();
    }

    @Override
    public Type<DataList> type() {
        return Type.LIST_TYPE;
    }
}
