package asted;

import asted.grammar.*;
import asted.grammar.Pattern;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    private interface KeyPatternVisitor {
        boolean accepts(char ch);
        boolean expectsMore();
        void next(char ch);
        default void setSelected(boolean isSelected) {

        }
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

        public void prev(char ch) {
            index--;
        }

        public int getIndex() {
            return index;
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

                        return new KeyPatternVisitor() {
                            private int partsIndex = -1;
                            private int nodeIndex = -1;
                            private KeyPatternVisitor currentNode;

                            @Override
                            public boolean accepts(char ch) {
                                return currentNode.accepts(ch);
                            }

                            @Override
                            public boolean expectsMore() {
                                return currentNode.expectsMore();
                            }

                            @Override
                            public void next(char ch) {
                                if(partsIndex == -1) {
                                    partsIndex++;
                                    updatePart();
                                }

                                currentNode.next(ch);

                                /*if(currentNode.expectsMore()) {
                                    currentNode.next(ch);
                                }*/
                            }

                            private void updatePart() {
                                currentNode = partsIndex < parts.size()
                                    ? createKeyPatternVisitor(parts.get(partsIndex))
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

                            ArrayList<KeyPatternVisitor> selectors = new ArrayList<>();
                            //int currentSelector = -1;

                            private void select(int index) {
                                if(nodeIndex != -1) {
                                    currentNode.setSelected(false);
                                }

                                nodeIndex = index;
                                partsIndex = index;

                                if(nodeIndex != -1) {
                                    currentNode = selectors.get(nodeIndex);
                                    currentNode.setSelected(true);
                                }
                            }

                            public KeyPatternVisitor createKeyPatternVisitor(Pattern pattern) {
                                KeyPatternVisitor kpv = pattern.accept(new PatternVisitor<KeyPatternVisitor>() {
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
                                            int length = 0;

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
                                                if(ch == KeyEvent.VK_BACK_SPACE) {
                                                    if(label.getText().length() > 0) {
                                                        label.setForeground(Color.BLACK);
                                                        label.setText(label.getText().substring(0, length - 1));

                                                        if (length == keyKeywordVisitor.getIndex()) {
                                                            keyKeywordVisitor.prev(ch);
                                                        } else if (length > keyKeywordVisitor.getIndex()) {
                                                            label.setForeground(Color.RED);
                                                        }

                                                        length--;

                                                        if (length == keyKeywordVisitor.getIndex()) {
                                                            label.setForeground(Color.BLACK);
                                                        }
                                                    } else {
                                                        line.remove(label);
                                                        selectors.remove(this);
                                                        int previousIndex = nodeIndex - 1;
                                                        nodeIndex = -1;
                                                        select(previousIndex);
                                                    }
                                                } else {
                                                    if(keyKeywordVisitor.expectsMore()) {
                                                        label.setForeground(Color.BLACK);
                                                        label.setText(label.getText() + ch);

                                                        if (keyKeywordVisitor.accepts(ch)) {
                                                            keyKeywordVisitor.next(ch);
                                                            if (!keyKeywordVisitor.expectsMore()) {
                                                                //label.setOpaque(false);
                                                                label.setForeground(Color.BLUE);
                                                                //label.setBackground(line.getBackground());

                                                                line.add(Box.createRigidArea(new Dimension(5, 0)));

                                                                partsIndex++;
                                                                updatePart();
                                                            }
                                                        } else {
                                                            //canMatch = false;
                                                            label.setForeground(Color.RED);
                                                        }

                                                        length++;
                                                    } else {
                                                        partsIndex++;
                                                        updatePart();
                                                        currentNode.next(ch);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void setSelected(boolean isSelected) {
                                                if(isSelected) {
                                                    label.setOpaque(true);
                                                    label.setBackground(Color.WHITE);
                                                } else {
                                                    label.setOpaque(false);
                                                }

                                                label.revalidate();
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

                                selectors.add(kpv);
                                select(selectors.size() - 1);
                                //currentSelector = selectors.size() - 1;

                                return kpv;
                            };
                        };

                        /*KeyPatternVisitorFactory keyPatternVisitorFactory = new KeyPatternVisitorFactory() {
                            ArrayList<KeyPatternVisitor> selectors = new ArrayList<>();
                            int currentSelector = -1;

                            private void setSelected(int index) {
                                if(currentSelector != -1)
                                    selectors.get(currentSelector).setSelected(false);

                                if(index != -1)
                                    selectors.get(index).setSelected(true);

                                currentSelector = index;
                            }

                            @Override
                            public KeyPatternVisitor createKeyPatternVisitor(Pattern pattern) {
                                KeyPatternVisitor kpv = pattern.accept(new PatternVisitor<KeyPatternVisitor>() {
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
                                            int length = 0;

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
                                                if(ch == KeyEvent.VK_BACK_SPACE) {
                                                    label.setForeground(Color.BLACK);
                                                    label.setText(label.getText().substring(0, length - 1));

                                                    if(length == keyKeywordVisitor.getIndex()) {
                                                        keyKeywordVisitor.prev(ch);
                                                    } else if(length > keyKeywordVisitor.getIndex()) {
                                                        label.setForeground(Color.RED);
                                                    }

                                                    length--;

                                                    if(length == keyKeywordVisitor.getIndex()) {
                                                        label.setForeground(Color.BLACK);
                                                    }
                                                } else {
                                                    label.setForeground(Color.BLACK);
                                                    label.setText(label.getText() + ch);

                                                    if (keyKeywordVisitor.accepts(ch)) {
                                                        keyKeywordVisitor.next(ch);
                                                        if (!keyKeywordVisitor.expectsMore()) {
                                                            label.setOpaque(false);
                                                            label.setForeground(Color.BLUE);
                                                            //label.setBackground(line.getBackground());

                                                            line.add(Box.createRigidArea(new Dimension(5, 0)));
                                                        }
                                                    } else {
                                                        //canMatch = false;
                                                        label.setForeground(Color.RED);
                                                    }

                                                    length++;
                                                }
                                            }

                                            @Override
                                            public void setSelected(boolean isSelected) {
                                                if(isSelected) {
                                                    label.setOpaque(true);
                                                    label.setBackground(Color.WHITE);
                                                } else {
                                                    label.setOpaque(false);
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

                                selectors.add(kpv);
                                setSelected(selectors.size() - 1);
                                currentSelector = selectors.size() - 1;

                                return kpv;
                            };
                        };

                        return new SequenceKeywordVisitor(parts, keyPatternVisitorFactory);*/
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
                /*if (keyPatternVisitor.expectsMore()) {
                    if (keyPatternVisitor.accepts(e.getKeyChar())) {
                        keyPatternVisitor.next(e.getKeyChar());
                    }
                }*/
                keyPatternVisitor.next(e.getKeyChar());
            }
        });

        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
