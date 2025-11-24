package me.wawwior.toth.util.function;

import java.util.function.Function;

public interface Boxable<B> {

    B box();

    default  <T,
            TR,
            R extends Boxable<BoxedFunction<T, TR, R>>>
    R map(Function<B, BoxedFunction<T, TR, R>> mapping) {
        return mapping.apply(this.box()).unbox();
    }

}
