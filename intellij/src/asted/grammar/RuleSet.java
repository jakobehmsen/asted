package asted.grammar;

import java.util.Map;
import java.util.Set;

public interface RuleSet {
    Set<Map.Entry<String, Pattern>> rules();
    Pattern get(String name);
}
