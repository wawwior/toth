package me.wawwior.toth.codec;

import me.wawwior.toth.data.DataElement;
import me.wawwior.toth.data.DataList;
import me.wawwior.toth.data.DataMap;
import me.wawwior.toth.data.primitives.DataBoolean;
import me.wawwior.toth.data.primitives.DataNull;
import me.wawwior.toth.data.primitives.DataNumber;
import me.wawwior.toth.data.primitives.DataString;
import me.wawwior.toth.util.function.Boxable;
import me.wawwior.toth.util.function.BoxedFunction;
import me.wawwior.toth.util.function.Function2;
import me.wawwior.toth.util.function.Function3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class Codec<T> implements Encoder<T>, Decoder<T> {

    public static Codec<Boolean> BOOLEAN_CODEC = Codec.of(
            bool -> Result.result(new DataBoolean(bool)),
            data -> {
                if (data.type() != DataElement.Type.BOOLEAN_TYPE) return Result.error("Data Type is not Boolean!");
                return Result.result(data.as(DataElement.Type.BOOLEAN_TYPE).value());
            }
    );

    public static Codec<Integer> INT_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> {
                if (data.type() != DataElement.Type.NUMBER_TYPE) return Result.error("Data Type is not Number!");
                try {
                    return Result.result(data.as(DataElement.Type.NUMBER_TYPE).asInt());
                } catch (NumberFormatException e) {
                    return Result.error(e.getMessage());
                }
            }
    );

    public static Codec<Long> LONG_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> {
                if (data.type() != DataElement.Type.NUMBER_TYPE) return Result.error("Data Type is not Number!");
                try {
                    return Result.result(data.as(DataElement.Type.NUMBER_TYPE).asLong());
                } catch (NumberFormatException e) {
                    return Result.error(e.getMessage());
                }
            }
    );

    public static Codec<Float> FLOAT_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> {
                if (data.type() != DataElement.Type.NUMBER_TYPE) return Result.error("Data Type is not Number!");
                try {
                    return Result.result(data.as(DataElement.Type.NUMBER_TYPE).asFloat());
                } catch (NumberFormatException e) {
                    return Result.error(e.getMessage());
                }
            }
    );

    public static Codec<Double> DOUBLE_CODEC = Codec.of(
            number -> Result.result(new DataNumber(number)),
            data -> {
                if (data.type() != DataElement.Type.NUMBER_TYPE) return Result.error("Data Type is not Number!");
                try {
                    return Result.result(data.as(DataElement.Type.NUMBER_TYPE).asDouble());
                } catch (NumberFormatException e) {
                    return Result.error(e.getMessage());
                }
            }
    );

    public static Codec<String> STRING_CODEC = Codec.of(
            string -> Result.result(new DataString(string)),
            data -> {
                if (data.type() != DataElement.Type.STRING_TYPE) return Result.error("Data Type is not String!");
                return Result.result(data.as(DataElement.Type.STRING_TYPE).value());
            }
    );

    public static <T> Codec<T> of(Encoder<T> encoder, Decoder<T> decoder) {
        return new Codec<>() {

            @Override
            public @NotNull Result<DataElement, String> encode(T t) {
                return encoder.encode(t);
            }

            @Override
            public @NotNull Result<T, String> decode(DataElement element) {
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
                        if (!result.isPresent()) return result.cast();
                        data.add(result.value());
                    }
                    return Result.result(data);
                },
                data -> {
                    if (data.type() != DataElement.Type.LIST_TYPE) return Result.error("Data Type is not List!");
                    List<T> list = new ArrayList<>();
                    DataList dataList = data.as(DataElement.Type.LIST_TYPE);
                    for (DataElement element : dataList) {
                        Result<T, String> result = decode(element);
                        if (!result.isPresent()) return result.cast();
                        list.add(result.value());
                    }
                    return Result.result(list);
                }
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
                (map) -> {
                    if (map.type() != DataElement.Type.MAP_TYPE) return Result.error("Data Type is not Map!");
                    Optional<DataElement> data = map.as(DataElement.Type.MAP_TYPE).get(key);
                    if (data.isEmpty()) return Result.error("DataMap does not have key \"" + key + "\"!");
                    return decode(data.get());
                }
        );
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

    public static <T, P1> CodecGroup1<T, P1> group(
            BoundFieldCodec<T, P1> codec1
    ) {
        return builder -> of(
                t -> {
                    DataMap map = new DataMap();
                    Optional<String> optional = codec1.encode(t, map);
                    return optional.<Result<DataElement, String>>map(Result::error).orElseGet(() -> Result.result(map));
                },
                element -> {
                    Result<P1, String> result = codec1.decode(element);
                    if (!result.isPresent()) return result.cast();
                    return Result.result(builder.apply(result.value()));
                }
        );
    }

    public static <T, P1, P2> CodecGroup2<T, P1, P2> group(
            BoundFieldCodec<T, P1> codec1,
            BoundFieldCodec<T, P2> codec2
    ) {
        return builder -> of(
                t -> {
                    DataMap map = new DataMap();
                    Optional<String> optional1 = codec1.encode(t, map);
                    if (optional1.isPresent()) return Result.error(optional1.get());
                    Optional<String> optional2 = codec2.encode(t, map);
                    if (optional2.isPresent()) return Result.error(optional2.get());
                    return Result.result(map);
                },
                element -> {
                    Result<P1, String> result1 = codec1.decode(element);
                    if (!result1.isPresent()) return result1.cast();
                    Result<P2, String> result2 = codec2.decode(element);
                    if (!result2.isPresent()) return result2.cast();
                    return Result.result(builder.apply(result1.value(), result2.value()));
                }
        );
    }

    public static <T, P1, P2, P3> CodecGroup3<T, P1, P2, P3> group(
            BoundFieldCodec<T, P1> codec1,
            BoundFieldCodec<T, P2> codec2,
            BoundFieldCodec<T, P3> codec3
    ) {
        return builder -> of(
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
                    if (!result1.isPresent()) return result1.cast();
                    Result<P2, String> result2 = codec2.decode(element);
                    if (!result2.isPresent()) return result2.cast();
                    Result<P3, String> result3 = codec3.decode(element);
                    if (!result3.isPresent()) return result3.cast();
                    return Result.result(builder.apply(result1.value(), result2.value(), result3.value()));
                }
        );
    }

    public interface CodecGroup<T, P> {
        <B extends Boxable<BoxedFunction<P, T, B>>> Codec<T> build(B builder);
    }

    public interface CodecGroup1<T, P1> {

        Codec<T> build(Function<P1, T> builder);

    }

    public interface CodecGroup2<T, P1, P2> {

        Codec<T> build(Function2<P1, P2, T> builder);

    }

    public interface CodecGroup3<T, P1, P2, P3> {

        Codec<T> build(Function3<P1, P2, P3, T> builder);

    }

}
