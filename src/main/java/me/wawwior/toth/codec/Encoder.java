package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;
import org.jetbrains.annotations.NotNull;

public interface Encoder<T> {

    @NotNull Result<DataElement, String> encode(T t);

}
