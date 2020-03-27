/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogWrapper;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkingFolderInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class WorkspaceDialog
/*     */   extends DialogWrapper
/*     */ {
/*     */   private final WorkspaceForm myForm;
/*     */   
/*     */   public WorkspaceDialog(Project project, @NotNull ServerInfo server) {
/*  39 */     super(project, true);
/*  40 */     this.myForm = new WorkspaceForm(project, server);
/*  41 */     this.myForm.addListener(new ChangeListener()
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/*  44 */             WorkspaceDialog.this.revalidate();
/*     */           }
/*     */         });
/*     */     
/*  48 */     init();
/*     */     
/*  50 */     setTitle(TFSBundle.message("create.workspace.dialog.title", new Object[0]));
/*  51 */     setOKButtonText(TFSBundle.message("create.workspace.dialog.ok.button.text", new Object[0]));
/*  52 */     revalidate();
/*     */   }
/*     */   
/*     */   public WorkspaceDialog(Project project, @NotNull WorkspaceInfo workspace) {
/*  56 */     super(project, true);
/*     */     
/*  58 */     this.myForm = new WorkspaceForm(project, workspace);
/*  59 */     this.myForm.addListener(new ChangeListener()
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/*  62 */             WorkspaceDialog.this.revalidate();
/*     */           }
/*     */         });
/*     */     
/*  66 */     init();
/*     */     
/*  68 */     setTitle(TFSBundle.message("edit.workspace.dialog.title", new Object[0]));
/*  69 */     setOKButtonText(TFSBundle.message("create.workspace.dialog.ok.button.text", new Object[0]));
/*     */     
/*  71 */     revalidate();
/*     */   }
/*     */   
/*     */   private void revalidate() {
/*  75 */     String errorMessage = getErrorMessage();
/*  76 */     setOKActionEnabled((errorMessage == null));
/*  77 */     this.myForm.setErrorMessage(errorMessage);
/*     */   }
/*     */   
/*     */   private String getErrorMessage() {
/*  81 */     String message = validate(this.myForm.getWorkspaceName());
/*  82 */     if (message == null) {
/*  83 */       message = this.myForm.validateWorkingFolders();
/*     */     }
/*  85 */     return message;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  90 */   public JComponent getPreferredFocusedComponent() { return this.myForm.getPreferredFocusedComponent(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  95 */   protected String getDimensionServiceKey() { return "TFS.ManageWorkspace"; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   protected JComponent createCenterPanel() { return this.myForm.getContentPane(); }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private static String validate(String workspaceName) {
/* 105 */     if (StringUtil.isEmptyOrSpaces(workspaceName)) {
/* 106 */       return TFSBundle.message("workspace.name.empty", new Object[0]);
/*     */     }
/*     */     
/* 109 */     if (!WorkspaceInfo.isValidName(workspaceName)) {
/* 110 */       return TFSBundle.message("workspace.name.invalid", new Object[0]);
/*     */     }
/*     */     
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   
/* 117 */   public String getWorkspaceName() { return this.myForm.getWorkspaceName(); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/* 122 */   public WorkspaceInfo.Location getWorkspaceLocation() {  return this.myForm.getWorkspaceLocation(); }
/*     */ 
/*     */ 
/*     */   
/* 126 */   public String getWorkspaceComment() { return this.myForm.getWorkspaceComment(); }
/*     */ 
/*     */ 
/*     */   
/* 130 */   public List<WorkingFolderInfo> getWorkingFolders() { return this.myForm.getWorkingFolders(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 135 */   protected String getHelpId() { return "project.propVCSSupport.VCSs.TFS.manage.connect.createWorkspace"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\WorkspaceDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */