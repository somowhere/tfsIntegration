/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.conflicts.ResolveConflictHelper;
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
/*    */ public class ResolveConflictsDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final ResolveConflictHelper myResolveConflictHelper;
/*    */   
/*    */   public ResolveConflictsDialog(ResolveConflictHelper resolveConflictHelper) {
/* 31 */     super(true);
/* 32 */     this.myResolveConflictHelper = resolveConflictHelper;
/* 33 */     setTitle("Resolve Conflicts");
/* 34 */     setResizable(true);
/* 35 */     setOKButtonText("Close");
/* 36 */     init();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void doOKAction() {
/* 41 */     for (Conflict conflict : this.myResolveConflictHelper.getConflicts()) {
/* 42 */       this.myResolveConflictHelper.skip(conflict);
/*    */     }
/* 44 */     super.doOKAction();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createCenterPanel() {
/* 50 */     ResolveConflictsForm resolveConflictsForm = new ResolveConflictsForm(this.myResolveConflictHelper);
/* 51 */     resolveConflictsForm.addListener(new ResolveConflictsForm.Listener()
/*    */         {
/*    */           public void close() {
/* 54 */             ResolveConflictsDialog.this.close(0);
/*    */           }
/*    */         });
/* 57 */     return resolveConflictsForm.getPanel();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 63 */   protected Action[] createActions() { (new Action[1])[0] = getOKAction(); return new Action[1]; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 68 */   protected String getDimensionServiceKey() { return "TFS.ResolveConflicts"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ResolveConflictsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */