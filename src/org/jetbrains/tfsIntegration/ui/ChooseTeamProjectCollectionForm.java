//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.TableSpeedSearch;
import com.intellij.ui.table.TableView;
import com.intellij.util.EventDispatcher;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.config.TfsServerConnectionHelper.TeamProjectCollectionDescriptor;
import org.jetbrains.tfsIntegration.core.TFSBundle;

public class ChooseTeamProjectCollectionForm {
    private JPanel myContentPane;
    private JTextField myAddressField;
    private TableView myTable;
    private JLabel myMessageLabel;
    private final EventDispatcher<ChooseTeamProjectCollectionForm.Listener> myEventDispatcher;

    public ChooseTeamProjectCollectionForm(String serverAddress, Collection<? extends TeamProjectCollectionDescriptor> items) {
        this.myEventDispatcher = EventDispatcher.create(ChooseTeamProjectCollectionForm.Listener.class);
        this.myAddressField.setText(serverAddress);
        ColumnInfo<TeamProjectCollectionDescriptor, String> displayNameColumn = new ColumnInfo<TeamProjectCollectionDescriptor, String>(TFSBundle.message("team.project.collection.table.column.display.name", new Object[0])) {
            public String valueOf(TeamProjectCollectionDescriptor teamProjectCollectionDescriptor) {
                return teamProjectCollectionDescriptor.name;
            }
        };
        List<TeamProjectCollectionDescriptor> sorted = new ArrayList(items);
        Collections.sort(sorted, (o1, o2) -> {
            return o1.name.compareToIgnoreCase(o2.name);
        });
        this.myTable.setTableHeader((JTableHeader)null);
        this.myTable.setSelectionMode(0);
        this.myTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ((ChooseTeamProjectCollectionForm.Listener)ChooseTeamProjectCollectionForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
            }
        });
        this.myTable.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (10 == e.getKeyCode()) {
                    ChooseTeamProjectCollectionForm.this.selected();
                    e.consume();
                }

            }
        });
        (new DoubleClickListener() {
            protected boolean onDoubleClick(MouseEvent e) {
                ChooseTeamProjectCollectionForm.this.selected();
                return true;
            }
        }).installOn(this.myTable);
        this.myMessageLabel.setIcon(UIUtil.getBalloonWarningIcon());
        new TableSpeedSearch(this.myTable);
        this.myTable.setModelAndUpdateColumns(new ListTableModel(new ColumnInfo[]{displayNameColumn}, sorted, 0));
        this.myTable.setSelection(Collections.singletonList(sorted.get(0)));
    }

    private void selected() {
        TeamProjectCollectionDescriptor selectedItem = this.getSelectedItem();
        if (selectedItem != null) {
            ((ChooseTeamProjectCollectionForm.Listener)this.myEventDispatcher.getMulticaster()).selected();
        }

    }

    public JPanel getContentPane() {
        return this.myContentPane;
    }

    @Nullable
    public TeamProjectCollectionDescriptor getSelectedItem() {
        return (TeamProjectCollectionDescriptor)this.myTable.getSelectedObject();
    }

    public void addChangeListener(ChooseTeamProjectCollectionForm.Listener listener) {
        this.myEventDispatcher.addListener(listener);
    }

    public JComponent getPreferredFocusedComponent() {
        return this.myTable;
    }

    public void setErrorMessage(@Nullable String errorMessage) {
        if (errorMessage != null && !errorMessage.endsWith(".")) {
            errorMessage = errorMessage + ".";
        }

        this.myMessageLabel.setText(errorMessage);
        this.myMessageLabel.setVisible(errorMessage != null);
    }

    public interface Listener extends ChangeListener {
        void selected();
    }
}
