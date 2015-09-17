package asted.matching;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Optional;
import java.util.stream.IntStream;

public class NodeViewPanel extends JPanel implements NodeView, NodeViewContainer {
    @Override
    public void focusStart() {
        Optional<Component> textComponent = IntStream.iterate(0, i -> i + 1).limit(getComponentCount())
            .mapToObj(i -> getComponent(i))
            .filter(c -> c instanceof NodeView)
            .findFirst();

        if(textComponent.isPresent()) {
            ((NodeView)textComponent.get()).focusEnd();
        }
    }

    @Override
    public void focusEnd() {
        Optional<Component> textComponent = IntStream.iterate(getComponentCount() - 1, i -> i - 1).limit(getComponentCount())
            .mapToObj(i -> getComponent(i))
            .filter(c -> c instanceof NodeView)
            .findFirst();

        if(textComponent.isPresent()) {
            ((NodeView)textComponent.get()).focusEnd();
        }
    }

    @Override
    public void activate() {

    }

    @Override
    public void focusEndBefore(JComponent component) {
        int index = getComponentZOrder(component);

        Optional<Component> textComponent = IntStream.iterate(index - 1, i -> i - 1).limit(index)
            .mapToObj(i -> getComponent(i))
            .filter(c -> c instanceof NodeView)
            .findFirst();

        if(textComponent.isPresent()) {
            ((NodeView)textComponent.get()).focusEnd();
        } else {
            if(getParent() instanceof NodeViewContainer)
                ((NodeViewContainer)getParent()).focusEndBefore(this);
        }
    }

    @Override
    public void focusStartAfter(JComponent component) {
        int index = getComponentZOrder(component);

        Optional<Component> textComponent = IntStream.iterate(index + 1, i -> i + 1).limit(getComponentCount() - index - 1)
            .mapToObj(i -> getComponent(i))
            .filter(c -> c instanceof NodeView)
            .findFirst();

        if(textComponent.isPresent()) {
            ((NodeView)textComponent.get()).focusStart();
        } else {
            if(getParent() instanceof NodeViewContainer)
                ((NodeViewContainer)getParent()).focusStartAfter(this);
        }
    }
}
