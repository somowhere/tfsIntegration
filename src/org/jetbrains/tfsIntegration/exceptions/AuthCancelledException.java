/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import java.net.URI;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*    */ 
/*    */ 
/*    */ public class AuthCancelledException
/*    */   extends UserCancelledException
/*    */ {
/*    */   private final URI myServerUri;
/*    */   
/* 13 */   public AuthCancelledException(URI serverUri) { this.myServerUri = serverUri; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 18 */   public String getMessage() { return TFSBundle.message("authentication.canceled", new Object[] { TfsUtil.getPresentableUri(this.myServerUri) }); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\AuthCancelledException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */