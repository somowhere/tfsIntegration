/*     */ package org.jetbrains.tfsIntegration.core.tfs.operations;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingChange;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.TFSVcs;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.RootsCollection;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerStatus;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.StatusProvider;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.StatusVisitor;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
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
/*     */ public class ScheduleForDeletion
/*     */ {
/*     */   public static Collection<VcsException> execute(Project project, WorkspaceInfo workspace, List<ItemPath> paths) {
/*  40 */     Collection<VcsException> errors = new ArrayList<>();
/*     */     
/*     */     try {
/*  43 */       RootsCollection.ItemPathRootsCollection roots = new RootsCollection.ItemPathRootsCollection(paths);
/*     */ 
/*     */       
/*  46 */       Collection<PendingChange> pendingChanges = workspace.getServer().getVCS().queryPendingSetsByLocalPaths(workspace.getName(), workspace.getOwnerName(), (Collection)roots, RecursionType.Full, project, 
/*  47 */           TFSBundle.message("loading.changes", new Object[0]));
/*     */       
/*  49 */       Collection<String> revert = new ArrayList<>();
/*  50 */       for (PendingChange pendingChange : pendingChanges) {
/*  51 */         ChangeTypeMask change = new ChangeTypeMask(pendingChange.getChg());
/*  52 */         if (!change.contains(ChangeType_type0.Delete))
/*     */         {
/*  54 */           revert.add(pendingChange.getItem());
/*     */         }
/*     */       } 
/*     */ 
/*     */       
/*  59 */       UndoPendingChanges.UndoPendingChangesResult undoResult = UndoPendingChanges.execute(project, workspace, revert, true, ApplyProgress.EMPTY, false);
/*  60 */       errors.addAll(undoResult.errors);
/*     */       
/*  62 */       List<ItemPath> undoneRoots = new ArrayList<>(roots.size());
/*  63 */       for (ItemPath originalRoot : roots) {
/*  64 */         ItemPath undoneRoot = undoResult.undonePaths.get(originalRoot);
/*  65 */         undoneRoots.add((undoneRoot != null) ? undoneRoot : originalRoot);
/*     */       } 
/*     */       
/*  68 */       final List<FilePath> scheduleForDeletion = new ArrayList<>();
/*  69 */       StatusProvider.visitByStatus(workspace, undoneRoots, false, null, new StatusVisitor()
/*     */           {
/*     */ 
/*     */             
/*     */             public void unversioned(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException
/*     */             {
/*  75 */
/*     */             
/*     */             }
/*     */ 
/*     */             
/*  80 */             public void deleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {
/*     */                }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  86 */             public void checkedOutForEdit(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {       TFSVcs.error("Unexpected status " + serverStatus.getClass().getName() + " for " + localPath.getPresentableUrl()); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*  93 */             public void scheduledForAddition(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {       TFSVcs.error("Unexpected status " + serverStatus.getClass().getName() + " for " + localPath.getPresentableUrl()); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 100 */             public void scheduledForDeletion(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {       TFSVcs.error("Unexpected status " + serverStatus.getClass().getName() + " for " + localPath.getPresentableUrl()); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 106 */             public void outOfDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {         scheduleForDeletion.add(localPath); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 112 */             public void upToDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {         scheduleForDeletion.add(localPath); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 118 */             public void renamed(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {         TFSVcs.error("Unexpected status " + serverStatus.getClass().getName() + " for " + localPath.getPresentableUrl()); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 125 */             public void renamedCheckedOut(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {         TFSVcs.error("Unexpected status " + serverStatus.getClass().getName() + " for " + localPath.getPresentableUrl()); }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/*     */             public void undeleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {
/* 131 */                       TFSVcs.error("Unexpected status " + serverStatus.getClass().getName() + " for " + localPath.getPresentableUrl());
/*     */             }
/*     */           },  project);
/*     */ 
/*     */       
/* 136 */       ResultWithFailures<GetOperation> schedulingForDeletionResults = workspace.getServer().getVCS().scheduleForDeletionAndUpateLocalVersion(workspace.getName(), workspace.getOwnerName(), scheduleForDeletion, project, 
/* 137 */           TFSBundle.message("scheduling.for.deletion", new Object[0]));
/* 138 */       errors.addAll(TfsUtil.getVcsExceptions(schedulingForDeletionResults.getFailures()));
/*     */       
/* 140 */       for (GetOperation getOperation : schedulingForDeletionResults.getResult())
/*     */       {
/* 142 */         TfsFileUtil.markFileDirty(project, VersionControlPath.getFilePath(getOperation.getSlocal(), (getOperation.getType() == ItemType.Folder)));
/*     */       }
/*     */     }
/* 145 */     catch (TfsException e) {
/* 146 */       errors.add(new VcsException((Throwable)e));
/*     */     } 
/*     */     
/* 149 */     return errors;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\operations\ScheduleForDeletion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */