//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.actions;

import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.MergeResponse;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.conflicts.ConflictsEnvironment;
import org.jetbrains.tfsIntegration.core.tfs.conflicts.ResolveConflictHelper;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations.DownloadMode;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress.ProgressIndicatorWrapper;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.MergeBranchDialog;

public class MergeBranchAction extends SingleItemAction implements DumbAware {
    public MergeBranchAction() {
    }

    protected void execute(@NotNull Project project, @NotNull WorkspaceInfo workspace, @NotNull FilePath localPath, @NotNull ExtendedItem extendedItem) throws TfsException {

        String title = "Merge Branch Changes";
        MergeBranchDialog d = new MergeBranchDialog(project, workspace, extendedItem.getSitem(), extendedItem.getType() == ItemType.Folder, "Merge Branch Changes");
        if (d.showAndGet()) {
            if (!workspace.hasLocalPathForServerPath(d.getTargetPath(), project)) {
                String message = MessageFormat.format("No mapping found for ''{0}'' in workspace ''{1}''.", d.getTargetPath(), workspace.getName());
                Messages.showErrorDialog(project, message, "Merge Branch Changes");
            } else {
                MergeResponse mergeResponse = workspace.getServer().getVCS().merge(workspace.getName(), workspace.getOwnerName(), d.getSourcePath(), d.getTargetPath(), d.getFromVersion(), d.getToVersion(), project, TFSBundle.message("merging", new Object[0]));
                List<VcsException> errors = new ArrayList();
                if (mergeResponse.getMergeResult().getGetOperation() != null) {
                    ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                        Collection<VcsException> applyErrors = ApplyGetOperations.execute(project, workspace, Arrays.asList(mergeResponse.getMergeResult().getGetOperation()), new ProgressIndicatorWrapper(ProgressManager.getInstance().getProgressIndicator()), (UpdatedFiles)null, DownloadMode.ALLOW);
                        errors.addAll(applyErrors);
                    }, "Merge Branch Changes", false, project);
                }

                Collection<Conflict> unresolvedConflicts = ResolveConflictHelper.getUnresolvedConflicts(mergeResponse.getConflicts().getConflict() != null ? Arrays.asList(mergeResponse.getConflicts().getConflict()) : Collections.emptyList());
                if (!unresolvedConflicts.isEmpty()) {
                    ResolveConflictHelper resolveConflictHelper = new ResolveConflictHelper(project, Collections.singletonMap(workspace, unresolvedConflicts), (UpdatedFiles)null);
                    ConflictsEnvironment.getConflictsHandler().resolveConflicts(resolveConflictHelper);
                }

                if (mergeResponse.getFailures().getFailure() != null) {
                    errors.addAll(TfsUtil.getVcsExceptions(Arrays.asList(mergeResponse.getFailures().getFailure())));
                }

                if (errors.isEmpty()) {
                    FilePath targetLocalPath = workspace.findLocalPathByServerPath(d.getTargetPath(), true, project);
                    VirtualFile[] var11 = ProjectRootManager.getInstance(project).getContentRoots();
                    int var12 = var11.length;

                    for(int var13 = 0; var13 < var12; ++var13) {
                        VirtualFile root = var11[var13];
                        if (targetLocalPath.isUnder(TfsFileUtil.getFilePath(root), false)) {
                            TfsFileUtil.refreshAndInvalidate(project, new FilePath[]{targetLocalPath}, true);
                            break;
                        }
                    }

                    String message;
                    if (unresolvedConflicts.isEmpty() && mergeResponse.getMergeResult().getGetOperation() == null) {
                        message = MessageFormat.format("No changes to merge from ''{0}'' to ''{1}''.", d.getSourcePath(), d.getTargetPath());
                        TfsUtil.showBalloon(project, MessageType.INFO, message);
                    } else {
                        message = MessageFormat.format("Changes merged successfully from ''{0}'' to ''{1}''.", d.getSourcePath(), d.getTargetPath());
                        TfsUtil.showBalloon(project, MessageType.INFO, message);
                    }
                } else {
                    AbstractVcsHelper.getInstance(project).showErrors(errors, "TFS");
                }

            }
        }
    }
}
