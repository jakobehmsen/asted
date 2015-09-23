package asted.matching;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringPattern {
    public static <T> Pattern<Character, T> is(CharSequence chars) {
        return (locals, input, output) -> IntStream.range(0, chars.length()).allMatch(i -> {
            if(input.hasMore()) {
                if(chars.charAt(i) == input.peekChar()) {
                    input.consume();
                    return true;
                }
            }

            return false;
        });
    }

    public static Pattern<Character, String> join() {
        return (locals, input, output) -> {
            String reduction = input.toStream().map(x -> x.toString()).collect(Collectors.joining());

            output.append(reduction);

            return true;
        };
    }

    public static <T> Pattern<Character, T> ws() {
        return new asted.matching.RepeatPattern<>(
            new SequencePattern<>(Arrays.asList(
                CharPattern.<T>isWhitespace(),
                InputPattern.<Character, T>consume()
            ))
        );
    }

    public static <T> Pattern<Character, T> id() {
        return new asted.matching.SequencePattern<>(Arrays.asList(
            CharPattern.<T>isLetter(),
            InputPattern.<Character, T>consume(),
            new asted.matching.RepeatPattern<>(
                new SequencePattern<Character, T>(Arrays.asList(
                    new asted.matching.DecisionPattern<>(Arrays.asList(
                        CharPattern.<T>isLetter(),
                        CharPattern.<T>isDigit()
                    )),
                    InputPattern.<Character, T>consume()
                ))
            )
        ));
    }
}
