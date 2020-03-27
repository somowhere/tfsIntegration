//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.ui.TextFieldWithBrowseButton.NoPathCompletion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTextField;
import org.jetbrains.annotations.Nullable;

public class FieldWithButtonCellEditor<T> extends DefaultCellEditor {
    public FieldWithButtonCellEditor(boolean pathCompletion, final FieldWithButtonCellEditor.Helper<T> helper) {
        super(new JTextField());
        this.setClickCountToStart(1);
        final TextFieldWithBrowseButton field = pathCompletion ? new TextFieldWithBrowseButton() : new NoPathCompletion();
        ((TextFieldWithBrowseButton)field).setOpaque(false);
        ((TextFieldWithBrowseButton)field).getTextField().setBorder(BorderFactory.createEmptyBorder());
        ((TextFieldWithBrowseButton)field).getButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String result = helper.processButtonClick(((TextFieldWithBrowseButton)field).getText());
                if (result != null) {
                    ((TextFieldWithBrowseButton)field).setText(result);
                }

            }
        });
        this.delegate = new EditorDelegate() {
            public void setValue(Object value) {
                ((TextFieldWithBrowseButton)field).setText(helper.toStringRepresentation((T) value));
            }

            @Nullable
            public Object getCellEditorValue() {
                return helper.fromStringRepresentation(((TextFieldWithBrowseButton)field).getText());
            }

            public boolean shouldSelectCell(EventObject anEvent) {
                if (anEvent instanceof MouseEvent) {
                    MouseEvent e = (MouseEvent)anEvent;
                    return e.getID() != 506;
                } else {
                    return true;
                }
            }
        };
        this.editorComponent = (JComponent)field;
    }

    public interface Helper<T> {
        String toStringRepresentation(@Nullable T var1);

        @Nullable
        T fromStringRepresentation(@Nullable String var1);

        String processButtonClick(String var1);
    }
}
