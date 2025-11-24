package me.wawwior.toth.util.functors;

import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Apply<F extends Cat, _C extends Apply._Cat> extends Functor<F, _C> {

    interface _Cat extends Functor._Cat {}

    static <F extends Cat> Apply<F, _Cat> unbox(Obj<_Cat, F> obj) {
        return (Apply<F, _Cat>) obj;
    }

    <A, B> Function<Obj<F, A>, Obj<F, B>> lift(Obj<F, Function<A, B>> obj);

    default  <A, B> Obj<F, B> apply(Obj<F, Function<A, B>> f, Obj<F, A> obj) {
        return lift(f).apply(obj);
    };

}
