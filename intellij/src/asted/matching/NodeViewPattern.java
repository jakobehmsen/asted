package asted.matching;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NodeViewPattern {
    private interface Builder {
        String getName();
        JComponent toView(String captured);
    }

    private ArrayList<Builder> builders = new ArrayList<>();

    public Pattern<Character, NodeView> capture(String name, Pattern<Character, Character> pattern, Function<String, JComponent> viewFactory) {
        builders.add(new Builder() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public JComponent toView(String captured) {
                return viewFactory.apply(captured);
            }
        });

        return LocalsPattern.capture(name, pattern);
    }

    public Pattern<Character, NodeView> toPattern() {
        return new OutputPattern<>(locals -> {
            NodeViewPanel pnl = new NodeViewPanel();

            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));

            builders.stream().map(b -> {
                String captured = locals.get(b.getName()).toStream().map(x -> x.toString()).collect(Collectors.joining());
                return b.toView(captured);
            }).forEach(c -> pnl.add((JComponent)c));

            return pnl;
        });
    }
}
