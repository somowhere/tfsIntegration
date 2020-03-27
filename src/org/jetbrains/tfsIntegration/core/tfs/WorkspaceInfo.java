//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.vcs.FilePath;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.vcsUtil.VcsUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfWorkingFolder;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.DeletedState;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.WorkingFolder;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.WorkingFolderType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Workspace;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.core.tfs.WorkingFolderInfo.Status;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.exceptions.WorkspaceNotFoundException;

public class WorkspaceInfo {
    private static final Collection<String> WORKSPACE_NAME_INVALID_CHARS = Arrays.asList("\"", "/", ":", "<", ">", "|", "*", "?");
    private static final Collection<String> WORKSPACE_NAME_INVALID_ENDING_CHARS = Arrays.asList(" ", ".");
    private final ServerInfo myServerInfo;
    private final String myOwnerName;
    private final String myComputer;
    private String myOriginalName;
    private String myComment;
    private Calendar myTimestamp;
    private boolean myLoaded;
    private String myModifiedName;
    @NotNull
    private WorkspaceInfo.Location myLocation;
    @Nullable
    private String myOwnerDisplayName;
    @NotNull
    private final List<String> myOwnerAliases;
    @Nullable
    private String mySecurityToken;
    private int myOptions;
    private List<WorkingFolderInfo> myWorkingFoldersInfos;

    public WorkspaceInfo(@NotNull ServerInfo serverInfo, @NotNull String owner, @NotNull String computer) {

        super();
        this.myLocation = WorkspaceInfo.Location.SERVER;
        this.myOwnerAliases = new ArrayList();
        this.myWorkingFoldersInfos = new ArrayList();
        this.myServerInfo = serverInfo;
        this.myOwnerName = owner;
        this.myComputer = computer;
        this.myTimestamp = new GregorianCalendar();
    }

    public WorkspaceInfo(@NotNull ServerInfo serverInfo, @NotNull String name, String owner, String computer, String comment, Calendar timestamp, boolean isLocal, @Nullable String ownerDisplayName, @Nullable String securityToken, int options) {
        this(serverInfo, owner, computer);
        this.myOriginalName = name;
        this.myComment = comment;
        this.myTimestamp = timestamp;
        this.myLocation = WorkspaceInfo.Location.from(isLocal);
        this.myOwnerDisplayName = ownerDisplayName;
        this.mySecurityToken = securityToken;
        this.myOptions = options;
    }

    @NotNull
    public ServerInfo getServer() {
        ServerInfo var10000 = this.myServerInfo;

        return var10000;
    }

    public String getOwnerName() {
        return this.myOwnerName;
    }

    public String getComputer() {
        return this.myComputer;
    }

    public String getName() {
        return this.myModifiedName != null ? this.myModifiedName : this.myOriginalName;
    }

    public void setName(String name) {
        this.checkCurrentOwnerAndComputer();
        this.myModifiedName = name;
    }

    @NotNull
    public WorkspaceInfo.Location getLocation() {
        WorkspaceInfo.Location var10000 = this.myLocation;

        return var10000;
    }

    public void setLocation(@NotNull WorkspaceInfo.Location location) {

        this.myLocation = location;
    }

    public boolean isLocal() {
        return WorkspaceInfo.Location.LOCAL.equals(this.myLocation);
    }

    public String getComment() {
        return this.myComment;
    }

    public void setComment(String comment) {
        this.checkCurrentOwnerAndComputer();
        this.myComment = comment;
    }

    public Calendar getTimestamp() {
        return this.myTimestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.checkCurrentOwnerAndComputer();
        this.myTimestamp = timestamp;
    }

    @Nullable
    public String getOwnerDisplayName() {
        return this.myOwnerDisplayName;
    }

    @Nullable
    public String getSecurityToken() {
        return this.mySecurityToken;
    }

    public int getOptions() {
        return this.myOptions;
    }

    @NotNull
    public List<String> getOwnerAliases() {
        List var10000 = this.myOwnerAliases;

        return var10000;
    }

    public List<WorkingFolderInfo> getWorkingFolders(Object projectOrComponent) throws TfsException {
        this.loadFromServer(projectOrComponent, false);
        return this.getWorkingFoldersCached();
    }

    public List<WorkingFolderInfo> getWorkingFoldersCached() {
        return Collections.unmodifiableList(this.myWorkingFoldersInfos);
    }

    public void loadFromServer(Object projectOrComponent, boolean force) throws TfsException {
        if (this.myOriginalName != null && !this.myLoaded && this.hasCurrentOwnerAndComputer()) {
            Workspace workspaceBean = this.getServer().getVCS().loadWorkspace(this.getName(), this.getOwnerName(), projectOrComponent, force);
            if (!this.hasCurrentOwnerAndComputer()) {
                throw new WorkspaceNotFoundException(TFSBundle.message("workspace.wrong.owner", new Object[]{this.getName(), this.getOwnerName()}));
            } else {
                fromBean(workspaceBean, this);
                this.myLoaded = true;
            }
        }
    }

    boolean hasMappingCached(FilePath localPath, boolean considerChildMappings) {
        return hasMapping(this.getWorkingFoldersCached(), localPath, considerChildMappings);
    }

    boolean hasMapping(FilePath localPath, boolean considerChildMappings, Object projectOrComponent) throws TfsException {
        return hasMapping(this.getWorkingFolders(projectOrComponent), localPath, considerChildMappings) && this.hasCurrentOwnerAndComputer();
    }

    boolean hasCurrentOwnerAndComputer() {
        String owner = this.getServer().getQualifiedUsername();
        return owner != null && this.isWorkspaceOwner(owner) && Workstation.getComputerName().equalsIgnoreCase(this.getComputer());
    }

    public boolean isWorkspaceOwner(@NotNull String owner) {

        return owner.equalsIgnoreCase(this.myOwnerName) || ContainerUtil.or(this.myOwnerAliases, (alias) -> {
            return owner.equalsIgnoreCase(alias);
        });
    }

    private void checkCurrentOwnerAndComputer() {
        if (!this.hasCurrentOwnerAndComputer()) {
            throw new IllegalStateException("Workspace " + this.getName() + " has other owner");
        }
    }

    public Collection<String> findServerPathsByLocalPath(@NotNull FilePath localPath, boolean considerChildMappings, Object projectOrComponent) throws TfsException {

        FilePath localPathOnLocalFileSystem = VcsUtil.getFilePath(localPath.getPath(), localPath.isDirectory());
        WorkingFolderInfo parentMapping = this.findNearestParentMapping(localPathOnLocalFileSystem, projectOrComponent);
        if (parentMapping != null) {
            return Collections.singletonList(parentMapping.getServerPathByLocalPath(localPathOnLocalFileSystem));
        } else if (considerChildMappings) {
            Collection<String> childMappings = new ArrayList();
            Iterator var7 = this.getWorkingFolders(projectOrComponent).iterator();

            while(var7.hasNext()) {
                WorkingFolderInfo workingFolder = (WorkingFolderInfo)var7.next();
                if (workingFolder.getLocalPath().isUnder(localPathOnLocalFileSystem, false)) {
                    childMappings.add(workingFolder.getServerPath());
                }
            }

            return childMappings;
        } else {
            return Collections.emptyList();
        }
    }

    @Nullable
    public FilePath findLocalPathByServerPath(@NotNull String serverPath, boolean isDirectory, Object projectOrComponent) throws TfsException {

        WorkingFolderInfo parentMapping = this.findNearestParentMapping(serverPath, isDirectory, projectOrComponent);
        return parentMapping != null ? parentMapping.getLocalPathByServerPath(serverPath, isDirectory) : null;
    }

    public boolean hasLocalPathForServerPath(@NotNull String serverPath, Object projectOrComponent) throws TfsException {

        return this.findLocalPathByServerPath(serverPath, false, projectOrComponent) != null;
    }

    @Nullable
    private WorkingFolderInfo findNearestParentMapping(@NotNull FilePath localPath, Object projectOrComponent) throws TfsException {

        WorkingFolderInfo mapping = null;
        Iterator var4 = this.getWorkingFolders(projectOrComponent).iterator();

        while(true) {
            WorkingFolderInfo folderInfo;
            do {
                do {
                    if (!var4.hasNext()) {
                        return mapping;
                    }

                    folderInfo = (WorkingFolderInfo)var4.next();
                } while(folderInfo.getServerPathByLocalPath(localPath) == null);
            } while(mapping != null && !folderInfo.getLocalPath().isUnder(mapping.getLocalPath(), false));

            mapping = folderInfo;
        }
    }

    @Nullable
    private WorkingFolderInfo findNearestParentMapping(@NotNull String serverPath, boolean isDirectory, Object projectOrComponent) throws TfsException {

        WorkingFolderInfo mapping = null;
        Iterator var5 = this.getWorkingFolders(projectOrComponent).iterator();

        while(true) {
            WorkingFolderInfo folderInfo;
            do {
                do {
                    if (!var5.hasNext()) {
                        return mapping;
                    }

                    folderInfo = (WorkingFolderInfo)var5.next();
                } while(folderInfo.getLocalPathByServerPath(serverPath, isDirectory) == null);
            } while(mapping != null && !VersionControlPath.isUnder(mapping.getServerPath(), folderInfo.getServerPath()));

            mapping = folderInfo;
        }
    }

    public void addWorkingFolderInfo(WorkingFolderInfo workingFolderInfo) {
        this.myWorkingFoldersInfos.add(workingFolderInfo);
    }

    public void addOwnerAlias(@NotNull String alias) {
        this.myOwnerAliases.add(alias);
    }

    public void removeWorkingFolderInfo(WorkingFolderInfo folderInfo) {
        this.checkCurrentOwnerAndComputer();
        this.myWorkingFoldersInfos.remove(folderInfo);
    }

    public void setWorkingFolders(List<WorkingFolderInfo> workingFolders) {
        this.checkCurrentOwnerAndComputer();
        this.myWorkingFoldersInfos.clear();
        this.myWorkingFoldersInfos.addAll(workingFolders);
    }

    public void saveToServer(Object projectOrComponent, WorkspaceInfo originalWorkspace) throws TfsException {
        this.checkCurrentOwnerAndComputer();
        if (this.myOriginalName != null) {
            this.getServer().getVCS().updateWorkspace(this.myOriginalName, toBean(this), projectOrComponent, true);
            this.getServer().replaceWorkspace(originalWorkspace, this);
        } else {
            this.getServer().getVCS().createWorkspace(toBean(this), projectOrComponent);
            this.getServer().addWorkspaceInfo(this);
        }

        this.myOriginalName = this.getName();
        Workstation.getInstance().update();
    }

    private static Workspace toBean(WorkspaceInfo info) {
        ArrayOfWorkingFolder folders = new ArrayOfWorkingFolder();
        List<WorkingFolderInfo> workingFolders = info.getWorkingFoldersCached();
        List<WorkingFolder> foldersList = new ArrayList(workingFolders.size());
        Iterator var4 = workingFolders.iterator();

        while(var4.hasNext()) {
            WorkingFolderInfo folderInfo = (WorkingFolderInfo)var4.next();
            foldersList.add(toBean(folderInfo));
        }

        folders.setWorkingFolder((WorkingFolder[])foldersList.toArray(new WorkingFolder[0]));
        Workspace bean = new Workspace();
        bean.setComment(info.getComment());
        bean.setComputer(info.getComputer());
        bean.setFolders(folders);
        bean.setLastAccessDate(info.getTimestamp());
        bean.setName(info.getName());
        bean.setOwner(info.getOwnerName());
        bean.setIslocal(info.isLocal());
        bean.setOwnerdisp(info.myOwnerDisplayName);
        bean.setOwnerAliases(TfsUtil.toArrayOfString(ContainerUtil.nullize(info.myOwnerAliases)));
        bean.setSecuritytoken(info.mySecurityToken);
        bean.setOptions(info.myOptions);
        return bean;
    }

    @NotNull
    private static WorkingFolder toBean(WorkingFolderInfo folderInfo) {
        WorkingFolder bean = new WorkingFolder();
        bean.setItem(folderInfo.getServerPath());
        bean.setLocal(VersionControlPath.toTfsRepresentation(folderInfo.getLocalPath()));
        bean.setType(folderInfo.getStatus() == Status.Cloaked ? WorkingFolderType.Cloak : WorkingFolderType.Map);

        return bean;
    }

    @Nullable
    private static WorkingFolderInfo fromBean(WorkingFolder bean) {
        Status status = WorkingFolderType.Cloak.equals(bean.getType()) ? Status.Cloaked : Status.Active;
        if (bean.getLocal() != null) {
            return new WorkingFolderInfo(status, VersionControlPath.getFilePath(bean.getLocal(), true), bean.getItem());
        } else {
            TFSVcs.LOG.info("null local folder mapping for " + bean.getItem());
            return null;
        }
    }

    static void fromBean(Workspace bean, WorkspaceInfo workspace) {
        workspace.myOriginalName = bean.getName();
        workspace.setLocation(WorkspaceInfo.Location.from(bean.getIslocal()));
        workspace.setComment(bean.getComment());
        workspace.setTimestamp(bean.getLastAccessDate());
        workspace.myOwnerDisplayName = bean.getOwnerdisp();
        workspace.myOwnerAliases.clear();
        if (bean.getOwnerAliases() != null) {
            ContainerUtil.addAll(workspace.myOwnerAliases, bean.getOwnerAliases().getString());
        }

        workspace.mySecurityToken = bean.getSecuritytoken();
        workspace.myOptions = bean.getOptions();
        WorkingFolder[] folders;
        if (bean.getFolders().getWorkingFolder() != null) {
            folders = bean.getFolders().getWorkingFolder();
        } else {
            folders = new WorkingFolder[0];
        }

        List<WorkingFolderInfo> workingFoldersInfos = new ArrayList(folders.length);
        WorkingFolder[] var4 = folders;
        int var5 = folders.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            WorkingFolder folderBean = var4[var6];
            WorkingFolderInfo folderInfo = fromBean(folderBean);
            if (folderInfo != null) {
                workingFoldersInfos.add(folderInfo);
            }
        }

        workspace.myWorkingFoldersInfos = workingFoldersInfos;
    }

    public WorkspaceInfo getCopy() {
        WorkspaceInfo copy = new WorkspaceInfo(this.myServerInfo, this.myOwnerName, this.myComputer);
        copy.myLocation = this.myLocation;
        copy.myComment = this.myComment;
        copy.myLoaded = this.myLoaded;
        copy.myOriginalName = this.myOriginalName;
        copy.myModifiedName = this.myModifiedName;
        copy.myTimestamp = this.myTimestamp;
        copy.myOwnerDisplayName = this.myOwnerDisplayName;
        copy.myOwnerAliases.addAll(this.myOwnerAliases);
        copy.mySecurityToken = this.mySecurityToken;
        copy.myOptions = this.myOptions;
        Iterator var2 = this.myWorkingFoldersInfos.iterator();

        while(var2.hasNext()) {
            WorkingFolderInfo workingFolder = (WorkingFolderInfo)var2.next();
            copy.myWorkingFoldersInfos.add(workingFolder.getCopy());
        }

        return copy;
    }

    public Map<FilePath, ExtendedItem> getExtendedItems2(List<ItemPath> paths, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.getServer().getVCS().getExtendedItems(this.getName(), this.getOwnerName(), TfsUtil.getLocalPaths(paths), DeletedState.Any, projectOrComponent, progressTitle);
    }

    public Map<FilePath, ExtendedItem> getExtendedItems(List<FilePath> paths, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.getServer().getVCS().getExtendedItems(this.getName(), this.getOwnerName(), paths, DeletedState.Any, projectOrComponent, progressTitle);
    }

    public static boolean isValidName(String name) {
        Iterator var1 = WORKSPACE_NAME_INVALID_CHARS.iterator();

        String invalidEnd;
        do {
            if (!var1.hasNext()) {
                var1 = WORKSPACE_NAME_INVALID_ENDING_CHARS.iterator();

                do {
                    if (!var1.hasNext()) {
                        return true;
                    }

                    invalidEnd = (String)var1.next();
                } while(!name.endsWith(invalidEnd));

                return false;
            }

            invalidEnd = (String)var1.next();
        } while(!name.contains(invalidEnd));

        return false;
    }

    private static boolean hasMapping(Collection<WorkingFolderInfo> mappings, FilePath localPath, boolean considerChildMappings) {
        FilePath localPathOnLocalFileSystem = VcsUtil.getFilePath(localPath.getPath(), localPath.isDirectory());
        Iterator var4 = mappings.iterator();

        WorkingFolderInfo mapping;
        do {
            if (!var4.hasNext()) {
                return false;
            }

            mapping = (WorkingFolderInfo)var4.next();
        } while(!localPathOnLocalFileSystem.isUnder(mapping.getLocalPath(), false) && (!considerChildMappings || !mapping.getLocalPath().isUnder(localPathOnLocalFileSystem, false)));

        return true;
    }

    public String toString() {
        return "WorkspaceInfo[server=" + this.getServer().getUri() + ",name=" + this.getName() + ",owner=" + this.getOwnerName() + ",computer=" + this.getComputer() + ",comment=" + this.getComment() + "]";
    }

    public static enum Location {
        LOCAL,
        SERVER;

        private Location() {
        }

        @NotNull
        public static WorkspaceInfo.Location from(boolean isLocal) {
            WorkspaceInfo.Location var10000 = isLocal ? LOCAL : SERVER;
            return var10000;
        }
    }
}
