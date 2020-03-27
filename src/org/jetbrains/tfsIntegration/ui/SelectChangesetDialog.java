/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.Nullable;
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
/*    */ public class SelectChangesetDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final WorkspaceInfo myWorkspace;
/*    */   private SelectChangesetForm myForm;
/*    */   private final String myServerPath;
/*    */   private final boolean myRecursive;
/*    */   
/*    */   public SelectChangesetDialog(Project project, WorkspaceInfo workspace, String serverPath, boolean recursive) {
/* 34 */     super(project, true);
/* 35 */     this.myWorkspace = workspace;
/* 36 */     this.myServerPath = serverPath;
/* 37 */     this.myRecursive = recursive;
/*    */     
/* 39 */     setOKButtonText("Choose");
/* 40 */     setTitle("Find Changeset");
/* 41 */     setResizable(true);
/* 42 */     init();
/*    */     
/* 44 */     setOKActionEnabled(false);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createCenterPanel() {
/* 50 */     this.myForm = new SelectChangesetForm(this.myWorkspace, this.myServerPath, this.myRecursive);
/*    */     
/* 52 */     this.myForm.addListener(new SelectChangesetForm.Listener()
/*    */         {
/*    */           public void selectionChanged(Integer changeset) {
/* 55 */             SelectChangesetDialog.this.setOKActionEnabled((changeset != null));
/*    */           }
/*    */ 
/*    */           
/*    */           public void selected(Integer changeset) {
/* 60 */             SelectChangesetDialog.this.close(0);
/*    */           }
/*    */         });
/*    */     
/* 64 */     return this.myForm.getContentPane();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/* 69 */   public Integer getChangeset() { return this.myForm.getSelectedChangeset(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 74 */   protected String getDimensionServiceKey() { return "TFS.SelectChangeset"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\SelectChangesetDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */