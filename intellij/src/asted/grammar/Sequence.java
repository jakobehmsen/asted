package asted.grammar;

import java.util.List;

public class Sequence implements Pattern {
    private List<Pattern> parts;

    public Sequence(List<Pattern> parts) {
        this.parts = parts;
    }

    @Override
    public <T> T accept(PatternVisitor<T> visitor) {
        return visitor.visitSequence(parts);
    }
}
