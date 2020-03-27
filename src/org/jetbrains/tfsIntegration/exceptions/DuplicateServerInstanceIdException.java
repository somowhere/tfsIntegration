/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import java.net.URI;
/*    */ import java.text.MessageFormat;
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
/*    */ public class DuplicateServerInstanceIdException
/*    */   extends TfsException
/*    */ {
/*    */   private final URI myExistingServerUri;
/*    */   
/* 26 */   public DuplicateServerInstanceIdException(URI existingServerUri) { this.myExistingServerUri = existingServerUri; }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 31 */     return 
/* 32 */       MessageFormat.format("Duplicate server instance. You are probably using an alias to the server ''{0}''", new Object[] { this.myExistingServerUri });
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\DuplicateServerInstanceIdException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */