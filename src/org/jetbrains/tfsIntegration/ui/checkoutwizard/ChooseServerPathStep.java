/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import com.intellij.ide.wizard.AbstractWizardStepEx;
/*     */ import com.intellij.ide.wizard.CommitStepException;
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.ui.servertree.TfsTreeForm;
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
/*     */ public class ChooseServerPathStep
/*     */   extends CheckoutWizardStep
/*     */ {
/*  33 */   public static final Object ID = new Object();
/*     */   
/*  35 */   private final TfsTreeForm myForm = new TfsTreeForm();
/*     */   
/*     */   public ChooseServerPathStep(CheckoutWizardModel model) {
/*  38 */     super("Choose Source Path", model);
/*     */     
/*  40 */     Disposer.register((Disposable)this, (Disposable)this.myForm);
/*  41 */     this.myForm.addListener(new TfsTreeForm.SelectionListener()
/*     */         {
/*     */           public void selectionChanged() {
/*  44 */             ChooseServerPathStep.this.validate();
/*  45 */             ChooseServerPathStep.this.fireStateChanged();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  53 */   public Object getStepId() { return ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  59 */   public Object getNextStepId() { return SummaryStep.ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  65 */   public Object getPreviousStepId() { return ChooseWorkspaceStep.ID; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   public boolean isComplete() { return isAcceptable(this.myForm.getSelectedPath()); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  75 */   public JComponent getComponent() { return this.myForm.getContentPane(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void _init() {
/*  80 */     this.myForm.initialize(this.myModel.getServer(), this.myModel.getServerPath(), true, false, path -> isAcceptable(path));
/*  81 */     validate();
/*     */   }
/*     */ 
/*     */   
/*     */   public void commit(AbstractWizardStepEx.CommitType commitType) throws CommitStepException {
/*  86 */     if (isAcceptable(this.myForm.getSelectedPath())) {
/*  87 */       this.myModel.setServerPath(this.myForm.getSelectedPath());
/*     */     }
/*     */   }
/*     */   
/*     */   private boolean isAcceptable(String serverPath) {
/*  92 */     if (StringUtil.isEmpty(serverPath)) {
/*  93 */       return false;
/*     */     }
/*     */     try {
/*  96 */       if (this.myModel.getWorkspace().findLocalPathByServerPath(serverPath, true, null) == null) {
/*  97 */         return false;
/*     */       }
/*     */     }
/* 100 */     catch (TfsException e) {
/* 101 */       return false;
/*     */     } 
/* 103 */     return true;
/*     */   }
/*     */   
/*     */   private void validate() {
/* 107 */     String serverPath = this.myForm.getSelectedPath();
/* 108 */     if (StringUtil.isEmpty(serverPath)) {
/* 109 */       this.myForm.setMessage(TFSBundle.message("server.path.is.not.selected", new Object[0]), true);
/*     */     } else {
/*     */       
/*     */       try {
/* 113 */         FilePath localPath = this.myModel.getWorkspace().findLocalPathByServerPath(serverPath, true, null);
/* 114 */         if (localPath != null) {
/* 115 */           this.myForm.setMessage(TFSBundle.message("server.path.0.is.mapped.to.1", new Object[] { serverPath, localPath.getPresentableUrl() }), false);
/*     */         } else {
/*     */           
/* 118 */           this.myForm.setMessage(TFSBundle.message("no.mapping.for.0", new Object[] { serverPath }), true);
/*     */         }
/*     */       
/* 121 */       } catch (TfsException e) {
/* 122 */         this.myForm.setMessage(TFSBundle.message("failed.to.connect", new Object[] { e.getMessage() }), true);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 129 */   public JComponent getPreferredFocusedComponent() { return this.myForm.getPreferredFocusedComponent(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 134 */   public String getHelpId() { return "reference.checkoutTFS.sourcepath"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\ChooseServerPathStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */