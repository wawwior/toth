package me.wawwior.toth.util.tuple;

public interface Tuple2<T1, T2> {

    T1 one();

    T2 two();

    static <T1, T2> Tuple2<T1, T2> of(T1 t1, T2 t2) {
        return new Tuple2<>() {
            @Override
            public T1 one() {
                return t1;
            }

            @Override
            public T2 two() {
                return t2;
            }
        };
    }
}
