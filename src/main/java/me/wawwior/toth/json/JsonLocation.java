package me.wawwior.toth.json;

public enum JsonLocation {
    ROOT,
    LIST,
    EMPTY_LIST,
    MAP,
    EMPTY_MAP,
    KEY,
    CLOSED;

    public boolean isMap() {
        return this == MAP || this == EMPTY_MAP;
    }

    public boolean isList() {
        return this == LIST || this == EMPTY_LIST;
    }
}
