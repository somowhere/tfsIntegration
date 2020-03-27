/*    */ package org.jetbrains.tfsIntegration.core.tfs.conflicts;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ConflictType;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.ui.MergeNameDialog;
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
/*    */ public class DialogNameMerger
/*    */   implements NameMerger
/*    */ {
/*    */   @Nullable
/*    */   public String mergeName(WorkspaceInfo workspace, Conflict conflict, Project project) {
/*    */     String theirsName, yoursName;
/* 32 */     if (conflict.getCtype() == ConflictType.Merge) {
/* 33 */       yoursName = conflict.getYsitem();
/* 34 */       theirsName = conflict.getYsitemsrc();
/*    */     } else {
/*    */       
/* 37 */       yoursName = conflict.getYsitemsrc();
/* 38 */       theirsName = conflict.getTsitem();
/*    */     } 
/* 40 */     MergeNameDialog d = new MergeNameDialog(workspace, yoursName, theirsName, project);
/* 41 */     if (d.showAndGet()) {
/* 42 */       return d.getSelectedPath();
/*    */     }
/* 44 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\conflicts\DialogNameMerger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */