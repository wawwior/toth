package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.DataMap;

import java.util.Optional;
import java.util.function.Function;

public abstract class FieldCodec<T> implements FieldEncoder<T>, Decoder<T>  {

    public static <T> FieldCodec<T> of(FieldEncoder<T> encoder, Decoder<T> decoder) {
        return new FieldCodec<>() {
            @Override
            public Optional<String> encode(T element, DataMap map) {
                return encoder.encode(element, map);
            }

            @Override
            public Result<T, String> decode(DataElement element) {
                return decoder.decode(element);
            }
        };
    }

    public <O> BoundFieldCodec<O, T> bind(Function<O, T> getter) {
        return new BoundFieldCodec<>() {
            @Override
            public Result<T, String> decode(DataElement element) {
                return FieldCodec.this.decode(element);
            }

            @Override
            public Optional<String> encode(O element, DataMap map) {
                return FieldCodec.this.encode(getter.apply(element), map);
            }
        };
    }
}
