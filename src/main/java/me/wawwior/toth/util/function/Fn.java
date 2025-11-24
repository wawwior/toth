package me.wawwior.toth.util.function;

import me.wawwior.toth.util.functors.Symmetric;
import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Mor;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Fn<A, B> extends Mor<Fn._Cat, A, B> {

    interface _Cat extends Cat {}

    static  <A, B> Fn<A, B> unbox(Mor<_Cat, A, B> f) {
        return (Fn<A, B>) f;
    }

    enum Ops implements Symmetric<_Cat, Ops._Cat>, Obj<Ops._Cat, _Cat> {
        INSTANCE;

        interface _Cat extends Symmetric._Cat {}

        @Override
        public <A, B> Fn<A, B> lift(Function<A, B> f) {
            return f::apply;
        }

        @Override
        public <A, B, C> Fn<A, C> compose(Mor<Fn._Cat, A, B> f, Mor<Fn._Cat, B, C> g) {
            Fn<A, B> fn1 = (Fn<A, B>) f;
            Fn<B, C> fn2 = (Fn<B, C>) g;
            return a -> fn2.apply(fn1.apply(a));
        }

        @Override
        public <A, B, C> Fn<B, Fn<A, C>> flip(Mor<Fn._Cat, A, Mor<Fn._Cat, B, C>> mor) {
            return null;
        }

        @Override
        public <A> Fn<A, A> id() {
            return unbox(Symmetric.super.id());
        }

    }

    static Ops ops() {
        return Ops.INSTANCE;
    }

    B apply(A a);
}
