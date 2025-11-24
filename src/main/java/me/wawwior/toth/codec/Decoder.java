package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;

public interface Decoder<T> {

    Result<T, String> decode(DataElement element);

}
