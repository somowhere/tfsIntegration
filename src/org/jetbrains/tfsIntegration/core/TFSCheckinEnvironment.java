//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.ProjectLevelVcsManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vcs.checkin.CheckinChangeListSpecificComponent;
import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
import com.intellij.openapi.vcs.ui.RefreshableOnComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.MultiLineTooltipUI;
import com.intellij.ui.components.labels.BoldLabel;
import com.intellij.util.ui.UIUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinResult;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinWorkItemAction;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Failure;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingChange;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolTip;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters.CheckinNote;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters.Severity;
import org.jetbrains.tfsIntegration.core.TFSVcs.CheckinData;
import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
import org.jetbrains.tfsIntegration.core.tfs.WorkItemsCheckinParameters;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper.VoidProcessDelegate;
import org.jetbrains.tfsIntegration.core.tfs.operations.ScheduleForAddition;
import org.jetbrains.tfsIntegration.core.tfs.operations.ScheduleForDeletion;
import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.CheckinParametersDialog;

public class TFSCheckinEnvironment implements CheckinEnvironment {
    @NotNull
    private final TFSVcs myVcs;

    public TFSCheckinEnvironment(@NotNull TFSVcs vcs) {

        super();
        this.myVcs = vcs;
    }

    @NotNull
    public RefreshableOnComponent createCommitOptions(@NotNull final CheckinProjectPanel commitPanel, @NotNull CommitContext commitContext) {

        JComponent panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 0));
        this.myVcs.getCheckinData().messageLabel = new BoldLabel() {
            public JToolTip createToolTip() {
                JToolTip toolTip = new JToolTip() {
                    {
                        this.setUI(new MultiLineTooltipUI());
                    }
                };
                toolTip.setComponent(this);
                return toolTip;
            }
        };
        panel.add(this.myVcs.getCheckinData().messageLabel, "West");
        JButton configureButton = new JButton("Configure...");
        panel.add(configureButton, "East");
        configureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                CheckinParameters copy = TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters.createCopy();
                CheckinParametersDialog d = new CheckinParametersDialog(commitPanel.getProject(), copy);
                if (d.showAndGet()) {
                    TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters = copy;
                    TFSCheckinEnvironment.updateMessage(TFSCheckinEnvironment.this.myVcs.getCheckinData());
                }

            }
        });
        TFSCheckinEnvironment.TFSAdditionalOptionsPanel var10000 = new TFSCheckinEnvironment.TFSAdditionalOptionsPanel(panel, commitPanel, configureButton);

        return var10000;
    }

    public static void updateMessage(CheckinData checkinData) {
        if (checkinData.parameters != null) {
            Pair<String, Severity> message = checkinData.parameters.getValidationMessage(Severity.BOTH);
            if (message == null) {
                checkinData.messageLabel.setText("<html>Ready to commit</html>");
                checkinData.messageLabel.setIcon((Icon)null);
                checkinData.messageLabel.setToolTipText((String)null);
            } else {
                checkinData.messageLabel.setToolTipText((String)message.first);
                if (message.second == Severity.ERROR) {
                    checkinData.messageLabel.setText("Errors found");
                    checkinData.messageLabel.setIcon(UIUtil.getBalloonErrorIcon());
                } else {
                    checkinData.messageLabel.setText("Warnings found");
                    checkinData.messageLabel.setIcon(UIUtil.getBalloonWarningIcon());
                }
            }

        }
    }

    @Nullable
    @NonNls
    public String getHelpId() {
        return null;
    }

    public String getCheckinOperationName() {
        return "Checkin";
    }

    @NotNull
    public List<VcsException> commit(@NotNull List<Change> changes, @NotNull final String commitMessage, @NotNull CommitContext commitContext, @NotNull Set<String> feedback) {

        this.myVcs.getCheckinData().messageLabel = null;
        final ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
        List<FilePath> files = new ArrayList();
        Iterator var7 = changes.iterator();

        while(var7.hasNext()) {
            Change change = (Change)var7.next();
            FilePath path = null;
            ContentRevision beforeRevision = change.getBeforeRevision();
            ContentRevision afterRevision = change.getAfterRevision();
            if (afterRevision != null) {
                path = afterRevision.getFile();
            } else if (beforeRevision != null) {
                path = beforeRevision.getFile();
            }

            if (path != null) {
                files.add(path);
            }
        }

        final ArrayList errors = new ArrayList();

        try {
            WorkstationHelper.processByWorkspaces(files, false, this.myVcs.getProject(), new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    try {
                        TFSProgressUtil.setProgressText(progressIndicator, TFSBundle.message("loading.pending.changes", new Object[0]));
                        Collection<PendingChange> pendingChanges = workspace.getServer().getVCS().queryPendingSetsByLocalPaths(workspace.getName(), workspace.getOwnerName(), paths, RecursionType.None, TFSCheckinEnvironment.this.myVcs.getProject(), TFSBundle.message("loading.pending.changes", new Object[0]));
                        if (!pendingChanges.isEmpty()) {
                            Collection<String> checkIn = new ArrayList();
                            TFSProgressUtil.setProgressText(progressIndicator, TFSBundle.message("uploading.files", new Object[0]));

                            PendingChange pendingChange;
                            for(Iterator var5 = pendingChanges.iterator(); var5.hasNext(); checkIn.add(pendingChange.getItem())) {
                                pendingChange = (PendingChange)var5.next();
                                if (pendingChange.getType() == ItemType.File) {
                                    ChangeTypeMask changeType = new ChangeTypeMask(pendingChange.getChg());
                                    if (changeType.contains(ChangeType_type0.Edit) || changeType.contains(ChangeType_type0.Add)) {
                                        TFSProgressUtil.setProgressText2(progressIndicator, VersionControlPath.localPathFromTfsRepresentation(pendingChange.getLocal()));
                                        workspace.getServer().getVCS().uploadItem(workspace, pendingChange, TFSCheckinEnvironment.this.myVcs.getProject(), (String)null);
                                    }
                                }
                            }

                            TFSProgressUtil.setProgressText2(progressIndicator, "");
                            WorkItemsCheckinParameters state = TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters.getWorkItems(workspace.getServer());
                            Map<WorkItem, CheckinWorkItemAction> workItemActions = state != null ? state.getWorkItemsActions() : Collections.emptyMap();
                            List<Pair<String, String>> checkinNotes = new ArrayList(TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters.getCheckinNotes(workspace.getServer()).size());
                            Iterator var8 = TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters.getCheckinNotes(workspace.getServer()).iterator();

                            while(var8.hasNext()) {
                                CheckinNote checkinNote = (CheckinNote)var8.next();
                                checkinNotes.add(Pair.create(checkinNote.name, StringUtil.notNullize(checkinNote.value)));
                            }

                            TFSProgressUtil.setProgressText(progressIndicator, TFSBundle.message("checking.in", new Object[0]));
                            ResultWithFailures<CheckinResult> result = workspace.getServer().getVCS().checkIn(workspace.getName(), workspace.getOwnerName(), checkIn, commitMessage, workItemActions, checkinNotes, TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters.getPolicyOverride(workspace.getServer()), TFSCheckinEnvironment.this.myVcs.getProject(), (String)null);
                            errors.addAll(TfsUtil.getVcsExceptions(result.getFailures()));
                            Collection<String> commitFailed = new ArrayList(result.getFailures().size());
                            Iterator var10 = result.getFailures().iterator();

                            while(var10.hasNext()) {
                                Failure failure = (Failure)var10.next();
                                TFSVcs.assertTrue(failure.getItem() != null);
                                commitFailed.add(failure.getItem());
                            }

                            Collection<FilePath> invalidateRoots = new ArrayList(pendingChanges.size());
                            Collection<FilePath> invalidateFiles = new ArrayList();
                            Collection<VirtualFile> makeReadOnly = new ArrayList();
                            Iterator var13 = pendingChanges.iterator();

                            while(true) {
                                VirtualFile vcsRoot;
                                FilePath path;
                                do {
                                    ChangeTypeMask changeTypex;
                                    do {
                                        PendingChange pendingChangex;
                                        do {
                                            if (!var13.hasNext()) {
                                                TfsFileUtil.setReadOnly(makeReadOnly, true);
                                                TFSProgressUtil.setProgressText(progressIndicator, TFSBundle.message("updating.work.items", new Object[0]));
                                                if (commitFailed.isEmpty()) {
                                                    CheckinResult checkinResult = (CheckinResult)result.getResult().iterator().next();
                                                    workspace.getServer().getVCS().updateWorkItemsAfterCheckin(workspace.getOwnerName(), workItemActions, checkinResult.getCset(), TFSCheckinEnvironment.this.myVcs.getProject(), (String)null);
                                                }

                                                TfsFileUtil.markDirty(TFSCheckinEnvironment.this.myVcs.getProject(), invalidateRoots, invalidateFiles);
                                                return;
                                            }

                                            pendingChangex = (PendingChange)var13.next();
                                            TFSVcs.assertTrue(pendingChangex.getItem() != null);
                                        } while(commitFailed.contains(pendingChangex.getItem()));

                                        changeTypex = new ChangeTypeMask(pendingChangex.getChg());
                                        if (pendingChangex.getType() == ItemType.File && (changeTypex.contains(ChangeType_type0.Edit) || changeTypex.contains(ChangeType_type0.Add) || changeTypex.contains(ChangeType_type0.Rename))) {
                                            VirtualFile file = VersionControlPath.getVirtualFile(pendingChangex.getLocal());
                                            if (file != null && file.isValid()) {
                                                makeReadOnly.add(file);
                                            }
                                        }

                                        path = VersionControlPath.getFilePath(pendingChangex.getLocal(), pendingChangex.getType() == ItemType.Folder);
                                        invalidateRoots.add(path);
                                    } while(!changeTypex.contains(ChangeType_type0.Add) && !changeTypex.contains(ChangeType_type0.Rename));

                                    vcsRoot = ProjectLevelVcsManager.getInstance(TFSCheckinEnvironment.this.myVcs.getProject()).getVcsRootFor(path);
                                } while(vcsRoot == null);

                                FilePath vcsRootPath = TfsFileUtil.getFilePath(vcsRoot);

                                for(FilePath parent = path.getParentPath(); parent != null && parent.isUnder(vcsRootPath, false); parent = parent.getParentPath()) {
                                    invalidateFiles.add(parent);
                                }
                            }
                        }
                    } catch (IOException var20) {
                        errors.add(new VcsException(var20));
                    }
                }
            });
        } catch (TfsException var12) {
            errors.add(new VcsException(var12));
        }

        this.myVcs.getCheckinData().parameters = null;
        this.myVcs.fireRevisionChanged();
        return errors;
    }

    @Nullable
    public List<VcsException> scheduleMissingFileForDeletion(@NotNull List<FilePath> files) {

        final ArrayList errors = new ArrayList();

        try {
            WorkstationHelper.processByWorkspaces(files, false, this.myVcs.getProject(), new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) {
                    Collection<VcsException> schedulingErrors = ScheduleForDeletion.execute(TFSCheckinEnvironment.this.myVcs.getProject(), workspace, paths);
                    errors.addAll(schedulingErrors);
                }
            });
        } catch (TfsException var4) {
            errors.add(new VcsException(var4));
        }

        return errors;
    }

    @Nullable
    public List<VcsException> scheduleUnversionedFilesForAddition(@NotNull List<VirtualFile> files) {

        final ArrayList exceptions = new ArrayList();

        try {
            List<FilePath> orphans = WorkstationHelper.processByWorkspaces(TfsFileUtil.getFilePaths(files), false, this.myVcs.getProject(), new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) {
                    Collection<VcsException> schedulingErrors = ScheduleForAddition.execute(TFSCheckinEnvironment.this.myVcs.getProject(), workspace, paths);
                    exceptions.addAll(schedulingErrors);
                }
            });
            if (!orphans.isEmpty()) {
                StringBuilder s = new StringBuilder();

                FilePath orpan;
                for(Iterator var5 = orphans.iterator(); var5.hasNext(); s.append(orpan.getPresentableUrl())) {
                    orpan = (FilePath)var5.next();
                    if (s.length() > 0) {
                        s.append("\n");
                    }
                }

                exceptions.add(new VcsException("Team Foundation Server mapping not found for: " + s.toString()));
            }
        } catch (TfsException var7) {
            exceptions.add(new VcsException(var7));
        }

        return exceptions;
    }

    public boolean isRefreshAfterCommitNeeded() {
        return true;
    }

    private class TFSAdditionalOptionsPanel implements CheckinChangeListSpecificComponent {
        private final JComponent myPanel;
        private final CheckinProjectPanel myCheckinProjectPanel;
        private final JButton myConfigureButton;
        private LocalChangeList myCurrentList;

        TFSAdditionalOptionsPanel(JComponent panel, CheckinProjectPanel checkinProjectPanel, JButton configureButton) {
            this.myPanel = panel;
            this.myCheckinProjectPanel = checkinProjectPanel;
            this.myConfigureButton = configureButton;
        }

        public JComponent getComponent() {
            return this.myPanel;
        }

        public void refresh() {
        }

        public void saveState() {
        }

        public void restoreState() {
        }

        public void onChangeListSelected(LocalChangeList list) {
            if (this.myCurrentList != list) {
                this.myCurrentList = list;
                if (!this.myCheckinProjectPanel.hasDiffs()) {
                    this.myPanel.setVisible(false);
                } else {
                    this.myPanel.setVisible(true);

                    try {
                        TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters = new CheckinParameters(this.myCheckinProjectPanel, true);
                        this.myConfigureButton.setEnabled(true);
                        TFSCheckinEnvironment.updateMessage(TFSCheckinEnvironment.this.myVcs.getCheckinData());
                    } catch (OperationFailedException var3) {
                        TFSCheckinEnvironment.this.myVcs.getCheckinData().parameters = null;
                        this.myConfigureButton.setEnabled(false);
                        TFSCheckinEnvironment.this.myVcs.getCheckinData().messageLabel.setIcon(UIUtil.getBalloonErrorIcon());
                        TFSCheckinEnvironment.this.myVcs.getCheckinData().messageLabel.setText("Validation failed");
                        TFSCheckinEnvironment.this.myVcs.getCheckinData().messageLabel.setToolTipText(var3.getMessage());
                    }

                }
            }
        }
    }
}
