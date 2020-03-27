//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.diagnostic.Logger;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Workspace;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TfsBeansHolder;
import org.jetbrains.tfsIntegration.core.configuration.Credentials;
import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.exceptions.WorkspaceNotFoundException;

public class ServerInfo {
    private static final Logger LOG = Logger.getInstance(ServerInfo.class.getName());
    private final URI myUri;
    private final String myGuid;
    private VersionControlServer myServer;
    private final List<WorkspaceInfo> myWorkspaceInfos;
    private final TfsBeansHolder myBeans;

    public ServerInfo(URI uri, String guid, TfsBeansHolder beans) {
        this.myWorkspaceInfos = new ArrayList();
        this.myUri = uri;
        this.myGuid = guid;
        this.myBeans = beans;
    }

    public ServerInfo(URI uri, String guid, @Nullable Workspace[] workspaces, String authorizedUsername, TfsBeansHolder beans) {
        this(uri, guid, beans);
        if (workspaces != null) {
            Workspace[] var6 = workspaces;
            int var7 = workspaces.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                Workspace workspace = var6[var8];
                WorkspaceInfo workspaceInfo = new WorkspaceInfo(this, authorizedUsername, Workstation.getComputerName());
                WorkspaceInfo.fromBean(workspace, workspaceInfo);
                this.addWorkspaceInfo(workspaceInfo);
            }
        }

    }

    public void addWorkspaceInfo(@NotNull WorkspaceInfo workspaceInfo) {
        this.myWorkspaceInfos.add(workspaceInfo);
    }

    public URI getUri() {
        return this.myUri;
    }

    public String getPresentableUri() {
        return TfsUtil.getPresentableUri(this.myUri);
    }

    public String getGuid() {
        return this.myGuid;
    }

    @Nullable
    public String getQualifiedUsername() {
        Credentials credentials = TFSConfigurationManager.getInstance().getCredentials(this.getUri());
        return credentials != null ? credentials.getQualifiedUsername() : null;
    }

    public List<WorkspaceInfo> getWorkspacesForCurrentOwnerAndComputer() {
        List<WorkspaceInfo> result = new ArrayList();
        List<WorkspaceInfo> workspaces = this.getWorkspaces();
        Iterator var3 = workspaces.iterator();

        while(var3.hasNext()) {
            WorkspaceInfo workspaceInfo = (WorkspaceInfo)var3.next();
            if (workspaceInfo.hasCurrentOwnerAndComputer()) {
                result.add(workspaceInfo);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<WorkspaceInfo> getWorkspaces() {
        return Collections.unmodifiableList(this.myWorkspaceInfos);
    }

    public void deleteWorkspace(WorkspaceInfo workspaceInfo, Object projectOrComponent, boolean force) throws TfsException {
        try {
            this.getVCS().deleteWorkspace(workspaceInfo.getName(), workspaceInfo.getOwnerName(), projectOrComponent, force);
        } catch (WorkspaceNotFoundException var5) {
        }

        this.myWorkspaceInfos.remove(workspaceInfo);
        Workstation.getInstance().update();
    }

    @NotNull
    public VersionControlServer getVCS() {
        if (this.myServer == null) {
            this.myServer = new VersionControlServer(this.myUri, this.myBeans, this.myGuid);
        }

        VersionControlServer var10000 = this.myServer;

        return var10000;
    }

    public void refreshWorkspacesForCurrentOwnerAndComputer(Object projectOrComponent, boolean force) throws TfsException {
        Workspace[] newWorkspaces = this.getVCS().queryWorkspaces(Workstation.getComputerName(), projectOrComponent, force);
        String owner = this.getQualifiedUsername();
        LOG.assertTrue(owner != null);
        Iterator i = this.myWorkspaceInfos.iterator();

        while(i.hasNext()) {
            WorkspaceInfo workspaceInfo = (WorkspaceInfo)i.next();
            if (workspaceInfo.hasCurrentOwnerAndComputer()) {
                i.remove();
            }
        }

        Workspace[] var10 = newWorkspaces;
        int var11 = newWorkspaces.length;

        for(int var7 = 0; var7 < var11; ++var7) {
            Workspace workspace = var10[var7];
            WorkspaceInfo workspaceInfo = new WorkspaceInfo(this, owner, Workstation.getComputerName());
            WorkspaceInfo.fromBean(workspace, workspaceInfo);
            this.addWorkspaceInfo(workspaceInfo);
        }

        Workstation.getInstance().update();
    }

    public void replaceWorkspace(@NotNull WorkspaceInfo existingWorkspace, @NotNull WorkspaceInfo newWorkspace) {

        this.myWorkspaceInfos.set(this.myWorkspaceInfos.indexOf(existingWorkspace), newWorkspace);
    }

    public String toString() {
        return "ServerInfo[uri=" + this.getUri() + ",guid=" + this.getGuid() + "," + this.getWorkspaces().size() + " workspaces]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ServerInfo that = (ServerInfo)o;
            return this.myGuid.equals(that.myGuid);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.myGuid.hashCode();
    }
}
