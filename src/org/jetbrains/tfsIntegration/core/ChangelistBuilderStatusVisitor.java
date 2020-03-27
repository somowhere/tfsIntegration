/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.changes.Change;
/*     */ import com.intellij.openapi.vcs.changes.ChangelistBuilder;
/*     */ import com.intellij.openapi.vcs.changes.ContentRevision;
/*     */ import com.intellij.openapi.vcs.changes.CurrentContentRevision;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.revision.TFSContentRevision;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerStatus;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.StatusVisitor;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
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
/*     */ class ChangelistBuilderStatusVisitor
/*     */   implements StatusVisitor
/*     */ {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   @NotNull
/*     */   private final ChangelistBuilder myChangelistBuilder;
/*     */   @NotNull
/*     */   private final WorkspaceInfo myWorkspace;
/*     */   
/*     */   ChangelistBuilderStatusVisitor(@NotNull Project project, @NotNull ChangelistBuilder changelistBuilder, @NotNull WorkspaceInfo workspace) {
/*  41 */     this.myProject = project;
/*  42 */     this.myChangelistBuilder = changelistBuilder;
/*  43 */     this.myWorkspace = workspace;
/*     */   }
/*     */ 
/*     */   
/*     */   public void unversioned(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {
/*  48 */       if (localItemExists) {
/*  49 */       this.myChangelistBuilder.processUnversionedFile(localPath.getVirtualFile());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void checkedOutForEdit(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {
/*  56 */      if (localItemExists) {
/*     */       
/*  58 */       TFSContentRevision baseRevision = TFSContentRevision.create(this.myProject, this.myWorkspace, localPath, serverStatus.localVer, serverStatus.itemId);
/*  59 */       this.myChangelistBuilder.processChange(new Change((ContentRevision)baseRevision, CurrentContentRevision.create(localPath)), TFSVcs.getKey());
/*     */     } else {
/*     */       
/*  62 */       this.myChangelistBuilder.processLocallyDeletedFile(localPath);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void scheduledForAddition(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {
/*  70 */       if (localItemExists) {
/*  71 */       this.myChangelistBuilder.processChange(new Change(null, (ContentRevision)new CurrentContentRevision(localPath)), TFSVcs.getKey());
/*     */     } else {
/*     */       
/*  74 */       this.myChangelistBuilder.processLocallyDeletedFile(localPath);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void scheduledForDeletion(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {
/*  82 */
/*  83 */     TFSContentRevision baseRevision = TFSContentRevision.create(this.myProject, this.myWorkspace, localPath, serverStatus.localVer, serverStatus.itemId);
/*  84 */     this.myChangelistBuilder.processChange(new Change((ContentRevision)baseRevision, null), TFSVcs.getKey());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  89 */   public void outOfDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatusm) {         upToDate(localPath, localItemExists, serverStatusm); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void deleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {
/*  94 */     if (localItemExists) {
/*  95 */       this.myChangelistBuilder.processUnversionedFile(localPath.getVirtualFile());
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void upToDate(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) {
/* 101 */     if (localItemExists) {
/* 102 */       if (!this.myWorkspace.isLocal() && TfsFileUtil.isFileWritable(localPath)) {
/* 103 */         this.myChangelistBuilder.processModifiedWithoutCheckout(localPath.getVirtualFile());
/*     */       }
/*     */     } else {
/*     */       
/* 107 */       this.myChangelistBuilder.processLocallyDeletedFile(localPath);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void renamed(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {
/* 114 */      if (localItemExists) {
/*     */ 
/*     */       
/* 117 */       FilePath beforePath = this.myWorkspace.findLocalPathByServerPath(serverStatus.sourceItem, serverStatus.isDirectory, this.myProject);
/*     */ 
/*     */       
/* 120 */       TFSContentRevision before = TFSContentRevision.create(this.myProject, this.myWorkspace, beforePath, serverStatus.localVer, serverStatus.itemId);
/* 121 */       ContentRevision after = CurrentContentRevision.create(localPath);
/* 122 */       this.myChangelistBuilder.processChange(new Change((ContentRevision)before, after), TFSVcs.getKey());
/*     */     } else {
/*     */       
/* 125 */       this.myChangelistBuilder.processLocallyDeletedFile(localPath);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void renamedCheckedOut(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {
/* 132 */     if (localItemExists) {
/*     */ 
/*     */       
/* 135 */       FilePath beforePath = this.myWorkspace.findLocalPathByServerPath(serverStatus.sourceItem, serverStatus.isDirectory, this.myProject);
/*     */ 
/*     */       
/* 138 */       TFSContentRevision before = TFSContentRevision.create(this.myProject, this.myWorkspace, beforePath, serverStatus.localVer, serverStatus.itemId);
/* 139 */       ContentRevision after = CurrentContentRevision.create(localPath);
/* 140 */       this.myChangelistBuilder.processChange(new Change((ContentRevision)before, after), TFSVcs.getKey());
/*     */     } else {
/*     */       
/* 143 */       this.myChangelistBuilder.processLocallyDeletedFile(localPath);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 150 */   public void undeleted(@NotNull FilePath localPath, boolean localItemExists, @NotNull ServerStatus serverStatus) throws TfsException {       checkedOutForEdit(localPath, localItemExists, serverStatus); }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\ChangelistBuilderStatusVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */