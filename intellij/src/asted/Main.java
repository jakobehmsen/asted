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

        asted.matching.Pattern<Character, NodeView> javaGrammar = new asted.matching.Pattern<Character, NodeView>() {
            private asted.matching.Pattern<Character, NodeView> wsImpl = new asted.matching.Pattern<Character, NodeView>() {
                @Override
                public boolean matches(Map<String, Buffer<Object>> locals, Input<Character> input, Buffer<NodeView> output) {
                    while(Character.isWhitespace(input.peekChar()))
                        input.consume();

                    return true;
                }
            };
            private asted.matching.Pattern<Character, NodeView> classKW = new asted.matching.KeywordPattern("class");
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

            private JTextArea createTextView(String text) {
                final JTextArea textView = new JTextArea(text) {
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

            private asted.matching.Pattern<Character, NodeView> ws = new ReferencePattern<>(() -> wsImpl);
            private asted.matching.Pattern<Character, NodeView> member =
                new asted.matching.SequencePattern<Character, NodeView>(Arrays.asList(
                    new asted.matching.KeywordPattern("private"),
                    ws,
                    LocalsPattern.capture("type", id),
                    ws,
                    LocalsPattern.capture("name", id),
                    ws,
                    new asted.matching.KeywordPattern(";"),
                    new OutputPattern<Character, NodeView>(locals -> new NodeView() {
                        String type = locals.get("type").toStream().map(x -> x.toString()).collect(Collectors.joining());
                        String name = locals.get("name").toStream().map(x -> x.toString()).collect(Collectors.joining());

                        @Override
                        public JComponent toComponent() {
                            JPanel pnl = new JPanel();

                            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
                            pnl.setLayout(new BoxLayout(pnl, BoxLayout.LINE_AXIS));

                            pnl.add(new JLabel("private "));
                            final JTextArea nameField = createTextView(type);
                            pnl.add(nameField);
                            pnl.add(new JLabel(" "));
                            final JTextArea typeField = createTextView(name);
                            pnl.add(typeField);
                            pnl.add(new JLabel(";"));

                            pnl.addAncestorListener(new AncestorListener() {
                                @Override
                                public void ancestorAdded(AncestorEvent event) {
                                    //membersView.grabFocus();
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
                    })
                ));
            private asted.matching.Pattern<Character, NodeView> classDeclaration =
                new asted.matching.SequencePattern<Character, NodeView>(Arrays.asList(
                    ws,
                    classKW,
                    ws,
                    LocalsPattern.capture("name", id),
                    ws,
                    new asted.matching.KeywordPattern("{"),
                    ws,
                    new asted.matching.KeywordPattern("}"),
                    new OutputPattern<Character, NodeView>(locals -> new NodeView() {
                        String name = locals.get("name").toStream().map(x -> x.toString()).collect(Collectors.joining());
                        @Override
                        public JComponent toComponent() {
                            JPanel pnl = new JPanel();

                            pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
                            pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));

                            JPanel classDesc = new JPanel();
                            classDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
                            classDesc.setLayout(new BoxLayout(classDesc, BoxLayout.LINE_AXIS));

                            classDesc.add(new JLabel("class "));

                            final JTextArea nameField = createTextView(name);

                            classDesc.add(nameField);
                            JLabel openBraLbl = new JLabel(" {");
                            classDesc.add(openBraLbl);

                            pnl.add(classDesc);

                            JPanel membersViewHolder = new JPanel();
                            UnderConstructionView membersView = new UnderConstructionView(member);
                            membersViewHolder.setAlignmentX(Component.LEFT_ALIGNMENT);
                            membersViewHolder.setLayout(new BoxLayout(membersViewHolder, BoxLayout.PAGE_AXIS));
                            membersViewHolder.add(membersView);

                            membersViewHolder.setAlignmentY(Component.TOP_ALIGNMENT);
                            membersViewHolder.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            pnl.add(membersViewHolder);

                            pnl.add(new JLabel("}"));

                            pnl.addAncestorListener(new AncestorListener() {
                                @Override
                                public void ancestorAdded(AncestorEvent event) {
                                    membersView.grabFocus();
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
            private asted.matching.Pattern<Character, NodeView> program = classDeclaration;

            @Override
            public boolean matches(Map<String, Buffer<Object>> locals, Input<Character> input, Buffer<NodeView> output) {
                return program.matches(locals, input, output);
            }
        };

        JComponent view = new UnderConstructionView(javaGrammar);
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(view, BorderLayout.CENTER);

        frame.setContentPane(contentPane);

        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
