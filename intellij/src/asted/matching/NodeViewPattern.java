package asted.matching;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.IntStream;

public class NodeViewPattern {
    public static Pattern<Character, JComponent> repeat(Pattern<Character, JComponent> pattern) {
        return new Pattern<Character, JComponent>() {
            @Override
            public boolean matches(Map<String, Buffer<Object>> locals, Input<Character> input, Buffer<JComponent> output) {
                NodeViewPanel repeatView = new NodeViewPanel() {
                    @Override
                    public void activate() {
                        NodeViewText membersView = createUnderConstructionView(pattern);
                        membersView.setAlignmentX(Component.LEFT_ALIGNMENT);
                        this.add(membersView);
                        membersView.grabFocus();
                    }
                };

                repeatView.setAlignmentX(Component.LEFT_ALIGNMENT);
                repeatView.setLayout(new BoxLayout(repeatView, BoxLayout.PAGE_AXIS));
                repeatView.setAlignmentY(Component.TOP_ALIGNMENT);
                repeatView.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

                repeatView.addAncestorListener(new AncestorListener() {
                    @Override
                    public void ancestorAdded(AncestorEvent event) {
                        repeatView.activate();
                    }

                    @Override
                    public void ancestorRemoved(AncestorEvent event) {

                    }

                    @Override
                    public void ancestorMoved(AncestorEvent event) {

                    }
                });

                output.append(repeatView);

                return true;
            }
        };
    }

    public static NodeViewText createUnderConstructionView(Pattern<Character, JComponent> pattern) {
        NodeViewText underConstruction = new NodeViewText();

        underConstruction.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                switch ((int) e.getKeyChar()) {
                    case KeyEvent.VK_ENTER: {
                        String inputStr = underConstruction.getText();
                        Buffer<JComponent> output = Buffer.Util.create(JComponent.class);

                        Hashtable<String, Buffer<Object>> locals = new Hashtable<>();
                        boolean matches = pattern.matches(
                            locals, Buffer.Util.wrap(inputStr).traverse(),
                            output
                        );

                        if (matches) {
                            JComponent outputAsView = output.traverse().peek();

                            outputAsView.addAncestorListener(new AncestorListener() {
                                @Override
                                public void ancestorAdded(AncestorEvent event) {
                                    ((NodeViewContainer)outputAsView.getParent()).activate();
                                }

                                @Override
                                public void ancestorRemoved(AncestorEvent event) {

                                }

                                @Override
                                public void ancestorMoved(AncestorEvent event) {

                                }
                            });

                            int zOrder = underConstruction.getParent().getComponentZOrder(underConstruction);
                            Container parent = underConstruction.getParent();
                            underConstruction.getParent().remove(underConstruction);
                            parent.add(outputAsView);
                            parent.setComponentZOrder(outputAsView, zOrder);
                            parent.revalidate();
                            parent.repaint();
                        }

                        break;
                    }
                }
            }
        });

        return underConstruction;
    }

    public static Pattern<String, JComponent> decision(java.util.List<String> alternatives) {
        return (locals, input, output) -> {
            if(input.hasMore()) {
                String str = input.peek();

                if(alternatives.stream().anyMatch(x -> x.equals(str))) {
                    JComboBox<String> view = new NodeViewComboBox<>();
                    DefaultComboBoxModel<String> viewModel = (DefaultComboBoxModel<String>) view.getModel();
                    for (String alternative : alternatives)
                        viewModel.addElement(alternative);
                    view.setSelectedItem(str);

                    output.append(view);

                    return true;
                }

                return false;
            }

            return false;
        };
    }

    public static Pattern<String, JComponent> label() {
        return (locals, input, output) -> {
            if(input.hasMore()) {
                String str = input.peek();

                output.append(new JLabel(str));

                return true;
            }

            return false;
        };
    }

    public static Pattern<String, JComponent> text() {
        return (locals, input, output) -> {
            if(input.hasMore()) {
                String str = input.peek();

                output.append(createTextView(str));

                return true;
            }

            return false;
        };
    }

    public static NodeViewText createTextView(String text) {
        final NodeViewText textView = new NodeViewText() {
            @Override
            public Dimension getPreferredSize() {
                Rectangle r = null;
                try {
                    r = modelToView(getDocument().getLength());
                } catch (BadLocationException e2) {
                    e2.printStackTrace();
                }
                if (r != null) {
                    return new Dimension(r.x + r.width, r.height);
                } else
                    return super.getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        textView.setText(text);

        textView.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateTextViewSize(textView);
                textView.removeComponentListener(this);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
        textView.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTextViewSize(textView);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTextViewSize(textView);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateTextViewSize(textView);
            }
        });

        return textView;
    }

    private static void updateTextViewSize(JTextArea textView) {
        textView.revalidate();
        textView.repaint();
    }

    public static Pattern<JComponent, JComponent> leftToRightList() {
        return (locals, input, output) -> {
            NodeViewPanel pnl = new NodeViewPanel();

            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));

            input.toStream().forEach(x -> pnl.add((JComponent)x));

            output.append(pnl);

            return true;
        };
    }

    public static Pattern<JComponent, JComponent> topDownList() {
        return (locals, input, output) -> {
            NodeViewPanel pnl = new NodeViewPanel();

            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
            pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));

            input.toStream().forEach(x -> pnl.add(x));

            output.append(pnl);

            return true;
        };
    }
}
