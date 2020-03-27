//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.revision;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.changes.ByteBackedContentRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.DeletedState;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Collection;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.Workstation;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil.ContentWriter;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public abstract class TFSContentRevision implements ByteBackedContentRevision {
    private final Project myProject;
    private final ServerInfo myServer;
    @Nullable
    private byte[] myContent;

    protected TFSContentRevision(Project project, ServerInfo server) {
        this.myProject = project;
        this.myServer = server;
    }

    @Nullable
    protected abstract Item getItem() throws TfsException;

    protected abstract int getItemId() throws TfsException;

    protected abstract int getChangeset() throws TfsException;

    public static TFSContentRevision create(Project project, @NotNull WorkspaceInfo workspace, final int changeset, final int itemId) throws TfsException {

        final Item item = workspace.getServer().getVCS().queryItemById(itemId, changeset, true, project, TFSBundle.message("loading.item", new Object[0]));
        final FilePath localPath;
        if (item != null) {
            localPath = workspace.findLocalPathByServerPath(item.getItem(), item.getType() == ItemType.Folder, project);
            if (localPath == null) {
                throw new TfsException(TFSBundle.message("no.mapping.for.0", new Object[]{item.getItem()}));
            }
        } else {
            localPath = null;
        }

        return new TFSContentRevision(project, workspace.getServer()) {
            @Nullable
            protected Item getItem() {
                return item;
            }

            protected int getItemId() {
                return itemId;
            }

            protected int getChangeset() throws TfsException {
                return changeset;
            }

            @NotNull
            public FilePath getFile() {
                FilePath var10000 = localPath;

                return var10000;
            }

            @NotNull
            public VcsRevisionNumber getRevisionNumber() {
                TfsRevisionNumber var10000 = new TfsRevisionNumber(changeset, itemId);
                return var10000;
            }
        };
    }

    public static TFSContentRevision create(final Project project, @NotNull final WorkspaceInfo workspace, @NotNull final FilePath localPath, final int changeset, final int itemId) {

        return new TFSContentRevision(project, workspace.getServer()) {
            @Nullable
            protected Item getItem() throws TfsException {
                return workspace.getServer().getVCS().queryItemById(itemId, changeset, true, project, TFSBundle.message("loading.item", new Object[0]));
            }

            protected int getItemId() {
                return itemId;
            }

            protected int getChangeset() {
                return changeset;
            }

            @NotNull
            public FilePath getFile() {
                FilePath var10000 = localPath;

                return var10000;
            }

            @NotNull
            public VcsRevisionNumber getRevisionNumber() {
                TfsRevisionNumber var10000 = new TfsRevisionNumber(changeset, itemId);

                return var10000;
            }
        };
    }

    public static TFSContentRevision create(final Project project, @NotNull final FilePath localPath, final int changeset) throws TfsException {

        Collection<WorkspaceInfo> workspaces = Workstation.getInstance().findWorkspaces(localPath, false, project);
        if (workspaces.isEmpty()) {
            throw new OperationFailedException("Cannot find mapping for item " + localPath.getPresentableUrl());
        } else {
            final WorkspaceInfo workspace = (WorkspaceInfo)workspaces.iterator().next();
            return new TFSContentRevision(project, workspace.getServer()) {
                @Nullable
                private Item myItem;

                @Nullable
                protected Item getItem() throws TfsException {
                    if (this.myItem == null) {
                        this.myItem = workspace.getServer().getVCS().queryItem(workspace.getName(), workspace.getOwnerName(), VersionControlPath.toTfsRepresentation(localPath), new ChangesetVersionSpec(changeset), DeletedState.Any, true, project, TFSBundle.message("loading.item", new Object[0]));
                    }

                    return this.myItem;
                }

                protected int getItemId() throws TfsException {
                    Item item = this.getItem();
                    return item != null ? item.getItemid() : -2147483648;
                }

                protected int getChangeset() throws TfsException {
                    Item item = this.getItem();
                    return item != null ? item.getCs() : -2147483648;
                }

                @NotNull
                public VcsRevisionNumber getRevisionNumber() {
                    TfsRevisionNumber var10000 = new TfsRevisionNumber(changeset);

                    return var10000;
                }

                @NotNull
                public FilePath getFile() {
                    FilePath var10000 = localPath;

                    return var10000;
                }
            };
        }
    }

    @Nullable
    public String getContent() throws VcsException {
        return new String(this.getContentAsBytes(), this.getFile().getCharset(this.myProject));
    }

    @Nullable
    public byte[] getContentAsBytes() throws VcsException {
        if (this.myContent == null) {
            try {
                this.myContent = this.loadContent();
            } catch (TfsException var2) {
                throw new VcsException(var2);
            } catch (IOException var3) {
                throw new VcsException(var3);
            }
        }

        return this.myContent;
    }

    @Nullable
    private byte[] loadContent() throws TfsException, IOException {
        int itemId = this.getItemId();
        int changeset = this.getChangeset();
        TFSContentStore store = TFSContentStoreFactory.find(this.myServer.getUri().toASCIIString(), itemId, changeset);
        if (store == null) {
            Item item = this.getItem();
            if (item == null) {
                return null;
            }

            final String downloadUrl;
            if (item.getType() == ItemType.Folder) {
                downloadUrl = MessageFormat.format("''{0}'' refers to a folder", this.getFile().getPresentableUrl());
                throw new OperationFailedException(downloadUrl);
            }

            downloadUrl = item.getDurl();
            TFSVcs.assertTrue(downloadUrl != null, "Item without download URL: " + item.getItem());
            store = TFSContentStoreFactory.create(this.myServer.getUri().toASCIIString(), itemId, changeset);
            final Ref<TfsException> exception = new Ref();
            store.saveContent(new ContentWriter() {
                public void write(OutputStream outputStream) {
                    try {
                        TFSContentRevision.this.myServer.getVCS().downloadItem(TFSContentRevision.this.myProject, downloadUrl, outputStream, TFSBundle.message("downloading.0", new Object[]{TFSContentRevision.this.getFile().getName()}));
                    } catch (TfsException var3) {
                        exception.set(var3);
                    }

                }
            });
            if (!exception.isNull()) {
                throw (TfsException)exception.get();
            }
        }

        return store.loadContent();
    }

    @NonNls
    public String toString() {
        return "TFSContentRevision [file=" + this.getFile() + ", revision=" + ((TfsRevisionNumber)this.getRevisionNumber()).getValue() + "]";
    }
}
