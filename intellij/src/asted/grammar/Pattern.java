package asted.grammar;

public interface Pattern {
    <T> T accept(PatternVisitor<T> visitor);
}
