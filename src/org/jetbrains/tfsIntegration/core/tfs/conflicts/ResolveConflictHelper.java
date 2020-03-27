//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs.conflicts;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.CurrentContentRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtilRt;
import com.intellij.vcsUtil.VcsRunnable;
import com.intellij.vcsUtil.VcsUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ConflictType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LockLevel;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Resolution;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ResolveResponse;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.core.revision.TFSContentRevision;
import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer.ResolveConflictParams;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations.DownloadMode;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.ContentTriplet;

public class ResolveConflictHelper {
    @NotNull
    private final Project myProject;
    @Nullable
    private final UpdatedFiles myUpdatedFiles;
    private final Map<Conflict, WorkspaceInfo> myConflict2Workspace = new HashMap();

    public ResolveConflictHelper(Project project, Map<WorkspaceInfo, Collection<Conflict>> workspace2Conflicts, UpdatedFiles updatedFiles) {
        this.myProject = project;
        Iterator var4 = workspace2Conflicts.entrySet().iterator();

        while(var4.hasNext()) {
            Entry<WorkspaceInfo, Collection<Conflict>> e = (Entry)var4.next();
            Iterator var6 = ((Collection)e.getValue()).iterator();

            while(var6.hasNext()) {
                Conflict conflict = (Conflict)var6.next();
                this.myConflict2Workspace.put(conflict, e.getKey());
            }
        }

        this.myUpdatedFiles = updatedFiles;
    }

    public void acceptMerge(@NotNull final Conflict conflict) throws TfsException, VcsException {

        TFSVcs.assertTrue(canMerge(conflict));
        final WorkspaceInfo workspace = (WorkspaceInfo)this.myConflict2Workspace.get(conflict);
        final FilePath localPath = VersionControlPath.getFilePath(conflict.getSrclitem() != null ? conflict.getSrclitem() : conflict.getTgtlitem(), conflict.getYtype() == ItemType.Folder);
        final ContentTriplet contentTriplet = new ContentTriplet();
        VcsRunnable runnable = new VcsRunnable() {
            public void run() throws VcsException {
                TfsFileUtil.refreshAndFindFile(localPath);

                try {
                    if (conflict.getYtype() == ItemType.File) {
                        byte[] current;
                        byte[] last;
                        if (conflict.getCtype() == ConflictType.Merge) {
                            current = TFSContentRevision.create(ResolveConflictHelper.this.myProject, workspace, conflict.getTver(), conflict.getTitemid()).getContentAsBytes();
                            last = TFSContentRevision.create(ResolveConflictHelper.this.myProject, workspace, conflict.getYver(), conflict.getYitemid()).getContentAsBytes();
                        } else {
                            current = ((CurrentContentRevision)CurrentContentRevision.create(localPath)).getContentAsBytes();
                            last = TFSContentRevision.create(ResolveConflictHelper.this.myProject, workspace, conflict.getTver(), conflict.getTitemid()).getContentAsBytes();
                        }

                        byte[] original = TFSContentRevision.create(ResolveConflictHelper.this.myProject, workspace, conflict.getBver(), conflict.getBitemid()).getContentAsBytes();
                        contentTriplet.baseContent = original != null ? original : ArrayUtilRt.EMPTY_BYTE_ARRAY;
                        contentTriplet.localContent = current != null ? current : ArrayUtilRt.EMPTY_BYTE_ARRAY;
                        contentTriplet.serverContent = last != null ? last : ArrayUtilRt.EMPTY_BYTE_ARRAY;
                    }

                } catch (TfsException var4) {
                    throw new VcsException(TFSBundle.message("cannot.load.revisions", new Object[]{localPath.getPresentableUrl(), var4.getMessage()}));
                }
            }
        };
        if (isContentConflict(conflict)) {
            VcsUtil.runVcsProcessWithProgress(runnable, "Preparing merge data...", false, this.myProject);
        }

        String localName;
        if (isNameConflict(conflict)) {
            String mergedServerPath = ConflictsEnvironment.getNameMerger().mergeName(workspace, conflict, this.myProject);
            if (mergedServerPath == null) {
                return;
            }

            FilePath mergedLocalPath = workspace.findLocalPathByServerPath(mergedServerPath, conflict.getYtype() == ItemType.Folder, this.myProject);
            localName = mergedLocalPath.getPath();
        } else {
            localName = VersionControlPath.localPathFromTfsRepresentation(conflict.getTgtlitem());
        }

        boolean resolved = true;
        if (isContentConflict(conflict)) {
            TFSVcs.assertTrue(conflict.getYtype() == ItemType.File);
            VirtualFile vFile = localPath.getVirtualFile();
            if (vFile == null) {
                String errorMessage = MessageFormat.format("File ''{0}'' is missing", localPath.getPresentableUrl());
                throw new VcsException(errorMessage);
            }

            try {
                TfsFileUtil.setReadOnly(vFile, false);
                resolved = ConflictsEnvironment.getContentMerger().mergeContent(conflict, contentTriplet, this.myProject, vFile, localName, new TfsRevisionNumber(conflict.getTver(), conflict.getTitemid()));
            } catch (IOException var10) {
                throw new VcsException(var10);
            }
        }

        if (resolved) {
            this.conflictResolved(conflict, Resolution.AcceptMerge, localName, isNameConflict(conflict));
        }

    }

    public void acceptYours(@NotNull Conflict conflict) throws TfsException, VcsException {

        String localPath = VersionControlPath.localPathFromTfsRepresentation(conflict.getSrclitem() != null ? conflict.getSrclitem() : conflict.getTgtlitem());
        this.conflictResolved(conflict, Resolution.AcceptYours, conflict.getTgtlitem(), false);
        if (this.myUpdatedFiles != null) {
            this.myUpdatedFiles.getGroupById("SKIPPED").add(localPath, TFSVcs.getKey(), (VcsRevisionNumber)null);
        }

    }

    public void acceptTheirs(@NotNull Conflict conflict) throws TfsException, IOException, VcsException {
        String localPath = VersionControlPath.localPathFromTfsRepresentation(conflict.getTgtlitem() != null ? conflict.getTgtlitem() : conflict.getSrclitem());
        this.conflictResolved(conflict, Resolution.AcceptTheirs, localPath, false);
    }

    public void skip(@NotNull Conflict conflict) {

        if (this.myUpdatedFiles != null) {
            String localPath = VersionControlPath.localPathFromTfsRepresentation(conflict.getSrclitem() != null ? conflict.getSrclitem() : conflict.getTgtlitem());
            this.myUpdatedFiles.getGroupById("SKIPPED").add(localPath, TFSVcs.getKey(), (VcsRevisionNumber)null);
        }

    }

    public Collection<Conflict> getConflicts() {
        return Collections.unmodifiableCollection(this.myConflict2Workspace.keySet());
    }

    public static boolean canMerge(@NotNull Conflict conflict) {

        if (conflict.getSrclitem() == null) {
            return false;
        } else {
            ChangeTypeMask yourChange = new ChangeTypeMask(conflict.getYchg());
            ChangeTypeMask yourLocalChange = new ChangeTypeMask(conflict.getYlchg());
            ChangeTypeMask baseChange = new ChangeTypeMask(conflict.getBchg());
            boolean isNamespaceConflict = (conflict.getCtype().equals(ConflictType.Get) || conflict.getCtype().equals(ConflictType.Checkin)) && conflict.getIsnamecflict();
            if (!isNamespaceConflict) {
                boolean yourRenamedOrModified = yourChange.containsAny(new ChangeType_type0[]{ChangeType_type0.Rename, ChangeType_type0.Edit});
                boolean baseRenamedOrModified = baseChange.containsAny(new ChangeType_type0[]{ChangeType_type0.Rename, ChangeType_type0.Edit});
                if (yourRenamedOrModified && baseRenamedOrModified) {
                    return true;
                }
            }

            if (conflict.getYtype() != ItemType.Folder && !isNamespaceConflict && conflict.getCtype().equals(ConflictType.Merge) && baseChange.contains(ChangeType_type0.Edit)) {
                if (yourLocalChange.contains(ChangeType_type0.Edit)) {
                    return true;
                }

                if (conflict.getIsforced()) {
                    return true;
                }

                if (conflict.getTlmver() != conflict.getBver() || conflict.getYlmver() != conflict.getYver()) {
                    return true;
                }
            }

            return false;
        }
    }

    private void conflictResolved(Conflict conflict, Resolution resolution, @NotNull String newLocalPath, boolean sendPath) throws TfsException, VcsException {

        WorkspaceInfo workspace = (WorkspaceInfo)this.myConflict2Workspace.get(conflict);
        ResolveConflictParams resolveConflictParams = new ResolveConflictParams(conflict.getCid(), resolution, LockLevel.Unchanged, -2, sendPath ? VersionControlPath.toTfsRepresentation(newLocalPath) : null);
        ResolveResponse response = workspace.getServer().getVCS().resolveConflict(workspace.getName(), workspace.getOwnerName(), resolveConflictParams, this.myProject, TFSBundle.message("reporting.conflict.resolved", new Object[0]));
        UpdatedFiles updatedFiles = resolution != Resolution.AcceptMerge ? this.myUpdatedFiles : null;
        if (response.getResolveResult().getGetOperation() != null) {
            DownloadMode downloadMode = resolution == Resolution.AcceptTheirs ? DownloadMode.FORCE : DownloadMode.MERGE;
            Collection<VcsException> applyErrors = ApplyGetOperations.execute(this.myProject, workspace, Arrays.asList(response.getResolveResult().getGetOperation()), ApplyProgress.EMPTY, updatedFiles, downloadMode);
            if (!applyErrors.isEmpty()) {
                throw TfsUtil.collectExceptions(applyErrors);
            }
        }

        if (response.getUndoOperations().getGetOperation() != null) {
            Collection<VcsException> applyErrors = ApplyGetOperations.execute(this.myProject, workspace, Arrays.asList(response.getUndoOperations().getGetOperation()), ApplyProgress.EMPTY, updatedFiles, DownloadMode.FORCE);
            if (!applyErrors.isEmpty()) {
                throw TfsUtil.collectExceptions(applyErrors);
            }
        }

        if (resolution == Resolution.AcceptMerge && this.myUpdatedFiles != null) {
            this.myUpdatedFiles.getGroupById("MERGED").add(newLocalPath, TFSVcs.getKey(), (VcsRevisionNumber)null);
        }

        this.myConflict2Workspace.remove(conflict);
    }

    private static boolean isNameConflict(@NotNull Conflict conflict) {

        ChangeTypeMask yourChange = new ChangeTypeMask(conflict.getYchg());
        ChangeTypeMask baseChange = new ChangeTypeMask(conflict.getBchg());
        return yourChange.contains(ChangeType_type0.Rename) || baseChange.contains(ChangeType_type0.Rename);
    }

    private static boolean isContentConflict(@NotNull Conflict conflict) {

        ChangeTypeMask yourChange = new ChangeTypeMask(conflict.getYchg());
        ChangeTypeMask baseChange = new ChangeTypeMask(conflict.getBchg());
        return yourChange.contains(ChangeType_type0.Edit) || baseChange.contains(ChangeType_type0.Edit);
    }

    public static Collection<Conflict> getUnresolvedConflicts(Collection<? extends Conflict> conflicts) {
        Collection<Conflict> result = new ArrayList();
        Iterator var2 = conflicts.iterator();

        while(var2.hasNext()) {
            Conflict c = (Conflict)var2.next();
            if (!c.getIsresolved()) {
                TFSVcs.assertTrue(c.getCid() != 0);
                result.add(c);
            }
        }

        return result;
    }
}
