package me.wawwior.toth.util;

import me.wawwior.toth.util.function.Fn;
import me.wawwior.toth.util.functors.Functor;
import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Obj;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class FunctionTest {

    @Test
    void test() {

        Option<String> none = Option.none();
        Option<String> some = Option.some("World");
        Value<String> value = Value.of("World");
        Either<String, Integer> left = Either.left("World");

        Fn<String, String> greet = s -> "Hello, " + s + "!";

        var morphism = Fn.ops().compose(
                Fn.ops().compose(Fn.ops().lift((Integer i) -> Integer.toString(i)), greet),
                Fn.ops().lift(List::of)
        );

        // Contains R type for type safety
        Either.Ops<Integer> eitherOps = Either.ops();

        var optionMapper = mapper(Option.ops(), greet::apply);
        var valueMapper = mapper(Value.ops(), greet::apply);
        var eitherMapper = mapper(eitherOps, greet::apply);

        var mappedNone = optionMapper.apply(none);
        var mappedSome = optionMapper.apply(some);
        var mappedValue = valueMapper.apply(value);
        var mappedEither = eitherMapper.apply(left);

        assertInstanceOf(Option.None.class, mappedNone);
        assertInstanceOf(Option.Some.class, mappedSome);
        assertEquals("Hello, World!", ((Option.Some<String>)mappedSome).value());
        assertEquals("Hello, World!", Value.unbox(mappedValue).value());
        assertInstanceOf(Option.Some.class, Either.unbox(mappedEither).left());
        assertEquals("Hello, World!", ((Option.Some<String>)Either.unbox(mappedEither).left()).value());
    }

    <C extends Cat> Fn<Obj<C, String>, Obj<C, String>> mapper(Functor<C, ?> functor, Function<String, String> mapping) {
        return obj -> functor.map(mapping, obj);
    }

}
