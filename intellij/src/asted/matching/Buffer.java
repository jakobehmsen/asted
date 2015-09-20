package asted.matching;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Buffer<T> {
    void append(T item);

    default void appendAll(List<T> sequence) {
        for (T x : sequence)
            append(x);
    }

    default void appendChar(char item) {
        append((T) new Character(item));
    }

    default void appendChars(CharSequence charSequence) {
        for(int i = 0; i < charSequence.length(); i++)
            appendChar(charSequence.charAt(i));
    }

    Input<T> traverse();

    default Stream<T> toStream() {
        return traverse().toStream();
    }

    State state();

    class Util {
        public static Buffer<Object> create() {
            return create(Object.class);
        }

        public static <T> Buffer<T> create(Class<T> c) {
            return wrap(new ArrayList<T>());
        }

        public static <T> Buffer<T> wrap(List<T> list) {
            return new Buffer<T>() {
                @Override
                public void append(T item) {
                    list.add(item);
                }

                @Override
                public void appendAll(List<T> sequence) {
                    list.addAll(sequence);
                }

                @Override
                public Input<T> traverse() {
                    return new Input<T>() {
                        private int index;

                        @Override
                        public boolean hasMore() {
                            return index < list.size();
                        }

                        @Override
                        public void consume() {
                            index++;
                        }

                        @Override
                        public T peek() {
                            return list.get(index);
                        }

                        @Override
                        public InputState<T> state() {
                            return new SequenceInputState<T>(index) {
                                @Override
                                public void tillCopy(InputState<T> end, Buffer<T> target) {
                                    int endIndex = ((SequenceInputState<T>)end).getIndex();
                                    target.appendAll(list.subList(getIndex(), endIndex));
                                }

                                @Override
                                public void restore() {
                                    index = getIndex();
                                }
                            };
                        }
                    };
                }

                @Override
                public State state() {
                    return new State() {
                        private int size = list.size();

                        @Override
                        public void restore() {
                            list.subList(size, list.size()).clear();
                        }
                    };
                }
            };
        }

        public static Buffer<Character> wrap(CharSequence chars) {
            return wrap(new StringBuffer(chars));
        }

        public static Buffer<Character> wrap(StringBuffer chars) {
            return new Buffer<Character>() {
                @Override
                public void append(Character item) {
                    append(item.charValue());
                }

                @Override
                public void appendChar(char item) {
                    chars.append(item);
                }

                @Override
                public void appendChars(CharSequence charSequence) {
                    chars.append(charSequence);
                }

                @Override
                public Input<Character> traverse() {
                    return new Input<Character>() {
                        private int index;

                        @Override
                        public boolean hasMore() {
                            return index < chars.length();
                        }

                        @Override
                        public void consume() {
                            index++;
                        }

                        @Override
                        public Character peek() {
                            return peekChar();
                        }

                        @Override
                        public char peekChar() {
                            return hasMore() ? chars.charAt(index) : (char)-1;
                        }

                        @Override
                        public InputState<Character> state() {
                            return new SequenceInputState<Character>(index) {
                                @Override
                                public void tillCopy(InputState<Character> end, Buffer<Character> target) {
                                    int endIndex = ((SequenceInputState<Character>)end).getIndex();
                                    target.appendChars(chars.subSequence(getIndex(), endIndex));
                                }

                                @Override
                                public void restore() {
                                    index = getIndex();
                                }
                            };
                        }

                        @Override
                        public void copyTo(Buffer<Character> output) {
                            output.appendChar(peekChar());
                        }
                    };
                }

                @Override
                public State state() {
                    return new State() {
                        private int length = chars.length();

                        @Override
                        public void restore() {
                            chars.delete(length, chars.length());
                        }
                    };
                }
            };
        }
    };
}
