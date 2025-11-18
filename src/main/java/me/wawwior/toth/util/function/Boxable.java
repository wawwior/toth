package me.wawwior.toth.util.function;

public interface Boxable<B> {

    B box();

    default  <T,
            TR,
            R extends Boxable<BoxedFunction<T, TR, R>>>
    R map(Function1<B, BoxedFunction<T, TR, R>> mapping) {
        return mapping.apply(this.box()).unbox();
    }

}
