/*     */ package org.jetbrains.tfsIntegration.core.configuration;
/*     */ 
/*     */ import com.intellij.notification.Notification;
/*     */ import com.intellij.notification.NotificationListener;
/*     */ import com.intellij.notification.NotificationType;
/*     */ import com.intellij.notification.Notifications;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.components.PersistentStateComponent;
/*     */ import com.intellij.openapi.components.ServiceManager;
/*     */ import com.intellij.openapi.components.State;
/*     */ import com.intellij.openapi.components.Storage;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.util.xmlb.annotations.OptionTag;
/*     */ import com.intellij.util.xmlb.annotations.XMap;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.swing.event.HyperlinkEvent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.config.TfsServerConnectionHelper;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.Workstation;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @State(name = "org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager", storages = {@Storage("tfs.xml")})
/*     */ public class TFSConfigurationManager
/*     */   implements PersistentStateComponent<TFSConfigurationManager.State>
/*     */ {
/*     */   private static final String TFS_NOTIFICATION_GROUP = "TFS";
/*     */   
/*     */   public static class State
/*     */   {
/*     */     @OptionTag
/*     */     @XMap(entryTagName = "server", keyAttributeName = "uri")
/*  51 */     public Map<String, ServerConfiguration> config = new HashMap<>();
/*     */     
/*     */     public boolean useIdeaHttpProxy = true;
/*     */     
/*     */     public boolean supportTfsCheckinPolicies = true;
/*     */     
/*     */     public boolean supportStatefulCheckinPolicies = true;
/*     */     
/*     */     public boolean reportNotInstalledCheckinPolicies = true;
/*     */   }
/*  61 */   private Map<String, ServerConfiguration> myServersConfig = new HashMap<>();
/*     */   
/*     */   private boolean myUseIdeaHttpProxy = true;
/*     */   private boolean mySupportTfsCheckinPolicies = true;
/*     */   private boolean mySupportStatefulCheckinPolicies = true;
/*     */   private boolean myReportNotInstalledCheckinPolicies = true;
/*     */   
/*     */   @NotNull
/*  69 */   public static synchronized TFSConfigurationManager getInstance() {      return (TFSConfigurationManager)ServiceManager.getService(TFSConfigurationManager.class); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public synchronized Credentials getCredentials(@NotNull URI serverUri) {
/*  77 */        ServerConfiguration serverConfiguration = getConfiguration(serverUri);
/*  78 */     return (serverConfiguration != null) ? serverConfiguration.getCredentials() : null;
/*     */   }
/*     */   
/*     */   public synchronized boolean isAuthCanceled(URI serverUri) {
/*  82 */     ServerConfiguration serverConfiguration = getConfiguration(serverUri);
/*  83 */     return (serverConfiguration != null && serverConfiguration.getAuthCanceledNotification() != null);
/*     */   }
/*     */   
/*     */   public synchronized void setAuthCanceled(final URI serverUri, @Nullable Object projectOrComponent) {
/*  87 */     ServerConfiguration serverConfiguration = getOrCreateServerConfiguration(serverUri);
/*  88 */     if (serverConfiguration.getAuthCanceledNotification() != null) {
/*     */       return;
/*     */     }
/*     */     
/*  92 */     final Project project = (projectOrComponent instanceof Project) ? (Project)projectOrComponent : null;
/*     */ 
/*     */ 
/*     */     
/*  96 */     Notification notification = new Notification("TFS", TFSBundle.message("notification.auth.canceled.title", new Object[] { TfsUtil.getPresentableUri(serverUri) }), TFSBundle.message("notification.auth.canceled.text", new Object[0]), NotificationType.ERROR, new NotificationListener()
/*     */         {
/*     */           
/*     */           public void hyperlinkUpdate(@NotNull Notification notification, @NotNull HyperlinkEvent event)
/*     */           {
/* 101 */                   try { TfsServerConnectionHelper.ensureAuthenticated(project, serverUri, true);
/* 102 */               notification.expire(); }
/*     */             
/* 104 */             catch (TfsException tfsException) {}
/*     */           }
/*     */         });
/*     */ 
/*     */     
/* 109 */     serverConfiguration.setAuthCanceledNotification(notification);
/*     */ 
/*     */     
/* 112 */     Notifications.Bus.notify(notification, null);
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public URI getProxyUri(@NotNull URI serverUri) {
/* 117 */        ServerConfiguration serverConfiguration = getConfiguration(serverUri);
/*     */     try {
/* 119 */       return (serverConfiguration != null && serverConfiguration.getProxyUri() != null) ? new URI(serverConfiguration.getProxyUri()) : null;
/*     */     }
/* 121 */     catch (URISyntaxException e) {
/* 122 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean shouldTryProxy(@NotNull URI serverUri) {
/* 127 */        ServerConfiguration serverConfiguration = getConfiguration(serverUri);
/* 128 */     return (serverConfiguration != null && serverConfiguration.getProxyUri() != null && !serverConfiguration.isProxyInaccessible());
/*     */   }
/*     */ 
/*     */   
/* 132 */   public void setProxyInaccessible(@NotNull URI serverUri) {    getConfiguration(serverUri).setProxyInaccessible(); }
/*     */ 
/*     */   
/*     */   public void setProxyUri(@NotNull URI serverUri, @Nullable URI proxyUri) {
/* 136 */        String proxyUriString = (proxyUri != null) ? proxyUri.toString() : null;
/* 137 */     getOrCreateServerConfiguration(serverUri).setProxyUri(proxyUriString);
/*     */   }
/*     */   
/*     */   public synchronized void storeCredentials(@NotNull URI serverUri, @NotNull Credentials credentials) {
/* 141 */           ServerConfiguration serverConfiguration = getOrCreateServerConfiguration(serverUri);
/* 142 */     serverConfiguration.setCredentials(credentials);
/* 143 */     Notification notification = serverConfiguration.getAuthCanceledNotification();
/* 144 */     if (notification != null) {
/* 145 */       ApplicationManager.getApplication().invokeLater(() -> notification.expire());
/*     */     }
/* 147 */     serverConfiguration.setAuthCanceledNotification(null);
/*     */   }
/*     */   
/*     */   public synchronized void resetStoredPasswords() {
/* 151 */     for (ServerConfiguration serverConfiguration : this.myServersConfig.values()) {
/* 152 */       Credentials credentials = serverConfiguration.getCredentials();
/* 153 */       if (credentials != null) {
/* 154 */         credentials.resetPassword();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void loadState(@NotNull State state) {
/* 161 */        this.myServersConfig = state.config;
/* 162 */     this.myUseIdeaHttpProxy = state.useIdeaHttpProxy;
/* 163 */     this.mySupportTfsCheckinPolicies = state.supportTfsCheckinPolicies;
/* 164 */     this.mySupportStatefulCheckinPolicies = state.supportStatefulCheckinPolicies;
/* 165 */     this.myReportNotInstalledCheckinPolicies = state.reportNotInstalledCheckinPolicies;
/*     */   }
/*     */ 
/*     */   
/*     */   public State getState() {
/* 170 */     State state = new State();
/* 171 */     state.config = this.myServersConfig;
/* 172 */     state.supportStatefulCheckinPolicies = this.mySupportStatefulCheckinPolicies;
/* 173 */     state.supportTfsCheckinPolicies = this.mySupportTfsCheckinPolicies;
/* 174 */     state.useIdeaHttpProxy = this.myUseIdeaHttpProxy;
/* 175 */     state.reportNotInstalledCheckinPolicies = this.myReportNotInstalledCheckinPolicies;
/* 176 */     return state;
/*     */   }
/*     */   
/*     */   private static String getConfigKey(URI serverUri) {
/* 180 */     String uriString = serverUri.toString();
/* 181 */     if (!uriString.endsWith("/"))
/*     */     {
/* 183 */       uriString = uriString + "/";
/*     */     }
/* 185 */     return uriString;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/* 190 */   private ServerConfiguration getConfiguration(URI serverUri) { return this.myServersConfig.get(getConfigKey(serverUri)); }
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private ServerConfiguration getOrCreateServerConfiguration(@NotNull URI serverUri) {
/* 195 */        ServerConfiguration config = this.myServersConfig.get(getConfigKey(serverUri));
/* 196 */     if (config == null) {
/* 197 */       config = new ServerConfiguration();
/* 198 */       this.myServersConfig.put(getConfigKey(serverUri), config);
/*     */     } 
/* 200 */         return config;
/*     */   }
/*     */   
/*     */   public boolean serverKnown(@NotNull String instanceId) {
/* 204 */         for (ServerInfo server : Workstation.getInstance().getServers()) {
/* 205 */       if (server.getGuid().equalsIgnoreCase(instanceId)) {
/* 206 */         return true;
/*     */       }
/*     */     } 
/* 209 */     return false;
/*     */   }
/*     */   
/*     */   public void remove(@NotNull URI serverUri) {
/* 213 */         ServerConfiguration config = this.myServersConfig.get(getConfigKey(serverUri));
/* 214 */     if (config != null && config.getAuthCanceledNotification() != null) {
/* 215 */       ApplicationManager.getApplication().invokeLater(() -> config.getAuthCanceledNotification().expire());
/*     */     }
/* 217 */     this.myServersConfig.remove(getConfigKey(serverUri));
/*     */   }
/*     */ 
/*     */   
/* 221 */   public void setUseIdeaHttpProxy(boolean useIdeaHttpProxy) { this.myUseIdeaHttpProxy = useIdeaHttpProxy; }
/*     */ 
/*     */ 
/*     */   
/* 225 */   public boolean useIdeaHttpProxy() { return this.myUseIdeaHttpProxy; }
/*     */ 
/*     */ 
/*     */   
/* 229 */   public TfsCheckinPoliciesCompatibility getCheckinPoliciesCompatibility() { return new TfsCheckinPoliciesCompatibility(this.mySupportStatefulCheckinPolicies, this.mySupportTfsCheckinPolicies, this.myReportNotInstalledCheckinPolicies); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 234 */   public void setSupportTfsCheckinPolicies(boolean supportTfsCheckinPolicies) { this.mySupportTfsCheckinPolicies = supportTfsCheckinPolicies; }
/*     */ 
/*     */ 
/*     */   
/* 238 */   public void setSupportStatefulCheckinPolicies(boolean supportStatefulCheckinPolicies) { this.mySupportStatefulCheckinPolicies = supportStatefulCheckinPolicies; }
/*     */ 
/*     */ 
/*     */   
/* 242 */   public void setReportNotInstalledCheckinPolicies(boolean reportNotInstalledCheckinPolicies) { this.myReportNotInstalledCheckinPolicies = reportNotInstalledCheckinPolicies; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\configuration\TFSConfigurationManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */