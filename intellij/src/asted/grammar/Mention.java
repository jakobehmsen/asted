package asted.grammar;

public class Mention implements Pattern {
    private String name;

    public Mention(String name) {
        this.name = name;
    }

    @Override
    public <T> T accept(PatternVisitor<T> visitor) {
        return visitor.visitMention(name);
    }
}
