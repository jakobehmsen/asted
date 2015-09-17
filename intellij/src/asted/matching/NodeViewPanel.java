package asted.matching;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Optional;
import java.util.stream.IntStream;

public class NodeViewPanel extends JPanel implements NodeView {
    @Override
    public void focusStart() {

    }

    @Override
    public void focusEnd() {
        // Find last JTextComponent, grab focus for it, and set caret to last position

        Optional<Component> textComponent = IntStream.iterate(getComponentCount() - 1, i -> i - 1).limit(getComponentCount())
            .mapToObj(i -> getComponent(i))
            .filter(c -> c instanceof JTextComponent)
            .findFirst();

        if(textComponent.isPresent()) {
            ((JTextComponent)textComponent.get()).setCaretPosition(((JTextComponent)textComponent.get()).getDocument().getLength());
            ((JTextComponent)textComponent.get()).grabFocus();
        }
    }
}
