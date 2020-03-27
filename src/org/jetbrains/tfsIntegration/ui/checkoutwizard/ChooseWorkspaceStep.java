/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import com.intellij.ide.wizard.AbstractWizardStepEx;
/*     */ import com.intellij.ide.wizard.CommitStepCancelledException;
/*     */ import com.intellij.ide.wizard.CommitStepException;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.config.TfsServerConnectionHelper;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkingFolderInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.UserCancelledException;
/*     */ import org.jetbrains.tfsIntegration.ui.ManageWorkspacesForm;
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
/*     */ public class ChooseWorkspaceStep
/*     */   extends CheckoutWizardStep
/*     */ {
/*  37 */   public static final Object ID = new Object();
/*     */   
/*     */   private final ManageWorkspacesForm myManageWorkspacesForm;
/*     */   
/*     */   public ChooseWorkspaceStep(Project project, CheckoutWizardModel model) {
/*  42 */     super("Source Workspace", model);
/*  43 */     this.myManageWorkspacesForm = new ManageWorkspacesForm(project, false);
/*  44 */     this.myManageWorkspacesForm.addSelectionListener(new ManageWorkspacesForm.Listener()
/*     */         {
/*     */           public void selectionChanged() {
/*  47 */             ChooseWorkspaceStep.this.fireStateChanged();
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
/*     */   public Object getNextStepId() {
/*  60 */     if (this.myModel.getMode() == CheckoutWizardModel.Mode.Manual) {
/*  61 */       return ChooseServerPathStep.ID;
/*     */     }
/*     */     
/*  64 */     return ChooseLocalAndServerPathsStep.ID;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  70 */   public Object getPreviousStepId() { return ChooseModeStep.ID; }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isComplete() {
/*  75 */     if (this.myModel.getMode() == CheckoutWizardModel.Mode.Manual) {
/*  76 */       return (this.myManageWorkspacesForm.getSelectedWorkspace() != null);
/*     */     }
/*     */     
/*  79 */     return (this.myManageWorkspacesForm.getSelectedServer() != null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void _init() {
/*  85 */     if (this.myModel.getMode() == CheckoutWizardModel.Mode.Manual) {
/*  86 */       setTitle("Source Workspace");
/*  87 */       this.myManageWorkspacesForm.setShowWorkspaces(true);
/*  88 */       if (this.myModel.getWorkspace() != null) {
/*  89 */         this.myManageWorkspacesForm.setSelectedWorkspace(this.myModel.getWorkspace());
/*     */       } else {
/*  91 */         this.myManageWorkspacesForm.selectFirstWorkspace();
/*     */       } 
/*     */     } else {
/*     */       
/*  95 */       setTitle("Source Server");
/*  96 */       this.myManageWorkspacesForm.setShowWorkspaces(false);
/*  97 */       if (this.myModel.getServer() != null) {
/*  98 */         this.myManageWorkspacesForm.setSelectedServer(this.myModel.getServer());
/*     */       } else {
/*     */         
/* 101 */         this.myManageWorkspacesForm.selectFirstServer();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void commit(AbstractWizardStepEx.CommitType commitType) throws CommitStepException {
/* 108 */     if (this.myModel.getMode() == CheckoutWizardModel.Mode.Manual) {
/* 109 */       WorkspaceInfo workspace = this.myManageWorkspacesForm.getSelectedWorkspace();
/* 110 */       if (workspace != null) {
/* 111 */         this.myModel.setServer(workspace.getServer());
/*     */       }
/* 113 */       this.myModel.setWorkspace(workspace);
/* 114 */       if (commitType == AbstractWizardStepEx.CommitType.Next || commitType == AbstractWizardStepEx.CommitType.Finish) {
/*     */         
/*     */         try {
/*     */ 
/*     */           
/* 119 */           workspace.loadFromServer(null, true);
/* 120 */           List<WorkingFolderInfo> workingFolders = workspace.getWorkingFolders(null);
/* 121 */           if (!workingFolders.isEmpty()) {
/* 122 */             this.myModel.setServerPath(((WorkingFolderInfo)workingFolders.get(0)).getServerPath());
/*     */           } else {
/*     */             
/* 125 */             String message = MessageFormat.format("Workspace ''{0}'' has no mappings.", new Object[] { workspace.getName() });
/* 126 */             throw new CommitStepException(message);
/*     */           }
/*     */         
/* 129 */         } catch (UserCancelledException e) {
/* 130 */           throw new CommitStepCancelledException();
/*     */         }
/* 132 */         catch (TfsException e) {
/* 133 */           throw new CommitStepException(e.getMessage());
/*     */         } 
/*     */       }
/*     */     } else {
/*     */       
/* 138 */       ServerInfo server = this.myManageWorkspacesForm.getSelectedServer();
/* 139 */       this.myModel.setServer(server);
/* 140 */       if (commitType == AbstractWizardStepEx.CommitType.Next || commitType == AbstractWizardStepEx.CommitType.Finish) {
/*     */         try {
/* 142 */           TfsServerConnectionHelper.ensureAuthenticated(this.myManageWorkspacesForm.getContentPane(), server.getUri(), true);
/*     */         }
/* 144 */         catch (UserCancelledException e) {
/* 145 */           throw new CommitStepCancelledException();
/*     */         }
/* 147 */         catch (TfsException e) {
/* 148 */           throw new CommitStepException(e.getMessage());
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 156 */   public JComponent getComponent() { return this.myManageWorkspacesForm.getContentPane(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 161 */   public JComponent getPreferredFocusedComponent() { return this.myManageWorkspacesForm.getPreferredFocusedComponent(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public String getHelpId() {
/* 166 */     if (this.myModel.getMode() == CheckoutWizardModel.Mode.Manual) {
/* 167 */       return "reference.checkoutTFS.sourceworkspace";
/*     */     }
/*     */     
/* 170 */     return "reference.checkoutTFS.sourceserver";
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\ChooseWorkspaceStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */