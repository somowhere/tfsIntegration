//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.config;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Trinity;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ThrowableConvertor;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.FrameworkRegistrationEntry;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.GetRegistrationEntries;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.GetRegistrationEntriesResponse;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.RegistrationExtendedAttribute2;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.RegistrationStub;
import com.microsoft.schemas.teamfoundation._2005._06.services.serverstatus._03.CheckAuthentication;
import com.microsoft.schemas.teamfoundation._2005._06.services.serverstatus._03.CheckAuthenticationResponse;
import com.microsoft.schemas.teamfoundation._2005._06.services.serverstatus._03.ServerStatusStub;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryWorkspaces;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Workspace;
import com.microsoft.webservices.ArrayOfGuid;
import com.microsoft.webservices.ArrayOfKeyValueOfStringString;
import com.microsoft.webservices.ArrayOfServiceTypeFilter;
import com.microsoft.webservices.ArrayOfString;
import com.microsoft.webservices.CatalogResource;
import com.microsoft.webservices.CatalogWebServiceStub;
import com.microsoft.webservices.Connect;
import com.microsoft.webservices.ConnectResponse;
import com.microsoft.webservices.ConnectionData;
import com.microsoft.webservices.KeyValueOfStringString;
import com.microsoft.webservices.LocationWebServiceStub;
import com.microsoft.webservices.QueryNodes;
import com.microsoft.webservices.QueryNodesResponse;
import com.microsoft.webservices.QueryResources;
import com.microsoft.webservices.QueryResourcesResponse;
import com.microsoft.webservices.ServiceDefinition;
import com.microsoft.webservices.ServiceTypeFilter;
import com.microsoft.wsdl.types.Guid;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.JComponent;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.TFSConstants;
import org.jetbrains.tfsIntegration.core.TfsBeansHolder;
import org.jetbrains.tfsIntegration.core.configuration.Credentials;
import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
import org.jetbrains.tfsIntegration.core.configuration.Credentials.Type;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.core.tfs.Workstation;
import org.jetbrains.tfsIntegration.exceptions.HostNotApplicableException;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.exceptions.TfsExceptionManager;
import org.jetbrains.tfsIntegration.exceptions.UserCancelledException;
import org.jetbrains.tfsIntegration.ui.ChooseTeamProjectCollectionDialog;
import org.jetbrains.tfsIntegration.webservice.TfsRequestManager;
import org.jetbrains.tfsIntegration.webservice.WebServiceHelper;
import org.jetbrains.tfsIntegration.webservice.TfsRequestManager.Request;

public class TfsServerConnectionHelper {
    private static final Logger LOG = Logger.getInstance(TfsServerConnectionHelper.class.getName());

    public TfsServerConnectionHelper() {
    }

    public static void ensureAuthenticated(Object projectOrComponent, URI serverUri, boolean force) throws TfsException {
        TfsRequestManager.executeRequest(serverUri, projectOrComponent, force, new Request<Void>((String)null) {
            public Void execute(Credentials credentials, URI serverUri, ProgressIndicator pi) throws Exception {
                TfsServerConnectionHelper.connect(serverUri, credentials, true, pi);
                return null;
            }

            @NotNull
            public String getProgressTitle(Credentials credentials, URI serverUri) {
                String var10000 = TFSBundle.message("connect.to", new Object[]{TfsUtil.getPresentableUri(serverUri)});
                return var10000;
            }
        });
    }

    @Nullable
    public static TfsServerConnectionHelper.AddServerResult addServer(JComponent parentComponent) {
        Request connectRequest = new Request<Trinity<URI, TfsServerConnectionHelper.ServerDescriptor, Credentials>>((String)null) {
            public Trinity<URI, TfsServerConnectionHelper.ServerDescriptor, Credentials> execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                TfsServerConnectionHelper.ServerDescriptor serverDescriptor = TfsServerConnectionHelper.connect(serverUri, credentials, false, pi);
                serverUri = serverDescriptor.uri;
                if (serverDescriptor instanceof TfsServerConnectionHelper.Tfs200xServerDescriptor && TFSConfigurationManager.getInstance().serverKnown(((TfsServerConnectionHelper.Tfs200xServerDescriptor)serverDescriptor).instanceId)) {
                    throw new TfsException(TFSBundle.message("duplicate.server", new Object[0]));
                } else if (credentials.getType() != Type.NtlmNative && !credentials.getUserName().equalsIgnoreCase(serverDescriptor.getUserName())) {
                    TfsServerConnectionHelper.LOG.warn("authorized user mismatch: current=" + credentials.getQualifiedUsername() + ", authorized: " + serverDescriptor.authorizedCredentials);
                    throw new TfsException(TFSBundle.message("authorized.user.mismatch", new Object[0]));
                } else {
                    if (serverDescriptor instanceof TfsServerConnectionHelper.Tfs2010ServerDescriptor) {
                        Collection<TfsServerConnectionHelper.TeamProjectCollectionDescriptor> teamProjectCollections = ((TfsServerConnectionHelper.Tfs2010ServerDescriptor)serverDescriptor).teamProjectCollections;
                        if (teamProjectCollections.size() == 0) {
                            throw new TfsException(TFSBundle.message("no.team.project.collections", new Object[0]));
                        }

                        boolean newCollection = false;
                        Iterator var7 = teamProjectCollections.iterator();

                        while(var7.hasNext()) {
                            TfsServerConnectionHelper.TeamProjectCollectionDescriptor teamProjectCollection = (TfsServerConnectionHelper.TeamProjectCollectionDescriptor)var7.next();
                            if (!TFSConfigurationManager.getInstance().serverKnown(teamProjectCollection.instanceId)) {
                                newCollection = true;
                                break;
                            }
                        }

                        if (!newCollection) {
                            throw new TfsException(TFSBundle.message("all.team.project.collections.duplicate", new Object[0]));
                        }
                    }

                    return Trinity.create(serverUri, serverDescriptor, credentials);
                }
            }

            @NotNull
            public String getProgressTitle(Credentials credentials, URI serverUri) {
                String var10000 = TFSBundle.message("connect.to", new Object[]{TfsUtil.getPresentableUri(serverUri)});

                return var10000;
            }

            public boolean retrieveAuthorizedCredentials() {
                return false;
            }
        };

        Trinity result;
        try {
            result = (Trinity)TfsRequestManager.getInstance((URI)null).executeRequestInForeground(parentComponent, true, (Credentials)null, true, connectRequest);
        } catch (UserCancelledException var14) {
            return null;
        } catch (TfsException var15) {
            LOG.error(var15);
            return null;
        }

        final TfsServerConnectionHelper.ServerDescriptor serverDescriptor = (TfsServerConnectionHelper.ServerDescriptor)result.second;
        if (serverDescriptor instanceof TfsServerConnectionHelper.Tfs200xServerDescriptor) {
            TfsServerConnectionHelper.Tfs200xServerDescriptor tfs200xServerDescriptor = (TfsServerConnectionHelper.Tfs200xServerDescriptor)serverDescriptor;
            return new TfsServerConnectionHelper.AddServerResult((URI)result.first, tfs200xServerDescriptor.instanceId, serverDescriptor.authorizedCredentials, tfs200xServerDescriptor.workspaces, (String)null, serverDescriptor.beans);
        } else {
            Collection<TfsServerConnectionHelper.TeamProjectCollectionDescriptor> teamProjectCollections = ((TfsServerConnectionHelper.Tfs2010ServerDescriptor)serverDescriptor).teamProjectCollections;
            TfsServerConnectionHelper.TeamProjectCollectionDescriptor collection;
            if (teamProjectCollections.size() == 1) {
                collection = (TfsServerConnectionHelper.TeamProjectCollectionDescriptor)teamProjectCollections.iterator().next();
            } else {
                ChooseTeamProjectCollectionDialog d2 = new ChooseTeamProjectCollectionDialog(parentComponent, ((URI)result.first).toString(), teamProjectCollections);
                if (!d2.showAndGet()) {
                    return null;
                }

                collection = d2.getSelectedItem();
                LOG.assertTrue(collection != null);
            }

            URI collectionUri = getCollectionUri((URI)result.first, collection);
            final TfsBeansHolder collectionBeans = new TfsBeansHolder(collectionUri);
            Request<Workspace[]> loadWorkspacesRequest = new Request<Workspace[]>((String)null) {
                public Workspace[] execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                    return TfsServerConnectionHelper.queryWorkspaces(serverDescriptor.authorizedCredentials, pi, collectionBeans);
                }

                @NotNull
                public String getProgressTitle(Credentials credentials, URI serverUri) {
                    String var10000 = TFSBundle.message("connect.to", new Object[]{TfsUtil.getPresentableUri(serverUri)});

                    return var10000;
                }

                public boolean retrieveAuthorizedCredentials() {
                    return false;
                }
            };
            Workspace[] workspaces = null;
            String workspacesLoadError = null;

            try {
                workspaces = (Workspace[])TfsRequestManager.getInstance(collectionUri).executeRequestInForeground(parentComponent, false, (Credentials)result.third, true, loadWorkspacesRequest);
            } catch (UserCancelledException var12) {
                return null;
            } catch (TfsException var13) {
                workspacesLoadError = var13.getMessage();
            }

            return new TfsServerConnectionHelper.AddServerResult(collectionUri, collection.instanceId, serverDescriptor.authorizedCredentials, workspaces, workspacesLoadError, collectionBeans);
        }
    }

    private static URI getCollectionUri(URI serverUri, TfsServerConnectionHelper.TeamProjectCollectionDescriptor collection) {
        String path = serverUri.getPath();
        if (!path.endsWith("/")) {
            path = path + "/";
        }

        try {
            if (!(collection.name + ".visualstudio.com").equals(serverUri.getHost())) {
                path = path + collection.name;
            }

            return new URI(serverUri.getScheme(), serverUri.getUserInfo(), serverUri.getHost(), serverUri.getPort(), path, (String)null, (String)null);
        } catch (URISyntaxException var4) {
            LOG.error(var4);
            return null;
        }
    }

    public static TfsServerConnectionHelper.ServerDescriptor connect(URI uri, Credentials credentials, boolean justAuthenticate, @Nullable ProgressIndicator pi) throws RemoteException, TfsException {
        if (pi != null) {
            pi.setText(TFSBundle.message("connecting.to.server", new Object[0]));
        }

        if (justAuthenticate) {
            uri = getBareUri(uri);
        }

        ConfigurationContext context = WebServiceHelper.getStubConfigurationContext();
        Pair registrationEntries = null;

        Pair connectResponse;
        try {
            connectResponse = tryDifferentUris(uri, !justAuthenticate, (uri12) -> {
                LocationWebServiceStub locationService = new LocationWebServiceStub(context, TfsUtil.appendPath(uri12, "TeamFoundation/Administration/v3.0/LocationService.asmx"));
                WebServiceHelper.setupStub(locationService, credentials, uri12);
                Connect connectParam = new Connect();
                connectParam.setConnectOptions(1);
                ArrayOfServiceTypeFilter serviceTypeFilters = new ArrayOfServiceTypeFilter();
                ServiceTypeFilter filter = new ServiceTypeFilter();
                filter.setServiceType("*");
                filter.setIdentifier(TFSConstants.FRAMEWORK_SERVER_DATA_PROVIDER_FILTER_GUID);
                serviceTypeFilters.addServiceTypeFilter(filter);
                connectParam.setServiceTypeFilters(serviceTypeFilters);
                connectParam.setLastChangeId(-1);
                return locationService.connect(connectParam);
            });
        } catch (RemoteException var31) {
            LOG.info("Failed to connect to " + uri + ". Trying to use legacy Registration service.", var31);
            connectResponse = null;
            ThrowableConvertor<URI, FrameworkRegistrationEntry[], RemoteException> c = (uri1) -> {
                RegistrationStub registrationStub = new RegistrationStub(context, TfsUtil.appendPath(uri1, "Services/v1.0/Registration.asmx"));
                WebServiceHelper.setupStub(registrationStub, credentials, uri1);
                GetRegistrationEntries getRegistrationEntriesParam = new GetRegistrationEntries();
                getRegistrationEntriesParam.setToolId("vstfs");
                GetRegistrationEntriesResponse registrationEntries1 = registrationStub.getRegistrationEntries(getRegistrationEntriesParam);
                return registrationEntries1.getGetRegistrationEntriesResult().getRegistrationEntry();
            };
            registrationEntries = tryDifferentUris(uri, !justAuthenticate, c);
        }

        if (connectResponse != null) {
            uri = (URI)connectResponse.first;
            ConnectionData connectResult = ((ConnectResponse)connectResponse.second).getConnectResult();
            ArrayOfKeyValueOfStringString userProps = connectResult.getAuthorizedUser().getAttributes();
            String domain = getPropertyValue(userProps, "Domain");
            String userName = getPropertyValue(userProps, "Account");
            Credentials authorizedCredentials = new Credentials(userName, domain, credentials.getPassword(), credentials.isStorePassword(), credentials.getType());
            if (justAuthenticate) {
                return new TfsServerConnectionHelper.ServerDescriptor(authorizedCredentials, uri, (TfsBeansHolder)null);
            } else {
                if (pi != null) {
                    pi.setText(TFSBundle.message("loading.team.project.collections", new Object[0]));
                }

                ServiceDefinition[] serviceDefinitions = connectResult.getLocationServiceData().getServiceDefinitions().getServiceDefinition();
                if (serviceDefinitions == null) {
                    LOG.warn("service definitions node is null");
                    throw new HostNotApplicableException((AxisFault)null);
                } else {
                    String catalogServicePath = null;
                    ServiceDefinition[] var45 = serviceDefinitions;
                    int var47 = serviceDefinitions.length;

                    for(int var16 = 0; var16 < var47; ++var16) {
                        ServiceDefinition serviceDefinition = var45[var16];
                        if ("c2f9106f-127a-45b7-b0a3-e0ad8239a2a7".equalsIgnoreCase(serviceDefinition.getIdentifier().getGuid())) {
                            catalogServicePath = serviceDefinition.getRelativePath();
                        }
                    }

                    if (catalogServicePath == null) {
                        LOG.warn("catalog service not found by guid");
                        throw new HostNotApplicableException((AxisFault)null);
                    } else {
                        Guid catalogResourceId = connectResult.getCatalogResourceId();
                        CatalogWebServiceStub catalogService = new CatalogWebServiceStub(context, TfsUtil.appendPath(uri, catalogServicePath));
                        WebServiceHelper.setupStub(catalogService, credentials, uri);
                        QueryResources queryResourcesParam = new QueryResources();
                        ArrayOfGuid resourceIdentitiers = new ArrayOfGuid();
                        resourceIdentitiers.addGuid(catalogResourceId);
                        queryResourcesParam.setResourceIdentifiers(resourceIdentitiers);
                        QueryResourcesResponse queryResourcesResponse = catalogService.queryResources(queryResourcesParam);
                        String referencePath = null;
                        CatalogResource[] var20 = queryResourcesResponse.getQueryResourcesResult().getCatalogResources().getCatalogResource();
                        int var21 = var20.length;

                        for(int var22 = 0; var22 < var21; ++var22) {
                            CatalogResource catalogResource = var20[var22];
                            if (catalogResource.getIdentifier().getGuid().equals(catalogResourceId.getGuid())) {
                                referencePath = catalogResource.getNodeReferencePaths().getString()[0];
                                break;
                            }
                        }

                        if (referencePath == null) {
                            throw new HostNotApplicableException((AxisFault)null);
                        } else {
                            QueryNodes queryNodesParam = new QueryNodes();
                            ArrayOfString pathSpecs = new ArrayOfString();
                            pathSpecs.addString(referencePath + "*");
                            queryNodesParam.setPathSpecs(pathSpecs);
                            ArrayOfGuid resourceTypeFilters = new ArrayOfGuid();
                            resourceTypeFilters.addGuid(TFSConstants.PROJECT_COLLECTION_GUID);
                            queryNodesParam.setResourceTypeFilters(resourceTypeFilters);
                            QueryNodesResponse queryNodesResponse = catalogService.queryNodes(queryNodesParam);
                            CatalogResource[] teamProjectCollections = queryNodesResponse.getQueryNodesResult().getCatalogResources().getCatalogResource();
                            List<TfsServerConnectionHelper.TeamProjectCollectionDescriptor> descriptors = new ArrayList(teamProjectCollections.length);
                            CatalogResource[] var26 = teamProjectCollections;
                            int var27 = teamProjectCollections.length;

                            for(int var28 = 0; var28 < var27; ++var28) {
                                CatalogResource collectionNode = var26[var28];
                                String instanceId = getPropertyValue(collectionNode.getProperties(), "InstanceId");
                                if (instanceId == null) {
                                    throw new HostNotApplicableException((AxisFault)null);
                                }

                                descriptors.add(new TfsServerConnectionHelper.TeamProjectCollectionDescriptor(collectionNode.getDisplayName(), instanceId));
                            }

                            TfsBeansHolder beans = new TfsBeansHolder(uri);
                            return new TfsServerConnectionHelper.Tfs2010ServerDescriptor(descriptors, authorizedCredentials, uri, beans);
                        }
                    }
                }
            }
        } else {
            LOG.assertTrue(registrationEntries != null);
            uri = (URI)registrationEntries.first;
            String instanceId;
            if (justAuthenticate) {
                instanceId = getAuthorizedCredentialsFor200x(context, uri, credentials);
                Credentials authorizedCredentials = new Credentials(instanceId, credentials.getPassword(), credentials.isStorePassword(), credentials.getType());
                return new TfsServerConnectionHelper.ServerDescriptor(authorizedCredentials, uri, (TfsBeansHolder)null);
            } else {
                instanceId = null;
                if (registrationEntries.second != null) {
                    FrameworkRegistrationEntry[] var33 = (FrameworkRegistrationEntry[])registrationEntries.second;
                    int var9 = var33.length;

                    label118:
                    for(int var10 = 0; var10 < var9; ++var10) {
                        FrameworkRegistrationEntry entry = var33[var10];
                        if ("vstfs".equals(entry.getType())) {
                            RegistrationExtendedAttribute2[] var12 = entry.getRegistrationExtendedAttributes().getRegistrationExtendedAttribute();
                            int var13 = var12.length;

                            for(int var14 = 0; var14 < var13; ++var14) {
                                RegistrationExtendedAttribute2 attribute = var12[var14];
                                if ("InstanceId".equals(attribute.getName())) {
                                    instanceId = attribute.getValue();
                                    break label118;
                                }
                            }
                        }
                    }
                }

                if (instanceId == null) {
                    throw new HostNotApplicableException((AxisFault)null);
                } else {
                    String qName = getAuthorizedCredentialsFor200x(context, uri, credentials);
                    Credentials authorizedCredentials = new Credentials(qName, credentials.getPassword(), credentials.isStorePassword(), credentials.getType());
                    TfsBeansHolder beans = new TfsBeansHolder(uri);
                    Workspace[] workspaces = queryWorkspaces(authorizedCredentials, pi, beans);
                    return new TfsServerConnectionHelper.Tfs200xServerDescriptor(instanceId, authorizedCredentials, uri, workspaces, beans);
                }
            }
        }
    }

    private static RemoteException getMoreDescriptiveException(RemoteException first, RemoteException second) {
        if (first instanceof AxisFault && second instanceof AxisFault) {
            if (((AxisFault)first).getFaultDetailElement() == null && ((AxisFault)second).getFaultDetailElement() != null) {
                return second;
            }

            int transportErrorCode1 = TfsExceptionManager.getTransportErrorCode((AxisFault)first);
            int transportErrorCode2 = TfsExceptionManager.getTransportErrorCode((AxisFault)second);
            if (transportErrorCode1 == 404 && transportErrorCode2 != -1) {
                return second;
            }
        }

        return first;
    }

    private static URI getBareUri(URI uri) {
        String path = uri.getPath();
        if (StringUtil.isNotEmpty(path) && !path.equals("/")) {
            path = path.substring(0, path.lastIndexOf("/"));

            try {
                uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), path, (String)null, (String)null);
            } catch (URISyntaxException var3) {
                LOG.error(var3);
            }
        }

        return uri;
    }

    private static Workspace[] queryWorkspaces(Credentials authorizedCredentials, @Nullable ProgressIndicator pi, TfsBeansHolder beans) throws RemoteException, HostNotApplicableException {
        if (pi != null) {
            pi.setText(TFSBundle.message("loading.workspaces", new Object[0]));
        }

        QueryWorkspaces param = new QueryWorkspaces();
        param.setOwnerName(authorizedCredentials.getQualifiedUsername());
        param.setComputer(Workstation.getComputerName());
        return beans.getRepositoryStub(authorizedCredentials, pi).queryWorkspaces(param).getQueryWorkspacesResult().getWorkspace();
    }

    private static String getAuthorizedCredentialsFor200x(ConfigurationContext context, URI uri, Credentials credentials) throws RemoteException {
        ServerStatusStub serverStatusStub = new ServerStatusStub(context, TfsUtil.appendPath(uri, "Services/v1.0/ServerStatus.asmx"));
        WebServiceHelper.setupStub(serverStatusStub, credentials, uri);
        CheckAuthenticationResponse response = serverStatusStub.checkAuthentication(new CheckAuthentication());
        return response.getCheckAuthenticationResult();
    }

    @Nullable
    private static String getPropertyValue(ArrayOfKeyValueOfStringString props, String propertyKey) {
        KeyValueOfStringString[] var2 = props.getKeyValueOfStringString();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            KeyValueOfStringString prop = var2[var4];
            if (propertyKey.equals(prop.getKey())) {
                return prop.getValue();
            }
        }

        return null;
    }

    private static <T> Pair<URI, T> tryDifferentUris(URI initialUri, boolean doTry, ThrowableConvertor<URI, T, RemoteException> request) throws RemoteException {
        try {
            return Pair.create(initialUri, request.convert(initialUri));
        } catch (RemoteException var9) {
            LOG.debug("connect to URI '" + initialUri + "' failed", var9);
            if (!doTry) {
                throw var9;
            } else {
                String path = initialUri.getPath();
                if (!StringUtil.isEmpty(path) && !"/".equals(path)) {
                    path = "/";
                } else {
                    path = "/tfs";
                }

                URI uri;
                try {
                    uri = new URI(initialUri.getScheme(), initialUri.getUserInfo(), initialUri.getHost(), initialUri.getPort(), path, (String)null, (String)null);
                } catch (URISyntaxException var8) {
                    LOG.error(var9);
                    return null;
                }

                LOG.debug("Trying to connect to '" + uri + "'");

                try {
                    return Pair.create(uri, request.convert(uri));
                } catch (RemoteException var7) {
                    throw getMoreDescriptiveException(var9, var7);
                }
            }
        }
    }

    public static class AddServerResult {
        public final URI uri;
        public final String instanceId;
        public final Credentials authorizedCredentials;
        public final Workspace[] workspaces;
        public final String workspacesLoadError;
        @Nullable
        public final TfsBeansHolder beans;

        public AddServerResult(URI uri, String instanceId, Credentials authorizedCredentials, Workspace[] workspaces, String workspacesLoadError, TfsBeansHolder beans) {
            this.uri = uri;
            this.instanceId = instanceId;
            this.authorizedCredentials = authorizedCredentials;
            this.workspaces = workspaces;
            this.workspacesLoadError = workspacesLoadError;
            this.beans = beans;
        }
    }

    public static class TeamProjectCollectionDescriptor {
        @NotNull
        public final String name;
        @NotNull
        public final String instanceId;

        public TeamProjectCollectionDescriptor(@NotNull String name, @NotNull String instanceId) {

            super();
            this.name = name;
            this.instanceId = instanceId;
        }
    }

    private static class Tfs2010ServerDescriptor extends TfsServerConnectionHelper.ServerDescriptor {
        public final Collection<TfsServerConnectionHelper.TeamProjectCollectionDescriptor> teamProjectCollections;

        Tfs2010ServerDescriptor(Collection<TfsServerConnectionHelper.TeamProjectCollectionDescriptor> teamProjectCollections, Credentials authorizedCredentials, URI uri, TfsBeansHolder servicesPaths) {
            super(authorizedCredentials, uri, servicesPaths);
            this.teamProjectCollections = teamProjectCollections;
        }
    }

    private static class Tfs200xServerDescriptor extends TfsServerConnectionHelper.ServerDescriptor {
        public final String instanceId;
        public final Workspace[] workspaces;

        Tfs200xServerDescriptor(String instanceId, Credentials authorizedCredentials, URI uri, Workspace[] workspaces, TfsBeansHolder servicesPaths) {
            super(authorizedCredentials, uri, servicesPaths);
            this.instanceId = instanceId;
            this.workspaces = workspaces;
        }
    }

    public static class ServerDescriptor {
        public final Credentials authorizedCredentials;
        @Nullable
        public final TfsBeansHolder beans;
        @Nullable
        public final URI uri;

        protected ServerDescriptor(Credentials authorizedCredentials, URI uri, TfsBeansHolder beans) {
            this.authorizedCredentials = authorizedCredentials;
            this.uri = uri;
            this.beans = beans;
        }

        public String getUserName() {
            return this.authorizedCredentials.getUserName();
        }
    }
}
