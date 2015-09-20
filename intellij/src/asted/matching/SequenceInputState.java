package asted.matching;

public abstract class SequenceInputState<T> implements InputState<T> {
    private int index;

    public SequenceInputState(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
