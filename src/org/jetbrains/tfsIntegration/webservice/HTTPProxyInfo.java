/*    */ package org.jetbrains.tfsIntegration.webservice;
/*    */ 
/*    */ import com.intellij.util.net.HttpConfigurable;
/*    */ import com.intellij.util.proxy.CommonProxy;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
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
/*    */ public class HTTPProxyInfo
/*    */ {
/*    */   @Nullable
/*    */   public final String host;
/*    */   public final int port;
/*    */   @Nullable
/*    */   public final String user;
/*    */   @Nullable
/*    */   public final String password;
/*    */   
/*    */   private HTTPProxyInfo(@Nullable String host, int port, @Nullable String user, @Nullable String password) {
/* 32 */     this.host = host;
/* 33 */     this.port = port;
/* 34 */     this.user = user;
/* 35 */     this.password = password;
/*    */   }
/*    */   
/*    */   public static boolean shouldPromptForPassword() {
/* 39 */     if (TFSConfigurationManager.getInstance().useIdeaHttpProxy()) {
/* 40 */       HttpConfigurable hc = HttpConfigurable.getInstance();
/* 41 */       return (hc.USE_HTTP_PROXY && hc.PROXY_AUTHENTICATION && !hc.KEEP_PROXY_PASSWORD);
/*    */     } 
/* 43 */     return false;
/*    */   }
/*    */   
/*    */   public static void promptForPassword() {
/* 47 */     HttpConfigurable hc = HttpConfigurable.getInstance();
/* 48 */     hc.getPromptedAuthentication(hc.PROXY_HOST, "Proxy authentication");
/*    */   }
/*    */ 
/*    */   
/*    */   public static HTTPProxyInfo getCurrent() {
/* 53 */     CommonProxy.isInstalledAssertion();
/*    */     
/* 55 */     if (TFSConfigurationManager.getInstance().useIdeaHttpProxy()) {
/* 56 */       HttpConfigurable hc = HttpConfigurable.getInstance();
/* 57 */       if (hc.USE_HTTP_PROXY) {
/* 58 */         if (hc.PROXY_AUTHENTICATION)
/*    */         {
/* 60 */           return new HTTPProxyInfo(hc.PROXY_HOST, hc.PROXY_PORT, hc.getProxyLogin(), hc.getPlainProxyPassword());
/*    */         }

/*    */         
/* 63 */         return new HTTPProxyInfo(hc.PROXY_HOST, hc.PROXY_PORT, null, null);
/*    */       } 
/*    */     } 
/*    */     
/* 67 */     return new HTTPProxyInfo(null, -1, null, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\HTTPProxyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */