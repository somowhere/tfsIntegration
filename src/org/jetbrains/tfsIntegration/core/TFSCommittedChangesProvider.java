//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.CachingCommittedChangesProvider;
import com.intellij.openapi.vcs.ChangeListColumn;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.RepositoryLocation;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.ChangeListColumn.ChangeListNumberColumn;
import com.intellij.openapi.vcs.actions.VcsContextFactory.SERVICE;
import com.intellij.openapi.vcs.changes.committed.DecoratorManager;
import com.intellij.openapi.vcs.changes.committed.VcsCommittedListsZipper;
import com.intellij.openapi.vcs.changes.committed.VcsCommittedViewAuxiliary;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vcs.versionBrowser.ChangeBrowserSettings;
import com.intellij.openapi.vcs.versionBrowser.ChangesBrowserSettingsEditor;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.AsynchConsumer;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Changeset;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemSpec;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper.VoidProcessDelegate;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.DateVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.TFSVersionFilterComponent;

public class TFSCommittedChangesProvider implements CachingCommittedChangesProvider<TFSChangeList, ChangeBrowserSettings> {
    private final Project myProject;
    private final TFSVcs myVcs;

    public TFSCommittedChangesProvider(TFSVcs vcs, Project project) {
        this.myProject = project;
        this.myVcs = vcs;
    }

    public TFSCommittedChangesProvider(Project project) {
        this.myProject = project;
        this.myVcs = TFSVcs.getInstance(this.myProject);
    }

    public ChangesBrowserSettingsEditor<ChangeBrowserSettings> createFilterUI(boolean showDateFilter) {
        return new TFSVersionFilterComponent(showDateFilter);
    }

    @Nullable
    public VcsCommittedListsZipper getZipper() {
        return null;
    }

    public RepositoryLocation getLocationFor(FilePath root) {
        final HashMap pathsByWorkspaces = new HashMap();

        try {
            WorkstationHelper.processByWorkspaces(Collections.singletonList(root), true, this.myProject, new VoidProcessDelegate() {
                public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) {
                    pathsByWorkspaces.put(workspace, TfsUtil.getLocalPaths(paths));
                }
            });
            if (!pathsByWorkspaces.isEmpty()) {
                return new TFSRepositoryLocation(pathsByWorkspaces);
            }
        } catch (TfsException var4) {
            AbstractVcsHelper.getInstance(this.myProject).showError(new VcsException(var4.getMessage(), var4), "TFS");
        } catch (ProcessCanceledException var5) {
            AbstractVcsHelper.getInstance(this.myProject).showError(new VcsException(TFSBundle.message("operation.canceled", new Object[0])), "TFS");
        }

        return null;
    }

    public Pair<TFSChangeList, FilePath> getOneList(VirtualFile file, VcsRevisionNumber number) throws VcsException {
        ChangeBrowserSettings settings = this.createDefaultSettings();
        settings.USE_CHANGE_AFTER_FILTER = true;
        settings.USE_CHANGE_BEFORE_FILTER = true;
        settings.CHANGE_BEFORE = settings.CHANGE_AFTER = String.valueOf(((TfsRevisionNumber)number).getValue());
        FilePath filePath = SERVICE.getInstance().createFilePathOn(file);
        List<TFSChangeList> list = this.getCommittedChanges(settings, this.getLocationFor(filePath), 1);
        return list.size() == 1 ? Pair.create(list.get(0), filePath) : null;
    }

    public RepositoryLocation getForNonLocal(VirtualFile file) {
        return null;
    }

    public boolean supportsIncomingChanges() {
        return true;
    }

    public void loadCommittedChanges(ChangeBrowserSettings settings, RepositoryLocation location, int maxCount, AsynchConsumer<? super CommittedChangeList> consumer) throws VcsException {
        VersionSpec versionFrom = new ChangesetVersionSpec(1);
        if (settings.getChangeAfterFilter() != null) {
            versionFrom = new ChangesetVersionSpec(settings.getChangeAfterFilter().intValue());
        }

        if (settings.getDateAfterFilter() != null) {
            versionFrom = new DateVersionSpec(settings.getDateAfterFilter());
        }

        VersionSpec versionTo = LatestVersionSpec.INSTANCE;
        if (settings.getChangeBeforeFilter() != null) {
            versionTo = new ChangesetVersionSpec(settings.getChangeBeforeFilter().intValue());
        }

        if (settings.getDateBeforeFilter() != null) {
            versionTo = new DateVersionSpec(settings.getDateBeforeFilter());
        }

        TFSRepositoryLocation tfsRepositoryLocation = (TFSRepositoryLocation)location;

        try {
            Iterator var8 = tfsRepositoryLocation.getPathsByWorkspaces().entrySet().iterator();

            label148:
            while(var8.hasNext()) {
                Entry<WorkspaceInfo, List<FilePath>> entry = (Entry)var8.next();
                WorkspaceInfo workspace = (WorkspaceInfo)entry.getKey();
                Map<FilePath, ExtendedItem> extendedItems = workspace.getExtendedItems((List)entry.getValue(), this.myProject, TFSBundle.message("loading.items", new Object[0]));
                Iterator var12 = extendedItems.entrySet().iterator();

                while(true) {
                    Entry localPath2ExtendedItem;
                    ExtendedItem extendedItem;
                    int itemLatestVersion;
                    ChangesetVersionSpec changesetVersionTo;
                    do {
                        do {
                            if (!var12.hasNext()) {
                                continue label148;
                            }

                            localPath2ExtendedItem = (Entry)var12.next();
                            extendedItem = (ExtendedItem)localPath2ExtendedItem.getValue();
                        } while(extendedItem == null);

                        itemLatestVersion = this.getLatestChangesetId(workspace, settings.getUserFilter(), extendedItem);
                        if (!(versionFrom instanceof ChangesetVersionSpec)) {
                            break;
                        }

                        changesetVersionTo = (ChangesetVersionSpec)versionFrom;
                    } while(changesetVersionTo.getChangeSetId() > itemLatestVersion);

                    if (versionTo instanceof ChangesetVersionSpec) {
                        changesetVersionTo = (ChangesetVersionSpec)versionTo;
                        if (changesetVersionTo.getChangeSetId() > itemLatestVersion) {
                            versionTo = new ChangesetVersionSpec(itemLatestVersion);
                        }
                    }

                    VersionSpec itemVersion = LatestVersionSpec.INSTANCE;
                    RecursionType recursionType = ((FilePath)localPath2ExtendedItem.getKey()).isDirectory() ? RecursionType.Full : null;
                    ItemSpec itemSpec = VersionControlServer.createItemSpec(extendedItem.getSitem(), recursionType);
                    List<Changeset> changeSets = workspace.getServer().getVCS().queryHistory(workspace.getName(), workspace.getOwnerName(), itemSpec, settings.getUserFilter(), itemVersion, (VersionSpec)versionFrom, (VersionSpec)versionTo, maxCount, this.myProject, TFSBundle.message("loading.history", new Object[0]));
                    Iterator var20 = changeSets.iterator();

                    while(var20.hasNext()) {
                        Changeset changeset = (Changeset)var20.next();
                        TFSChangeList newList = new TFSChangeList(workspace, changeset.getCset(), changeset.getOwner(), changeset.getDate().getTime(), changeset.getComment(), this.myVcs);
                        consumer.consume(newList);
                    }
                }
            }
        } catch (TfsException var26) {
            throw new VcsException(var26);
        } finally {
            consumer.finished();
        }

    }

    public List<TFSChangeList> getCommittedChanges(ChangeBrowserSettings settings, RepositoryLocation location, int maxCount) throws VcsException {
        final List<TFSChangeList> result = new ArrayList();
        this.loadCommittedChanges(settings, location, maxCount, new AsynchConsumer<CommittedChangeList>() {
            public void finished() {
            }

            public void consume(CommittedChangeList committedChangeList) {
                result.add((TFSChangeList)committedChangeList);
            }
        });
        return result;
    }

    private int getLatestChangesetId(WorkspaceInfo workspace, String user, ExtendedItem extendedItem) throws TfsException {
        if (extendedItem.getType() == ItemType.File) {
            return extendedItem.getLatest();
        } else {
            VersionSpec itemVersion = LatestVersionSpec.INSTANCE;
            VersionSpec versionFrom = new ChangesetVersionSpec(1);
            VersionSpec versionTo = LatestVersionSpec.INSTANCE;
            ItemSpec itemSpec = VersionControlServer.createItemSpec(extendedItem.getSitem(), RecursionType.Full);
            List<Changeset> changeSets = workspace.getServer().getVCS().queryHistory(workspace.getName(), workspace.getOwnerName(), itemSpec, user, itemVersion, versionFrom, versionTo, 1, this.myProject, TFSBundle.message("loading.history", new Object[0]));
            return ((Changeset)changeSets.get(0)).getCset();
        }
    }

    public ChangeListColumn[] getColumns() {
        return new ChangeListColumn[]{new ChangeListNumberColumn("Revision"), ChangeListColumn.NAME, ChangeListColumn.DATE, ChangeListColumn.DESCRIPTION};
    }

    public int getFormatVersion() {
        return 1;
    }

    public void writeChangeList(DataOutput stream, TFSChangeList list) throws IOException {
        list.writeToStream(stream);
    }

    public TFSChangeList readChangeList(RepositoryLocation location, DataInput stream) {
        return new TFSChangeList(this.myVcs, stream);
    }

    public boolean isMaxCountSupported() {
        return true;
    }

    public Collection<FilePath> getIncomingFiles(RepositoryLocation location) {
        return null;
    }

    public boolean refreshCacheByNumber() {
        return true;
    }

    public String getChangelistTitle() {
        return "Changelist";
    }

    public boolean isChangeLocallyAvailable(FilePath filePath, @Nullable VcsRevisionNumber localRevision, VcsRevisionNumber changeRevision, TFSChangeList changeList) {
        return localRevision != null && localRevision.compareTo(changeRevision) >= 0;
    }

    public boolean refreshIncomingWithCommitted() {
        return false;
    }

    public int getUnlimitedCountValue() {
        return 0;
    }

    @Nullable
    public VcsCommittedViewAuxiliary createActions(DecoratorManager manager, RepositoryLocation location) {
        return null;
    }
}
