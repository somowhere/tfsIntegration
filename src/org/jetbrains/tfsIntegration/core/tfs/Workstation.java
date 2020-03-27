//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.util.Function;
import com.intellij.util.Functions;
import com.intellij.util.JdomKt;
import com.intellij.util.containers.ContainerUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.SAXParserFactory;
import org.apache.axis2.databinding.utils.ConverterUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.config.TfsServerConnectionHelper;
import org.jetbrains.tfsIntegration.core.TfsSdkManager;
import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
import org.jetbrains.tfsIntegration.exceptions.DuplicateMappingException;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.exceptions.WorkspaceHasNoMappingException;
import org.xml.sax.InputSource;

public class Workstation {
    public static boolean PRESERVE_CONFIG_FILE = false;
    private static final Logger LOG = Logger.getInstance(Workstation.class.getName());
    @NotNull
    private final List<ServerInfo> myServerInfos;
    @Nullable
    private Ref<FilePath> myDuplicateMappedPath;
    private static String ourComputerName;

    private Workstation() {
        this.myServerInfos = loadCache();
    }

    @NotNull
    public static Workstation getInstance() {
        Workstation var10000 = Workstation.WorkstationHolder.ourInstance;
        return var10000;
    }

    @NotNull
    public List<ServerInfo> getServers() {
        List var10000 = Collections.unmodifiableList(this.myServerInfos);

        return var10000;
    }

    @Nullable
    public ServerInfo getServer(@NotNull URI uri) {

        return (ServerInfo)ContainerUtil.find(this.getServers(), (serverInfo) -> {
            return serverInfo.getUri().equals(uri);
        });
    }

    @NotNull
    private List<WorkspaceInfo> getAllWorkspacesForCurrentOwnerAndComputer(boolean showLoginIfNoCredentials) {
        List<WorkspaceInfo> result = new ArrayList();
        Iterator var3 = this.getServers().iterator();

        while(true) {
            ServerInfo server;
            while(true) {
                if (!var3.hasNext()) {

                    return result;
                }

                server = (ServerInfo)var3.next();
                if (!showLoginIfNoCredentials || server.getQualifiedUsername() != null) {
                    break;
                }

                try {
                    TfsServerConnectionHelper.ensureAuthenticated((Object)null, server.getUri(), false);
                    break;
                } catch (TfsException var6) {
                }
            }

            result.addAll(server.getWorkspacesForCurrentOwnerAndComputer());
        }
    }

    @NotNull
    private static List<ServerInfo> loadCache() {
        Path cacheFile = getCacheFile(true);
        if (cacheFile != null) {
            label106: {
                List var16;
                try {
                    WorkstationCacheReader reader = new WorkstationCacheReader();
                    BufferedReader stream = Files.newBufferedReader(cacheFile);
                    Throwable var3 = null;

                    try {
                        TfsUtil.forcePluginClassLoader(() -> {
                            SAXParserFactory.newInstance().newSAXParser().parse(new InputSource(stream), reader);
                        });
                    } catch (Throwable var13) {
                        var3 = var13;
                        throw var13;
                    } finally {
                        if (stream != null) {
                            if (var3 != null) {
                                try {
                                    stream.close();
                                } catch (Throwable var12) {
                                    var3.addSuppressed(var12);
                                }
                            } else {
                                stream.close();
                            }
                        }

                    }

                    var16 = reader.getServers();
                } catch (Exception var15) {
                    LOG.info("Cannot read workspace cache", var15);
                    break label106;
                }


                return var16;
            }
        }

        ArrayList var10000 = new ArrayList();

        return var10000;
    }

    @Nullable
    private static Path getCacheFile(boolean existingOnly) {
        if (PRESERVE_CONFIG_FILE) {
            return null;
        } else {
            Path cacheFile = TfsSdkManager.getInstance().getCacheFile();
            return !Files.exists(cacheFile, new LinkOption[0]) && existingOnly ? null : cacheFile;
        }
    }

    void update() {
        this.invalidateDuplicateMappedPath();
        Path cacheFile = getCacheFile(false);
        if (cacheFile != null) {
            try {
                Element serversElement = new Element("Servers");
                Iterator var3 = this.getServers().iterator();

                while(var3.hasNext()) {
                    ServerInfo serverInfo = (ServerInfo)var3.next();
                    Element serverInfoElement = (new Element("ServerInfo")).setAttribute("uri", serverInfo.getUri().toString()).setAttribute("repositoryGuid", serverInfo.getGuid());
                    serversElement.addContent(serverInfoElement);
                    Iterator var6 = serverInfo.getWorkspaces().iterator();

                    while(var6.hasNext()) {
                        WorkspaceInfo workspaceInfo = (WorkspaceInfo)var6.next();
                        Element workspaceInfoElement = (new Element("WorkspaceInfo")).setAttribute("computer", workspaceInfo.getComputer()).setAttribute("ownerName", workspaceInfo.getOwnerName()).setAttribute("LastSavedCheckinTimeStamp", ConverterUtil.convertToString(workspaceInfo.getTimestamp())).setAttribute("name", workspaceInfo.getName()).setAttribute("isLocalWorkspace", String.valueOf(workspaceInfo.isLocal())).setAttribute("options", String.valueOf(workspaceInfo.getOptions())).setAttribute("comment", StringUtil.notNullize(workspaceInfo.getComment())).setAttribute("ownerDisplayName", StringUtil.notNullize(workspaceInfo.getOwnerDisplayName()));
                        setIfNotNull(workspaceInfoElement, "securityToken", workspaceInfo.getSecurityToken());
                        addItems(workspaceInfoElement, "MappedPaths", "MappedPath", "path", workspaceInfo.getWorkingFoldersCached(), (folderInfo) -> {
                            return folderInfo.getLocalPath().getPresentableUrl();
                        });
                        addItems(workspaceInfoElement, "OwnerAliases", "OwnerAlias", "OwnerAlias", workspaceInfo.getOwnerAliases(), Functions.TO_STRING());
                        serverInfoElement.addContent(workspaceInfoElement);
                    }
                }

                JdomKt.write((new Element("VersionControlServer")).addContent(serversElement), cacheFile);
            } catch (IOException var9) {
                LOG.info("Cannot update workspace cache", var9);
            }
        }

    }

    private static <T> void addItems(@NotNull Element parentElement, @NotNull String elementName, @NotNull String itemElementName, @NotNull String itemAttributeName, @NotNull List<T> items, @NotNull Function<T, String> valueProvider) {
        Element element = new Element(elementName);
        parentElement.addContent(element);
        Iterator var7 = items.iterator();

        while(var7.hasNext()) {
            T item = (T) var7.next();
            element.addContent((new Element(itemElementName)).setAttribute(itemAttributeName, StringUtil.notNullize((String)valueProvider.fun(item))));
        }

    }

    private static void setIfNotNull(@NotNull Element element, @NotNull String attributeName, @Nullable String value) {
        if (value != null) {
            element.setAttribute(attributeName, value);
        }

    }

    public void addServer(ServerInfo serverInfo) {
        this.myServerInfos.add(serverInfo);
        this.update();
    }

    public void removeServer(ServerInfo serverInfo) {
        this.myServerInfos.remove(serverInfo);
        TFSConfigurationManager.getInstance().remove(serverInfo.getUri());
        this.update();
    }

    public static synchronized String getComputerName() {
        if (ourComputerName == null) {
            try {
                InetAddress address = InetAddress.getLocalHost();
                String hostName = address.getHostName();
                int i = hostName.indexOf(46);
                if (i != -1) {
                    hostName = hostName.substring(0, i);
                }

                ourComputerName = hostName;
            } catch (UnknownHostException var3) {
                throw new RuntimeException("Cannot retrieve host name.");
            }
        }

        return ourComputerName;
    }

    @NotNull
    public Collection<WorkspaceInfo> findWorkspacesCached(@NotNull FilePath localPath, boolean considerChildMappings) {
        Collection<WorkspaceInfo> result = new ArrayList();
        Iterator var4 = this.getAllWorkspacesForCurrentOwnerAndComputer(false).iterator();

        while(var4.hasNext()) {
            WorkspaceInfo workspace = (WorkspaceInfo)var4.next();
            if (workspace.hasMappingCached(localPath, considerChildMappings)) {
                result.add(workspace);
                if (!considerChildMappings) {
                    break;
                }
            }
        }

        return result;
    }

    @NotNull
    public Collection<WorkspaceInfo> findWorkspaces(@NotNull FilePath localPath, boolean considerChildMappings, Object projectOrComponent) throws TfsException {

        this.checkDuplicateMappings();
        Collection<WorkspaceInfo> resultCached = this.findWorkspacesCached(localPath, considerChildMappings);
        if (!resultCached.isEmpty()) {
            Iterator var11 = resultCached.iterator();

            WorkspaceInfo workspace;
            do {
                if (!var11.hasNext()) {

                    return resultCached;
                }

                workspace = (WorkspaceInfo)var11.next();
            } while(workspace.hasMapping(localPath, considerChildMappings, projectOrComponent));

            throw new WorkspaceHasNoMappingException(workspace);
        } else {
            Collection<WorkspaceInfo> result = new ArrayList();
            Collection<ServerInfo> serversToSkip = new ArrayList();
            Iterator var7 = this.getAllWorkspacesForCurrentOwnerAndComputer(true).iterator();

            while(true) {
                WorkspaceInfo workspace;
                do {
                    if (!var7.hasNext()) {
                        return result;
                    }

                    workspace = (WorkspaceInfo)var7.next();
                } while(serversToSkip.contains(workspace.getServer()));

                try {
                    if (workspace.hasMapping(localPath, considerChildMappings, projectOrComponent)) {
                        result.add(workspace);
                        if (!considerChildMappings) {
                            return result;
                        }
                    }
                } catch (TfsException var10) {
                    serversToSkip.add(workspace.getServer());
                }
            }


        }
    }

    public void checkDuplicateMappings() throws DuplicateMappingException {
        if (this.myDuplicateMappedPath == null) {
            this.myDuplicateMappedPath = Ref.create(this.findDuplicateMappedPath());
        }

        if (!this.myDuplicateMappedPath.isNull()) {
            throw new DuplicateMappingException((FilePath)this.myDuplicateMappedPath.get());
        }
    }

    private void invalidateDuplicateMappedPath() {
        this.myDuplicateMappedPath = null;
    }

    @Nullable
    private FilePath findDuplicateMappedPath() {
        Collection<FilePath> otherServersPaths = new ArrayList();
        Iterator var2 = this.getServers().iterator();

        while(var2.hasNext()) {
            ServerInfo server = (ServerInfo)var2.next();
            Collection<FilePath> currentServerPaths = new ArrayList();
            Iterator var5 = server.getWorkspacesForCurrentOwnerAndComputer().iterator();

            while(var5.hasNext()) {
                WorkspaceInfo workspace = (WorkspaceInfo)var5.next();
                Iterator var7 = workspace.getWorkingFoldersCached().iterator();

                while(var7.hasNext()) {
                    WorkingFolderInfo workingFolder = (WorkingFolderInfo)var7.next();
                    FilePath currentServerPath = workingFolder.getLocalPath();
                    Iterator var10 = otherServersPaths.iterator();

                    while(var10.hasNext()) {
                        FilePath otherServerPath = (FilePath)var10.next();
                        if (currentServerPath.isUnder(otherServerPath, false)) {
                            return currentServerPath;
                        }

                        if (otherServerPath.isUnder(currentServerPath, false)) {
                            return otherServerPath;
                        }
                    }

                    currentServerPaths.add(currentServerPath);
                }
            }

            otherServersPaths.addAll(currentServerPaths);
        }

        return null;
    }

    private static class WorkstationHolder {
        private static final Workstation ourInstance = new Workstation();

        private WorkstationHolder() {
        }
    }
}
