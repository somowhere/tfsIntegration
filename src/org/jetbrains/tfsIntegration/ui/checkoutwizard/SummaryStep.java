/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import com.intellij.ide.wizard.AbstractWizardStepEx;
/*     */ import com.intellij.ide.wizard.CommitStepException;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.vcsUtil.VcsUtil;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSVcs;
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
/*     */ public class SummaryStep
/*     */   extends CheckoutWizardStep
/*     */ {
/*  31 */   public static final Object ID = new Object();
/*     */   private final SummaryForm mySummaryForm;
/*     */   
/*     */   public SummaryStep(CheckoutWizardModel model) {
/*  35 */     super("Summary", model);
/*     */     
/*  37 */     this.mySummaryForm = new SummaryForm();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  43 */   public Object getStepId() {    return ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  49 */   public Object getNextStepId() { return null; }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Object getPreviousStepId() {
/*  55 */     if (this.myModel.getMode() == CheckoutWizardModel.Mode.Manual) {
/*  56 */       return ChooseServerPathStep.ID;
/*     */     }
/*     */     
/*  59 */     return ChooseLocalAndServerPathsStep.ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   public boolean isComplete() { return true; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void commit(AbstractWizardStepEx.CommitType commitType) throws CommitStepException {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public JComponent getComponent() { return this.mySummaryForm.getContentPanel(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void _init() {
/*  80 */     this.mySummaryForm.setServer(this.myModel.getServer());
/*  81 */     this.mySummaryForm.setServerPath(this.myModel.getServerPath());
/*     */     
/*  83 */     if (this.myModel.getMode() == CheckoutWizardModel.Mode.Auto) {
/*  84 */       this.mySummaryForm.setNewWorkspaceName(this.myModel.getNewWorkspaceName());
/*  85 */       this.mySummaryForm.setLocalPath(VcsUtil.getFilePath(this.myModel.getDestinationFolder()).getPresentableUrl());
/*     */     } else {
/*     */       
/*  88 */       this.mySummaryForm.setWorkspace(this.myModel.getWorkspace());
/*     */       try {
/*  90 */         FilePath localPath = this.myModel.getWorkspace().findLocalPathByServerPath(this.myModel.getServerPath(), true, null);
/*  91 */         this.mySummaryForm.setLocalPath(localPath.getPresentableUrl());
/*     */       }
/*  93 */       catch (TfsException e) {
/*     */         
/*  95 */         TFSVcs.error(e.getMessage());
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 102 */   public JComponent getPreferredFocusedComponent() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 107 */   public String getHelpId() { return "reference.checkoutTFS.summary"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\SummaryStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */