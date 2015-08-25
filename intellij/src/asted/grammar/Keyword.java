package asted.grammar;

public class Keyword implements Pattern {
    private String word;

    public Keyword(String word) {
        this.word = word;
    }

    @Override
    public <T> T accept(PatternVisitor<T> visitor) {
        return visitor.visitKeyword(word);
    }
}
