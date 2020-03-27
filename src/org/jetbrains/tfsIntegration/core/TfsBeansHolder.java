//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.microsoft.schemas.teamfoundation._2005._06.services.groupsecurity._03.GroupSecurityServiceStub;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.ArrayOfFrameworkRegistrationEntry;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.FrameworkRegistrationEntry;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.GetRegistrationEntries;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.RegistrationServiceInterface;
import com.microsoft.schemas.teamfoundation._2005._06.services.registration._03.RegistrationStub;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RepositoryStub;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ClientService2Stub;
import java.net.URI;
import java.rmi.RemoteException;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.configuration.Credentials;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.exceptions.HostNotApplicableException;
import org.jetbrains.tfsIntegration.webservice.WebServiceHelper;

public class TfsBeansHolder {
    private static final Logger LOG = Logger.getInstance(TfsBeansHolder.class.getName());
    private final URI myServerUri;
    private RepositoryStub myRepository;
    private RepositoryStub myRepository4;
    private ClientService2Stub myWorkItemTrackingClientService;
    private GroupSecurityServiceStub myGroupSecurityService;
    private String myDownloadUrl;
    private String myUploadUrl;
    private final HttpClient[] myUploadDownloadClients = new HttpClient[2];

    public TfsBeansHolder(URI serverUri) {
        this.myServerUri = serverUri;
    }

    @NotNull
    public RepositoryStub getRepositoryStub(Credentials credentials, ProgressIndicator pi) throws HostNotApplicableException, RemoteException {
        if (this.myRepository == null) {
            this.createStubs(credentials, pi);
        }

        WebServiceHelper.setupStub(this.myRepository, credentials, this.myServerUri);
        RepositoryStub var10000 = this.myRepository;

        return var10000;
    }

    @NotNull
    public RepositoryStub getRepository4Stub(Credentials credentials, ProgressIndicator pi) throws HostNotApplicableException, RemoteException {
        if (this.myRepository4 == null) {
            this.createStubs(credentials, pi);
        }

        WebServiceHelper.setupStub(this.myRepository4, credentials, this.myServerUri);
        RepositoryStub var10000 = this.myRepository4;
        return var10000;
    }

    @NotNull
    public ClientService2Stub getWorkItemServiceStub(Credentials credentials, ProgressIndicator pi) throws HostNotApplicableException, RemoteException {
        if (this.myWorkItemTrackingClientService == null) {
            this.createStubs(credentials, pi);
        }

        WebServiceHelper.setupStub(this.myWorkItemTrackingClientService, credentials, this.myServerUri);
        ClientService2Stub var10000 = this.myWorkItemTrackingClientService;

        return var10000;
    }

    @NotNull
    public GroupSecurityServiceStub getGroupSecurityServiceStub(Credentials credentials, ProgressIndicator pi) throws HostNotApplicableException, RemoteException {
        if (this.myGroupSecurityService == null) {
            this.createStubs(credentials, pi);
        }

        WebServiceHelper.setupStub(this.myGroupSecurityService, credentials, this.myServerUri);
        GroupSecurityServiceStub var10000 = this.myGroupSecurityService;

        return var10000;
    }

    @NotNull
    public String getDownloadUrl(Credentials credentials, ProgressIndicator pi) throws HostNotApplicableException, RemoteException {
        if (this.myDownloadUrl == null) {
            this.createStubs(credentials, pi);
        }

        String var10000 = this.myDownloadUrl;

        return var10000;
    }

    @NotNull
    public String getUploadUrl(Credentials credentials, ProgressIndicator pi) throws HostNotApplicableException, RemoteException {
        if (this.myUploadUrl == null) {
            this.createStubs(credentials, pi);
        }

        String var10000 = this.myUploadUrl;

        return var10000;
    }

    private void createStubs(Credentials authorizedCredentials, @Nullable ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
        LOG.assertTrue(!ApplicationManager.getApplication().isDispatchThread());
        String piText = pi != null ? pi.getText() : null;
        if (pi != null) {
            pi.setText(TFSBundle.message("loading.services", new Object[0]));
        }

        ConfigurationContext configContext = WebServiceHelper.getStubConfigurationContext();
        RegistrationStub registrationStub = new RegistrationStub(configContext, TfsUtil.appendPath(this.myServerUri, "Services/v1.0/Registration.asmx"));
        WebServiceHelper.setupStub(registrationStub, authorizedCredentials, this.myServerUri);
        GetRegistrationEntries getRegistrationEntriesParam = new GetRegistrationEntries();
        ArrayOfFrameworkRegistrationEntry registrationEntries = registrationStub.getRegistrationEntries(getRegistrationEntriesParam).getGetRegistrationEntriesResult();
        String isccProvider = findServicePath(registrationEntries, "VersionControl", "ISCCProvider");
        if (isccProvider == null) {
            throw new HostNotApplicableException((AxisFault)null);
        } else {
            String isccProvider4 = findServicePath(registrationEntries, "VersionControl", "ISCCProvider4", "ISCCProvider");
            if (isccProvider4 == null) {
                throw new HostNotApplicableException((AxisFault)null);
            } else {
                String download = findServicePath(registrationEntries, "VersionControl", "Download");
                if (download == null) {
                    throw new HostNotApplicableException((AxisFault)null);
                } else {
                    String upload = findServicePath(registrationEntries, "VersionControl", "Upload");
                    if (upload == null) {
                        throw new HostNotApplicableException((AxisFault)null);
                    } else {
                        String workItemService = findServicePath(registrationEntries, "WorkItemTracking", "WorkitemService");
                        if (workItemService == null) {
                            throw new HostNotApplicableException((AxisFault)null);
                        } else {
                            String groupSecurityService = findServicePath(registrationEntries, "vstfs", "GroupSecurity");
                            if (groupSecurityService == null) {
                                throw new HostNotApplicableException((AxisFault)null);
                            } else {
                                this.doCreateStubs(configContext, isccProvider, isccProvider4, download, upload, workItemService, groupSecurityService);
                                if (pi != null) {
                                    pi.setText(piText);
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    private void doCreateStubs(@Nullable ConfigurationContext configContext, String isccProvider, String isccProvider4, String download, String upload, String workItemService, String groupSecurity) {
        this.myDownloadUrl = download;
        this.myUploadUrl = upload;

        try {
            if (configContext == null) {
                configContext = WebServiceHelper.getStubConfigurationContext();
            }

            this.myRepository = new RepositoryStub(configContext, TfsUtil.appendPath(this.myServerUri, isccProvider));
            this.myRepository4 = new RepositoryStub(configContext, TfsUtil.appendPath(this.myServerUri, isccProvider4));
            this.myWorkItemTrackingClientService = new ClientService2Stub(configContext, TfsUtil.appendPath(this.myServerUri, workItemService));
            this.myGroupSecurityService = new GroupSecurityServiceStub(configContext, TfsUtil.appendPath(this.myServerUri, groupSecurity));
        } catch (Exception var9) {
            LOG.error("Failed to initialize web service stub", var9);
        }

    }

    public HttpClient getUploadDownloadClient(boolean forProxy) {
        int index = forProxy ? 1 : 0;
        if (this.myUploadDownloadClients[index] == null) {
            HttpConnectionManager connManager = new MultiThreadedHttpConnectionManager();
            this.myUploadDownloadClients[index] = new HttpClient(connManager);
            HttpClientParams clientParams = new HttpClientParams();
            clientParams.setConnectionManagerTimeout(30000L);
            this.myUploadDownloadClients[index].setParams(clientParams);
        }

        return this.myUploadDownloadClients[index];
    }

    @Nullable
    private static String findServicePath(ArrayOfFrameworkRegistrationEntry registrationEntries, String entryType, String... interfaceNames) {
        if (registrationEntries == null) {
            return null;
        } else {
            FrameworkRegistrationEntry[] var3 = registrationEntries.getRegistrationEntry();
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                FrameworkRegistrationEntry entry = var3[var5];
                if (entryType.equals(entry.getType())) {
                    RegistrationServiceInterface[] interfaces = entry.getServiceInterfaces().getServiceInterface();
                    if (interfaces != null) {
                        String[] var8 = interfaceNames;
                        int var9 = interfaceNames.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                            String interfaceName = var8[var10];
                            RegistrationServiceInterface[] var12 = interfaces;
                            int var13 = interfaces.length;

                            for(int var14 = 0; var14 < var13; ++var14) {
                                RegistrationServiceInterface anInterface = var12[var14];
                                if (interfaceName.equals(anInterface.getName())) {
                                    return anInterface.getUrl();
                                }
                            }
                        }
                    }
                }
            }

            return null;
        }
    }
}
