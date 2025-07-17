package me.wawwior.toth.util;

public interface CatchingFunction<T, R, E extends Exception> {

    R apply(T t) throws E;

}
