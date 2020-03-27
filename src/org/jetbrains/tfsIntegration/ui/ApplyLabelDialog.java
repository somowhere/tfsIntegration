/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogWrapper;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelItemSpec;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionControlLabel;
/*     */ import java.awt.event.ComponentAdapter;
/*     */ import java.awt.event.ComponentEvent;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
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
/*     */ 
/*     */ public class ApplyLabelDialog
/*     */   extends DialogWrapper
/*     */ {
/*     */   private final Project myProject;
/*     */   private final WorkspaceInfo myWorkspace;
/*     */   private final String mySourcePath;
/*     */   private ApplyLabelForm myApplyLabelForm;
/*     */   
/*     */   public ApplyLabelDialog(Project project, WorkspaceInfo workspace, String sourcePath) {
/*  44 */     super(project, true);
/*  45 */     this.myProject = project;
/*  46 */     this.myWorkspace = workspace;
/*  47 */     this.mySourcePath = sourcePath;
/*     */     
/*  49 */     setTitle("Apply Label");
/*     */     
/*  51 */     init();
/*  52 */     setOKActionEnabled(false);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected JComponent createCenterPanel() {
/*  58 */     this.myApplyLabelForm = new ApplyLabelForm(this.myProject, this.myWorkspace, this.mySourcePath);
/*     */     
/*  60 */     getWindow().addComponentListener(new ComponentAdapter()
/*     */         {
/*     */           public void componentShown(ComponentEvent e) {
/*  63 */             ApplyLabelDialog.this.myApplyLabelForm.addItems();
/*     */           }
/*     */         });
/*     */     
/*  67 */     this.myApplyLabelForm.addListener(new ApplyLabelForm.Listener()
/*     */         {
/*     */           public void dataChanged(String labelName, int visibleItemsCount) {
/*  70 */             ApplyLabelDialog.this.setOKActionEnabled((visibleItemsCount > 0 && labelName.length() > 0));
/*     */           }
/*     */         });
/*     */     
/*  74 */     return this.myApplyLabelForm.getContentPane();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doOKAction() {
/*     */     try {
/*  81 */       List<VersionControlLabel> labels = this.myWorkspace.getServer().getVCS().queryLabels(getLabelName(), null, null, false, null, null, false, this.myApplyLabelForm.getContentPane(), 
/*  82 */           TFSBundle.message("checking.existing.labels", new Object[0]));
/*  83 */       if (!labels.isEmpty()) {
/*  84 */         String message = MessageFormat.format("Label ''{0}'' already exists.\nDo you want to update it?", new Object[] { getLabelName() });
/*  85 */         if (Messages.showOkCancelDialog(this.myProject, message, getTitle(), "Update Label", "Cancel", Messages.getQuestionIcon()) != 0) {
/*     */           return;
/*     */         }
/*     */       }
/*     */     
/*     */     }
/*  91 */     catch (TfsException e) {
/*  92 */       Messages.showErrorDialog(this.myProject, e.getMessage(), getTitle());
/*     */       return;
/*     */     } 
/*  95 */     super.doOKAction();
/*     */   }
/*     */ 
/*     */   
/*  99 */   public String getLabelName() { return this.myApplyLabelForm.getLabelName(); }
/*     */ 
/*     */ 
/*     */   
/* 103 */   public String getLabelComment() { return this.myApplyLabelForm.getLabelComment(); }
/*     */ 
/*     */ 
/*     */   
/* 107 */   public List<LabelItemSpec> getLabelItemSpecs() { return this.myApplyLabelForm.getLabelItemSpecs(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   protected String getDimensionServiceKey() { return "TFS.ApplyLabel"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ApplyLabelDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */