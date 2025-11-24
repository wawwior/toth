package me.wawwior.toth.util;

import me.wawwior.toth.util.functors.Monad;
import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Value<T> extends Obj<Value.Type, T> {

    interface Type extends Cat {}

    static <T> Value<T> unbox(Obj<Type, T> obj) {
        return (Value<T>) obj;
    }

    enum Ops implements Monad<Type, Ops.Type> {
        INSTANCE;

        interface Type extends _Cat {}

        @Override
        public <T> Value<T> pure(T t) {
            return Value.of(t);
        }

        @Override
        public <T, U> Value<U> flatMap(Obj<Value.Type, T> obj, Function<T, Obj<Value.Type, U>> f) {
            Value<T> value = (Value<T>) obj;
            return (Value<U>) f.apply(value.value());
        }
    }

    static Ops ops() {
        return Ops.INSTANCE;
    }

    T value();

    static <T> Value<T> of(T t) {
        return () -> t;
    }

}
