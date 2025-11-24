package me.wawwior.toth.util.functors;

import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Functor<F extends Cat, _C extends Functor._Cat> extends Obj<_C, F> {

    interface _Cat extends Cat {};

    static <F extends Cat> Functor<F, _Cat> unbox(Obj<_Cat, F> obj) {
        return (Functor<F, _Cat>) obj;
    }

    <A, B> Obj<F, B> map(Function<A, B> f, Obj<F, A> obj);

}
