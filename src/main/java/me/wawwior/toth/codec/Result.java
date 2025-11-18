package me.wawwior.toth.codec;

public interface Result<T, E> {

    boolean isPresent();

    T value();

    E error();

    default <N> Result<N, E> cast() {
        return (Result<N, E>) this;
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
