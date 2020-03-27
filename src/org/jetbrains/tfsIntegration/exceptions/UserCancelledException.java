/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
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
/*    */ public class UserCancelledException
/*    */   extends TfsException
/*    */ {
/* 25 */   public String getMessage() { return TFSBundle.message("operation.canceled", new Object[0]); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\UserCancelledException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */