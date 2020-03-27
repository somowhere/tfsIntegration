/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.intellij.openapi.ui.Messages;
/*    */ import java.text.MessageFormat;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
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
/*    */ public class MergeNameDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private MergeNameForm myMergeNameForm;
/*    */   private final WorkspaceInfo myWorkspace;
/*    */   private final String myLocalName;
/*    */   private final String myServerName;
/*    */   private final Project myProject;
/*    */   
/*    */   public MergeNameDialog(WorkspaceInfo workspace, String yourName, String theirsName, Project project) {
/* 38 */     super(project, false);
/* 39 */     this.myWorkspace = workspace;
/* 40 */     this.myLocalName = yourName;
/* 41 */     this.myServerName = theirsName;
/* 42 */     this.myProject = project;
/* 43 */     setTitle("Resolve Conflicting Names");
/* 44 */     setResizable(true);
/* 45 */     init();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createCenterPanel() {
/* 51 */     this.myMergeNameForm = new MergeNameForm(this.myLocalName, this.myServerName);
/* 52 */     this.myMergeNameForm.addListener(new MergeNameForm.Listener()
/*    */         {
/*    */           public void selectedPathChanged() {
/* 55 */             String errorMessage = MergeNameDialog.this.validate(MergeNameDialog.this.myMergeNameForm.getSelectedPath());
/* 56 */             MergeNameDialog.this.myMergeNameForm.setErrorText(errorMessage);
/* 57 */             MergeNameDialog.this.setOKActionEnabled((errorMessage == null));
/*    */           }
/*    */         });
/* 60 */     return this.myMergeNameForm.getPanel();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 67 */   public String getSelectedPath() {  return this.myMergeNameForm.getSelectedPath(); }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private String validate(String path) {
/* 72 */     if (path == null || path.length() == 0) {
/* 73 */       return "Path is empty";
/*    */     }
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 79 */       if (!this.myWorkspace.hasLocalPathForServerPath(path, this.myProject)) {
/* 80 */         return MessageFormat.format("No mapping found for ''{0}'' in workspace ''{1}''", new Object[] { path, this.myWorkspace.getName() });
/*    */       }
/*    */     }
/* 83 */     catch (TfsException e) {
/* 84 */       Messages.showErrorDialog(e.getMessage(), "Merge");
/* 85 */       close(1);
/*    */     } 
/* 87 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 92 */   protected String getDimensionServiceKey() { return "TFS.MergeName"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\MergeNameDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */