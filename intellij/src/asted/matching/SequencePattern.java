package asted.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SequencePattern<T, R> implements Pattern<T, R> {
    private List<Pattern<T, R>> steps;

    public SequencePattern() {
        this(new ArrayList<Pattern<T, R>>());
    }

    public SequencePattern(List<Pattern<T, R>> steps) {
        this.steps = steps;
    }

    public SequencePattern<T, R> append(Pattern<T, R> step) {
        steps.add(step);
        return this;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<R> output) {
        return steps.stream().allMatch(p ->
            p.matches(locals, input, output)
        );
    }
}
