package asted;

import asted.grammar.*;

import java.util.Arrays;
import java.util.Collections;

public class Main {
    public static void main(String[] args) {
        DefaultRuleSet rules = new DefaultRuleSet();

        rules.put("program", new Sequence(Arrays.asList(
            new Keyword("if"),
            new Keyword("then"),
            new Decision(
                Arrays.asList(
                    new Keyword("else"),
                    new Sequence(Collections.<Pattern>emptyList())
                )
            )
        )));
    }
}
