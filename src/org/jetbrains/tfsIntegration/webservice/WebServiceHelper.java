//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.webservice;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.ClassLoaderUtil;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.StreamUtil;
import com.intellij.openapi.util.registry.Registry;
import com.intellij.util.containers.ContainerUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.Stub;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.transport.http.HttpTransportProperties.Authenticator;
import org.apache.axis2.transport.http.HttpTransportProperties.ProxyProperties;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.auth.BasicScheme;
import org.apache.commons.httpclient.auth.DigestScheme;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HostParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.core.configuration.Credentials;
import org.jetbrains.tfsIntegration.core.configuration.Credentials.Type;
import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.exceptions.TfsExceptionManager;
import org.jetbrains.tfsIntegration.webservice.auth.NativeNTLM2Scheme;
import org.jetbrains.tfsIntegration.webservice.compatibility.CustomSOAP12Factory;
import org.jetbrains.tfsIntegration.webservice.compatibility.CustomSOAPBuilder;

public class WebServiceHelper {
    private static final Logger LOG = Logger.getInstance(WebServiceHelper.class.getName());
    @NonNls
    private static final String SOAP_BUILDER_KEY = "application/soap+xml";
    @NonNls
    private static final String CONTENT_TYPE_GZIP = "application/gzip";
    public static final String USE_NATIVE_CREDENTIALS = WebServiceHelper.class.getName() + ".overrideCredentials";
    private static final int SOCKET_TIMEOUT = Integer.getInteger("org.jetbrains.tfsIntegration.socketTimeout", 30000);

    public WebServiceHelper() {
    }

    public static void httpGet(URI serverUri, String downloadUrl, OutputStream outputStream, Credentials credentials, HttpClient httpClient) throws TfsException, IOException {
        TFSVcs.assertTrue(downloadUrl != null);
        setupHttpClient(credentials, serverUri, httpClient);
        GetMethod method = new GetMethod(downloadUrl);

        try {
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != 200) {
                if (statusCode == 500) {
                    throw new OperationFailedException(method.getResponseBodyAsString());
                }

                throw TfsExceptionManager.createHttpTransportErrorException(statusCode, (AxisFault)null);
            }

            StreamUtil.copyStreamContent(getInputStream(method), outputStream);
        } finally {
            method.releaseConnection();
        }

    }

    public static void httpPost(@NotNull String uploadUrl, @NotNull Part[] parts, @Nullable OutputStream outputStream, Credentials credentials, URI serverUri, HttpClient httpClient) throws IOException, TfsException {

        setupHttpClient(credentials, serverUri, httpClient);
        PostMethod method = new PostMethod(uploadUrl);

        try {
            method.setRequestHeader("X-TFS-Version", "1.0.0.0");
            method.setRequestHeader("accept-language", "en-US");
            method.setRequestEntity(new MultipartRequestEntity(parts, method.getParams()));
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != 200) {
                if (statusCode == 500) {
                    throw new OperationFailedException(method.getResponseBodyAsString());
                }

                throw TfsExceptionManager.createHttpTransportErrorException(statusCode, (AxisFault)null);
            }

            if (outputStream != null) {
                StreamUtil.copyStreamContent(getInputStream(method), outputStream);
            }
        } finally {
            method.releaseConnection();
        }

    }

    public static ConfigurationContext getStubConfigurationContext() {
        return (ConfigurationContext)ClassLoaderUtil.computeWithClassLoader(TFSVcs.class.getClassLoader(), () -> {
            try {
                ConfigurationContext configContext = ConfigurationContextFactory.createDefaultConfigurationContext();
                configContext.getAxisConfiguration().addMessageBuilder("application/soap+xml", new CustomSOAPBuilder());
                return configContext;
            } catch (Exception var1) {
                LOG.error("Axis2 configuration error", var1);
                return null;
            }
        });
    }

    private static void setProxy(HttpClient httpClient) {
        HTTPProxyInfo proxy = HTTPProxyInfo.getCurrent();
        if (proxy.host != null) {
            httpClient.getHostConfiguration().setProxy(proxy.host, proxy.port);
            if (proxy.user != null) {
                Pair<String, String> domainAndUser = getDomainAndUser(proxy.user);
                UsernamePasswordCredentials creds = new NTCredentials((String)domainAndUser.second, proxy.password, proxy.host, (String)domainAndUser.first);
                httpClient.getState().setProxyCredentials(AuthScope.ANY, creds);
            }
        } else {
            httpClient.getHostConfiguration().setProxyHost((ProxyHost)null);
        }

    }

    private static void setupHttpClient(Credentials credentials, URI serverUri, HttpClient httpClient) {
        setCredentials(httpClient, credentials, serverUri);
        setProxy(httpClient);
        httpClient.getParams().setSoTimeout(SOCKET_TIMEOUT);
        if (Registry.is("tfs.set.connection.timeout", false)) {
            httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(SOCKET_TIMEOUT);
            httpClient.getHttpConnectionManager().getParams().setSoTimeout(SOCKET_TIMEOUT);
        }

    }

    public static void setupStub(@NotNull Stub stub, @NotNull Credentials credentials, @NotNull URI serverUri) {

        Options options = stub._getServiceClient().getOptions();
        options.setProperty("__CHUNKED__", "false");
        options.setProperty("transport.http.acceptGzip", Boolean.TRUE);
        options.setProperty("SO_TIMEOUT", SOCKET_TIMEOUT);
        if (Registry.is("tfs.set.connection.timeout", false)) {
            options.setProperty("CONNECTION_TIMEOUT", SOCKET_TIMEOUT);
        }

        if (credentials.getType() == Type.Alternate) {
            String basicAuth = BasicScheme.authenticate(new UsernamePasswordCredentials(credentials.getUserName(), credentials.getPassword()), "UTF-8");
            Map<String, String> headers = new HashMap();
            headers.put("Authorization", basicAuth);
            options.setProperty("HTTP_HEADERS", headers);
        } else {
            Authenticator auth = new Authenticator();
            auth.setUsername(credentials.getUserName());
            auth.setPassword(credentials.getPassword() != null ? credentials.getPassword() : "");
            auth.setDomain(credentials.getDomain());
            auth.setHost(serverUri.getHost());
            options.setProperty("_NTLM_DIGEST_BASIC_AUTHENTICATION_", auth);
            HttpMethodParams params = new HttpMethodParams();
            params.setBooleanParameter(USE_NATIVE_CREDENTIALS, credentials.getType() == Type.NtlmNative);
            options.setProperty("HTTP_METHOD_PARAMS", params);
        }

        HTTPProxyInfo proxy = HTTPProxyInfo.getCurrent();
        ProxyProperties proxyProperties;
        if (proxy.host != null) {
            proxyProperties = new ProxyProperties();
            Pair<String, String> domainAndUser = getDomainAndUser(proxy.user);
            proxyProperties.setProxyName(proxy.host);
            proxyProperties.setProxyPort(proxy.port);
            proxyProperties.setDomain((String)domainAndUser.first);
            proxyProperties.setUserName((String)domainAndUser.second);
            proxyProperties.setPassWord(proxy.password);
        } else {
            proxyProperties = null;
        }

        options.setProperty("PROXY", proxyProperties);
    }

    private static void setCredentials(@NotNull HttpClient httpClient, @NotNull Credentials credentials, @NotNull URI serverUri) {

        if (credentials.getType() == Type.Alternate) {
            HostParams parameters = httpClient.getHostConfiguration().getParams();
            Collection<Header> headers = (Collection)parameters.getParameter("http.default-headers");
            if (headers == null) {
                headers = new ArrayList();
                parameters.setParameter("http.default-headers", headers);
            }

            Header authHeader = (Header)ContainerUtil.find((Iterable)headers, (header) -> {
                return ((Header)header).getName().equals("Authorization");
            });
            if (authHeader == null) {
                authHeader = new Header("Authorization", "");
                ((Collection)headers).add(authHeader);
            }

            authHeader.setValue(BasicScheme.authenticate(new UsernamePasswordCredentials(credentials.getUserName(), credentials.getPassword()), "UTF-8"));
        } else {
            NTCredentials ntCreds = new NTCredentials(credentials.getUserName(), credentials.getPassword(), serverUri.getHost(), credentials.getDomain());
            httpClient.getState().setCredentials(AuthScope.ANY, ntCreds);
            httpClient.getParams().setBooleanParameter(USE_NATIVE_CREDENTIALS, credentials.getType() == Type.NtlmNative);
        }

    }

    private static InputStream getInputStream(HttpMethod method) throws IOException {
        Header contentType = method.getResponseHeader("Content-Type");
        return (InputStream)(contentType != null && "application/gzip".equalsIgnoreCase(contentType.getValue()) ? new GZIPInputStream(method.getResponseBodyAsStream()) : method.getResponseBodyAsStream());
    }

    private static void trace(long threadId, @NonNls String msg) {
        String dispatch = ApplicationManager.getApplication().isDispatchThread() ? " [d]" : "";
        (new StringBuilder()).append(System.currentTimeMillis()).append(", thread=").append(threadId).append(", cur thread=").append(Thread.currentThread().getId()).append(dispatch).append(": ").append(msg).toString();
    }

    private static Pair<String, String> getDomainAndUser(@Nullable String s) {
        if (s == null) {
            return Pair.create(null, null);
        } else {
            int slashPos = s.indexOf(92);
            String domain = slashPos >= 0 ? s.substring(0, slashPos) : "";
            String user = slashPos >= 0 ? s.substring(slashPos + 1) : s;
            return Pair.create(domain, user);
        }
    }

    static {
        AuthPolicy.unregisterAuthScheme("NTLM");
        AuthPolicy.unregisterAuthScheme("Digest");
        AuthPolicy.unregisterAuthScheme("Basic");
        AuthPolicy.registerAuthScheme("NTLM", NativeNTLM2Scheme.class);
        AuthPolicy.registerAuthScheme("Digest", DigestScheme.class);
        AuthPolicy.registerAuthScheme("Basic", BasicScheme.class);
        System.setProperty("soap12.factory", CustomSOAP12Factory.class.getName());
    }

    public interface Delegate<T> {
        @Nullable
        T executeRequest() throws RemoteException;
    }
}
