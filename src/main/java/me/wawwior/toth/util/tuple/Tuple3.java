package me.wawwior.toth.util.tuple;

public interface Tuple3<T1, T2, T3> {

    T1 one();

    T2 two();

    T3 three();

    static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>() {
            @Override
            public T1 one() {
                return t1;
            }

            @Override
            public T2 two() {
                return t2;
            }

            @Override
            public T3 three() {
                return t3;
            }
        };
    }

}
