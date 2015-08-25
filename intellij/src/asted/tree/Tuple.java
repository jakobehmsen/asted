package asted.tree;

import java.util.List;

public class Tuple implements Node {
    private List<Node> items;

    public Tuple(List<Node> items) {
        this.items = items;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.acceptTuple(items);
    }
}
