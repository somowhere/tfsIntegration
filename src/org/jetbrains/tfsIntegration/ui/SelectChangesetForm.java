//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.DoubleClickListener;
import com.intellij.util.EventDispatcher;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Changeset;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.EventListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.DateVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.ChangesetsTableModel.Column;

public class SelectChangesetForm {
    private static final DateFormat DATE_FORMAT = DateFormat.getInstance();
    private JTable myChangesetsTable;
    private JTextField myPathField;
    private JTextField myUserField;
    private JRadioButton myAllChangesRadioButton;
    private JRadioButton myChangeNumberRadioButton;
    private JTextField myFromChangesetField;
    private JTextField myToChangesetField;
    private JRadioButton myCreatedDateRadioButton;
    private JTextField myFromDateField;
    private JTextField myToDateField;
    private JButton myFindButton;
    private JPanel panel;
    private final ChangesetsTableModel myChangesetsTableModel;
    private final WorkspaceInfo myWorkspace;
    private final String myServerPath;
    private final boolean myRecursive;
    private final EventDispatcher<SelectChangesetForm.Listener> myEventDispatcher;

    public SelectChangesetForm(WorkspaceInfo workspace, String serverPath, boolean recursive) {
        this.myEventDispatcher = EventDispatcher.create(SelectChangesetForm.Listener.class);
        this.myWorkspace = workspace;
        this.myServerPath = serverPath;
        this.myRecursive = recursive;
        this.myChangesetsTableModel = new ChangesetsTableModel();
        this.myChangesetsTable.setModel(this.myChangesetsTableModel);
        this.myChangesetsTable.setSelectionMode(0);

        for(int i = 0; i < Column.values().length; ++i) {
            this.myChangesetsTable.getColumnModel().getColumn(i).setPreferredWidth(Column.values()[i].getWidth());
        }

        this.myPathField.setText(serverPath);
        this.myFindButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SelectChangesetForm.this.search();
            }
        });
        ActionListener radioButtonListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SelectChangesetForm.this.updateControls();
            }
        };
        this.myAllChangesRadioButton.addActionListener(radioButtonListener);
        this.myChangeNumberRadioButton.addActionListener(radioButtonListener);
        this.myCreatedDateRadioButton.addActionListener(radioButtonListener);
        this.myChangesetsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                ((SelectChangesetForm.Listener)SelectChangesetForm.this.myEventDispatcher.getMulticaster()).selectionChanged(SelectChangesetForm.this.getSelectedChangeset());
            }
        });
        (new DoubleClickListener() {
            protected boolean onDoubleClick(MouseEvent e) {
                Integer changeset = SelectChangesetForm.this.getSelectedChangeset();
                if (changeset != null) {
                    ((SelectChangesetForm.Listener)SelectChangesetForm.this.myEventDispatcher.getMulticaster()).selected(changeset);
                    return true;
                } else {
                    return false;
                }
            }
        }).installOn(this.myChangesetsTable);
        this.myAllChangesRadioButton.setSelected(true);
        this.updateControls();
    }

    private void search() {
        VersionSpec versionFrom = null;
        Object versionTo = LatestVersionSpec.INSTANCE;

        try {
            if (this.myChangeNumberRadioButton.isSelected()) {
                if (this.myFromChangesetField.getText() != null && this.myFromChangesetField.getText().length() > 0) {
                    versionFrom = new ChangesetVersionSpec(Integer.parseInt(this.myFromChangesetField.getText()));
                }

                if (this.myToChangesetField.getText() != null && this.myToChangesetField.getText().length() > 0) {
                    versionTo = new ChangesetVersionSpec(Integer.parseInt(this.myToChangesetField.getText()));
                }
            } else if (this.myCreatedDateRadioButton.isSelected()) {
                if (this.myFromDateField.getText() != null && this.myFromDateField.getText().length() > 0) {
                    versionFrom = new DateVersionSpec(DateFormat.getInstance().parse(this.myFromDateField.getText()));
                }

                if (this.myToDateField.getText() != null && this.myToDateField.getText().length() > 0) {
                    versionTo = new DateVersionSpec(DateFormat.getInstance().parse(this.myToDateField.getText()));
                }
            }

            List<Changeset> changesets = this.myWorkspace.getServer().getVCS().queryHistory(this.myWorkspace, this.myServerPath, this.myRecursive, this.myUserField.getText(), (VersionSpec)versionFrom, (VersionSpec)versionTo, this.getContentPane(), TFSBundle.message("loading.history", new Object[0]), 2147483647);
            if (changesets.isEmpty()) {
                Messages.showInfoMessage(this.panel, "No matching changesets found", "Find Changeset");
            }

            this.myChangesetsTableModel.setChangesets(changesets);
        } catch (TfsException var4) {
            this.myChangesetsTableModel.setChangesets(Collections.emptyList());
            Messages.showErrorDialog(this.panel, var4.getMessage(), "Find Changeset");
        } catch (NumberFormatException var5) {
            this.myChangesetsTableModel.setChangesets(Collections.emptyList());
            Messages.showErrorDialog(this.panel, "Invalid changeset number specified", "Find Changeset");
        } catch (ParseException var6) {
            this.myChangesetsTableModel.setChangesets(Collections.emptyList());
            Messages.showErrorDialog(this.panel, "Invalid date specified", "Find Changeset");
        }

    }

    private void updateControls() {
        this.myFromChangesetField.setEnabled(this.myChangeNumberRadioButton.isSelected());
        this.myToChangesetField.setEnabled(this.myChangeNumberRadioButton.isSelected());
        if (!this.myChangeNumberRadioButton.isSelected()) {
            this.myFromChangesetField.setText((String)null);
            this.myToChangesetField.setText((String)null);
        }

        this.myFromDateField.setEnabled(this.myCreatedDateRadioButton.isSelected());
        this.myToDateField.setEnabled(this.myCreatedDateRadioButton.isSelected());
        if (!this.myCreatedDateRadioButton.isSelected()) {
            this.myFromDateField.setText((String)null);
            this.myToDateField.setText((String)null);
        } else {
            String currentDate = DATE_FORMAT.format(new Date());
            this.myFromDateField.setText(currentDate);
            this.myToDateField.setText(currentDate);
        }

    }

    public JComponent getContentPane() {
        return this.panel;
    }

    @Nullable
    public Integer getSelectedChangeset() {
        return this.myChangesetsTable.getSelectedRowCount() == 1 ? ((Changeset)this.myChangesetsTableModel.getChangesets().get(this.myChangesetsTable.getSelectedRow())).getCset() : null;
    }

    public void addListener(SelectChangesetForm.Listener listener) {
        this.myEventDispatcher.addListener(listener);
    }

    public void removeListener(SelectChangesetForm.Listener listener) {
        this.myEventDispatcher.removeListener(listener);
    }

    interface Listener extends EventListener {
        void selectionChanged(Integer var1);

        void selected(Integer var1);
    }
}
