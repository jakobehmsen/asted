package asted;

import asted.matching.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class Main {
    public static void main(String[] args) throws BadLocationException {
        /*ScriptEngineManager engineManager =
            new ScriptEngineManager();
        ScriptEngine engine =
            engineManager.getEngineByName("nashorn");
        engine.eval("function sum(a, b) { return a + b; }");
        System.out.println(engine.eval("sum(1, 2);"));*/

        /*
        var stack =
        new java.util.LinkedList();
        [1, 2, 3, 4].forEach(function(item) {
          stack.push(item);
        });

        print(stack);
        print(stack.getClass());
        */

        /*if(1 != 2)
            return;*/

        JFrame frame = new JFrame();

        /*asted.matching.Pattern<Character, NodeViewFactory> javaGrammar = new asted.matching.Pattern<Character, NodeViewFactory>() {
            private asted.matching.Pattern<Character, NodeViewFactory> wsImpl = new asted.matching.Pattern<Character, NodeViewFactory>() {
                @Override
                public boolean matches(Map<String, Buffer<Object>> locals, Input<Character> input, Buffer<NodeViewFactory> output) {
                    while(Character.isWhitespace(input.peekChar()))
                        input.consume();

                    return true;
                }
            };
            private asted.matching.Pattern<Character, NodeViewFactory> classKW = new IsStringPattern("class");
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
                    new IsStringPattern("private"),
                    ws,
                    LocalsPattern.capture("type", id),
                    ws,
                    LocalsPattern.capture("name", id),
                    ws,
                    new IsStringPattern(";"),
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
                    new IsStringPattern("{"),
                    ws,
                    new IsStringPattern("}"),
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

                            pnl.add(membersViewHolder);

                            pnl.add(new JLabel("}"));

                            membersViewHolder.addAncestorListener(new AncestorListener() {
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
        };*/

        /*Pattern<Character, String> p = new SequencePattern<>(Arrays.asList(
            new PipePattern<>(new CapturePattern<>(StringPattern.<Character>is("class")), StringPattern.join()),
            StringPattern.ws(),
            new PipePattern<>(new CapturePattern<>(StringPattern.id()), StringPattern.join())
        ));*/

        /*Pattern<Character, JComponent> p2 = new PipePattern<>(
            new SequencePattern<>(Arrays.asList(
                new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.<Character>is("class")), StringPattern.join()), NodeViewPattern.label()),
                StringPattern.ws(),
                new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.id()), StringPattern.join()), NodeViewPattern.text())
            )),
            NodeViewPattern.leftToRightList()
        );*/


        Pattern<Character, Character> modifier = new DecisionPattern<>(Arrays.asList(
            StringPattern.<Character>is("private"),
            StringPattern.<Character>is("public"),
            StringPattern.<Character>is("protected")
        ));

        Pattern<Character, JComponent> p2 = new PipePattern<>(
            new SequencePattern<>(Arrays.asList(
                new PipePattern<>(
                    new SequencePattern<>(Arrays.asList(
                        new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.<Character>is("class")), StringPattern.join()), NodeViewPattern.label()),
                        StringPattern.ws(),
                        new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.id()), StringPattern.join()), NodeViewPattern.text()),
                        StringPattern.ws(),
                        new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.<Character>is("{")), StringPattern.join()), NodeViewPattern.label())
                    )),
                    NodeViewPattern.leftToRightList()
                ),
                StringPattern.ws(),
                NodeViewPattern.repeat(
                    new PipePattern<>(
                        new SequencePattern<>(Arrays.asList(
                            //new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.<Character>is("private")), StringPattern.join()), NodeViewPattern.label()),
                            new PipePattern<>(new PipePattern<>(new CapturePattern<>(modifier), StringPattern.join()), NodeViewPattern.decision(Arrays.asList("private", "public", "protected"))),
                            StringPattern.ws(),
                            new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.id()), StringPattern.join()), NodeViewPattern.text()),
                            StringPattern.ws(),
                            new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.id()), StringPattern.join()), NodeViewPattern.text()),
                            StringPattern.ws(),
                            new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.<Character>is(";")), StringPattern.join()), NodeViewPattern.label())
                        )),
                        NodeViewPattern.leftToRightList()
                    )
                ),
                new PipePattern<>(
                    new SequencePattern<>(Arrays.asList(
                        new PipePattern<>(new PipePattern<>(new CapturePattern<>(StringPattern.<Character>is("}")), StringPattern.join()), NodeViewPattern.label())
                    )),
                    NodeViewPattern.leftToRightList()
                )
            )),
            NodeViewPattern.topDownList()
        );

        Buffer<JComponent> o = Buffer.Util.create(JComponent.class);
        p2.matches(new Hashtable<>(), Buffer.Util.wrap("class MyClass {}").traverse(), o);

        //JComponent view = new UnderConstructionView(javaGrammar);
        JComponent view = NodeViewPattern.createUnderConstructionView(p2);
        //view = o.traverse().peek();
        NodeViewPanel contentPane = new NodeViewPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(view, BorderLayout.CENTER);

        frame.setContentPane(contentPane);

        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final Color COLOR_BUTTON_BACKGROUND = Color.decode("#d3dedb");

        /*UIManager.put("ComboBox.buttonBackground", COLOR_BUTTON_BACKGROUND);
        UIManager.put("ComboBox.buttonShadow", COLOR_BUTTON_BACKGROUND);
        UIManager.put("ComboBox.buttonDarkShadow", COLOR_BUTTON_BACKGROUND);
        UIManager.put("ComboBox.buttonHighlight", COLOR_BUTTON_BACKGROUND);*/

        frame.setVisible(true);
    }
}
