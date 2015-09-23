package asted.matching;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class NodeViewComboBox<E> extends JComboBox<E> implements NodeView {
    public NodeViewComboBox() {

        setEditor(new ComboBoxEditor() {
            private NodeViewText component;

            {
                component = NodeViewPattern.createTextView("");
            }

            @Override
            public Component getEditorComponent() {
                return component;
            }

            @Override
            public void setItem(Object anObject) {
                component.setText(anObject != null ? anObject.toString() : "");
            }

            @Override
            public Object getItem() {
                return component.getText();
            }

            @Override
            public void selectAll() {
                component.selectAll();
            }

            @Override
            public void addActionListener(ActionListener l) {

            }

            @Override
            public void removeActionListener(ActionListener l) {

            }
        });

        setRenderer(new BasicComboBoxRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                ((JComponent)component).setBorder(BorderFactory.createEmptyBorder());

                return component;
            }
        });
    }

    @Override
    protected void processComponentKeyEvent(java.awt.event.KeyEvent e) {
        if(e.getID() == KeyEvent.KEY_PRESSED) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    ((NodeViewContainer) getParent()).focusEndBefore(NodeViewComboBox.this);
                    return;
                case KeyEvent.VK_RIGHT:
                    ((NodeViewContainer)getParent()).focusStartAfter(NodeViewComboBox.this);
                    return;
            }
        }

        super.processComponentKeyEvent(e);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(super.getMinimumSize().width, getPreferredSize().height);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(getPreferredSize().width, getPreferredSize().height);
    }

    @Override
    public void focusStart() {
        grabFocus();
    }

    @Override
    public void focusEnd() {
        grabFocus();
    }
}
