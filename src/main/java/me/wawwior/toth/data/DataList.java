package me.wawwior.toth.data;

import com.google.gson.JsonElement;
import me.wawwior.toth.DataWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataList extends DataElement {

    private final List<DataElement> elements = new ArrayList<>();

    public DataElement get(int index) {
        return elements.get(index);
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
