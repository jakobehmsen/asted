package asted.matching;

import java.util.Map;
import java.util.function.Function;

public class OutputPattern<T, R> implements Pattern<T, R> {
    private Function<Map<String, Buffer<Object>>, R> value;

    public OutputPattern(Function<Map<String, Buffer<Object>>, R> value) {
        this.value = value;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<R> output) {
        output.append(value.apply(locals));

        return true;
    }
}
