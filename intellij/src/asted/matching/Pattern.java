package asted.matching;

import java.util.Map;

public interface Pattern<T, R> {
    boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<R> output);
}
