package asted.matching;

import javax.swing.*;

public interface NodeViewContainer {
    void activate();
    void focusEndBefore(JComponent component);
    void focusStartAfter(JComponent component);
}
