/*    */ package org.jetbrains.tfsIntegration.core.tfs.conflicts;
/*    */ 
/*    */ import com.intellij.util.WaitForProgressToShow;
/*    */ import org.jetbrains.tfsIntegration.ui.ResolveConflictsDialog;
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
/*    */ public class DialogConflictsHandler
/*    */   implements ConflictsHandler
/*    */ {
/*    */   public void resolveConflicts(ResolveConflictHelper resolveConflictHelper) {
/* 24 */     if (resolveConflictHelper.getConflicts().isEmpty()) {
/*    */       return;
/*    */     }
/*    */     
/* 28 */     WaitForProgressToShow.runOrInvokeAndWaitAboveProgress(() -> {
/* 29 */           ResolveConflictsDialog d = new ResolveConflictsDialog(resolveConflictHelper);
/* 30 */           d.show();
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\conflicts\DialogConflictsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */