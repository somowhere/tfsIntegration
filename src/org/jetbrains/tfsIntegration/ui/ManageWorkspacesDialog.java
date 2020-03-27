/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
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
/*    */ public class ManageWorkspacesDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final Project myProject;
/*    */   
/*    */   public ManageWorkspacesDialog(Project project) {
/* 31 */     super(project, true);
/* 32 */     this.myProject = project;
/* 33 */     setTitle("Manage TFS Servers and Workspaces");
/* 34 */     setOKButtonText("Close");
/*    */     
/* 36 */     init();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 42 */   protected Action[] createActions() { (new Action[2])[0] = getOKAction(); (new Action[2])[1] = getHelpAction();   return new Action[2]; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createCenterPanel() {
/* 49 */     ManageWorkspacesForm f = new ManageWorkspacesForm(this.myProject, true);
/* 50 */     f.setShowWorkspaces(true);
/* 51 */     return f.getContentPane();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 56 */   protected String getDimensionServiceKey() { return "TFS.ManageWorkspaces"; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 61 */   protected String getHelpId() { return "project.propVCSSupport.VCSs.TFS.manage"; }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void doOKAction() {
/* 66 */     super.doOKAction();
/* 67 */     VcsDirtyScopeManager.getInstance(this.myProject).markEverythingDirty();
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ManageWorkspacesDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */