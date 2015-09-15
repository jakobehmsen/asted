package asted.matching;

import java.util.Map;
import java.util.function.Supplier;

public class ReferencePattern<T, R> implements Pattern<T, R> {
    private Supplier<Pattern<T, R>> patternSupplier;

    public ReferencePattern(Supplier<Pattern<T, R>> patternSupplier) {
        this.patternSupplier = patternSupplier;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<R> output) {
        return patternSupplier.get().matches(locals, input, output);
    }
}
