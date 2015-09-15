package asted.matching;

public interface Input<T> {
    boolean hasMore();
    void consume();
    T peek();
    default char peekChar() {
        return (Character)peek();
    }
    State state();

    default void copyTo(Buffer<T> output) {
        output.append(peek());
    }
}
