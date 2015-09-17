package asted.matching;

import javax.swing.*;

public interface NodeViewFactory {
    JComponent toComponent(NodeViewContainer container);
}
