package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;
import org.jetbrains.annotations.NotNull;

public interface Decoder<T> {

    @NotNull Result<T, String> decode(DataElement element);

}
