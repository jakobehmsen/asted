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
            private asted.matching.Pattern<Character, NodeView> classKW =
                new asted.matching.SequencePattern<Character, NodeView>(Arrays.asList(
                    new asted.matching.KeywordPattern("class")
                ));
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

            private asted.matching.Pattern<Character, NodeView> ws = new ReferencePattern<>(() -> wsImpl);
            private asted.matching.Pattern<Character, NodeView> program =
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

                            final JTextArea nameField = new JTextArea(name);

                            nameField.addComponentListener(new ComponentListener() {
                                boolean initialized;
                                @Override
                                public void componentResized(ComponentEvent e) {
                                    if(!initialized) {
                                        Rectangle r = null;
                                        try {
                                            r = nameField.modelToView(nameField.getDocument().getLength());
                                            System.out.println(r);
                                        } catch (BadLocationException e2) {
                                            e2.printStackTrace();
                                        }
                                        if(r != null) {
                                            nameField.setPreferredSize(new Dimension(r.x + r.width, r.height));
                                            nameField.setMinimumSize(new Dimension(r.x + r.width, r.height));
                                            nameField.setMaximumSize(new Dimension(r.x + r.width, r.height));
                                            pnl.revalidate();
                                            pnl.repaint();
                                        }
                                    }
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
                            nameField.addAncestorListener(new AncestorListener() {
                                @Override
                                public void ancestorAdded(AncestorEvent event) {
                                    Rectangle r = null;
                                    try {
                                        r = nameField.modelToView(nameField.getDocument().getLength());
                                        System.out.println(r);
                                    } catch (BadLocationException e) {
                                        e.printStackTrace();
                                    }
                                    if(r != null) {
                                        nameField.setPreferredSize(new Dimension(r.x + r.width, r.height));
                                        nameField.setMinimumSize(new Dimension(r.x + r.width, r.height));
                                        nameField.setMaximumSize(new Dimension(r.x + r.width, r.height));
                                        pnl.revalidate();
                                        pnl.repaint();
                                    }
                                }

                                @Override
                                public void ancestorRemoved(AncestorEvent event) {

                                }

                                @Override
                                public void ancestorMoved(AncestorEvent event) {

                                }
                            });
                            nameField.getDocument().addDocumentListener(new DocumentListener() {
                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                    Rectangle r = null;
                                    try {
                                        r = nameField.modelToView(nameField.getDocument().getLength());
                                        System.out.println(r);
                                    } catch (BadLocationException e2) {
                                        e2.printStackTrace();
                                    }
                                    nameField.setPreferredSize(new Dimension(r.x + r.width, r.height));
                                    nameField.setMinimumSize(new Dimension(r.x + r.width, r.height));
                                    nameField.setMaximumSize(new Dimension(r.x + r.width, r.height));
                                    pnl.revalidate();
                                    pnl.repaint();
                                }

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                    Rectangle r = null;
                                    try {
                                        r = nameField.modelToView(nameField.getDocument().getLength());
                                        System.out.println(r);
                                    } catch (BadLocationException e2) {
                                        e2.printStackTrace();
                                    }
                                    nameField.setPreferredSize(new Dimension(r.x + r.width, r.height));
                                    nameField.setMinimumSize(new Dimension(r.x + r.width, r.height));
                                    nameField.setMaximumSize(new Dimension(r.x + r.width, r.height));
                                    pnl.revalidate();
                                    pnl.repaint();
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                    Rectangle r = null;
                                    try {
                                        r = nameField.modelToView(nameField.getDocument().getLength());
                                        System.out.println(r);
                                    } catch (BadLocationException e2) {
                                        e2.printStackTrace();
                                    }
                                    nameField.setPreferredSize(new Dimension(r.x + r.width, r.height));
                                    nameField.setMinimumSize(new Dimension(r.x + r.width, r.height));
                                    nameField.setMaximumSize(new Dimension(r.x + r.width, r.height));
                                    pnl.revalidate();
                                    pnl.repaint();
                                }
                            });

                            classDesc.add(nameField);
                            JLabel openBraLbl = new JLabel(" {");
                            classDesc.add(openBraLbl);

                            pnl.add(classDesc);

                            UnderConstructionView membersView = new UnderConstructionView(classKW);
                            pnl.addAncestorListener(new AncestorListener() {
                                @Override
                                public void ancestorAdded(AncestorEvent event) {
                                    nameField.setPreferredSize(new Dimension(nameField.getPreferredSize().width, classDesc.getComponent(0).getMaximumSize().height));
                                    nameField.setMaximumSize(nameField.getPreferredSize());
                                    membersView.grabFocus();
                                }

                                @Override
                                public void ancestorRemoved(AncestorEvent event) {

                                }

                                @Override
                                public void ancestorMoved(AncestorEvent event) {

                                }
                            });
                            membersView.setAlignmentX(Component.LEFT_ALIGNMENT);
                            membersView.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
                            pnl.add(membersView);

                            pnl.add(new JLabel("}"));

                            return pnl;
                        }

                        @Override
                        public String toString() {
                            return "class '" + name + "'{ }";
                        }
                    })
                ));

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
