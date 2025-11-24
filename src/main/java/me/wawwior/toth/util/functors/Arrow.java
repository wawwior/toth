package me.wawwior.toth.util.functors;

import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Mor;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Arrow<F extends Cat, _C extends Arrow._Cat> extends Obj<_C, F> {

    interface _Cat extends Cat {}

    static <F extends Cat> Arrow<F, _Cat> unbox(Obj<_Cat, F> obj) {
        return (Arrow<F, _Cat>) obj;
    }

    <A, B> Mor<F, A, B> lift(Function<A, B> f);

    <A, B, C> Mor<F, A, C> compose(Mor<F, A, B> f, Mor<F, B, C> g);

    default <A> Mor<F, A, A> id() {
        return lift(Function.identity());
    };

}
