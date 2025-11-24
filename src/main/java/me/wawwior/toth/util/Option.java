package me.wawwior.toth.util;

import me.wawwior.toth.util.functors.Functor;
import me.wawwior.toth.util.functors.Monad;
import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Option<T> extends Obj<Option._Cat, T> {

    interface _Cat extends Cat {}

    static <T> Option<T> unbox(Obj<_Cat, T> obj) {
        return (Option<T>) obj;
    }

    enum Ops implements Monad<_Cat, Ops.K>, Obj<Ops.K, _Cat> {
        INSTANCE;

        interface K extends _Cat {}

        @Override
        public <A, B> Option<B> flatMap(Obj<Option._Cat, A> obj, Function<A, Obj<Option._Cat, B>> f) {
            Option<A> option = (Option<A>) obj;
            if (option instanceof Option.Some<A> some) {
                return (Option<B>) f.apply(some.value());
            }
            return Option.none();
        }

        @Override
        public <A> Option<A> pure(A a) {
            return a == null ? Option.none() : Option.some(a);
        }
    }

    static Ops ops() {
        return Ops.INSTANCE;
    }

    static <T> Option<T> some(@NotNull T t) {
        return new Some<>(t);
    }

    static <T> Option<T> none() {
        @SuppressWarnings("unchecked")
        Option<T> none = (Option<T>) None.INSTANCE;
        return none;
    }

    void ifPresent(Consumer<T> then);

    class Some<T> implements Option<T> {
        private final T value;

        private Some(T value) {
            this.value = value;
        }

        public T value() {
            return value;
        }

        @Override
        public void ifPresent(Consumer<T> then) {
            then.accept(value);
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }
    }

    class None<T> implements Option<T> {

        private static final None<?> INSTANCE = new None<>();

        private None() {}

        @Override
        public void ifPresent(Consumer<T> then) {}

        @Override
        public String toString() {
            return "None";
        }
    }

}
