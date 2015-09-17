package asted.matching;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NodeViewText extends JTextArea implements NodeView {
    public NodeViewText() {
        getInputMap().put(KeyStroke.getKeyStroke("LEFT"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getCaretPosition() == 0) {
                    ((NodeViewContainer)getParent()).focusEndBefore(NodeViewText.this);
                } else {
                    setCaretPosition(getCaretPosition() - 1);
                }
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getCaretPosition() == getDocument().getLength()) {
                    ((NodeViewContainer)getParent()).focusStartAfter(NodeViewText.this);
                } else {
                    setCaretPosition(getCaretPosition() + 1);
                }
            }
        });
    }

    @Override
    public void focusStart() {
        setCaretPosition(0);
        grabFocus();
    }

    @Override
    public void focusEnd() {
        setCaretPosition(getDocument().getLength());
        grabFocus();
    }

    @Override
    public Dimension getPreferredSize() {
        Rectangle r = null;
        try {
            r = modelToView(getDocument().getLength());
        } catch (BadLocationException e2) {
            e2.printStackTrace();
        }
        if (r != null) {
            return new Dimension(super.getPreferredSize().width, r.height);
        } else
            return super.getPreferredSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(super.getMinimumSize().width, getPreferredSize().height);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, getPreferredSize().height);
    }
}
