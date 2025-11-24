package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataMap;

import java.util.Optional;

public interface FieldEncoder<T> {

    Optional<String> encode(T element, DataMap map);

}
