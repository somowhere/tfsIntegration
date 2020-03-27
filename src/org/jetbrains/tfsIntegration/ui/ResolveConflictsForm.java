//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.util.EventDispatcher;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import org.jetbrains.tfsIntegration.core.tfs.conflicts.ResolveConflictHelper;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.ConflictsTableModel.Column;

public class ResolveConflictsForm {
    private JTable myItemsTable;
    private JPanel myContentPanel;
    private JButton myAcceptYoursButton;
    private JButton myAcceptTheirsButton;
    private JButton myMergeButton;
    private final ConflictsTableModel myItemsTableModel;
    private final ResolveConflictHelper myResolveConflictHelper;
    private final EventDispatcher<ResolveConflictsForm.Listener> myEventDispatcher;
    private static final Comparator<? super Conflict> CONFLICTS_COMPARATOR = (o1, o2) -> {
        String path1 = Column.Name.getValue(o1);
        String path2 = Column.Name.getValue(o2);
        return path1.compareTo(path2);
    };

    public ResolveConflictsForm(ResolveConflictHelper resolveConflictHelper) {
        this.myEventDispatcher = EventDispatcher.create(ResolveConflictsForm.Listener.class);
        this.myResolveConflictHelper = resolveConflictHelper;
        this.myItemsTableModel = new ConflictsTableModel();
        this.myItemsTable.setModel(this.myItemsTableModel);
        this.myItemsTable.setSelectionMode(2);
        this.addListeners();
        this.updateConflictsTable();
    }

    private void updateConflictsTable() {
        List<Conflict> conflicts = new ArrayList(this.myResolveConflictHelper.getConflicts());
        Collections.sort(conflicts, CONFLICTS_COMPARATOR);
        this.myItemsTableModel.setConflicts(conflicts);
    }

    private void addListeners() {
        this.myItemsTableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                if (ResolveConflictsForm.this.myItemsTableModel.getRowCount() == 0) {
                    ((ResolveConflictsForm.Listener)ResolveConflictsForm.this.myEventDispatcher.getMulticaster()).close();
                }

            }
        });
        this.myItemsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent se) {
                int[] selectedIndices = ResolveConflictsForm.this.myItemsTable.getSelectedRows();
                ResolveConflictsForm.this.enableButtons(selectedIndices);
            }
        });
        this.myAcceptYoursButton.addActionListener(new ResolveConflictsForm.MergeActionListener() {
            protected void execute(Conflict conflict) throws TfsException, VcsException {
                ResolveConflictsForm.this.myResolveConflictHelper.acceptYours(conflict);
            }
        });
        this.myAcceptTheirsButton.addActionListener(new ResolveConflictsForm.MergeActionListener() {
            protected void execute(Conflict conflict) throws TfsException, IOException, VcsException {
                ResolveConflictsForm.this.myResolveConflictHelper.acceptTheirs(conflict);
            }
        });
        this.myMergeButton.addActionListener(new ResolveConflictsForm.MergeActionListener() {
            protected void execute(Conflict conflict) throws TfsException, VcsException {
                ResolveConflictsForm.this.myResolveConflictHelper.acceptMerge(conflict);
            }
        });
    }

    public JComponent getPanel() {
        return this.myContentPanel;
    }

    public void addListener(ResolveConflictsForm.Listener listener) {
        this.myEventDispatcher.addListener(listener);
    }

    public void removeListener(ResolveConflictsForm.Listener listener) {
        this.myEventDispatcher.removeListener(listener);
    }

    private void enableButtons(int[] selectedIndices) {
        this.myAcceptYoursButton.setEnabled(selectedIndices.length > 0);
        this.myAcceptTheirsButton.setEnabled(selectedIndices.length > 0);
        boolean mergeEnabled = selectedIndices.length > 0;
        int[] var3 = selectedIndices;
        int var4 = selectedIndices.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int index = var3[var5];
            Conflict conflict = (Conflict)this.myItemsTableModel.getConflicts().get(index);
            if (!ResolveConflictHelper.canMerge(conflict)) {
                mergeEnabled = false;
                break;
            }
        }

        this.myMergeButton.setEnabled(mergeEnabled);
    }

    private abstract class MergeActionListener implements ActionListener {
        private MergeActionListener() {
        }

        public void actionPerformed(ActionEvent ae) {
            int[] selectedIndices = ResolveConflictsForm.this.myItemsTable.getSelectedRows();

            String message;
            try {
                int[] var3 = selectedIndices;
                int var11 = selectedIndices.length;

                for(int var5 = 0; var5 < var11; ++var5) {
                    int index = var3[var5];
                    Conflict conflict = (Conflict)ResolveConflictsForm.this.myItemsTableModel.getConflicts().get(index);
                    this.execute(conflict);
                }

                ResolveConflictsForm.this.updateConflictsTable();
            } catch (TfsException var8) {
                message = "Failed to resolve conlict.\n" + var8.getMessage();
                Messages.showErrorDialog(ResolveConflictsForm.this.myContentPanel, message, "Resolve Conflicts");
            } catch (IOException var9) {
                message = "Failed to resolve conlict.\n" + var9.getMessage();
                Messages.showErrorDialog(ResolveConflictsForm.this.myContentPanel, message, "Resolve Conflicts");
            } catch (VcsException var10) {
                message = "Failed to resolve conlict.\n" + var10.getMessage();
                Messages.showErrorDialog(ResolveConflictsForm.this.myContentPanel, message, "Resolve Conflicts");
            }

        }

        protected abstract void execute(Conflict var1) throws TfsException, IOException, VcsException;
    }

    public interface Listener extends EventListener {
        void close();
    }
}
