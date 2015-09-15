package asted.matching;

public class CharPattern {
    public static <T> Pattern<Character, T> isUpperCase() {
        return (locals, input, output) ->
            Character.isUpperCase(input.peekChar());
    }

    public static <T> Pattern<Character, T> isLowerCase() {
        return (locals, input, output) ->
            Character.isLowerCase(input.peekChar());
    }

    public static <T> Pattern<Character, T> isLetter() {
        return (locals, input, output) ->
            Character.isLetter(input.peekChar());
    }

    public static <T> Pattern<Character, T> isDigit() {
        return (locals, input, output) ->
            Character.isLetter(input.peekChar());
    }
}
