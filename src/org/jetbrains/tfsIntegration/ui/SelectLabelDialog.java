/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionControlLabel;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ public class SelectLabelDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final WorkspaceInfo myWorkspace;
/*    */   private SelectLabelForm mySelectLabelForm;
/*    */   
/*    */   public SelectLabelDialog(Project project, WorkspaceInfo workspace) {
/* 34 */     super(project, true);
/* 35 */     this.myWorkspace = workspace;
/* 36 */     setTitle("Choose Label");
/* 37 */     setOKButtonText("Choose");
/*    */     
/* 39 */     init();
/* 40 */     updateButtons();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createCenterPanel() {
/* 46 */     this.mySelectLabelForm = new SelectLabelForm(this, this.myWorkspace);
/* 47 */     this.mySelectLabelForm.addListener(new SelectLabelForm.Listener()
/*    */         {
/*    */           public void selectionChanged() {
/* 50 */             SelectLabelDialog.this.updateButtons();
/*    */           }
/*    */         });
/* 53 */     return this.mySelectLabelForm.getContentPane();
/*    */   }
/*    */ 
/*    */   
/* 57 */   private void updateButtons() { setOKActionEnabled(this.mySelectLabelForm.isLabelSelected()); }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String getLabelString() {
/* 62 */     VersionControlLabel label = this.mySelectLabelForm.getLabel();
/* 63 */       return label.getName() + "@" + label.getScope();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 68 */   protected String getDimensionServiceKey() { return "TFS.SelectLabel"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\SelectLabelDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */