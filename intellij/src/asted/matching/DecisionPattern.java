package asted.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DecisionPattern<T, R> implements Pattern<T, R> {
    private List<Pattern<T, R>> alternatives;

    public DecisionPattern() {
        this(new ArrayList<Pattern<T, R>>());
    }

    public DecisionPattern(List<Pattern<T, R>> alternatives) {
        this.alternatives = alternatives;
    }

    public DecisionPattern<T, R> append(Pattern<T, R> alternative) {
        alternatives.add(alternative);
        return this;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<T> input, Buffer<R> output) {
        return alternatives.stream().anyMatch(p -> {
            State inputState = input.state();
            State outputState = output.state();

            if(p.matches(locals, input, output)) {
                return true;
            } else {
                inputState.restore();
                outputState.restore();
                return false;
            }
        });
    }
}
