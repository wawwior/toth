package me.wawwior.toth.util.function;

import me.wawwior.toth.util.tuple.Tuple3;

public interface Function3<T1, T2, T3, R> extends Boxable<BoxedFunction<Tuple3<T1, T2, T3>, R, Function3<T1, T2, T3, R>>> {

    R apply(T1 t1, T2 t2, T3 t3);

    @Override
    default Boxed<T1, T2, T3, R> box() {
        return tuple -> this.apply(tuple.one(), tuple.two(), tuple.three());
    }

    interface Boxed<T1, T2, T3, R> extends BoxedFunction<Tuple3<T1, T2, T3>, R, Function3<T1, T2, T3, R>> {

        @Override
        default Function3<T1, T2, T3, R> unbox() {
            return (t1, t2, t3) -> this.apply(Tuple3.of(t1, t2, t3));
        }

        @Override
        default <R1, B extends BoxedFunction<Tuple3<T1, T2, T3>, R1, ?>> B newFromT(Function1<Tuple3<T1, T2, T3>, R1> function) {
            //noinspection unchecked
            return (B) (Boxed<T1, T2, T3, R1>) function::apply;
        }
    }
}
