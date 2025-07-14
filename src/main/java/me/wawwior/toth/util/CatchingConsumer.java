package me.wawwior.toth.util;

public interface CatchingConsumer<T, E extends Exception> {
    void accept(T t) throws E;
}
