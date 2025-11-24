package me.wawwior.toth.codec;

import me.wawwior.toth.util.function.Fn;
import me.wawwior.toth.util.tuple.Tuple2;

public interface Result<T, E> {

    boolean isPresent();

    T value();

    E error();

    default  <F> Result<F, E> mapValue(Fn<T, F> mapping) {
        if (isPresent()) return Result.result(mapping.apply(value()));
        return castError();
    }

    default <F> Result<F, E> flatMapValue(Fn<T, Result<F, E>> mapping) {
        if (isPresent()) return mapping.apply(value());
        return castError();
    }

    default <N> Result<N, E> castError() {
        return Result.error(this.error());
    }

    default <T2> Result<Tuple2<T, T2>, E> and(Result<T2, E> other) {
        if (!this.isPresent()) return this.castError();
        if (!other.isPresent()) return other.castError();
        return Result.result(Tuple2.of(this.value(), other.value()));
    }

    static <T, E> Result<T, E> error(E error) {
        return new Result<>() {
            @Override
            public boolean isPresent() {
                return false;
            }

            @Override
            public T value() {
                return null;
            }

            @Override
            public E error() {
                return error;
            }
        };
    }

    static <T, E> Result<T, E> result(T t) {
        return new Result<>() {
            @Override
            public boolean isPresent() {
                return true;
            }

            @Override
            public T value() {
                return t;
            }

            @Override
            public E error() {
                return null;
            }
        };
    }

}
