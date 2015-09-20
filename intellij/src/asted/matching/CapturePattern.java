package asted.matching;

import java.util.Map;

public class CapturePattern<T> implements Pattern<T, T> {
    private Pattern<T, T> pattern;

    public CapturePattern(Pattern<T, T> pattern) {
        this.pattern = pattern;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<T> output) {
        Input<T> capturingInput = new Input<T>() {
            @Override
            public boolean hasMore() {
                return input.hasMore();
            }

            @Override
            public void consume() {
                copyTo(output); // Could be collected and outputted in larger chunks
                input.consume();
            }

            @Override
            public T peek() {
                return input.peek();
            }

            @Override
            public InputState<T> state() {
                return input.state();
            }

            @Override
            public char peekChar() {
                return input.peekChar();
            }
        };

        return pattern.matches(locals, capturingInput, output);

        /*
        InputState<T> start = input.state();

        if(pattern.matches(locals, input, output)) {
            InputState<T>  end = input.state();

            start.tillCopy(end, output);

            return true;
        }

        return false;
        */
    }
}
