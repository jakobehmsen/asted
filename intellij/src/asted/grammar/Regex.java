package asted.grammar;

public class Regex implements Pattern {
    private java.util.regex.Pattern pattern;

    public Regex(java.util.regex.Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public <T> T accept(PatternVisitor<T> visitor) {
        return visitor.visitRegex(pattern);
    }
}
