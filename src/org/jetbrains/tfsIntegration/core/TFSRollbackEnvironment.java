//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.Change.Type;
import com.intellij.openapi.vcs.rollback.DefaultRollbackEnvironment;
import com.intellij.openapi.vcs.rollback.RollbackProgressListener;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
import org.jetbrains.tfsIntegration.core.tfs.ServerStatus;
import org.jetbrains.tfsIntegration.core.tfs.StatusProvider;
import org.jetbrains.tfsIntegration.core.tfs.StatusVisitor;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer.GetRequestParams;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper.VoidProcessDelegate;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress;
import org.jetbrains.tfsIntegration.core.tfs.operations.UndoPendingChanges;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations.DownloadMode;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress.RollbackProgressWrapper;
import org.jetbrains.tfsIntegration.core.tfs.operations.UndoPendingChanges.UndoPendingChangesResult;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.WorkspaceVersionSpec;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class TFSRollbackEnvironment extends DefaultRollbackEnvironment {
    @NotNull
    private final Project myProject;

    public TFSRollbackEnvironment(Project project) {
        this.myProject = project;
    }

    public void rollbackChanges(List<Change> changes, List<VcsException> vcsExceptions, @NotNull RollbackProgressListener listener) {
        List<FilePath> localPaths = new ArrayList();
        listener.determinate();
        Iterator var5 = changes.iterator();

        while(var5.hasNext()) {
            Change change = (Change)var5.next();
            ContentRevision revision = change.getType() == Type.DELETED ? change.getBeforeRevision() : change.getAfterRevision();
            localPaths.add(revision.getFile());
        }

        this.undoPendingChanges(localPaths, vcsExceptions, listener, false);
    }

    public void rollbackMissingFileDeletion(List<FilePath> files, final List<VcsException> errors, final RollbackProgressListener listener) {
        try {
            WorkstationHelper.processByWorkspaces(files, false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    final List<GetRequestParams> download = new ArrayList();
                    final Collection<String> undo = new ArrayList();
                    StatusProvider.visitByStatus(workspace, paths, false, (ProgressIndicator)null, new StatusVisitor() {
                        public void unversioned(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {


                            TFSVcs.error("Server returned status Unversioned when rolling back missing file deletion: " + localPath.getPresentableUrl());
                        }

                        public void checkedOutForEdit(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {


                            undo.add(serverStatus.targetItem);
                        }

                        public void scheduledForAddition(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {


                            undo.add(serverStatus.targetItem);
                        }

                        public void scheduledForDeletion(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {


                            TFSVcs.error("Server returned status ScheduledForDeletion when rolling back missing file deletion: " + localPath.getPresentableUrl());
                        }

                        public void outOfDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                            this.addForDownload(serverStatus);
                        }

                        public void deleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {


                            TFSVcs.error("Server returned status Deleted when rolling back missing file deletion: " + localPath.getPath());
                        }

                        public void upToDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {


                            this.addForDownload(serverStatus);
                        }

                        public void renamed(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {


                            undo.add(serverStatus.targetItem);
                        }

                        public void renamedCheckedOut(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {


                            undo.add(serverStatus.targetItem);
                        }

                        public void undeleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {


                            this.addForDownload(serverStatus);
                        }

                        private void addForDownload(@NotNull ServerStatus serverStatus) {

                            download.add(new GetRequestParams(serverStatus.targetItem, RecursionType.None, new ChangesetVersionSpec(serverStatus.localVer)));
                        }
                    }, TFSRollbackEnvironment.this.myProject);
                    List<GetOperation> operations = workspace.getServer().getVCS().get(workspace.getName(), workspace.getOwnerName(), download, TFSRollbackEnvironment.this.myProject, TFSBundle.message("preparing.for.download", new Object[0]));
                    Collection<VcsException> downloadErrors = ApplyGetOperations.execute(TFSRollbackEnvironment.this.myProject, workspace, operations, ApplyProgress.EMPTY, (UpdatedFiles)null, DownloadMode.FORCE);
                    errors.addAll(downloadErrors);
                    UndoPendingChangesResult undoResult = UndoPendingChanges.execute(TFSRollbackEnvironment.this.myProject, workspace, undo, false, new RollbackProgressWrapper(listener), false);
                    errors.addAll(undoResult.errors);
                }
            });
        } catch (TfsException var5) {
            errors.add(new VcsException(var5.getMessage(), var5));
        }

    }

    public void rollbackModifiedWithoutCheckout(List<VirtualFile> files, final List<VcsException> errors, final RollbackProgressListener listener) {
        try {
            WorkstationHelper.processByWorkspaces(TfsFileUtil.getFilePaths(files), false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    List<GetRequestParams> requests = new ArrayList(paths.size());
                    WorkspaceVersionSpec versionSpec = new WorkspaceVersionSpec(workspace.getName(), workspace.getOwnerName());
                    Iterator var5 = paths.iterator();

                    while(var5.hasNext()) {
                        ItemPath e = (ItemPath)var5.next();
                        requests.add(new GetRequestParams(e.getServerPath(), RecursionType.None, versionSpec));
                    }

                    List<GetOperation> operations = workspace.getServer().getVCS().get(workspace.getName(), workspace.getOwnerName(), requests, TFSRollbackEnvironment.this.myProject, TFSBundle.message("preparing.for.download", new Object[0]));
                    Collection<VcsException> applyingErrors = ApplyGetOperations.execute(TFSRollbackEnvironment.this.myProject, workspace, operations, new RollbackProgressWrapper(listener), (UpdatedFiles)null, DownloadMode.FORCE);
                    errors.addAll(applyingErrors);
                }
            });
        } catch (TfsException var5) {
            errors.add(new VcsException("Cannot undo pending changes", var5));
        }

    }

    private void undoPendingChanges(List<FilePath> localPaths, final List<VcsException> errors, @NotNull final RollbackProgressListener listener, final boolean tolerateNoChangesFailure) {


        try {
            WorkstationHelper.processByWorkspaces(localPaths, false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    Collection<String> serverPaths = new ArrayList(paths.size());
                    Iterator var4 = paths.iterator();

                    while(var4.hasNext()) {
                        ItemPath itemPath = (ItemPath)var4.next();
                        serverPaths.add(itemPath.getServerPath());
                    }

                    UndoPendingChangesResult undoResult = UndoPendingChanges.execute(TFSRollbackEnvironment.this.myProject, workspace, serverPaths, false, new RollbackProgressWrapper(listener), tolerateNoChangesFailure);
                    errors.addAll(undoResult.errors);
                    List<VirtualFile> refresh = new ArrayList(paths.size());
                    Iterator var6 = paths.iterator();

                    while(var6.hasNext()) {
                        ItemPath path = (ItemPath)var6.next();
                        listener.accept(path.getLocalPath());
                        ItemPath undone = (ItemPath)undoResult.undonePaths.get(path);
                        FilePath subject = (undone != null ? undone : path).getLocalPath();
                        VirtualFile file = subject.getVirtualFileParent();
                        if (file != null && file.exists()) {
                            refresh.add(file);
                        }
                    }

                    TfsFileUtil.refreshAndMarkDirty(TFSRollbackEnvironment.this.myProject, refresh, true);
                }
            });
        } catch (TfsException var6) {
            errors.add(new VcsException("Cannot undo pending changes", var6));
        }

    }
}
