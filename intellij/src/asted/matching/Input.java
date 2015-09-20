package asted.matching;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Input<T> {
    boolean hasMore();
    void consume();
    T peek();
    default char peekChar() {
        return (Character)peek();
    }
    InputState<T> state();

    default void copyTo(Buffer<T> output) {
        output.append(peek());
    }

    default Stream<T> toStream() {
        Iterable<T> iterable = new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return hasMore();
                    }

                    @Override
                    public T next() {
                        T value = peek();
                        consume();
                        return value;
                    }
                };
            }
        };

        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
