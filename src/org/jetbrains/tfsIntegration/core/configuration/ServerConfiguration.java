/*    */ package org.jetbrains.tfsIntegration.core.configuration;
/*    */ 
/*    */ import com.intellij.notification.Notification;
/*    */ import com.intellij.openapi.util.Comparing;
/*    */ import com.intellij.util.xmlb.annotations.Tag;
/*    */ import com.intellij.util.xmlb.annotations.Transient;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Tag("configuration")
/*    */ public class ServerConfiguration
/*    */ {
/*    */   @Nullable
/*    */   private Credentials myCredentials;
/*    */   @Nullable
/*    */   private String myProxyUri;
/*    */   private boolean myProxyInaccessible;
/*    */   private Notification myAuthCanceledNotification;
/*    */   
/*    */   public ServerConfiguration() {}
/*    */   
/* 41 */   public ServerConfiguration(Credentials credentials) { this.myCredentials = credentials; }
/*    */ 
/*    */ 
/*    */   
/*    */   @Tag("credentials")
/*    */   @Nullable
/* 47 */   public Credentials getCredentials() { return this.myCredentials; }
/*    */ 
/*    */ 
/*    */   
/* 51 */   public void setCredentials(@NotNull Credentials credentials) {    this.myCredentials = credentials; }
/*    */ 
/*    */ 
/*    */   
/*    */   @Tag("proxy")
/*    */   @Nullable
/* 57 */   public String getProxyUri() { return this.myProxyUri; }
/*    */ 
/*    */   
/*    */   public void setProxyUri(@Nullable String proxyUri) {
/* 61 */     if (!Comparing.equal(this.myProxyUri, proxyUri)) {
/* 62 */       this.myProxyInaccessible = false;
/*    */     }
/* 64 */     this.myProxyUri = proxyUri;
/*    */   }
/*    */ 
/*    */   
/*    */   @Transient
/* 69 */   public boolean isProxyInaccessible() { return this.myProxyInaccessible; }
/*    */ 
/*    */ 
/*    */   
/* 73 */   public void setProxyInaccessible() { this.myProxyInaccessible = true; }
/*    */ 
/*    */ 
/*    */   
/*    */   @Transient
/* 78 */   public Notification getAuthCanceledNotification() { return this.myAuthCanceledNotification; }
/*    */ 
/*    */ 
/*    */   
/* 82 */   public void setAuthCanceledNotification(Notification authCanceledNotification) { this.myAuthCanceledNotification = authCanceledNotification; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\configuration\ServerConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */