package me.wawwior.toth.util.function;

import me.wawwior.toth.util.tuple.Tuple1;

import java.util.function.Function;

public interface Function1<T, R> extends Boxable<BoxedFunction<Tuple1<T>, R, Function1<T, R>>>, Function<T, R> {

    R apply(T t);

    default Boxed<T, R> box() {
        return tuple -> this.apply(tuple.one());
    }

    interface Boxed<T, R> extends BoxedFunction<Tuple1<T>, R, Function1<T, R>> {

        @Override
        default Function1<T, R> unbox() {
            return t -> this.apply(Tuple1.of(t));
        }

        @Override
        default <R1, B extends BoxedFunction<Tuple1<T>, R1, ?>> B newFromT(Function1<Tuple1<T>, R1> function) {
            //noinspection unchecked
            return (B) (Boxed<T, R1>) function::apply;
        }
    }

}
