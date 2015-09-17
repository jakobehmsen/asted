package asted.matching;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

public class UnderConstructionView extends NodeViewText {
    private Pattern<Character, NodeViewFactory> pattern;

    public UnderConstructionView(Pattern<Character, NodeViewFactory> pattern) {
        this.pattern = pattern;
        //setHorizontalAlignment(SwingConstants.LEFT);
        //setVerticalAlignment(SwingConstants.TOP);

        /*getInputMap().put(KeyStroke.getKeyStroke("LEFT"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getCaretPosition() == 0) {
                    container.focusEndBefore(UnderConstructionView.this);
                } else {
                    setCaretPosition(getCaretPosition() - 1);
                }
            }
        });
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getCaretPosition() == getDocument().getLength()) {
                    //container.focusStartAfter(UnderConstructionView.this);
                } else {
                    setCaretPosition(getCaretPosition() + 1);
                }
            }
        });*/

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch ((int) e.getKeyChar()) {
                    case KeyEvent.VK_ENTER: {
                        String inputStr = getText();
                        Buffer<NodeViewFactory> output = Buffer.Util.create(NodeViewFactory.class);

                        Hashtable<String, Buffer<Object>> locals = new Hashtable<>();
                        boolean matches = pattern.matches(
                            locals, Buffer.Util.wrap(inputStr).traverse(),
                            output
                        );

                        if (matches) {
                            // Convert output to view
                            NodeViewFactory nodeViewFactory = output.traverse().peek();
                            //JComponent outputAsView = nodeViewFactory.toComponent(container);
                            JComponent outputAsView = nodeViewFactory.toComponent((NodeViewContainer)getParent());
                            int zOrder = getParent().getComponentZOrder(UnderConstructionView.this);
                            Container parent = getParent();
                            getParent().remove(UnderConstructionView.this);
                            parent.add(outputAsView);
                            parent.setComponentZOrder(outputAsView, zOrder);
                            //parent.add(new JLabel("Some more"));
                            parent.revalidate();
                            parent.repaint();
                        }

                        break;
                    }
                }
            }
        });
    }

    /*@Override
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
    }*/
}
