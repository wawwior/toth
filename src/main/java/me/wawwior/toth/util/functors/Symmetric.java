package me.wawwior.toth.util.functors;

import me.wawwior.toth.util.type.Cat;
import me.wawwior.toth.util.type.Mor;
import me.wawwior.toth.util.type.Obj;

public interface Symmetric<F extends Cat, T extends Symmetric._Cat> extends Arrow<F, T> {

    interface _Cat extends Arrow._Cat {}

    static <F extends Cat> Symmetric<F, _Cat> unbox(Obj<_Cat, F> obj) {
        return (Symmetric<F, _Cat>) obj;
    }

    <A, B, C> Mor<F, B, ? extends Mor<F, A, C>> flip(Mor<F, A, Mor<F, B, C>> f);

}
