package asted.grammar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultRuleSet implements RuleSet {
    private HashMap<String, Pattern> rules = new HashMap<String, Pattern>();

    @Override
    public Set<Map.Entry<String, Pattern>> rules() {
        return rules.entrySet();
    }

    public void put(String name, Pattern pattern) {
        rules.put(name, pattern);
    }

    @Override
    public Pattern get(String name) {
        return rules.get(name);
    }
}
