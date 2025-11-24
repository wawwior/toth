package me.wawwior.toth.util;

import me.wawwior.toth.util.functors.Monad;
import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;

import java.util.function.Function;

public interface Either<L, R> extends Obj<Either.Type<R>, L> {

    interface Type<R> extends Cat {}

    static <L, R> Either<L, R> unbox(Obj<Type<R>, L> obj) {
        return (Either<L, R>) obj;
    }

    final class Ops<R> implements Monad<Type<R>, Ops.K<R>> {

        interface K<R> extends _Cat {}

        @Override
        public <T, U> Either<U, R> flatMap(Obj<Either.Type<R>, T> obj, Function<T, Obj<Either.Type<R>, U>> f) {
            if (obj instanceof Either.Left<T, R> left) {
                return (Either<U, R>) f.apply(left.value);
            }
            return new Right<>(((Right<T, R>)obj).value);
        }

        @Override
        public <T> Obj<Either.Type<R>, T> pure(T t) {
            return new Left<>(t);
        }

    }

    static <R> Ops<R> ops() {
        return new Ops<>();
    }

    static <L, R> Either<L, R> left(L l) {
        return new Left<>(l);
    }

    static <L, R> Either<L, R> right(R r) {
        return new Right<>(r);
    }

    Option<L> left();

    Option<R> right();

    Either<R, L> swap();

    class Left<L, R> implements Either<L, R> {
        private final L value;

        private Left(L value) {
            this.value = value;
        }

        @Override
        public Option<L> left() {
            return Option.some(value);
        }

        @Override
        public Option<R> right() {
            return Option.none();
        }

        @Override
        public Either<R, L> swap() {
            return Either.right(value);
        }
    }

    class Right<L, R> implements Either<L, R> {
        private final R value;

        private Right(R value) {
            this.value = value;
        }

        @Override
        public Option<L> left() {
            return Option.none();
        }

        @Override
        public Option<R> right() {
            return Option.some(value);
        }

        @Override
        public Either<R, L> swap() {
            return Either.left(value);
        }
    }

}
