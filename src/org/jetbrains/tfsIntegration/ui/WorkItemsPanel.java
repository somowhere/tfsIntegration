//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.TreeTableSpeedSearch;
import com.intellij.ui.dualView.TreeTableView;
import com.intellij.ui.treeStructure.NullNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeStructure.Impl;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil;
import org.jetbrains.tfsIntegration.core.tfs.WorkItemsCheckinParameters;
import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil.Process;
import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil.ResultWithError;

public class WorkItemsPanel implements Disposable {
    private JPanel myMainPanel;
    private TreeTableView myWorkItemsTable;
    private SimpleTree myWorkItemQueriesTree;
    private WorkItemQueriesTreeBuilder myTreeBuilder;
    private WorkItemsTableModel myWorkItemsTableModel;
    private final CheckinParametersForm myForm;

    public WorkItemsPanel(CheckinParametersForm form) {
        this.myForm = form;
        this.myWorkItemsTable.setSelectionMode(0);
        this.myWorkItemsTable.getTree().setRootVisible(false);
        this.myWorkItemsTable.setShowGrid(true);
        this.myWorkItemsTable.setGridColor(UIUtil.getTableGridColor());
        this.myWorkItemsTable.setMaxItemsForSizeCalculation(1);
        new TreeTableSpeedSearch(this.myWorkItemsTable);
        this.setupWorkItemQueries();
    }

    private void setupWorkItemQueries() {
        this.myTreeBuilder = new WorkItemQueriesTreeBuilder(this.myWorkItemQueriesTree, new Impl(new NullNode()));
        Disposer.register(this, this.myTreeBuilder);
    }

    public void queryWorkItems(@NotNull Process<WorkItemsQueryResult> query) {

        ResultWithError<WorkItemsQueryResult> result = TfsExecutionUtil.executeInBackground("Performing Query", this.getProject(), query);
        String title = "Query Work Items";
        if (!result.cancelled && !result.showDialogIfError("Query Work Items")) {
            this.getState().getWorkItems(this.myForm.getSelectedServer()).update((WorkItemsQueryResult)ObjectUtils.assertNotNull(result.result));
            this.updateWorkItemsTable();
        }
    }

    private void updateWorkItemsTable() {
        this.myWorkItemsTableModel.setContent(this.getState().getWorkItems(this.myForm.getSelectedServer()));
        TreeUtil.expandAll(this.myWorkItemsTable.getTree());
    }

    public void update() {
        this.updateWorkItemsTable();
        this.updateWorkItemQueries();
    }

    private void updateWorkItemQueries() {
        this.clearOldTreeStructure();
        this.setNewTreeStructure();
    }

    private void clearOldTreeStructure() {
        AbstractTreeStructure oldTreeStructure = this.myTreeBuilder.getTreeStructure();
        if (oldTreeStructure instanceof Disposable) {
            Disposer.dispose((Disposable)oldTreeStructure);
        }

        this.myTreeBuilder.cleanUp();
    }

    private void setNewTreeStructure() {
        WorkItemQueriesTreeStructure newTreeStructure = new WorkItemQueriesTreeStructure(this);
        this.myTreeBuilder.setTreeStructure(newTreeStructure);
        Disposer.register(this.myTreeBuilder, newTreeStructure);
        this.myTreeBuilder.queueUpdate().doWhenDone(() -> {
            this.myTreeBuilder.expand(newTreeStructure.getPredefinedQueriesGroupNode(), (Runnable)null);
        });
    }

    @NotNull
    public CheckinParameters getState() {
        CheckinParameters var10000 = this.myForm.getState();

        return var10000;
    }

    @NotNull
    public ServerInfo getServer() {
        ServerInfo var10000 = this.myForm.getSelectedServer();
        return var10000;
    }

    @NotNull
    public Project getProject() {
        Project var10000 = this.myForm.getProject();

        return var10000;
    }

    public void dispose() {
    }

    private void createUIComponents() {
        this.myWorkItemsTableModel = new WorkItemsTableModel(new WorkItemsCheckinParameters());
        this.myWorkItemsTable = new TreeTableView(this.myWorkItemsTableModel) {
            protected void onTableChanged(@NotNull TableModelEvent e) {

                super.onTableChanged(e);
                this.getTree().setRowHeight(this.calculateRowHeight());
            }
        };
    }
}
