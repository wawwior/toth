package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataMap;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface FieldEncoder<T> {

    @NotNull Optional<String> encode(T element, DataMap map);

}
