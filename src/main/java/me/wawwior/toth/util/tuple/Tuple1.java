package me.wawwior.toth.util.tuple;

public interface Tuple1<T> {

    T one();

    static <T> Tuple1<T> of(T t) {
        return () -> t;
    }

}
