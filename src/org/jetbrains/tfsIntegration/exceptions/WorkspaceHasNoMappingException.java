/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import java.text.MessageFormat;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
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
/*    */ public class WorkspaceHasNoMappingException
/*    */   extends TfsException
/*    */ {
/*    */   @NotNull
/*    */   private final WorkspaceInfo myWorkspace;
/*    */   
/* 31 */   public WorkspaceHasNoMappingException(@NotNull WorkspaceInfo workspace) { this.myWorkspace = workspace; }
/*    */ 
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 36 */     return 
/* 37 */       MessageFormat.format("Mappings for workspace ''{0}'' were modified on server. Please review your mapping settings before you continue working.", new Object[] {
/* 38 */           this.myWorkspace.getName()
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\WorkspaceHasNoMappingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */