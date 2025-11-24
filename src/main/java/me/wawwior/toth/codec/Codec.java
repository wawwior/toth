package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.DataList;
import me.wawwior.toth.data.DataMap;
import me.wawwior.toth.data.primitives.DataBoolean;
import me.wawwior.toth.data.primitives.DataNull;
import me.wawwior.toth.data.primitives.DataNumber;
import me.wawwior.toth.data.primitives.DataString;
import me.wawwior.toth.util.function.*;
import me.wawwior.toth.util.tuple.Tuple1;
import me.wawwior.toth.util.tuple.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class Codec<T> implements Encoder<T>, Decoder<T> {

    public static Codec<Boolean> BOOLEAN_CODEC = Codec.of(
            bool -> Result.result(new DataBoolean(bool)),
            data -> typedResult(DataElement.Type.BOOLEAN_TYPE, data).mapValue(DataBoolean::value)
    );

    public static Codec<Integer> INT_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> typedResult(DataElement.Type.NUMBER_TYPE, data).flatMapValue(d -> {
               try {
                   return Result.result(d.asInt());
               } catch (NumberFormatException e) {
                   return Result.error(e.getMessage());
               }
            })
    );

    public static Codec<Long> LONG_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> typedResult(DataElement.Type.NUMBER_TYPE, data).flatMapValue(d -> {
                try {
                    return Result.result(d.asLong());
                } catch (NumberFormatException e) {
                    return Result.error(e.getMessage());
                }
            })
    );

    public static Codec<Float> FLOAT_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> typedResult(DataElement.Type.NUMBER_TYPE, data).flatMapValue(d -> {
                try {
                    return Result.result(d.asFloat());
                } catch (NumberFormatException e) {
                    return Result.error(e.getMessage());
                }
            })
    );

    public static Codec<Double> DOUBLE_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> typedResult(DataElement.Type.NUMBER_TYPE, data).flatMapValue(d -> {
                try {
                    return Result.result(d.asDouble());
                } catch (NumberFormatException e) {
                    return Result.error(e.getMessage());
                }
            })
    );

    public static Codec<String> STRING_CODEC = Codec.of(
            string -> Result.result(new DataString(string)),
            data -> typedResult(DataElement.Type.STRING_TYPE, data).mapValue(DataString::value)
    );

    public static <T> Codec<T> of(Encoder<T> encoder, Decoder<T> decoder) {
        return new Codec<>() {

            @Override
            public Result<DataElement, String> encode(T t) {
                return encoder.encode(t);
            }

            @Override
            public Result<T, String> decode(DataElement element) {
                return decoder.decode(element);
            }
        };
    }

    public final Codec<List<T>> listOf() {
        return Codec.of(
                list -> {
                    DataList data = new DataList();
                    for (T t : list) {
                        Result<DataElement, String> result = encode(t);
                        if (!result.isPresent()) return result.castError();
                        data.add(result.value());
                    }
                    return Result.result(data);
                },
                data -> typedResult(DataElement.Type.LIST_TYPE, data).flatMapValue(dataList -> {
                    List<T> list = new ArrayList<>();
                    for (DataElement element : dataList) {
                        Result<T, String> result = decode(element);
                        if (!result.isPresent()) return result.castError();
                        list.add(result.value());
                    }
                    return Result.result(list);
                })
        );
    }

    public final FieldCodec<T> fieldOf(String key) {
        return FieldCodec.of(
                (t, map) -> {
                    Result<DataElement, String> result = encode(t);
                    if (!result.isPresent()) return Optional.of(result.error());
                    map.put(key, result.value());
                    return Optional.empty();
                },
                (element) -> typedResult(DataElement.Type.MAP_TYPE, element)
                        .flatMapValue(map -> map.get(key)
                                .map(this::decode)
                                .orElseGet(() -> Result.error("DataMap does not have key \"" + key + "\"!")))
        );
    }

    private static <T extends DataElement> Result<T, String> typedResult(DataElement.Type<T> expectedType, DataElement element) {
        if (expectedType != element.type()) return Result.error("Expected \"" + expectedType + "\", got \"" + element.type() + "\"!");
        return Result.result(element.as(expectedType));
    }

    public final Codec<T> nullable() {
        return Codec.of(
                t -> {
                    if (t == null) return Result.result(DataNull.INSTANCE);
                    return this.encode(t);
                },
                element -> {
                    if (element.type() == DataElement.Type.NULL_TYPE) return Result.result(null);
                    return this.decode(element);
                }
        );
    }

    public static <T, P1> CodecGroup<T, Tuple1<P1>> group(
            Class<T> ignoredMu,
            BoundFieldCodec<T, P1> codec
    ) {
        return new CodecGroup<>() {
            @Override
            public <B extends Boxable<BoxedFunction<Tuple1<P1>, T, B>>> Codec<T> build(B builder) {
                return of(
                        t -> {
                            DataMap map = new DataMap();
                            Optional<String> optional = codec.encode(t, map);
                            return optional.<Result<DataElement, String>>map(Result::error).orElseGet(() -> Result.result(map));
                        },
                        element -> codec.decode(element).mapValue(p1 -> builder.box().apply(Tuple1.of(p1)))
                );
            }
        };
    }

    public static <T, P1, P2, P3> CodecGroup<T, Tuple3<P1, P2, P3>> group(
            Class<T> ignoredMu,
            BoundFieldCodec<T, P1> codec1,
            BoundFieldCodec<T, P2> codec2,
            BoundFieldCodec<T, P3> codec3
    ) {
        return new CodecGroup<T, Tuple3<P1, P2, P3>>() {
            @Override
            public <B extends Boxable<BoxedFunction<Tuple3<P1, P2, P3>, T, B>>> Codec<T> build(B builder) {
                return of(
                        t -> {
                            DataMap map = new DataMap();
                            Optional<String> optional1 = codec1.encode(t, map);
                            if (optional1.isPresent()) return Result.error(optional1.get());
                            Optional<String> optional2 = codec2.encode(t, map);
                            if (optional2.isPresent()) return Result.error(optional2.get());
                            Optional<String> optional3 = codec3.encode(t, map);
                            if (optional3.isPresent()) return Result.error(optional3.get());
                            return Result.result(map);
                        },
                        element -> {
                            Result<P1, String> result1 = codec1.decode(element);
                            if (!result1.isPresent()) return result1.castError();
                            Result<P2, String> result2 = codec2.decode(element);
                            if (!result2.isPresent()) return result2.castError();
                            Result<P3, String> result3 = codec3.decode(element);
                            if (!result3.isPresent()) return result3.castError();
                            return Result.result(builder.box().apply(Tuple3.of(result1.value(), result2.value(), result3.value())));
                        }
                );
            }
        };
    }

    public interface CodecGroup<T, P> {
        <B extends Boxable<BoxedFunction<P, T, B>>> Codec<T> build(B builder);
    }

}
