package me.wawwior.toth.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Streams {

    public static <A, B, C> Stream<C> zip(Stream<A> aStream, Stream<B> bStream, BiFunction<A, B, C> zipper) {
        final Iterator<A> aIterator = aStream.iterator();
        final Iterator<B> bIterator = bStream.iterator();
        final Iterator<C> cIterator = new Iterator<C>() {
            @Override
            public boolean hasNext() {
                return aIterator.hasNext() && bIterator.hasNext();
            }

            @Override
            public C next() {
                return zipper.apply(aIterator.next(), bIterator.next());
            }
        };
        final boolean parallel = aStream.isParallel() || bStream.isParallel();
        return iteratorToFiniteStream(cIterator, parallel);
    }

    public static <A, B, C> Stream<C> zip(List<A> as, List<B> bs, BiFunction<A, B, C> zipper){
        int shortestLength = Math.min(as.size(), bs.size());
        return IntStream.range(0, shortestLength).mapToObj(i -> zipper.apply(as.get(i), bs.get(i)));
    }

    public static <T> Stream<T> iteratorToFiniteStream(Iterator<T> iterator, boolean parallel) {
        final Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

}
