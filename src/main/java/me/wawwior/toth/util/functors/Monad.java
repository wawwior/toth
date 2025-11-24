package me.wawwior.toth.util.functors;

import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Monad<F extends Cat, _C extends Monad._Cat> extends Applicative<F, _C> {

    interface _Cat extends Applicative._Cat {}

    static <F extends Cat> Monad<F, _Cat> unbox(Obj<_Cat, F> obj) {
        return (Monad<F, _Cat>) obj;
    }

    <A, B> Obj<F, B> flatMap(Obj<F, A> obj, Function<A, Obj<F, B>> f);

    // Derived Apply method
    @Override
    default <A, B> Function<Obj<F, A>, Obj<F, B>> lift(Obj<F, Function<A, B>> obj) {
        return a -> flatMap(a, _a -> flatMap(obj, g -> pure(g.apply(_a))));
    }

    // Derived Functor method
    @Override
    default <A, B> Obj<F, B> map(Function<A, B> f, Obj<F, A> obj) {
        return flatMap(obj, _a -> pure(f.apply(_a)));
    }
}
