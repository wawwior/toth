package me.wawwior.toth.util;

import me.wawwior.toth.util.function.Function1;
import me.wawwior.toth.util.function.Function2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FunctionTest {

    @Test
    void test() {

        Function1<Integer, String> f1 = Object::toString;
        Function2<Integer, Integer, String> f2 = (x, y) -> String.valueOf(x + y);

        Function1<Integer, List<String>> w1 = f1.map(b -> b.map(List::of));
        Function2<Integer, Integer, List<String>> w2 = f2.map(b -> b.map(List::of));

        assertEquals(List.of("7"), w1.apply(7));
        assertEquals(List.of("7"), w2.apply(3, 4));

    }

}
