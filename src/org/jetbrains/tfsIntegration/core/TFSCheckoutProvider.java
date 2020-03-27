/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.application.ApplicationNamesInfo;
/*     */ import com.intellij.openapi.application.ModalityState;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.project.ProjectManager;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.openapi.vcs.CheckoutProvider;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.vcsUtil.VcsUtil;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*     */ import java.io.File;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkingFolderInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.Workstation;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
/*     */ import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.CheckoutWizard;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.CheckoutWizardModel;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.CheckoutWizardStep;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.ChooseLocalAndServerPathsStep;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.ChooseModeStep;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.ChooseServerPathStep;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.ChooseWorkspaceStep;
/*     */ import org.jetbrains.tfsIntegration.ui.checkoutwizard.SummaryStep;
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
/*     */ public class TFSCheckoutProvider
/*     */   implements CheckoutProvider
/*     */ {
/*     */   public void doCheckout(@NotNull Project project, @Nullable CheckoutProvider.Listener listener) {
/*  59 */        CheckoutWizardModel model = new CheckoutWizardModel();
/*     */     
/*  61 */     List<CheckoutWizardStep> steps = Arrays.asList(new CheckoutWizardStep[] { (CheckoutWizardStep)new ChooseModeStep(model), (CheckoutWizardStep)new ChooseWorkspaceStep(project, model), (CheckoutWizardStep)new ChooseLocalAndServerPathsStep(model), (CheckoutWizardStep)new ChooseServerPathStep(model), (CheckoutWizardStep)new SummaryStep(model) });
/*     */     
/*  63 */     CheckoutWizard w = new CheckoutWizard(project, steps, model);
/*  64 */     if (w.showAndGet()) {
/*  65 */       doCheckout(model, listener);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void doCheckout(CheckoutWizardModel model, CheckoutProvider.Listener listener) {
/*  70 */     Collection<VcsException> errors = new ArrayList<>();
/*  71 */     Ref<FilePath> localRoot = new Ref();
/*     */     
/*  73 */     Runnable checkoutRunnable = () -> {
/*  74 */         ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
/*     */         try {
/*     */           WorkspaceInfo workspace;
/*  77 */           if (model.getMode() == CheckoutWizardModel.Mode.Auto) {
/*  78 */             workspace = createWorkspace(model);
/*     */           } else {
/*     */             
/*  81 */             workspace = model.getWorkspace();
/*     */           } 
/*  83 */           localRoot.set(workspace.findLocalPathByServerPath(model.getServerPath(), true, null));
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  88 */           List<GetOperation> operations = workspace.getServer().getVCS().get(workspace.getName(), workspace.getOwnerName(), model.getServerPath(), (VersionSpec)LatestVersionSpec.INSTANCE, RecursionType.Full, null, null);
/*     */ 
/*     */ 
/*     */           
/*  92 */           Collection<VcsException> applyErrors = ApplyGetOperations.execute(ProjectManager.getInstance().getDefaultProject(), workspace, operations, (ApplyProgress)new ApplyProgress.ProgressIndicatorWrapper(progressIndicator), null, ApplyGetOperations.DownloadMode.ALLOW);
/*     */ 
/*     */           
/*  95 */           errors.addAll(applyErrors);
/*     */         }
/*  97 */         catch (TfsException e) {
/*  98 */           errors.add(new VcsException(e.getMessage(), (Throwable)e));
/*     */         } 
/*     */       };
/*     */     
/* 102 */     ProgressManager.getInstance()
/* 103 */       .runProcessWithProgressSynchronously(checkoutRunnable, "Checkout from TFS", true, ProjectManager.getInstance().getDefaultProject());
/*     */     
/* 105 */     if (errors.isEmpty()) {
/* 106 */       Runnable listenerNotificationRunnable = () -> {
/* 107 */           if (listener != null) {
/* 108 */             if (errors.isEmpty()) {
/* 109 */               listener.directoryCheckedOut(new File(((FilePath)localRoot.get()).getPath()), TFSVcs.getKey());
/*     */             }
/* 111 */             listener.checkoutCompleted();
/*     */           } 
/*     */         };
/*     */       
/* 115 */       VirtualFile vf = VcsUtil.getVirtualFile(((FilePath)localRoot.get()).getPath());
/* 116 */       if (vf != null) {
/* 117 */         vf.refresh(true, true, () -> {
/* 118 */               ModalityState current = ModalityState.current();
/* 119 */               ApplicationManager.getApplication().invokeLater(listenerNotificationRunnable, current);
/*     */             });
/*     */       } else {
/*     */         
/* 123 */         listenerNotificationRunnable.run();
/*     */       } 
/*     */     } else {
/*     */       
/* 127 */       StringBuilder errorMessage = new StringBuilder("The following errors occurred during checkout:\n\n");
/* 128 */       for (VcsException e : errors) {
/* 129 */         errorMessage.append(e.getMessage()).append("\n");
/*     */       }
/* 131 */       Messages.showErrorDialog(errorMessage.toString(), TFSBundle.message("checkout.from.tfs.error.dialog.title", new Object[0]));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NonNls
/* 139 */   public String getVcsName() { return "_TFS"; }
/*     */ 
/*     */   
/*     */   private static WorkspaceInfo createWorkspace(CheckoutWizardModel model) throws TfsException {
/* 143 */     WorkspaceInfo workspace = new WorkspaceInfo(model.getServer(), model.getServer().getQualifiedUsername(), Workstation.getComputerName());
/* 144 */     workspace.setName(model.getNewWorkspaceName());
/* 145 */     workspace.setComment(TFSBundle.message("automatic.workspace.comment", new Object[] {
/* 146 */             ApplicationNamesInfo.getInstance().getFullProductName(), model.getServerPath() }));
/* 147 */     FilePath localPath = VcsUtil.getFilePath(model.getDestinationFolder(), true);
/* 148 */     WorkingFolderInfo workingFolder = new WorkingFolderInfo(WorkingFolderInfo.Status.Active, localPath, model.getServerPath());
/* 149 */     workspace.addWorkingFolderInfo(workingFolder);
/*     */     try {
/* 151 */       workspace.saveToServer(null, null);
/*     */     }
/* 153 */     catch (TfsException e) {
/* 154 */       String errorMessage = MessageFormat.format("Cannot create workspace ''{0}''. {1}", new Object[] { workspace.getName(), e.getMessage() });
/* 155 */       throw new OperationFailedException(errorMessage);
/*     */     } 
/* 157 */     return workspace;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSCheckoutProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */