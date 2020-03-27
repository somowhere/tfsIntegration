/*     */ package org.jetbrains.tfsIntegration.actions;
/*     */ 
/*     */ import com.intellij.openapi.fileChooser.FileChooser;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.project.DumbAware;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.openapi.vcs.AbstractVcsHelper;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinResult;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Failure;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingChange;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkingFolderInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyGetOperations;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.operations.ApplyProgress;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.ui.CreateBranchDialog;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BranchAction
/*     */   extends SingleItemAction
/*     */   implements DumbAware
/*     */ {
/*     */   protected void execute(@NotNull Project project, @NotNull WorkspaceInfo workspace, @NotNull FilePath sourceLocalPath, @NotNull ExtendedItem sourceExtendedItem) {
/*  55 */      try { String sourceServerPath = sourceExtendedItem.getSitem();
/*  56 */       CreateBranchDialog d = new CreateBranchDialog(project, workspace, sourceServerPath, (sourceExtendedItem.getType() == ItemType.Folder));
/*  57 */       if (!d.showAndGet()) {
/*     */         return;
/*     */       }
/*     */       
/*  61 */       VersionSpecBase version = d.getVersionSpec();
/*  62 */       if (version == null) {
/*  63 */         Messages.showErrorDialog(project, "Incorrect version specified", "Create Branch");
/*     */         
/*     */         return;
/*     */       } 
/*  67 */       String targetServerPath = d.getTargetPath();
/*  68 */       if (d.isCreateWorkingCopies()) {
/*  69 */         FilePath targetLocalPath = workspace.findLocalPathByServerPath(targetServerPath, true, project);
/*  70 */         if (targetLocalPath == null) {
/*  71 */           FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
/*  72 */           descriptor.setTitle("Select Local Folder");
/*  73 */           descriptor.setShowFileSystemRoots(true);
/*     */           
/*  75 */           String message = MessageFormat.format("Branch target folder ''{0}'' is not mapped. Select a local folder to create a mapping in workspace ''{1}''", new Object[] {
/*  76 */                 targetServerPath, workspace.getName() });
/*  77 */           descriptor.setDescription(message);
/*     */           
/*  79 */           VirtualFile selectedFile = FileChooser.chooseFile(descriptor, project, null);
/*  80 */           if (selectedFile == null) {
/*     */             return;
/*     */           }
/*     */           
/*  84 */           workspace.addWorkingFolderInfo(new WorkingFolderInfo(WorkingFolderInfo.Status.Active, 
/*  85 */                 TfsFileUtil.getFilePath(selectedFile), targetServerPath));
/*  86 */           workspace.saveToServer(project, workspace);
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/*  91 */       ResultWithFailures<GetOperation> createBranchResult = workspace.getServer().getVCS().createBranch(workspace.getName(), workspace.getOwnerName(), sourceServerPath, version, targetServerPath, project, 
/*  92 */           TFSBundle.message("creating.branch", new Object[0]));
/*  93 */       if (!createBranchResult.getFailures().isEmpty()) {
/*  94 */         StringBuilder s = new StringBuilder("Failed to create branch:\n");
/*  95 */         for (Failure failure : createBranchResult.getFailures()) {
/*  96 */           s.append(failure.getMessage()).append("\n");
/*     */         }
/*  98 */         Messages.showErrorDialog(project, s.toString(), "Create Branch");
/*     */         
/*     */         return;
/*     */       } 
/* 102 */       if (d.isCreateWorkingCopies()) {
/* 103 */         Ref<Collection<VcsException>> downloadErrors = new Ref(Collections.emptyList());
/* 104 */         ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> 
/* 105 */             downloadErrors.set(ApplyGetOperations.execute(project, workspace, createBranchResult.getResult(), (ApplyProgress)new ApplyProgress.ProgressIndicatorWrapper(
/*     */                   
/* 107 */                   ProgressManager.getInstance().getProgressIndicator()), null, ApplyGetOperations.DownloadMode.ALLOW)), "Creating target working copies", false, project);
/*     */ 
/*     */         
/* 110 */         if (!((Collection)downloadErrors.get()).isEmpty()) {
/* 111 */           AbstractVcsHelper.getInstance(project).showErrors(new ArrayList((Collection)downloadErrors.get()), "Create Branch");
/*     */         }
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 117 */       Collection<PendingChange> pendingChanges = workspace.getServer().getVCS().queryPendingSetsByServerItems(workspace.getName(), workspace.getOwnerName(), Collections.singletonList(targetServerPath), RecursionType.Full, project, 
/* 118 */           TFSBundle.message("loading.changes", new Object[0]));
/* 119 */       Collection<String> checkin = new ArrayList<>();
/* 120 */       for (PendingChange change : pendingChanges) {
/* 121 */         if ((new ChangeTypeMask(change.getChg())).contains(ChangeType_type0.Branch)) {
/* 122 */           checkin.add(change.getItem());
/*     */         }
/*     */       } 
/* 125 */       String comment = MessageFormat.format("Branched from {0}", new Object[] { sourceServerPath });
/*     */       
/* 127 */       ResultWithFailures<CheckinResult> checkinResult = workspace.getServer().getVCS().checkIn(workspace.getName(), workspace.getOwnerName(), checkin, comment, Collections.emptyMap(), 
/* 128 */           Collections.emptyList(), null, project, TFSBundle.message("checking.in", new Object[0]));
/*     */       
/* 130 */       if (!checkinResult.getFailures().isEmpty()) {
/* 131 */         List<VcsException> checkinErrors = TfsUtil.getVcsExceptions(checkinResult.getFailures());
/* 132 */         AbstractVcsHelper.getInstance(project).showErrors(checkinErrors, "Create Branch");
/*     */       } 
/*     */       
/* 135 */       FilePath targetLocalPath = workspace.findLocalPathByServerPath(targetServerPath, true, project);
/* 136 */       if (targetLocalPath != null) {
/* 137 */         TfsFileUtil.markDirtyRecursively(project, targetLocalPath);
/*     */       }
/*     */       
/* 140 */       String message = MessageFormat.format("''{0}'' branched successfully to ''{1}''.", new Object[] { sourceServerPath, targetServerPath });
/* 141 */       Messages.showInfoMessage(project, message, "Create Branch"); }
/*     */     
/* 143 */     catch (TfsException ex)
/* 144 */     { String message = "Failed to create branch: " + ex.getMessage();
/* 145 */       Messages.showErrorDialog(project, message, "Create Branch"); }
/*     */   
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\actions\BranchAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */