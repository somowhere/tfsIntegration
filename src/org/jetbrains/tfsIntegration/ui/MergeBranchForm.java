//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton.NoPathCompletion;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.util.EventDispatcher;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.BranchRelative;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Changeset;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.MergeCandidate;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.exceptions.UserCancelledException;
import org.jetbrains.tfsIntegration.ui.ChangesetsTableModel.Column;
import org.jetbrains.tfsIntegration.ui.servertree.ServerBrowserDialog;
import org.jetbrains.tfsIntegration.ui.servertree.TfsTreeForm.SelectedItem;

public class MergeBranchForm {
    private NoPathCompletion mySourceField;
    private JComboBox myTargetCombo;
    private JComboBox myChangesTypeCombo;
    private final SelectRevisionForm mySelectRevisionForm;
    private JPanel myContentPanel;
    private JPanel myChangesetsPanel;
    private JLabel mySourceBranchLabel;
    private final Project myProject;
    private final WorkspaceInfo myWorkspace;
    private final JTable myChangesetsTable;
    private final ChangesetsTableModel myChangesetsTableModel;
    private final String myDialogTitle;
    private final EventDispatcher<MergeBranchForm.Listener> myEventDispatcher;
    private boolean mySourceIsDirectory;
    private final FocusListener mySourceFieldFocusListener;

    public MergeBranchForm(final Project project, final WorkspaceInfo workspace, String initialSourcePath, boolean initialSourcePathIsDirectory, String dialogTitle) {
        this.myEventDispatcher = EventDispatcher.create(MergeBranchForm.Listener.class);
        this.myProject = project;
        this.myWorkspace = workspace;
        this.myDialogTitle = dialogTitle;
        this.mySourceBranchLabel.setLabelFor(this.mySourceField.getChildComponent());
        this.myChangesetsTableModel = new ChangesetsTableModel();
        this.myChangesetsTable = new JBTable(this.myChangesetsTableModel);
        this.myChangesetsTable.setSelectionMode(1);

        for(int i = 0; i < Column.values().length; ++i) {
            this.myChangesetsTable.getColumnModel().getColumn(i).setPreferredWidth(Column.values()[i].getWidth());
        }

        this.mySelectRevisionForm = new SelectRevisionForm();
        this.myChangesetsPanel.add(this.mySelectRevisionForm.getPanel(), MergeBranchForm.ChangesType.ALL.toString());
        this.myChangesetsPanel.add(ScrollPaneFactory.createScrollPane(this.myChangesetsTable), MergeBranchForm.ChangesType.SELECTED.toString());
        this.mySourceField.setText(initialSourcePath);
        this.mySourceIsDirectory = initialSourcePathIsDirectory;
        this.mySourceField.getButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServerBrowserDialog d = new ServerBrowserDialog(TFSBundle.message("choose.source.item.dialog.title", new Object[0]), project, workspace.getServer(), MergeBranchForm.this.mySourceField.getText(), false, false);
                if (d.showAndGet()) {
                    SelectedItem selectedItem = d.getSelectedItem();
                    MergeBranchForm.this.mySourceField.setText(selectedItem != null ? selectedItem.path : null);
                    MergeBranchForm.this.mySourceIsDirectory = selectedItem == null || selectedItem.isDirectory;
                }

                MergeBranchForm.this.updateOnSourceChange();
            }
        });
        this.mySourceFieldFocusListener = new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                MergeBranchForm.this.mySourceIsDirectory = true;
                ApplicationManager.getApplication().invokeLater(() -> {
                    MergeBranchForm.this.updateOnSourceChange();
                }, ModalityState.current());
            }
        };
        this.mySourceField.getTextField().addFocusListener(this.mySourceFieldFocusListener);
        this.myTargetCombo.setModel(new DefaultComboBoxModel());
        this.myTargetCombo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    Item item = (Item)value;
                    this.setText(item.getItem());
                }

                return c;
            }
        });
        this.myTargetCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (MergeBranchForm.this.myChangesTypeCombo.getSelectedItem() == MergeBranchForm.ChangesType.SELECTED) {
                    MergeBranchForm.this.updateChangesetsTable();
                }

            }
        });
        this.myChangesTypeCombo.setModel(new DefaultComboBoxModel(MergeBranchForm.ChangesType.values()));
        this.myChangesTypeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (MergeBranchForm.this.myChangesTypeCombo.getSelectedItem() == MergeBranchForm.ChangesType.SELECTED) {
                    MergeBranchForm.this.updateChangesetsTable();
                }

                ((CardLayout)MergeBranchForm.this.myChangesetsPanel.getLayout()).show(MergeBranchForm.this.myChangesetsPanel, MergeBranchForm.this.myChangesTypeCombo.getSelectedItem().toString());
                MergeBranchForm.this.fireStateChanged();
            }
        });
        this.myChangesetsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                MergeBranchForm.this.fireStateChanged();
            }
        });
        this.myChangesTypeCombo.setSelectedIndex(0);
        this.mySelectRevisionForm.init(project, workspace, initialSourcePath, initialSourcePathIsDirectory);
    }

    public JComponent getContentPanel() {
        return this.myContentPanel;
    }

    private void updateChangesetsTable() {
        List<Changeset> changesets = new ArrayList();
        if (this.myTargetCombo.getSelectedIndex() != -1) {
            try {
                Collection<MergeCandidate> mergeCandidates = this.myWorkspace.getServer().getVCS().queryMergeCandidates(this.myWorkspace.getName(), this.myWorkspace.getOwnerName(), this.mySourceField.getText(), this.getTargetPath(), this.myProject, TFSBundle.message("loading.branches", new Object[0]));
                Iterator var3 = mergeCandidates.iterator();

                while(var3.hasNext()) {
                    MergeCandidate candidate = (MergeCandidate)var3.next();
                    changesets.add(candidate.getChangeset());
                }
            } catch (TfsException var5) {
                Messages.showErrorDialog(this.myProject, var5.getMessage(), this.myDialogTitle);
            }
        }

        this.myChangesetsTableModel.setChangesets(changesets);
    }

    public String getSourcePath() {
        return this.mySourceField.getText();
    }

    public String getTargetPath() {
        Item targetBranch = (Item)this.myTargetCombo.getSelectedItem();
        return targetBranch.getItem();
    }

    @Nullable
    public VersionSpecBase getFromVersion() {
        MergeBranchForm.ChangesType changesType = (MergeBranchForm.ChangesType)this.myChangesTypeCombo.getSelectedItem();
        if (changesType == MergeBranchForm.ChangesType.SELECTED) {
            Changeset fromChangeset = (Changeset)this.myChangesetsTableModel.getChangesets().get(this.myChangesetsTable.getSelectionModel().getMinSelectionIndex());
            return new ChangesetVersionSpec(fromChangeset.getCset());
        } else {
            return null;
        }
    }

    @Nullable
    public VersionSpecBase getToVersion() {
        MergeBranchForm.ChangesType changesType = (MergeBranchForm.ChangesType)this.myChangesTypeCombo.getSelectedItem();
        if (changesType == MergeBranchForm.ChangesType.SELECTED) {
            Changeset toChangeset = (Changeset)this.myChangesetsTableModel.getChangesets().get(this.myChangesetsTable.getSelectionModel().getMaxSelectionIndex());
            return new ChangesetVersionSpec(toChangeset.getCset());
        } else {
            return this.mySelectRevisionForm.getVersionSpec();
        }
    }

    public void addListener(MergeBranchForm.Listener listener) {
        this.myEventDispatcher.addListener(listener);
    }

    public void removeListener(MergeBranchForm.Listener listener) {
        this.myEventDispatcher.removeListener(listener);
    }

    private void fireStateChanged() {
        ((MergeBranchForm.Listener)this.myEventDispatcher.getMulticaster()).stateChanged(this.canFinish());
    }

    private boolean canFinish() {
        MergeBranchForm.ChangesType changesType = (MergeBranchForm.ChangesType)this.myChangesTypeCombo.getSelectedItem();
        if (changesType == MergeBranchForm.ChangesType.SELECTED && this.myChangesetsTable.getSelectedRowCount() == 0) {
            return false;
        } else {
            return this.myTargetCombo.getSelectedIndex() != -1;
        }
    }

    private void updateOnSourceChange() {
        ArrayList targetBranches = new ArrayList();

        try {
            Collection<BranchRelative> allBranches = this.myWorkspace.getServer().getVCS().queryBranches(this.mySourceField.getText(), LatestVersionSpec.INSTANCE, this.myProject, TFSBundle.message("loading.branches", new Object[0]));
            BranchRelative subject = null;
            Iterator var4 = allBranches.iterator();

            BranchRelative branch;
            while(var4.hasNext()) {
                branch = (BranchRelative)var4.next();
                if (branch.getReqstd()) {
                    subject = branch;
                    break;
                }
            }

            var4 = allBranches.iterator();

            label43:
            while(true) {
                do {
                    if (!var4.hasNext()) {
                        break label43;
                    }

                    branch = (BranchRelative)var4.next();
                } while(branch.getRelfromid() != subject.getReltoid() && branch.getReltoid() != subject.getRelfromid());

                if (branch.getBranchToItem().getDid() == -2147483648) {
                    targetBranches.add(branch.getBranchToItem());
                }
            }
        } catch (UserCancelledException var6) {
            return;
        } catch (TfsException var7) {
            Messages.showErrorDialog(this.myProject, var7.getMessage(), this.myDialogTitle);
        }

        ((DefaultComboBoxModel)this.myTargetCombo.getModel()).removeAllElements();
        Iterator var8 = targetBranches.iterator();

        while(var8.hasNext()) {
            Item targetBranch = (Item)var8.next();
            ((DefaultComboBoxModel)this.myTargetCombo.getModel()).addElement(targetBranch);
        }

        this.mySelectRevisionForm.init(this.myProject, this.myWorkspace, this.mySourceField.getText(), this.mySourceIsDirectory);
        this.fireStateChanged();
    }

    public void close() {
        this.mySourceField.getTextField().removeFocusListener(this.mySourceFieldFocusListener);
    }

    public JComponent getPreferredFocusedComponent() {
        return this.mySourceField.getChildComponent();
    }

    private static enum ChangesType {
        ALL {
            public String toString() {
                return "All changes up to a specific version";
            }
        },
        SELECTED {
            public String toString() {
                return "Selected changesets";
            }
        };

        private ChangesType() {
        }
    }

    public interface Listener extends EventListener {
        void stateChanged(boolean var1);
    }
}
