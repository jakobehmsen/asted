package asted.tree;

public interface Node {
    <T> T accept(NodeVisitor<T> visitor);
}
