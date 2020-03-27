/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import com.intellij.openapi.vcs.FilePath;
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
/*    */ 
/*    */ public class DuplicateMappingException
/*    */   extends TfsException
/*    */ {
/*    */   private final FilePath myLocalPath;
/*    */   
/* 27 */   public DuplicateMappingException(FilePath localPath) { this.myLocalPath = localPath; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 32 */   public String getMessage() { return 
/* 33 */       MessageFormat.format("Local path ''{0}'' is mapped in different workspaces. Please review your mappings.", new Object[] { this.myLocalPath.getPresentableUrl() }); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\DuplicateMappingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */