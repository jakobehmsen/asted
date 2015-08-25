package asted.tree;

import java.util.List;

public interface NodeVisitor<T> {
    T acceptAtom(String text);
    T acceptTuple(List<Node> items);
}
