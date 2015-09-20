package asted.matching;

public interface InputState<T> extends State {
    void tillCopy(InputState<T> end, Buffer<T> target);


}
