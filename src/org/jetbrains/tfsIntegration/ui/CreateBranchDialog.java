/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
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
/*    */ public class CreateBranchDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final CreateBranchForm myForm;
/*    */   
/*    */   public CreateBranchDialog(Project project, WorkspaceInfo workspace, String serverPath, boolean isDirectory) {
/* 35 */     super(project, true);
/* 36 */     this.myForm = new CreateBranchForm(project, workspace, serverPath, isDirectory);
/* 37 */     this.myForm.addListener(new ChangeListener()
/*    */         {
/*    */           public void stateChanged(ChangeEvent e) {
/* 40 */             CreateBranchDialog.this.revalidate();
/*    */           }
/*    */         });
/*    */     
/* 44 */     setTitle(TFSBundle.message("create.branch.dialog.title", new Object[0]));
/* 45 */     setSize(380, 450);
/*    */     
/* 47 */     init();
/* 48 */     revalidate();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 54 */   protected JComponent createCenterPanel() { return this.myForm.getContentPane(); }
/*    */ 
/*    */ 
/*    */   
/* 58 */   private void revalidate() { setOKActionEnabled(StringUtil.isNotEmpty(this.myForm.getTargetPath())); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 63 */   public VersionSpecBase getVersionSpec() { return this.myForm.getVersionSpec(); }
/*    */ 
/*    */ 
/*    */   
/* 67 */   public String getTargetPath() { return this.myForm.getTargetPath(); }
/*    */ 
/*    */ 
/*    */   
/* 71 */   public boolean isCreateWorkingCopies() { return this.myForm.isCreateWorkingCopies(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 76 */   public JComponent getPreferredFocusedComponent() { return this.myForm.getPreferredFocusedComponent(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 81 */   protected String getDimensionServiceKey() { return "TFS.CreateBranch"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\CreateBranchDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */