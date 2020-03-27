//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs.operations;

import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.util.WaitForProgressToShow;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LocalVersionUpdate;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil.ContentWriter;
import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class ApplyGetOperations {
    private static ApplyGetOperations.LocalConflictHandlingType ourLocalConflictHandlingType;
    private final Project myProject;
    private final WorkspaceInfo myWorkspace;
    private final Collection<GetOperation> myOperations;
    @NotNull
    private final ApplyProgress myProgress;
    @Nullable
    private final UpdatedFiles myUpdatedFiles;
    private final Collection<VcsException> myErrors;
    private final Collection<LocalVersionUpdate> myUpdateLocalVersions;
    private final ApplyGetOperations.DownloadMode myDownloadMode;

    private ApplyGetOperations(Project project, WorkspaceInfo workspace, Collection<GetOperation> operations, @NotNull ApplyProgress progress, @Nullable UpdatedFiles updatedFiles, ApplyGetOperations.DownloadMode downloadMode) {

        super();
        this.myErrors = new ArrayList();
        this.myUpdateLocalVersions = new ArrayList();
        this.myProject = project;
        this.myWorkspace = workspace;
        this.myOperations = operations;
        this.myProgress = progress;
        this.myUpdatedFiles = updatedFiles;
        this.myDownloadMode = downloadMode;
    }

    public static ApplyGetOperations.LocalConflictHandlingType getLocalConflictHandlingType() {
        return ourLocalConflictHandlingType;
    }

    public static void setLocalConflictHandlingType(ApplyGetOperations.LocalConflictHandlingType type) {
        ourLocalConflictHandlingType = type;
    }

    public static Collection<VcsException> execute(Project project, WorkspaceInfo workspace, Collection<GetOperation> operations, @NotNull ApplyProgress progress, @Nullable UpdatedFiles updatedFiles, ApplyGetOperations.DownloadMode downloadMode) {

        ApplyGetOperations session = new ApplyGetOperations(project, workspace, operations, progress, updatedFiles, downloadMode);
        session.execute();
        return session.myErrors;
    }

    private void execute() {
        if (!this.myOperations.isEmpty()) {
            ArrayList sortedOperations = new ArrayList(this.myOperations);

            try {
                int i = 0;

                while(true) {
                    if (i >= sortedOperations.size()) {
                        this.myWorkspace.getServer().getVCS().updateLocalVersions(this.myWorkspace.getName(), this.myWorkspace.getOwnerName(), this.myUpdateLocalVersions, this.myProject, TFSBundle.message("updating.local.version", new Object[0]));
                        break;
                    }

                    if (this.myProgress.isCancelled()) {
                        throw new ProcessCanceledException();
                    }

                    GetOperation operationToExecute = (GetOperation)sortedOperations.get(i);
                    String currentPath = VersionControlPath.localPathFromTfsRepresentation(operationToExecute.getTlocal() != null ? operationToExecute.getTlocal() : operationToExecute.getSlocal());
                    if (currentPath == null) {
                        FilePath unexistingPath = this.myWorkspace.findLocalPathByServerPath(operationToExecute.getTitem(), operationToExecute.getType() == ItemType.Folder, this.myProject);
                        currentPath = unexistingPath.getPresentableUrl();
                    }

                    this.myProgress.setFraction((double)(i / sortedOperations.size()));
                    this.myProgress.setText(currentPath);
                    if (operationToExecute.getCnflct()) {
                        this.processConflict(operationToExecute);
                    } else if (operationToExecute.getSlocal() == null && operationToExecute.getTlocal() == null) {
                        this.updateLocalVersion(operationToExecute);
                    } else if (operationToExecute.getTlocal() == null) {
                        if (operationToExecute.getType() == ItemType.File) {
                            this.processDeleteFile(operationToExecute);
                        } else {
                            this.processDeleteFolder(operationToExecute);
                        }
                    } else if (operationToExecute.getSlocal() == null) {
                        if (operationToExecute.getType() == ItemType.File) {
                            this.processCreateFile(operationToExecute);
                        } else {
                            this.processCreateFolder(operationToExecute);
                        }
                    } else if (operationToExecute.getType() == ItemType.File) {
                        this.processFileChange(operationToExecute);
                    } else {
                        this.processFolderChange(operationToExecute);
                        if (!operationToExecute.getSlocal().equals(operationToExecute.getTlocal())) {
                            GetOperationsUtil.updateSourcePaths(sortedOperations, i, operationToExecute);
                        }
                    }

                    ++i;
                }
            } catch (TfsException var6) {
                this.myErrors.add(new VcsException(var6));
            }

        }
    }

    private void processDeleteFile(GetOperation operation) throws TfsException {
        File source = VersionControlPath.getFile(operation.getSlocal());
        if (source.isDirectory()) {
            String errorMessage = MessageFormat.format("Cannot delete file ''{0}'' because there is a folder with the same name", source.getPath());
            this.myErrors.add(new VcsException(errorMessage));
        } else if (!source.canWrite() || this.canOverrideLocalConflictingItem(operation, true)) {
            boolean exists = source.exists();
            if (this.deleteFile(source)) {
                this.updateLocalVersion(operation);
                if (exists) {
                    this.addToGroup("REMOVED_FROM_REPOSITORY", source, operation);
                }

            }
        }
    }

    private void processDeleteFolder(GetOperation operation) throws TfsException {
        File source = VersionControlPath.getFile(operation.getSlocal());
        String errorMessage;
        if (source.isFile()) {
            errorMessage = MessageFormat.format("Cannot delete folder ''{0}'' because there is a file with the same name", source.getPath());
            this.myErrors.add(new VcsException(errorMessage));
        } else if (!this.canDeleteFolder(source)) {
            errorMessage = MessageFormat.format("Cannot delete folder ''{0}'' because it is not empty", source.getPath());
            this.myErrors.add(new VcsException(errorMessage));
        } else {
            boolean exists = source.exists();
            if (this.deleteFile(source)) {
                this.updateLocalVersion(operation);
                if (exists) {
                    this.addToGroup("REMOVED_FROM_REPOSITORY", source, operation);
                }

            }
        }
    }

    private void processCreateFile(@NotNull GetOperation operation) throws TfsException {
        File target = VersionControlPath.getFile(operation.getTlocal());
        String errorMessage;
        if (target.isDirectory()) {
            errorMessage = MessageFormat.format("Cannot create file ''{0}'' because there is a folder with the same name", target.getPath());
            this.myErrors.add(new VcsException(errorMessage));
        } else {
            if (target.canWrite()) {
                if (!this.canOverrideLocalConflictingItem(operation, false)) {
                    return;
                }

                if (!FileUtil.delete(target)) {
                    errorMessage = MessageFormat.format("Cannot overwrite file ''{0}''", target.getPath());
                    this.myErrors.add(new VcsException(errorMessage));
                    return;
                }
            }

            if (this.createFolder(target.getParentFile())) {
                if (this.downloadFile(operation)) {
                    this.updateLocalVersion(operation);
                    this.addToGroup("CREATED", target, operation);
                }

            }
        }
    }

    private void processCreateFolder(GetOperation operation) throws TfsException {
        File target = VersionControlPath.getFile(operation.getTlocal());
        if (!target.isFile() || !target.canWrite() || this.canOverrideLocalConflictingItem(operation, false)) {
            if (target.isFile() && !FileUtil.delete(target)) {
                String errorMessage = MessageFormat.format("Cannot create folder ''{0}'' because there is a folder with the same name", target.getPath());
                this.myErrors.add(new VcsException(errorMessage));
            } else {
                boolean folderExists = target.exists();
                if (this.createFolder(target)) {
                    this.updateLocalVersion(operation);
                    if (!folderExists) {
                        this.addToGroup("CREATED", target, operation);
                    }

                }
            }
        }
    }

    private void processFileChange(GetOperation operation) throws TfsException {
        File source = VersionControlPath.getFile(operation.getSlocal());
        File target = VersionControlPath.getFile(operation.getTlocal());
        ChangeTypeMask change = new ChangeTypeMask(operation.getChg());
        if (!source.equals(target) || operation.getLver() != operation.getSver() || !change.containsOnly(new ChangeType_type0[]{ChangeType_type0.Rename}) && (this.myDownloadMode == ApplyGetOperations.DownloadMode.FORCE || this.myDownloadMode == ApplyGetOperations.DownloadMode.MERGE)) {
            if (!source.equals(target) && source.canWrite()) {
                if (!this.canOverrideLocalConflictingItem(operation, true)) {
                    return;
                }

                if (this.myDownloadMode == ApplyGetOperations.DownloadMode.FORCE && !this.deleteFile(source)) {
                    return;
                }
            }

            if (source.equals(target) || !source.isDirectory() || this.canOverrideLocalConflictingItem(operation, true) && this.deleteFile(source)) {
                if (target.isDirectory()) {
                    String errorMessage = MessageFormat.format("Cannot create file ''{0}'' because there is a folder with same name", target.getPath());
                    this.myErrors.add(new VcsException(errorMessage));
                } else if (!target.canWrite() || target.equals(source) || this.canOverrideLocalConflictingItem(operation, false)) {
                    if (this.createFolder(target.getParentFile())) {
                        if (this.myDownloadMode == ApplyGetOperations.DownloadMode.FORCE || this.myDownloadMode != ApplyGetOperations.DownloadMode.MERGE && operation.getLver() != operation.getSver()) {
                            if ((source.equals(target) || this.deleteFile(source)) && (change.contains(ChangeType_type0.Add) || this.downloadFile(operation))) {
                                this.updateLocalVersion(operation);
                                if (source.equals(target)) {
                                    this.addToGroup("UPDATED", target, operation);
                                } else {
                                    this.addToGroup("REMOVED_FROM_REPOSITORY", source, operation);
                                    this.addToGroup("CREATED", target, operation);
                                }
                            }

                        } else {
                            if (!target.exists()) {
                                if (source.exists()) {
                                    if (this.rename(source, target)) {
                                        this.addToGroup("UPDATED", target, operation);
                                        this.updateLocalVersion(operation);
                                    }
                                } else if ((!change.contains(ChangeType_type0.Add) || !source.equals(target) || operation.getLver() != operation.getSver()) && this.downloadFile(operation)) {
                                    this.addToGroup("CREATED", target, operation);
                                    this.updateLocalVersion(operation);
                                }
                            } else {
                                if (!source.equals(target)) {
                                    this.deleteFile(source);
                                }

                                if (this.myDownloadMode == ApplyGetOperations.DownloadMode.MERGE) {
                                    this.addToGroup("MERGED", target, operation);
                                }

                                this.updateLocalVersion(operation);
                            }

                        }
                    }
                }
            }
        } else {
            this.updateLocalVersion(operation);
        }
    }

    private void processFolderChange(GetOperation operation) throws TfsException {
        File source = VersionControlPath.getFile(operation.getSlocal());
        File target = VersionControlPath.getFile(operation.getTlocal());
        ChangeTypeMask change = new ChangeTypeMask(operation.getChg());
        if (!source.equals(target) || operation.getLver() != operation.getSver() || !change.containsOnly(new ChangeType_type0[]{ChangeType_type0.Rename}) && (this.myDownloadMode == ApplyGetOperations.DownloadMode.FORCE || this.myDownloadMode == ApplyGetOperations.DownloadMode.MERGE)) {
            if (source.equals(target) || !source.isFile() || source.canWrite() || this.deleteFile(source)) {
                if (!target.isFile() || target.canWrite() || this.deleteFile(target)) {
                    if (!target.isFile() || !target.canWrite() || this.canOverrideLocalConflictingItem(operation, false) && this.deleteFile(target)) {
                        if (this.createFolder(target.getParentFile())) {
                            if (!target.exists()) {
                                if (source.isDirectory()) {
                                    if (this.rename(source, target)) {
                                        this.addToGroup("UPDATED", target, operation);
                                        this.updateLocalVersion(operation);
                                    }
                                } else if ((!change.contains(ChangeType_type0.Add) || !source.equals(target) || operation.getLver() != operation.getSver()) && this.createFolder(target)) {
                                    this.addToGroup("CREATED", target, operation);
                                    this.updateLocalVersion(operation);
                                }
                            } else {
                                if (!source.equals(target)) {
                                    this.deleteFile(source);
                                }

                                this.updateLocalVersion(operation);
                            }

                        }
                    }
                }
            }
        } else {
            this.updateLocalVersion(operation);
        }
    }

    private void processConflict(GetOperation operation) {
    }

    private void addToGroup(String groupId, File file, GetOperation operation) {
        if (this.myUpdatedFiles != null) {
            int revisionNumber = operation.getSver() != -2147483648 ? operation.getSver() : 0;
            this.myUpdatedFiles.getGroupById(groupId).add(file.getPath(), TFSVcs.getKey(), new TfsRevisionNumber(revisionNumber));
        }

    }

    private boolean deleteFile(File target) {
        if (this.myDownloadMode != ApplyGetOperations.DownloadMode.FORBID && !FileUtil.delete(target)) {
            String errorMessage = MessageFormat.format("Cannot delete {0} ''{1}''", target.isFile() ? "file" : "folder", target.getPath());
            this.myErrors.add(new VcsException(errorMessage));
            return false;
        } else {
            return true;
        }
    }

    private boolean canDeleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            File[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                File child = var3[var5];
                if (!child.isFile()) {
                    if (!this.canDeleteFolder(child)) {
                        return false;
                    }
                } else {
                    if (child.canWrite()) {
                        return false;
                    }

                    boolean childWillBeDeletedAnyway = false;
                    Iterator var8 = this.myOperations.iterator();

                    while(var8.hasNext()) {
                        GetOperation operation = (GetOperation)var8.next();
                        if (operation.getSlocal() != null && VersionControlPath.getFile(operation.getSlocal()).equals(child) && operation.getTlocal() == null) {
                            childWillBeDeletedAnyway = true;
                            break;
                        }
                    }

                    if (!childWillBeDeletedAnyway) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean createFolder(File target) {
        if (this.myDownloadMode != ApplyGetOperations.DownloadMode.FORBID && !target.exists() && !target.mkdirs()) {
            String errorMessage = MessageFormat.format("Cannot create folder ''{0}''", target.getPath());
            this.myErrors.add(new VcsException(errorMessage));
            return false;
        } else {
            return true;
        }
    }

    private boolean rename(File source, File target) {
        if (this.myDownloadMode != ApplyGetOperations.DownloadMode.FORBID && !source.equals(target) && !source.renameTo(target)) {
            String errorMessage = MessageFormat.format("Cannot rename {0} ''{1}'' to ''{2}''", source.isFile() ? "file" : "folder", source.getPath(), target.getPath());
            this.myErrors.add(new VcsException(errorMessage));
            return false;
        } else {
            return true;
        }
    }

    private boolean downloadFile(final GetOperation operation) throws TfsException {
        TFSVcs.assertTrue(operation.getDurl() != null, "Null download url for " + VersionControlPath.localPathFromTfsRepresentation(operation.getTlocal()));
        if (this.myDownloadMode == ApplyGetOperations.DownloadMode.FORBID) {
            return true;
        } else {
            final File target = VersionControlPath.getFile(operation.getTlocal());

            try {
                TfsFileUtil.setFileContent(target, new ContentWriter() {
                    public void write(OutputStream outputStream) throws TfsException {
                        ApplyGetOperations.this.myWorkspace.getServer().getVCS().downloadItem(ApplyGetOperations.this.myProject, operation.getDurl(), outputStream, TFSBundle.message("downloading.0", new Object[]{target.getName()}));
                    }
                });
                if (!target.setReadOnly()) {
                    String errorMessage = MessageFormat.format("Cannot write to file ''{0}''", target.getPath());
                    this.myErrors.add(new VcsException(errorMessage));
                    return false;
                } else {
                    return true;
                }
            } catch (IOException var5) {
                String errorMessage = MessageFormat.format("Cannot write to file ''{0}'': {1}", target.getPath(), var5.getMessage());
                this.myErrors.add(new VcsException(errorMessage));
                return false;
            }
        }
    }

    private boolean canOverrideLocalConflictingItem(GetOperation operation, boolean sourceNotTarget) throws TfsException {
        if (this.myDownloadMode != ApplyGetOperations.DownloadMode.FORCE && this.myDownloadMode != ApplyGetOperations.DownloadMode.MERGE) {
            ApplyGetOperations.LocalConflictHandlingType conflictHandlingType = getLocalConflictHandlingType();
            if (conflictHandlingType == ApplyGetOperations.LocalConflictHandlingType.ERROR) {
                throw new OperationFailedException("Local conflict detected for " + VersionControlPath.localPathFromTfsRepresentation(sourceNotTarget ? operation.getSlocal() : operation.getTlocal()));
            } else if (conflictHandlingType == ApplyGetOperations.LocalConflictHandlingType.SHOW_MESSAGE) {
                String path = VersionControlPath.localPathFromTfsRepresentation(sourceNotTarget ? operation.getSlocal() : operation.getTlocal());
                String message = MessageFormat.format("Local conflict detected. Override local item?\n {0}", path);
                String title = "Modify Files";
                Ref<Integer> result = new Ref();
                WaitForProgressToShow.runOrInvokeAndWaitAboveProgress(() -> {
                    result.set(Messages.showYesNoDialog(message, "Modify Files", Messages.getQuestionIcon()));
                });
                if ((Integer)result.get() == 0) {
                    return true;
                } else {
                    this.reportLocalConflict(operation, sourceNotTarget);
                    return false;
                }
            } else {
                throw new IllegalArgumentException("Unknown conflict handling type: " + conflictHandlingType);
            }
        } else {
            return true;
        }
    }

    private void reportLocalConflict(GetOperation operation, boolean sourceNotTarget) throws TfsException {
        int reason = sourceNotTarget ? 1 : 3;
        this.myWorkspace.getServer().getVCS().addLocalConflict(this.myWorkspace.getName(), this.myWorkspace.getOwnerName(), operation.getItemid(), operation.getSver(), operation.getPcid() != -2147483648 ? operation.getPcid() : 0, operation.getSlocal(), operation.getTlocal(), reason, this.myProject, TFSBundle.message("reporting.conflict", new Object[0]));
    }

    private void updateLocalVersion(GetOperation operation) {
        this.myUpdateLocalVersions.add(VersionControlServer.getLocalVersionUpdate(operation));
    }

    static {
        ourLocalConflictHandlingType = ApplyGetOperations.LocalConflictHandlingType.SHOW_MESSAGE;
    }

    public static enum LocalConflictHandlingType {
        SHOW_MESSAGE,
        ERROR;

        private LocalConflictHandlingType() {
        }
    }

    public static enum DownloadMode {
        FORCE,
        ALLOW,
        FORBID,
        MERGE;

        private DownloadMode() {
        }
    }
}
