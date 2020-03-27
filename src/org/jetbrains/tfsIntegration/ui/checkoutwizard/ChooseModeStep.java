/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import com.intellij.ide.wizard.AbstractWizardStepEx;
/*     */ import com.intellij.ide.wizard.CommitStepException;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
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
/*     */ public class ChooseModeStep
/*     */   extends CheckoutWizardStep
/*     */ {
/*  32 */   public static final Object ID = new Object();
/*     */   
/*     */   private final CheckoutModeForm myForm;
/*     */   
/*     */   public ChooseModeStep(CheckoutWizardModel model) {
/*  37 */     super("Checkout Mode", model);
/*  38 */     this.myForm = new CheckoutModeForm();
/*  39 */     this.myForm.addListener(new ChangeListener()
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/*  42 */             ChooseModeStep.this.revalidate();
/*  43 */             ChooseModeStep.this.fireStateChanged();
/*     */           }
/*     */         });
/*  46 */     revalidate();
/*     */   }
/*     */ 
/*     */   
/*  50 */   private void revalidate() { this.myForm.setErrorMessage(validate()); }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private String validate() {
/*  55 */     if (this.myForm.isAutoModeSelected()) {
/*  56 */       String name = this.myForm.getNewWorkspaceName();
/*  57 */       if (StringUtil.isEmpty(name)) {
/*  58 */         return TFSBundle.message("workspace.name.empty", new Object[0]);
/*     */       }
/*  60 */       if (!WorkspaceInfo.isValidName(name)) {
/*  61 */         return TFSBundle.message("workspace.name.invalid", new Object[0]);
/*     */       }
/*     */     } 
/*  64 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  70 */   public Object getStepId() { return ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  76 */   public Object getNextStepId() { return ChooseWorkspaceStep.ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  82 */   public Object getPreviousStepId() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  87 */   public boolean isComplete() { return (validate() == null); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void _init() {
/*  92 */     this.myForm.setNewWorkspaceName(this.myModel.getNewWorkspaceName());
/*  93 */     this.myForm.setAutoModeSelected((this.myModel.getMode() == CheckoutWizardModel.Mode.Auto));
/*  94 */     revalidate();
/*     */   }
/*     */ 
/*     */   
/*     */   public void commit(AbstractWizardStepEx.CommitType commitType) throws CommitStepException {
/*  99 */     this.myModel.setMode(this.myForm.isAutoModeSelected() ? CheckoutWizardModel.Mode.Auto : CheckoutWizardModel.Mode.Manual);
/* 100 */     this.myModel.setNewWorkspaceName(this.myForm.getNewWorkspaceName());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 105 */   public JComponent getComponent() { return this.myForm.getContentPanel(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 110 */   public JComponent getPreferredFocusedComponent() { return this.myForm.getPreferredFocusedComponent(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 115 */   public String getHelpId() { return "reference.checkoutTFS.checkoutmode"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\ChooseModeStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */