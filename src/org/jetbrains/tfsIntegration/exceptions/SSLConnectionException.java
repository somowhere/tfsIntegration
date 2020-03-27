/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import javax.net.ssl.SSLHandshakeException;
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
/*    */ public class SSLConnectionException
/*    */   extends TfsException
/*    */ {
/* 24 */   public SSLConnectionException(SSLHandshakeException cause) { super(cause); }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 29 */     Throwable cause = this;
/* 30 */     while (cause.getCause() != null && cause.getCause() != cause) {
/* 31 */       cause = cause.getCause();
/*    */     }
/* 33 */     return "SSL connection failed: " + cause.getMessage();
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\SSLConnectionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */