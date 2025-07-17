package me.wawwior.toth.util;

import java.util.Optional;
import java.util.function.Supplier;

public class Suppliers {

    public static <T> Supplier<T> memoize(Supplier<T> supplier) {
        return new Supplier<T>() {
            T t = null;

            @Override
            public T get() {
                if (t == null) {
                    t = supplier.get();
                }
                return t;
            }
        };
    }

}
