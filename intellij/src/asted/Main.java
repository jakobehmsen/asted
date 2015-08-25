package asted;

import asted.grammar.*;
import asted.grammar.Pattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    private interface KeyPatternVisitor {
        boolean accepts(char ch);
        boolean expectsMore();
        void next(char ch);
    }

    private interface KeyPatternVisitorFactory {
        KeyPatternVisitor createKeyPatternVisitor(Pattern pattern);
    }

    private interface Builder {
        void commit();
    }

    private interface TupleBuilder extends Builder {
        AtomBuilder appendAtom(Pattern pattern);
        TupleBuilder appendTuple(Pattern pattern);
    }

    private interface AtomBuilder extends Builder {
        void append(char ch);
    }

    private static class KeyKeywordVisitor implements KeyPatternVisitor {
        private String word;
        private int index;

        private KeyKeywordVisitor(String word) {
            this.word = word;
        }

        @Override
        public boolean accepts(char ch) {
            return word.charAt(index) == ch;
        }

        @Override
        public boolean expectsMore() {
            return index < word.length();
        }

        public void next(char ch) {
            index++;
        }
    }

    private static class SequenceKeywordVisitor implements KeyPatternVisitor {
        private List<Pattern> parts;
        private KeyPatternVisitorFactory keyPatternVisitorFactory;
        private KeyPatternVisitor part;
        private int index;

        private SequenceKeywordVisitor(List<Pattern> parts, KeyPatternVisitorFactory keyPatternVisitorFactory) {
            this.parts = parts;
            this.keyPatternVisitorFactory = keyPatternVisitorFactory;

            updatePart();
        }

        private void updatePart() {
            part = index < parts.size()
                ? keyPatternVisitorFactory.createKeyPatternVisitor(parts.get(index))
                : new KeyPatternVisitor() {
                @Override
                public boolean accepts(char ch) {
                    return false;
                }

                @Override
                public boolean expectsMore() {
                    return false;
                }

                @Override
                public void next(char ch) {

                }
            };
        }

        @Override
        public boolean accepts(char ch) {
            return part.accepts(ch);
        }

        @Override
        public boolean expectsMore() {
            return part.expectsMore();
        }

        @Override
        public void next(char ch) {
            if(part.expectsMore()) {
                part.next(ch);

                if(!part.expectsMore()) {
                    index++;
                    updatePart();
                }
            }
        }
    }

    public static void main(String[] args) {
        final DefaultRuleSet rules = new DefaultRuleSet();

        rules.put("program", new Sequence(Arrays.asList(
            new Keyword("if"),
            new Keyword("then"),
            new Decision(
                Arrays.asList(
                    new Keyword("else"),
                    new Sequence(Collections.<Pattern>emptyList())
                )
            )
        )));

        JFrame frame = new JFrame();

        final JPanel content = (JPanel) frame.getContentPane();

        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        final KeyPatternVisitorFactory keyPatternVisitorFactory = new KeyPatternVisitorFactory() {
            @Override
            public KeyPatternVisitor createKeyPatternVisitor(Pattern pattern) {
                return pattern.accept(new PatternVisitor<KeyPatternVisitor>() {
                    @Override
                    public KeyPatternVisitor visitMention(String name) {
                        Pattern mentionedPattern = rules.get(name);
                        return mentionedPattern.accept(this);
                    }

                    @Override
                    public KeyPatternVisitor visitKeyword(String word) {
                        return null;
                    }

                    @Override
                    public KeyPatternVisitor visitRegex(java.util.regex.Pattern pattern) {
                        return null;
                    }

                    @Override
                    public KeyPatternVisitor visitSequence(List<Pattern> parts) {
                        final JPanel line = new JPanel();
                        line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
                        content.add(line);

                        KeyPatternVisitorFactory keyPatternVisitorFactory = new KeyPatternVisitorFactory() {
                            @Override
                            public KeyPatternVisitor createKeyPatternVisitor(Pattern pattern) {
                                return pattern.accept(new PatternVisitor<KeyPatternVisitor>() {
                                    @Override
                                    public KeyPatternVisitor visitMention(String name) {
                                        return null;
                                    }

                                    @Override
                                    public KeyPatternVisitor visitKeyword(String word) {
                                        final JLabel label = new JLabel();
                                        final KeyKeywordVisitor keyKeywordVisitor = new KeyKeywordVisitor(word);
                                        line.add(label);

                                        return new KeyPatternVisitor() {
                                            @Override
                                            public boolean accepts(char ch) {
                                                return keyKeywordVisitor.accepts(ch);
                                            }

                                            @Override
                                            public boolean expectsMore() {
                                                return keyKeywordVisitor.expectsMore();
                                            }

                                            @Override
                                            public void next(char ch) {
                                                keyKeywordVisitor.next(ch);
                                                label.setText(label.getText() + ch);
                                                if(!keyKeywordVisitor.expectsMore()) {
                                                    label.setForeground(Color.BLUE);

                                                    line.add(Box.createRigidArea(new Dimension(5, 0)));
                                                }
                                            }
                                        };
                                    }

                                    @Override
                                    public KeyPatternVisitor visitRegex(java.util.regex.Pattern pattern) {
                                        return null;
                                    }

                                    @Override
                                    public KeyPatternVisitor visitSequence(List<Pattern> parts) {
                                        return null;
                                    }

                                    @Override
                                    public KeyPatternVisitor visitDecision(List<Pattern> alternatives) {
                                        return null;
                                    }
                                });
                            };
                        };

                        return new SequenceKeywordVisitor(parts, keyPatternVisitorFactory);
                    }

                    @Override
                    public KeyPatternVisitor visitDecision(List<Pattern> alternatives) {
                        return null;
                    }
                });
            }
        };

        frame.addKeyListener(new KeyAdapter() {
            KeyPatternVisitor keyPatternVisitor;

            {
                keyPatternVisitor = keyPatternVisitorFactory.createKeyPatternVisitor(new Mention("program"));
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (keyPatternVisitor.expectsMore()) {
                    if (keyPatternVisitor.accepts(e.getKeyChar())) {
                        keyPatternVisitor.next(e.getKeyChar());
                    }
                }
            }
        });

        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
