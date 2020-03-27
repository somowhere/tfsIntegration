/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import com.intellij.ide.wizard.AbstractWizardStepEx;
/*     */ import com.intellij.ide.wizard.CommitStepException;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.application.WriteAction;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vfs.LocalFileSystem;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
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
/*     */ public class ChooseLocalAndServerPathsStep
/*     */   extends CheckoutWizardStep
/*     */ {
/*  35 */   public static final Object ID = new Object();
/*     */   
/*  37 */   private final LocalAndServerPathsForm myPathsForm = new LocalAndServerPathsForm();
/*     */   
/*     */   public ChooseLocalAndServerPathsStep(CheckoutWizardModel model) {
/*  40 */     super("Choose Source and Destination Paths", model);
/*  41 */     Disposer.register((Disposable)this, this.myPathsForm);
/*     */     
/*  43 */     this.myPathsForm.addListener(new ChangeListener()
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/*  46 */             ChooseLocalAndServerPathsStep.this.updateMessage();
/*  47 */             ChooseLocalAndServerPathsStep.this.fireStateChanged();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  55 */   public Object getStepId() {  return ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  61 */   public Object getNextStepId() { return SummaryStep.ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  67 */   public Object getPreviousStepId() { return ChooseWorkspaceStep.ID; }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isComplete() {
/*  72 */     if (validateLocalPath(this.myPathsForm.getLocalPath()) != null) {
/*  73 */       return false;
/*     */     }
/*  75 */     if (validateServerPath(this.myPathsForm.getServerPath()) != null) {
/*  76 */       return false;
/*     */     }
/*  78 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  83 */   public JComponent getComponent() { return this.myPathsForm.getContentPanel(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void _init() {
/*  88 */     this.myPathsForm.initialize(this.myModel.getServer(), this.myModel.getServerPath());
/*  89 */     updateMessage();
/*     */   }
/*     */ 
/*     */   
/*     */   public void commit(AbstractWizardStepEx.CommitType commitType) throws CommitStepException {
/*  94 */     if (validateLocalPath(this.myPathsForm.getLocalPath()) == null) {
/*  95 */       this.myModel.setDestinationFolder(this.myPathsForm.getLocalPath());
/*     */     }
/*     */     
/*  98 */     if (validateServerPath(this.myPathsForm.getServerPath()) == null) {
/*  99 */       this.myModel.setServerPath(this.myPathsForm.getServerPath());
/*     */     }
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String validateLocalPath(String path) {
/* 105 */     if (StringUtil.isEmpty(path)) {
/* 106 */       return TFSBundle.message("destination.path.not.specified", new Object[0]);
/*     */     }
/* 108 */     VirtualFile file = (VirtualFile)WriteAction.compute(() -> 
/*     */         
/* 110 */         LocalFileSystem.getInstance().refreshAndFindFileByPath(path));
/*     */     
/* 112 */     if (file != null && file.exists() && !file.isDirectory()) {
/* 113 */       return TFSBundle.message("destination.path.is.not.a.file", new Object[0]);
/*     */     }
/* 115 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private static String validateServerPath(String path) {
/* 120 */     if (StringUtil.isEmpty(path)) {
/* 121 */       return TFSBundle.message("source.path.is.empty", new Object[0]);
/*     */     }
/* 123 */     return null;
/*     */   }
/*     */   
/*     */   private void updateMessage() {
/* 127 */     String errorMessage = validateServerPath(this.myPathsForm.getServerPath());
/* 128 */     if (errorMessage == null) {
/* 129 */       errorMessage = validateLocalPath(this.myPathsForm.getLocalPath());
/*     */     }
/* 131 */     if (errorMessage != null) {
/* 132 */       this.myPathsForm.setMessage(errorMessage, true);
/*     */     } else {
/*     */       
/* 135 */       this.myPathsForm.setMessage(TFSBundle.message("mapping.will.be.created", new Object[] { this.myModel.getNewWorkspaceName() }), false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 141 */   public JComponent getPreferredFocusedComponent() { return this.myPathsForm.getPreferredFocusedComponent(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 146 */   public String getHelpId() { return "reference.checkoutTFS.choosepaths"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\ChooseLocalAndServerPathsStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */