/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogWrapper;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
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
/*     */ 
/*     */ 
/*     */ public class MergeBranchDialog
/*     */   extends DialogWrapper
/*     */ {
/*     */   private final String mySourcePath;
/*     */   private final boolean mySourceIsDirectory;
/*     */   private final Project myProject;
/*     */   private final WorkspaceInfo myWorkspace;
/*     */   private MergeBranchForm myMergeBranchForm;
/*     */   
/*     */   public MergeBranchDialog(Project project, WorkspaceInfo workspace, String sourcePath, boolean sourceIsDirectory, String title) {
/*  39 */     super(project, true);
/*  40 */     this.myProject = project;
/*  41 */     this.myWorkspace = workspace;
/*  42 */     this.mySourcePath = sourcePath;
/*  43 */     this.mySourceIsDirectory = sourceIsDirectory;
/*     */     
/*  45 */     setTitle(title);
/*  46 */     setResizable(true);
/*  47 */     init();
/*     */   }
/*     */ 
/*     */   
/*  51 */   public String getSourcePath() { return this.myMergeBranchForm.getSourcePath(); }
/*     */ 
/*     */ 
/*     */   
/*  55 */   public String getTargetPath() { return this.myMergeBranchForm.getTargetPath(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  61 */   public VersionSpecBase getFromVersion() { return this.myMergeBranchForm.getFromVersion(); }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  66 */   public VersionSpecBase getToVersion() { return this.myMergeBranchForm.getToVersion(); }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   protected JComponent createCenterPanel() {
/*  72 */     this.myMergeBranchForm = new MergeBranchForm(this.myProject, this.myWorkspace, this.mySourcePath, this.mySourceIsDirectory, getTitle());
/*     */     
/*  74 */     this.myMergeBranchForm.addListener(new MergeBranchForm.Listener()
/*     */         {
/*     */           public void stateChanged(boolean canFinish) {
/*  77 */             MergeBranchDialog.this.setOKActionEnabled(canFinish);
/*     */           }
/*     */         });
/*     */     
/*  81 */     return this.myMergeBranchForm.getContentPanel();
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doOKAction() {
/*  86 */     this.myMergeBranchForm.close();
/*  87 */     super.doOKAction();
/*     */   }
/*     */ 
/*     */   
/*     */   public void doCancelAction() {
/*  92 */     this.myMergeBranchForm.close();
/*  93 */     super.doCancelAction();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  98 */   protected String getDimensionServiceKey() { return "TFS.MergeBranch"; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   public JComponent getPreferredFocusedComponent() { return this.myMergeBranchForm.getPreferredFocusedComponent(); }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\MergeBranchDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */