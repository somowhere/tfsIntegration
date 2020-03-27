//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.ui.CollectionListModel;
import com.intellij.ui.DoubleClickListener;
import com.intellij.util.EventDispatcher;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventListener;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jetbrains.tfsIntegration.checkin.PolicyBase;

public class ChooseCheckinPolicyForm {
    private JList myPoliciesList;
    private JPanel myContentPane;
    private JTextArea myDescriptionArea;
    private final EventDispatcher<ChooseCheckinPolicyForm.Listener> myEventDispatcher;

    public ChooseCheckinPolicyForm(List<PolicyBase> policies) {
        this.myEventDispatcher = EventDispatcher.create(ChooseCheckinPolicyForm.Listener.class);
        this.myPoliciesList.setSelectionMode(0);
        this.myPoliciesList.setModel(new CollectionListModel(policies));
        this.myPoliciesList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setText(((PolicyBase)value).getPolicyType().getName());
                return label;
            }
        });
        this.myPoliciesList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                PolicyBase policy = ChooseCheckinPolicyForm.this.getSelectedPolicy();
                ChooseCheckinPolicyForm.this.myDescriptionArea.setText(policy != null ? policy.getPolicyType().getDescription() : null);
                ((ChooseCheckinPolicyForm.Listener)ChooseCheckinPolicyForm.this.myEventDispatcher.getMulticaster()).stateChanged();
            }
        });
        (new DoubleClickListener() {
            protected boolean onDoubleClick(MouseEvent e) {
                if (ChooseCheckinPolicyForm.this.getSelectedPolicy() != null) {
                    ((ChooseCheckinPolicyForm.Listener)ChooseCheckinPolicyForm.this.myEventDispatcher.getMulticaster()).close();
                    return true;
                } else {
                    return false;
                }
            }
        }).installOn(this.myPoliciesList);
        this.myDescriptionArea.setWrapStyleWord(true);
    }

    public PolicyBase getSelectedPolicy() {
        return (PolicyBase)this.myPoliciesList.getSelectedValue();
    }

    public void addListener(ChooseCheckinPolicyForm.Listener listener) {
        this.myEventDispatcher.addListener(listener);
    }

    public JComponent getContentPane() {
        return this.myContentPane;
    }

    public interface Listener extends EventListener {
        void stateChanged();

        void close();
    }
}
