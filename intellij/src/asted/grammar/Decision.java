package asted.grammar;

import java.util.List;

public class Decision implements Pattern {
    private List<Pattern> alternatives;

    public Decision(List<Pattern> alternatives) {
        this.alternatives = alternatives;
    }

    @Override
    public <T> T accept(PatternVisitor<T> visitor) {
        return visitor.visitDecision(alternatives);
    }
}
