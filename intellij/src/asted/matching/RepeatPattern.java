package asted.matching;

import java.util.Map;

public class RepeatPattern<T, R> implements Pattern<T, R> {
    private Pattern<T, R> pattern;

    public RepeatPattern(Pattern<T, R> pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<R> output) {
        while(pattern.matches(locals, input, output));

        return true;
    }
}
