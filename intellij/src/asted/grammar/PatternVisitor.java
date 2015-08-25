package asted.grammar;

import java.util.List;

public interface PatternVisitor<T> {
    T visitMention(String name);
    T visitKeyword(String word);
    T visitRegex(java.util.regex.Pattern pattern);
    T visitSequence(List<Pattern> parts);
    T visitDecision(List<Pattern> alternatives);
}
