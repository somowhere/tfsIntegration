//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.AbstractVcs;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.vcsUtil.VcsUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Changeset;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemSpec;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.revision.TFSContentRevision;
import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class TFSChangeList implements CommittedChangeList {
    public static final boolean IDEADEV_29451_WORKAROUND = true;
    private WorkspaceInfo myWorkspace;
    private final TFSVcs myVcs;
    private int myRevisionNumber;
    private String myAuthor;
    private Date myDate;
    @NotNull
    private String myComment;
    private List<Change> myCachedChanges;
    private final Map<FilePath, Integer> myModifiedPaths = new HashMap();
    private final Set<FilePath> myAddedPaths = new HashSet();
    private final Map<FilePath, Integer> myDeletedPaths = new HashMap();
    private final Map<FilePath, Pair<FilePath, Integer>> myMovedPaths = new HashMap();
    private URI myServerUri;
    private String myWorkspaceName;

    public TFSChangeList(TFSVcs vcs, DataInput stream) {
        this.myVcs = vcs;
        this.readFromStream(stream);
    }

    public TFSChangeList(WorkspaceInfo workspace, int revisionNumber, String author, Date date, String comment, TFSVcs vcs) {
        this.myWorkspace = workspace;
        this.myRevisionNumber = revisionNumber;
        this.myAuthor = author;
        this.myDate = date;
        this.myComment = comment != null ? comment : "";
        this.myVcs = vcs;
        this.myWorkspaceName = this.myWorkspace.getName();
        this.myServerUri = this.myWorkspace.getServer().getUri();
    }

    public String getCommitterName() {
        return this.myAuthor;
    }

    public Date getCommitDate() {
        return this.myDate;
    }

    public long getNumber() {
        return (long)this.myRevisionNumber;
    }

    @Nullable
    public String getBranch() {
        return null;
    }

    public AbstractVcs getVcs() {
        return this.myVcs;
    }

    public Collection<Change> getChanges() {
        if (this.myCachedChanges == null) {
            try {
                if (this.myWorkspace != null) {
                    this.loadChanges();
                }

                this.myCachedChanges = new ArrayList();
                Iterator var1 = this.myAddedPaths.iterator();

                while(var1.hasNext()) {
                    FilePath path = (FilePath)var1.next();
                    this.myCachedChanges.add(new Change((ContentRevision)null, TFSContentRevision.create(this.myVcs.getProject(), path, this.myRevisionNumber)));
                }

                var1 = this.myDeletedPaths.entrySet().iterator();

                Entry entry;
                while(var1.hasNext()) {
                    entry = (Entry)var1.next();
                    this.myCachedChanges.add(new Change(TFSContentRevision.create(this.myVcs.getProject(), (FilePath)entry.getKey(), (Integer)entry.getValue()), (ContentRevision)null));
                }

                var1 = this.myModifiedPaths.entrySet().iterator();

                TFSContentRevision beforeRevision;
                TFSContentRevision afterRevision;
                while(var1.hasNext()) {
                    entry = (Entry)var1.next();
                    beforeRevision = TFSContentRevision.create(this.myVcs.getProject(), (FilePath)entry.getKey(), (Integer)entry.getValue());
                    afterRevision = TFSContentRevision.create(this.myVcs.getProject(), (FilePath)entry.getKey(), this.myRevisionNumber);
                    this.myCachedChanges.add(new Change(beforeRevision, afterRevision));
                }

                var1 = this.myMovedPaths.entrySet().iterator();

                while(var1.hasNext()) {
                    entry = (Entry)var1.next();
                    beforeRevision = TFSContentRevision.create(this.myVcs.getProject(), (FilePath)entry.getKey(), (Integer)((Pair)entry.getValue()).second);
                    afterRevision = TFSContentRevision.create(this.myVcs.getProject(), (FilePath)((Pair)entry.getValue()).first, this.myRevisionNumber);
                    this.myCachedChanges.add(new Change(beforeRevision, (ContentRevision)null));
                    this.myCachedChanges.add(new Change((ContentRevision)null, afterRevision));
                }
            } catch (TfsException var5) {
                AbstractVcsHelper.getInstance(this.myVcs.getProject()).showError(new VcsException(var5.getMessage(), var5), "TFS");
            }
        }

        return this.myCachedChanges;
    }

    public boolean isModifiable() {
        return true;
    }

    public void setDescription(String newMessage) {
        this.myComment = newMessage;
    }

    @NotNull
    public String getName() {
        String var10000 = this.myComment;

        return var10000;
    }

    @NotNull
    public String getComment() {
        String var10000 = this.myComment;
        return var10000;
    }

    void writeToStream(DataOutput stream) throws IOException {
        stream.writeUTF(this.myServerUri.toString());
        stream.writeUTF(this.myWorkspaceName);
        stream.writeInt(this.myRevisionNumber);
        stream.writeUTF(this.myAuthor);
        stream.writeLong(this.myDate.getTime());
        stream.writeUTF(this.myComment);
        writePathsInts(stream, this.myModifiedPaths);
        writePaths(stream, this.myAddedPaths);
        writePathsInts(stream, this.myDeletedPaths);
        writeMoved(stream, this.myMovedPaths);
    }

    private void loadChanges() {
        try {
            Changeset changeset = this.myWorkspace.getServer().getVCS().queryChangeset(this.myRevisionNumber, this.myVcs.getProject(), TFSBundle.message("loading.changes", new Object[0]));
            com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Change[] var2 = changeset.getChanges().getChange();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Change change = var2[var4];
                this.processChange(changeset.getCset(), change);
            }
        } catch (TfsException var6) {
            AbstractVcsHelper.getInstance(this.myVcs.getProject()).showError(new VcsException(var6.getMessage(), var6), "TFS");
        }

    }

    private void processChange(int changeset, com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Change change) throws TfsException {
        ChangeTypeMask changeType = new ChangeTypeMask(change.getType());
        FilePath localPath = this.myWorkspace.findLocalPathByServerPath(change.getItem().getItem(), change.getItem().getType() == ItemType.Folder, this.getVcs().getProject());
        if (localPath != null) {
            if (changeType.containsAny(new ChangeType_type0[]{ChangeType_type0.Add, ChangeType_type0.Undelete, ChangeType_type0.Branch})) {
                if (changeType.contains(ChangeType_type0.Add)) {
                    TFSVcs.assertTrue(changeType.contains(ChangeType_type0.Encoding));
                    if (change.getItem().getType() == ItemType.File) {
                        TFSVcs.assertTrue(changeType.contains(ChangeType_type0.Edit));
                    } else {
                        TFSVcs.assertTrue(!changeType.contains(ChangeType_type0.Edit));
                    }
                }

                TFSVcs.assertTrue(!changeType.contains(ChangeType_type0.Delete));
                this.myAddedPaths.add(localPath);
            } else {
                int previousCs;
                if (changeType.contains(ChangeType_type0.Delete)) {
                    TFSVcs.assertTrue(changeType.size() <= 3, "Unexpected change type: " + changeType);
                    previousCs = change.getItem().getCs() - 1;
                    this.myDeletedPaths.put(localPath, previousCs);
                } else if (changeType.contains(ChangeType_type0.Rename)) {
                    if (change.getItem().getDid() == -2147483648) {
                        Item item = this.getPreviousVersion(change.getItem(), changeset);
                        FilePath originalPath = this.myWorkspace.findLocalPathByServerPath(item.getItem(), item.getType() == ItemType.Folder, this.getVcs().getProject());
                        if (originalPath != null) {
                            this.myMovedPaths.put(originalPath, Pair.create(localPath, item.getCs()));
                        }

                    }
                } else if (changeType.containsAny(new ChangeType_type0[]{ChangeType_type0.Edit, ChangeType_type0.Merge})) {
                    previousCs = change.getItem().getCs() - 1;
                    this.myModifiedPaths.put(localPath, previousCs);
                } else {
                    TFSVcs.error("Unknown change: " + changeType + " for item " + change.getItem().getItem());
                }
            }
        }
    }

    private void readFromStream(DataInput stream) {
        try {
            this.myServerUri = new URI(stream.readUTF());
            this.myWorkspaceName = stream.readUTF();
            this.myRevisionNumber = stream.readInt();
            this.myAuthor = stream.readUTF();
            this.myDate = new Date(stream.readLong());
            this.myComment = stream.readUTF();
            readPathsInts(stream, this.myModifiedPaths);
            readPaths(stream, this.myAddedPaths);
            readPathsInts(stream, this.myDeletedPaths);
            readMoved(stream, this.myMovedPaths);
        } catch (IOException var3) {
            AbstractVcsHelper.getInstance(this.myVcs.getProject()).showError(new VcsException(var3), "TFS");
        } catch (URISyntaxException var4) {
            AbstractVcsHelper.getInstance(this.myVcs.getProject()).showError(new VcsException(var4), "TFS");
        }

    }

    private static void writePaths(DataOutput stream, Collection<FilePath> paths) throws IOException {
        stream.writeInt(paths.size());
        Iterator var2 = paths.iterator();

        while(var2.hasNext()) {
            FilePath path = (FilePath)var2.next();
            writePath(stream, path);
        }

    }

    private static void writePathsInts(DataOutput stream, Map<FilePath, Integer> paths) throws IOException {
        stream.writeInt(paths.size());
        Iterator var2 = paths.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<FilePath, Integer> e = (Entry)var2.next();
            writePath(stream, (FilePath)e.getKey());
            stream.writeInt((Integer)e.getValue());
        }

    }

    private static void writeMoved(DataOutput stream, Map<FilePath, Pair<FilePath, Integer>> paths) throws IOException {
        stream.writeInt(paths.size());
        Iterator var2 = paths.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<FilePath, Pair<FilePath, Integer>> e = (Entry)var2.next();
            writePath(stream, (FilePath)e.getKey());
            writePath(stream, (FilePath)((Pair)e.getValue()).first);
            stream.writeInt((Integer)((Pair)e.getValue()).second);
        }

    }

    private static void writePath(DataOutput stream, FilePath path) throws IOException {
        stream.writeUTF(path.getPath());
        stream.writeBoolean(path.isDirectory());
    }

    private static void readPaths(DataInput stream, Collection<FilePath> paths) throws IOException {
        int count = stream.readInt();

        for(int i = 0; i < count; ++i) {
            paths.add(readPath(stream));
        }

    }

    private static void readPathsInts(DataInput stream, Map<FilePath, Integer> paths) throws IOException {
        int count = stream.readInt();

        for(int i = 0; i < count; ++i) {
            paths.put(readPath(stream), stream.readInt());
        }

    }

    private static void readMoved(DataInput stream, Map<FilePath, Pair<FilePath, Integer>> paths) throws IOException {
        int count = stream.readInt();

        for(int i = 0; i < count; ++i) {
            paths.put(readPath(stream), Pair.create(readPath(stream), stream.readInt()));
        }

    }

    private static FilePath readPath(DataInput stream) throws IOException {
        return VcsUtil.getFilePath(stream.readUTF(), stream.readBoolean());
    }

    private Item getPreviousVersion(Item item, int changeset) throws TfsException {
        ItemSpec itemSpec = VersionControlServer.createItemSpec(item.getItem(), item.getDid(), RecursionType.None);
        List<Changeset> shortHistory = this.myWorkspace.getServer().getVCS().queryHistory(this.myWorkspace.getName(), this.myWorkspace.getOwnerName(), itemSpec, (String)null, new ChangesetVersionSpec(changeset), new ChangesetVersionSpec(1), new ChangesetVersionSpec(item.getCs()), 2, this.myVcs.getProject(), TFSBundle.message("loading.history", new Object[0]));
        TFSVcs.assertTrue(shortHistory.size() == 2);
        return ((Changeset)shortHistory.get(1)).getChanges().getChange()[0].getItem();
    }

    public String toString() {
        return this.myComment;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            TFSChangeList that = (TFSChangeList)o;
            if (this.myRevisionNumber != that.myRevisionNumber) {
                return false;
            } else {
                label60: {
                    if (this.myAuthor != null) {
                        if (this.myAuthor.equals(that.myAuthor)) {
                            break label60;
                        }
                    } else if (that.myAuthor == null) {
                        break label60;
                    }

                    return false;
                }

                if (this.myDate != null) {
                    if (!this.myDate.equals(that.myDate)) {
                        return false;
                    }
                } else if (that.myDate != null) {
                    return false;
                }

                if (this.myServerUri != null) {
                    if (!this.myServerUri.equals(that.myServerUri)) {
                        return false;
                    }
                } else if (that.myServerUri != null) {
                    return false;
                }

                if (this.myWorkspaceName != null) {
                    if (!this.myWorkspaceName.equals(that.myWorkspaceName)) {
                        return false;
                    }
                } else if (that.myWorkspaceName != null) {
                    return false;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = this.myRevisionNumber;
        result = 31 * result + (this.myAuthor != null ? this.myAuthor.hashCode() : 0);
        result = 31 * result + (this.myDate != null ? this.myDate.hashCode() : 0);
        result = 31 * result + (this.myServerUri != null ? this.myServerUri.hashCode() : 0);
        result = 31 * result + (this.myWorkspaceName != null ? this.myWorkspaceName.hashCode() : 0);
        return result;
    }
}
