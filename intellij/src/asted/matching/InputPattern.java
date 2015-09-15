package asted.matching;

import java.util.Map;

public class InputPattern {
    public static <T, R> Pattern<T, R> consume() {
        return (locals, input, output) -> {
            input.consume();
            return true;
        };
    }

    public static <T> Pattern<T, T> copy() {
        return (locals, input, output) -> {
            input.copyTo(output);
            return true;
        };
    }
}
