package me.wawwior.toth.util.functors;

import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Mor;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Profunctor<F extends Cat, _C extends Profunctor._Cat> extends Obj<_C, F> {

    interface _Cat extends Cat {}

    static <F extends Cat> Profunctor<F, _Cat> unbox(Obj<_Cat, F> obj) {
        return (Profunctor<F, _Cat>) obj;
    }

    <A, B, C, D> Mor<F, C, D> dimap(Mor<F, A, B> mor, Function<C, A> f, Function<B, D> g);

}
