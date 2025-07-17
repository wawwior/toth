package me.wawwior.toth.util;

public interface CatchingBiConsumer<T, U, E extends Exception> {

    void accept(T t, U u) throws E;

}
