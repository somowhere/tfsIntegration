/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import java.util.Collection;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
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
/*    */ public interface PolicyContext
/*    */ {
/*    */   Collection<FilePath> getFiles();
/*    */   
/*    */   Project getProject();
/*    */   
/*    */   String getCommitMessage();
/*    */   
/*    */   Map<WorkItem, WorkItemAction> getWorkItems();
/*    */   
/*    */   public enum WorkItemAction
/*    */   {
/* 32 */     Associate, Resolve;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\PolicyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */