package me.wawwior.toth.util.functors;

import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Applicative<F extends Cat, _C extends Applicative._Cat> extends Apply<F, _C> {

    interface _Cat extends Apply._Cat {}

    static <F extends Cat> Applicative<F, _Cat> unbox(Obj<_Cat, F> obj) {
        return (Applicative<F, _Cat>) obj;
    }

    <A> Obj<F, A> pure(A a);

    @Override
    default <A, B> Obj<F, B> map(Function<A, B> f, Obj<F, A> obj) {
        return lift(pure(f)).apply(obj);
    }
}
