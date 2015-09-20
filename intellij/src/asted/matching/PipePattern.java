package asted.matching;

import java.util.Map;

public class PipePattern<T, R, S> implements Pattern<T, S> {
    private Pattern<T, R> first;
    private Pattern<R, S> second;

    public PipePattern(Pattern<T, R> first, Pattern<R, S> second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<S> output) {
        Buffer<R> intermediate = createBuffer();

        if(first.matches(locals, input, intermediate)) {
            if(second.matches(locals, intermediate.traverse(), output)) {
                return true;
            }
        }

        return false;
    }

    protected Buffer<R> createBuffer() {
        return (Buffer<R>)Buffer.Util.create();
    }
}
