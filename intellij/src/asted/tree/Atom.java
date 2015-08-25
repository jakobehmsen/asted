package asted.tree;

public class Atom implements Node {
    private String text;

    public Atom(String text) {
        this.text = text;
    }

    @Override
    public <T> T accept(NodeVisitor<T> visitor) {
        return visitor.acceptAtom(text);
    }
}
