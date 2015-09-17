package asted;

import asted.matching.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws BadLocationException {
        JFrame frame = new JFrame();

        asted.matching.Pattern<Character, NodeViewFactory> javaGrammar = new asted.matching.Pattern<Character, NodeViewFactory>() {
            private asted.matching.Pattern<Character, NodeViewFactory> wsImpl = new asted.matching.Pattern<Character, NodeViewFactory>() {
                @Override
                public boolean matches(Map<String, Buffer<Object>> locals, Input<Character> input, Buffer<NodeViewFactory> output) {
                    while(Character.isWhitespace(input.peekChar()))
                        input.consume();

                    return true;
                }
            };
            private asted.matching.Pattern<Character, NodeViewFactory> classKW = new asted.matching.KeywordPattern("class");
            private asted.matching.Pattern<Character, Character> id =
                new asted.matching.SequencePattern<Character, Character>(Arrays.asList(
                    CharPattern.isLetter(),
                    InputPattern.copy(),
                    InputPattern.consume(),
                    new asted.matching.RepeatPattern(
                        new asted.matching.SequencePattern(Arrays.asList(
                            new asted.matching.DecisionPattern(Arrays.asList(CharPattern.<Character>isLetter(), CharPattern.<Character>isDigit())),
                            InputPattern.copy(),
                            InputPattern.consume()
                        ))
                    )
                ));

            private void updateTextViewSize(JTextArea textView) {
                textView.revalidate();
                textView.repaint();
            }

            private NodeViewText createTextView(String text) {
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

            private asted.matching.Pattern<Character, NodeViewFactory> ws = new ReferencePattern<>(() -> wsImpl);
            private asted.matching.Pattern<Character, NodeViewFactory> member =
                new asted.matching.SequencePattern<Character, NodeViewFactory>(Arrays.asList(
                    new asted.matching.KeywordPattern("private"),
                    ws,
                    LocalsPattern.capture("type", id),
                    ws,
                    LocalsPattern.capture("name", id),
                    ws,
                    new asted.matching.KeywordPattern(";"),
                    new OutputPattern<Character, NodeViewFactory>(locals -> new NodeViewFactory() {
                        String type = locals.get("type").toStream().map(x -> x.toString()).collect(Collectors.joining());
                        String name = locals.get("name").toStream().map(x -> x.toString()).collect(Collectors.joining());

                        @Override
                        public JComponent toComponent(NodeViewContainer container) {
                            NodeViewPanel pnl = new NodeViewPanel();

                            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
                            pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));

                            pnl.add(new JLabel("private "));
                            final NodeViewText nameField = createTextView(type);
                            pnl.add(nameField);
                            pnl.add(new JLabel(" "));
                            final NodeViewText typeField = createTextView(name);
                            pnl.add(typeField);
                            pnl.add(new JLabel(";"));

                            pnl.addAncestorListener(new AncestorListener() {
                                @Override
                                public void ancestorAdded(AncestorEvent event) {
                                    //membersView.grabFocus();
                                    //container.activate();
                                    ((NodeViewContainer)pnl.getParent()).activate();
                                }

                                @Override
                                public void ancestorRemoved(AncestorEvent event) {

                                }

                                @Override
                                public void ancestorMoved(AncestorEvent event) {

                                }
                            });

                            //container.activate();

                            return pnl;
                        }
                    })
                ));
            private asted.matching.Pattern<Character, NodeViewFactory> classDeclaration =
                new asted.matching.SequencePattern<Character, NodeViewFactory>(Arrays.asList(
                    ws,
                    classKW,
                    ws,
                    LocalsPattern.capture("name", id),
                    ws,
                    new asted.matching.KeywordPattern("{"),
                    ws,
                    new asted.matching.KeywordPattern("}"),
                    new OutputPattern<Character, NodeViewFactory>(locals -> new NodeViewFactory() {
                        String name = locals.get("name").toStream().map(x -> x.toString()).collect(Collectors.joining());
                        @Override
                        public JComponent toComponent(NodeViewContainer container) {
                            NodeViewPanel pnl = new NodeViewPanel();

                            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
                            pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));

                            NodeViewPanel classDesc = new NodeViewPanel();
                            classDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
                            classDesc.setLayout(new BoxLayout(classDesc, BoxLayout.LINE_AXIS));

                            classDesc.add(new JLabel("class "));

                            final NodeViewText nameField = createTextView(name);

                            classDesc.add(nameField);
                            JLabel openBraLbl = new JLabel(" {");
                            classDesc.add(openBraLbl);

                            pnl.add(classDesc);


                            NodeViewPanel membersViewHolder = new NodeViewPanel() {
                                @Override
                                public void activate() {
                                    UnderConstructionView membersView = new UnderConstructionView(member);
                                    membersView.setAlignmentX(Component.LEFT_ALIGNMENT);
                                    this.add(membersView);
                                    membersView.grabFocus();
                                }
                            };
                            membersViewHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
                            membersViewHolder.setLayout(new BoxLayout(membersViewHolder, BoxLayout.PAGE_AXIS));
                            membersViewHolder.setAlignmentY(Component.TOP_ALIGNMENT);
                            membersViewHolder.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

                            /*NodeViewContainer memberViewContainer = new NodeViewContainer() {
                                @Override
                                public void activate() {
                                    UnderConstructionView membersView = new UnderConstructionView(member);
                                    membersView.setAlignmentX(Component.LEFT_ALIGNMENT);
                                    membersViewHolder.add(membersView);
                                    membersView.grabFocus();
                                }

                                @Override
                                public void focusEndBefore(JComponent component) {
                                    int index = membersViewHolder.getComponentZOrder(component);
                                    if(index >= 0) {
                                        //((JComponent)membersViewHolder.getComponent(index - 1)).grabFocus();

                                        ((NodeView)membersViewHolder.getComponent(index - 1)).focusEnd();
                                    }
                                }
                            };*/

                            //UnderConstructionView membersView = new UnderConstructionView(member);
                            //membersViewHolder.add(membersView);

                            pnl.add(membersViewHolder);

                            pnl.add(new JLabel("}"));

                            pnl.addAncestorListener(new AncestorListener() {
                                @Override
                                public void ancestorAdded(AncestorEvent event) {
                                    //membersView.grabFocus();
                                    membersViewHolder.activate();
                                }

                                @Override
                                public void ancestorRemoved(AncestorEvent event) {

                                }

                                @Override
                                public void ancestorMoved(AncestorEvent event) {

                                }
                            });

                            return pnl;
                        }

                        @Override
                        public String toString() {
                            return "class '" + name + "'{ }";
                        }
                    })
                ));
            private asted.matching.Pattern<Character, NodeViewFactory> program = classDeclaration;

            @Override
            public boolean matches(Map<String, Buffer<Object>> locals, Input<Character> input, Buffer<NodeViewFactory> output) {
                return program.matches(locals, input, output);
            }
        };

        JComponent view = new UnderConstructionView(javaGrammar);
        NodeViewPanel contentPane = new NodeViewPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(view, BorderLayout.CENTER);

        frame.setContentPane(contentPane);

        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
