package me.wawwior.toth.util.function;

public interface BoxedFunction<T, R, U extends Boxable<BoxedFunction<T, R, U>>> {

    R apply(T t);

    U unbox();

    default <R1, U1 extends Boxable<BoxedFunction<T, R1, U1>>> BoxedFunction<T, R1, U1> map(Function1<R, R1> mapper) {
        return this.newFromT(t -> mapper.apply(this.apply(t)));
    }

    <R1, B extends BoxedFunction<T, R1, ?>> B newFromT(Function1<T, R1> function);

}
