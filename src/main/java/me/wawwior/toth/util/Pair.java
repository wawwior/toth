package me.wawwior.toth.util;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record Pair<A, B>(A a, B b) {

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair<>(a, b);
    }

    public void consume(BiConsumer<A, B> consumer) {
        consumer.accept(a, b);
    }

    public <E extends Exception> void catchingConsume(CatchingBiConsumer<A, B, E> consumer) throws E {
        consumer.accept(a, b);
    }

}
