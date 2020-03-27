//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.update.SequentialUpdatesContext;
import com.intellij.openapi.vcs.update.UpdateEnvironment;
import com.intellij.openapi.vcs.update.UpdateSession;
import com.intellij.openapi.vcs.update.UpdatedFiles;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.UpdateConfigurable;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer.GetRequestParams;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper.VoidProcessDelegate;
import org.jetbrains.tfsIntegration.core.tfs.conflicts.ConflictsEnvironment;
import org.jetbrains.tfsIntegration.core.tfs.conflicts.ResolveConflictHelper;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations.DownloadMode;
import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress.ProgressIndicatorWrapper;
import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.UpdateSettingsForm.WorkspaceSettings;

public class TFSUpdateEnvironment implements UpdateEnvironment {
    @NotNull
    private final TFSVcs myVcs;

    TFSUpdateEnvironment(@NotNull TFSVcs vcs) {

        super();
        this.myVcs = vcs;
    }

    public void fillGroups(UpdatedFiles updatedFiles) {
    }

    @NotNull
    public UpdateSession updateDirectories(@NotNull FilePath[] contentRoots, final UpdatedFiles updatedFiles, final ProgressIndicator progressIndicator, @NotNull Ref<SequentialUpdatesContext> context) throws ProcessCanceledException {

        final List<VcsException> exceptions = new ArrayList();
        TFSProgressUtil.setProgressText(progressIndicator, "Request update information");

        try {
            final Map<WorkspaceInfo, Collection<Conflict>> workspace2Conflicts = new HashMap();
            List<FilePath> orphanPaths = WorkstationHelper.processByWorkspaces(Arrays.asList(contentRoots), true, this.myVcs.getProject(), new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                    VersionSpecBase version = LatestVersionSpec.INSTANCE;
                    RecursionType recursionType = RecursionType.Full;
                    TFSProjectConfiguration configuration = TFSProjectConfiguration.getInstance(TFSUpdateEnvironment.this.myVcs.getProject());
                    if (configuration != null) {
                        version = configuration.getUpdateWorkspaceInfo(workspace).getVersion();
                        recursionType = configuration.getState().UPDATE_RECURSIVELY ? RecursionType.Full : RecursionType.None;
                    }

                    List<GetRequestParams> requests = new ArrayList(paths.size());
                    Iterator var7 = paths.iterator();

                    while(var7.hasNext()) {
                        ItemPath path = (ItemPath)var7.next();
                        requests.add(new GetRequestParams(path.getServerPath(), recursionType, (VersionSpec)version));
                        TFSProgressUtil.checkCanceled(progressIndicator);
                    }

                    List<GetOperation> operations = workspace.getServer().getVCS().get(workspace.getName(), workspace.getOwnerName(), requests, TFSUpdateEnvironment.this.myVcs.getProject(), TFSBundle.message("preparing.for.download", new Object[0]));
                    Collection<VcsException> applyErrors = ApplyGetOperations.execute(TFSUpdateEnvironment.this.myVcs.getProject(), workspace, operations, new ProgressIndicatorWrapper(progressIndicator), updatedFiles, DownloadMode.ALLOW);
                    exceptions.addAll(applyErrors);
                    Collection<Conflict> conflicts = workspace.getServer().getVCS().queryConflicts(workspace.getName(), workspace.getOwnerName(), paths, RecursionType.Full, TFSUpdateEnvironment.this.myVcs.getProject(), TFSBundle.message("loading.conflicts", new Object[0]));
                    Collection<Conflict> unresolvedConflicts = ResolveConflictHelper.getUnresolvedConflicts(conflicts);
                    if (!unresolvedConflicts.isEmpty()) {
                        workspace2Conflicts.put(workspace, unresolvedConflicts);
                    }

                }
            });
            if (!workspace2Conflicts.isEmpty()) {
                ResolveConflictHelper resolveConflictHelper = new ResolveConflictHelper(this.myVcs.getProject(), workspace2Conflicts, updatedFiles);
                ConflictsEnvironment.getConflictsHandler().resolveConflicts(resolveConflictHelper);
            }

            Iterator var11 = orphanPaths.iterator();

            while(var11.hasNext()) {
                FilePath orphanPath = (FilePath)var11.next();
                updatedFiles.getGroupById("UNKNOWN").add(orphanPath.getPresentableUrl(), TFSVcs.getKey(), (VcsRevisionNumber)null);
            }
        } catch (TfsException var10) {
            exceptions.add(new VcsException(var10));
        }

        TfsFileUtil.refreshAndInvalidate(this.myVcs.getProject(), contentRoots, false);
        UpdateSession var10000 = new UpdateSession() {
            @NotNull
            public List<VcsException> getExceptions() {
                List var10000 = exceptions;

                return var10000;
            }

            public void onRefreshFilesCompleted() {
                TFSUpdateEnvironment.this.myVcs.fireRevisionChanged();
            }

            public boolean isCanceled() {
                return false;
            }
        };

        return var10000;
    }

    @Nullable
    public Configurable createConfigurable(Collection<FilePath> files) {
        Map<WorkspaceInfo, WorkspaceSettings> workspacesSettings = new HashMap();
        Ref<TfsException> error = new Ref();
        Runnable r = () -> {
            try {
                WorkstationHelper.processByWorkspaces(files, true, this.myVcs.getProject(), new VoidProcessDelegate() {
                    public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                        Map result = workspace.getExtendedItems2(paths, TFSUpdateEnvironment.this.myVcs.getProject(), TFSBundle.message("loading.items", new Object[0]));
                        Collection items = new ArrayList(result.values());
                        Iterator i = items.iterator();

                        while(true) {
                            ExtendedItem extendedItem;
                            do {
                                if (!i.hasNext()) {
                                    if (items.isEmpty()) {
                                        return;
                                    }

                                    ExtendedItem someExtendedItem = (ExtendedItem)items.iterator().next();
                                    WorkspaceSettings workspaceSettings = new WorkspaceSettings(someExtendedItem.getSitem(), someExtendedItem.getType() == ItemType.Folder);
                                    Iterator var7 = items.iterator();

                                    while(var7.hasNext()) {
                                        ExtendedItem extendedItemx = (ExtendedItem)var7.next();
                                        String path1 = workspaceSettings.serverPath;
                                        String path2 = extendedItemx.getSitem();
                                        if (VersionControlPath.isUnder(path2, path1)) {
                                            workspaceSettings = new WorkspaceSettings(path2, extendedItemx.getType() == ItemType.Folder);
                                        } else if (!VersionControlPath.isUnder(path1, path2)) {
                                            workspaceSettings = new WorkspaceSettings(VersionControlPath.getCommonAncestor(path1, path2), true);
                                        }
                                    }

                                    workspacesSettings.put(workspace, workspaceSettings);
                                    return;
                                }

                                extendedItem = (ExtendedItem)i.next();
                            } while(extendedItem != null && extendedItem.getSitem() != null);

                            i.remove();
                        }
                    }
                });
            } catch (TfsException var5) {
                error.set(var5);
            }

        };
        ProgressManager.getInstance().runProcessWithProgressSynchronously(r, "TFS: preparing for update...", false, this.myVcs.getProject());
        if (!error.isNull()) {
            return null;
        } else {
            return workspacesSettings.isEmpty() ? null : new UpdateConfigurable(this.myVcs.getProject(), workspacesSettings);
        }
    }

    public boolean validateOptions(Collection<FilePath> roots) {
        return true;
    }
}
