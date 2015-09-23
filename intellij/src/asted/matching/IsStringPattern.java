package asted.matching;

import java.util.Map;
import java.util.stream.IntStream;

public class IsStringPattern<T> implements Pattern<Character, T> {
    private String word;

    public IsStringPattern(String word) {
        this.word = word;
    }

    @Override
    public boolean matches(Map<String, Buffer<Object>> locals, Input<Character> input, Buffer<T> output) {
        return IntStream.range(0, word.length()).allMatch(i -> {
            if(input.hasMore()) {
                if(word.charAt(i) == input.peekChar()) {
                    input.consume();
                    return true;
                }
            }

            return false;
        });
    }
}
