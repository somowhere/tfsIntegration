//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsVFSListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingChange;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
import org.jetbrains.tfsIntegration.core.tfs.ServerStatus;
import org.jetbrains.tfsIntegration.core.tfs.StatusProvider;
import org.jetbrains.tfsIntegration.core.tfs.StatusVisitor;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
import org.jetbrains.tfsIntegration.core.tfs.RootsCollection.ItemPathRootsCollection;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper.VoidProcessDelegate;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress;
import org.jetbrains.tfsIntegration.core.tfs.operations.ScheduleForAddition;
import org.jetbrains.tfsIntegration.core.tfs.operations.ScheduleForDeletion;
import org.jetbrains.tfsIntegration.core.tfs.operations.UndoPendingChanges;
import org.jetbrains.tfsIntegration.core.tfs.operations.UndoPendingChanges.UndoPendingChangesResult;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class TFSFileListener extends VcsVFSListener {
    public TFSFileListener(Project project, TFSVcs vcs) {
        super(project, vcs);
    }

    @NotNull
    protected String getAddTitle() {
        String var10000 = TFSBundle.message("add.items", new Object[0]);
        return var10000;
    }

    @NotNull
    protected String getSingleFileAddTitle() {
        String var10000 = TFSBundle.message("add.item", new Object[0]);

        return var10000;
    }

    @NotNull
    protected String getSingleFileAddPromptTemplate() {
        String var10000 = TFSBundle.message("add.item.prompt", new Object[0]);

        return var10000;
    }

    protected void executeAdd() {
        try {
            WorkstationHelper.processByWorkspaces(TfsFileUtil.getFilePaths(this.myAddedFiles), false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    StatusProvider.visitByStatus(workspace, paths, false, (ProgressIndicator)null, new StatusVisitor() {
                        public void unversioned(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void checkedOutForEdit(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                        }

                        public void scheduledForAddition(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                            TFSFileListener.this.myAddedFiles.remove(localPath.getVirtualFile());
                        }

                        public void scheduledForDeletion(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                        }

                        public void outOfDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void deleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                        }

                        public void upToDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatusm) throws TfsException {

                        }

                        public void renamed(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void renamedCheckedOut(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void undeleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }
                    }, TFSFileListener.this.myProject);
                }
            });
        } catch (TfsException var2) {
            AbstractVcsHelper.getInstance(this.myProject).showError(new VcsException(var2), "TFS");
        }

        if (!this.myAddedFiles.isEmpty()) {
            super.executeAdd();
        }

    }

    protected void executeDelete() {
        List<FilePath> deletedFiles = new ArrayList(this.myDeletedFiles);
        deletedFiles.addAll(this.myDeletedWithoutConfirmFiles);

        try {
            WorkstationHelper.processByWorkspaces(deletedFiles, false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    ItemPathRootsCollection roots = new ItemPathRootsCollection(paths);
                    Collection<PendingChange> pendingChanges = workspace.getServer().getVCS().queryPendingSetsByLocalPaths(workspace.getName(), workspace.getOwnerName(), roots, RecursionType.Full, TFSFileListener.this.myProject, TFSBundle.message("loading.changes", new Object[0]));
                    List<String> revertImmediately = new ArrayList();
                    List<ItemPath> pathsToProcess = new ArrayList(paths);
                    Iterator var7 = pendingChanges.iterator();

                    while(var7.hasNext()) {
                        PendingChange pendingChange = (PendingChange)var7.next();
                        ChangeTypeMask changeType = new ChangeTypeMask(pendingChange.getChg());
                        if (changeType.containsAny(new ChangeType_type0[]{ChangeType_type0.Add, ChangeType_type0.Undelete})) {
                            revertImmediately.add(pendingChange.getItem());
                            FilePath localPath = VersionControlPath.getFilePath(pendingChange.getLocal(), pendingChange.getType() == ItemType.Folder);
                            TFSFileListener.this.excludeFromFurtherProcessing(localPath);
                            ItemPath itemPath = new ItemPath(localPath, pendingChange.getItem());
                            pathsToProcess.remove(itemPath);
                        }
                    }

                    UndoPendingChangesResult undoResult = UndoPendingChanges.execute(TFSFileListener.this.myProject, workspace, revertImmediately, true, ApplyProgress.EMPTY, false);
                    if (!undoResult.errors.isEmpty()) {
                        AbstractVcsHelper.getInstance(TFSFileListener.this.myProject).showErrors(new ArrayList(undoResult.errors), "TFS");
                    }

                    StatusProvider.visitByStatus(workspace, pathsToProcess, false, (ProgressIndicator)null, new StatusVisitor() {
                        public void scheduledForAddition(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                            TFSVcs.error("Cannot revert an item scheduled for addition: " + localPath.getPresentableUrl());
                        }

                        public void unversioned(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                            TFSFileListener.this.excludeFromFurtherProcessing(localPath);
                        }

                        public void deleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {
                            TFSFileListener.this.excludeFromFurtherProcessing(localPath);
                        }

                        public void scheduledForDeletion(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                            TFSFileListener.this.excludeFromFurtherProcessing(localPath);
                        }

                        public void checkedOutForEdit(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                        }

                        public void outOfDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void upToDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void renamed(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void renamedCheckedOut(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                        }

                        public void undeleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {
                            TFSVcs.error("Cannot revert undeleted: " + localPath.getPresentableUrl());
                        }
                    }, TFSFileListener.this.myProject);
                }
            });
        } catch (TfsException var3) {
            AbstractVcsHelper.getInstance(this.myProject).showError(new VcsException(var3), "TFS");
        }

        if (!this.myDeletedFiles.isEmpty() || !this.myDeletedWithoutConfirmFiles.isEmpty()) {
            super.executeDelete();
        }

    }

    protected void performDeletion(@NotNull List<FilePath> filesToDelete) {

        final ArrayList errors = new ArrayList();

        try {
            WorkstationHelper.processByWorkspaces(filesToDelete, false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) {
                    Collection<VcsException> scheduleErrors = ScheduleForDeletion.execute(TFSFileListener.this.myProject, workspace, paths);
                    errors.addAll(scheduleErrors);
                }
            });
        } catch (TfsException var4) {
            errors.add(new VcsException(var4));
        }

        if (!errors.isEmpty()) {
            AbstractVcsHelper.getInstance(this.myProject).showErrors(errors, "TFS");
        }

    }

    private void excludeFromFurtherProcessing(FilePath localPath) {
        if (!this.myDeletedFiles.remove(localPath)) {
            this.myDeletedWithoutConfirmFiles.remove(localPath);
        }

    }

    protected void performAdding(@NotNull Collection<VirtualFile> addedFiles, @NotNull Map<VirtualFile, VirtualFile> copyFromMap) {

        final ArrayList errors = new ArrayList();

        try {
            List<FilePath> orphans = WorkstationHelper.processByWorkspaces(TfsFileUtil.getFilePaths(addedFiles), false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) {
                    Collection<VcsException> schedulingErrors = ScheduleForAddition.execute(TFSFileListener.this.myProject, workspace, paths);
                    errors.addAll(schedulingErrors);
                }
            });
            if (!orphans.isEmpty()) {
                StringBuilder s = new StringBuilder();

                FilePath orpan;
                for(Iterator var6 = orphans.iterator(); var6.hasNext(); s.append(orpan.getPresentableUrl())) {
                    orpan = (FilePath)var6.next();
                    if (s.length() > 0) {
                        s.append("\n");
                    }
                }

                errors.add(new VcsException("Team Foundation Server mappings not found for: " + s.toString()));
            }
        } catch (TfsException var8) {
            errors.add(new VcsException(var8));
        }

        if (!errors.isEmpty()) {
            AbstractVcsHelper.getInstance(this.myProject).showErrors(errors, "TFS");
        }

    }

    @NotNull
    protected String getDeleteTitle() {

        return "Do you want to schedule these items for deletion from TFS?";
    }

    protected String getSingleFileDeleteTitle() {
        return null;
    }

    protected String getSingleFileDeletePromptTemplate() {
        return null;
    }

    protected void performMoveRename(@NotNull List<MovedFileInfo> movedFiles) {

        final Map<FilePath, FilePath> movedPaths = new HashMap(movedFiles.size());
        Iterator var3 = movedFiles.iterator();

        while(var3.hasNext()) {
            MovedFileInfo movedFileInfo = (MovedFileInfo)var3.next();
            movedPaths.put(VcsUtil.getFilePath(movedFileInfo.myOldPath), VcsUtil.getFilePath(movedFileInfo.myNewPath));
        }

        final List<VcsException> errors = new ArrayList();
        final HashMap scheduleMove = new HashMap();

        try {
            WorkstationHelper.processByWorkspaces(movedPaths.keySet(), false, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    StatusProvider.visitByStatus(workspace, paths, false, (ProgressIndicator)null, new StatusVisitor() {
                        public void unversioned(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {
                        }

                        public void checkedOutForEdit(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                            scheduleMove.put(localPath, movedPaths.get(localPath));
                        }

                        public void scheduledForAddition(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                            scheduleMove.put(localPath, movedPaths.get(localPath));
                        }

                        public void scheduledForDeletion(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                            TFSVcs.error("Cannot rename a file that does not exist on local machine: " + localPath.getPresentableUrl());
                        }

                        public void outOfDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                            scheduleMove.put(localPath, movedPaths.get(localPath));
                        }

                        public void deleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {

                        }

                        public void upToDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                            scheduleMove.put(localPath, movedPaths.get(localPath));
                        }

                        public void renamed(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                            scheduleMove.put(localPath, movedPaths.get(localPath));
                        }

                        public void renamedCheckedOut(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                            scheduleMove.put(localPath, movedPaths.get(localPath));
                        }

                        public void undeleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {

                            scheduleMove.put(localPath, movedPaths.get(localPath));
                        }
                    }, TFSFileListener.this.myProject);
                    ResultWithFailures<GetOperation> renameResult = workspace.getServer().getVCS().renameAndUpdateLocalVersion(workspace.getName(), workspace.getOwnerName(), scheduleMove, TFSFileListener.this.myProject, TFSBundle.message("renaming", new Object[0]));
                    errors.addAll(TfsUtil.getVcsExceptions(renameResult.getFailures()));
                    Collection<FilePath> invalidate = new ArrayList(renameResult.getResult().size());
                    Iterator var5 = renameResult.getResult().iterator();

                    while(var5.hasNext()) {
                        GetOperation getOperation = (GetOperation)var5.next();
                        invalidate.add(VersionControlPath.getFilePath(getOperation.getTlocal(), getOperation.getType() == ItemType.Folder));
                    }

                    TfsFileUtil.markDirtyRecursively(TFSFileListener.this.myProject, invalidate);
                }
            });
        } catch (TfsException var6) {
            errors.add(new VcsException(var6));
        }

        if (!errors.isEmpty()) {
            AbstractVcsHelper.getInstance(this.myProject).showErrors(errors, "TFS");
        }

    }

    protected boolean isDirectoryVersioningSupported() {
        return true;
    }
}
