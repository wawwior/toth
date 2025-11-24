package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;

public interface Encoder<T> {

    Result<DataElement, String> encode(T t);
}
