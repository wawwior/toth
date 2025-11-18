package me.wawwior.toth.util.function;

import me.wawwior.toth.util.tuple.Tuple2;

import java.util.function.BiFunction;

public interface Function2<T1, T2, R> extends Boxable<BoxedFunction<Tuple2<T1, T2>, R, Function2<T1, T2, R>>>, BiFunction<T1, T2, R> {

    R apply(T1 t1, T2 t2);

    default Boxed<T1, T2, R> box() {
        return tuple -> this.apply(tuple.one(), tuple.two());
    }

    interface Boxed<T1, T2, R> extends BoxedFunction<Tuple2<T1, T2>, R, Function2<T1, T2, R>> {

        @Override
        default Function2<T1, T2, R> unbox() {
            return (t1, t2) -> this.apply(Tuple2.of(t1, t2));
        };

        @Override
        default <R1, B extends BoxedFunction<Tuple2<T1, T2>, R1, ?>> B newFromT(Function1<Tuple2<T1, T2>, R1> function) {
            //noinspection unchecked
            return (B) (Boxed<T1, T2, R1>) function::apply;
        }
    }
}
