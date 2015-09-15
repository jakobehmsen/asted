package asted.matching;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Buffer<T> {
    void append(T item);
    default void appendChar(char item) {
        append((T)new Character(item));
    }
    Input<T> traverse();
    default Stream<T> toStream() {
        Iterable<T> iterable = new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    Input<T> input = traverse();

                    @Override
                    public boolean hasNext() {
                        return input.hasMore();
                    }

                    @Override
                    public T next() {
                        T value = input.peek();
                        input.consume();
                        return value;
                    }
                };
            }
        };
        return StreamSupport.stream(iterable.spliterator(), false);
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
                        public State state() {
                            return new State() {
                                int indexToRestore = index;

                                @Override
                                public void restore() {
                                    index = indexToRestore;
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
                            return chars.charAt(index);
                        }

                        @Override
                        public State state() {
                            return new State() {
                                int indexToRestore = index;

                                @Override
                                public void restore() {
                                    index = indexToRestore;
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
    }
}
