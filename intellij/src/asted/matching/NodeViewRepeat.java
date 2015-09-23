package asted.matching;

import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;

public class NodeViewRepeat extends NodeViewPanel {
    private Pattern<Character, NodeViewFactory> pattern;

    public NodeViewRepeat(Pattern<Character, NodeViewFactory> pattern) {
        this.pattern = pattern;

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                activate();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {

            }

            @Override
            public void ancestorMoved(AncestorEvent event) {

            }
        });
    }

    @Override
    public void activate() {
        UnderConstructionView membersView = new UnderConstructionView(pattern);
        membersView.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.add(membersView);
        membersView.grabFocus();
    }
}
