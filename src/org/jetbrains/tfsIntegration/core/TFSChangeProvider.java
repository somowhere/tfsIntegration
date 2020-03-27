/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vcs.changes.ChangeListManager;
/*     */ import com.intellij.openapi.vcs.changes.ChangeListManagerGate;
/*     */ import com.intellij.openapi.vcs.changes.ChangeProvider;
/*     */ import com.intellij.openapi.vcs.changes.ChangelistBuilder;
/*     */ import com.intellij.openapi.vcs.changes.VcsDirtyScope;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.RootsCollection;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.StatusProvider;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
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
/*     */ public class TFSChangeProvider
/*     */   implements ChangeProvider
/*     */ {
/*     */   private final Project myProject;
/*     */   
/*  43 */   public TFSChangeProvider(Project project) { this.myProject = project; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  48 */   public boolean isModifiedDocumentTrackingRequired() { return true; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void doCleanup(List<VirtualFile> files) {}
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void getChanges(@NotNull VcsDirtyScope dirtyScope, @NotNull final ChangelistBuilder builder, @NotNull final ProgressIndicator progress, @NotNull ChangeListManagerGate addGate) throws VcsException {
/*  60 */                 if (this.myProject.isDisposed()) {
/*     */       return;
/*     */     }
/*  63 */     if (builder == null) {
/*     */       return;
/*     */     }
/*     */     
/*  67 */     progress.setText("Processing changes");
/*     */ 
/*     */     
/*  70 */     RootsCollection.FilePathRootsCollection roots = new RootsCollection.FilePathRootsCollection();
/*  71 */     roots.addAll(dirtyScope.getRecursivelyDirtyDirectories());
/*     */     
/*  73 */     ChangeListManager changeListManager = ChangeListManager.getInstance(this.myProject);
/*  74 */     for (FilePath dirtyFile : dirtyScope.getDirtyFiles()) {
/*     */       
/*  76 */       if (dirtyFile.getVirtualFile() == null || !changeListManager.isIgnoredFile(dirtyFile.getVirtualFile())) {
/*  77 */         roots.add(dirtyFile);
/*     */       }
/*     */     } 
/*     */     
/*  81 */     if (roots.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/*  86 */       final Ref<Boolean> mappingFound = Ref.create(Boolean.valueOf(false));
/*     */       
/*  88 */       WorkstationHelper.processByWorkspaces((Collection)roots, true, this.myProject, new WorkstationHelper.VoidProcessDelegate()
/*     */           {
/*     */             public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException
/*     */             {
/*  92 */               StatusProvider.visitByStatus(workspace, paths, true, progress, new ChangelistBuilderStatusVisitor(TFSChangeProvider.this.myProject, builder, workspace), TFSChangeProvider.this.myProject);
/*  93 */               mappingFound.set(Boolean.valueOf(true));
/*     */             }
/*     */           });
/*  96 */       if (!((Boolean)mappingFound.get()).booleanValue()) {
/*     */         String message;
/*  98 */         if (roots.size() > 1) {
/*  99 */           message = "Team Foundation Server mappings not found";
/*     */         } else {
/*     */           
/* 102 */           FilePath orphan = roots.iterator().next();
/* 103 */           message = MessageFormat.format("Team Foundation Server mappings not found for ''{0}''", new Object[] { orphan.getPresentableUrl() });
/*     */         } 
/* 105 */         throw new VcsException(message);
/*     */       }
/*     */     
/* 108 */     } catch (TfsException e) {
/* 109 */       throw new VcsException(e.getMessage(), (Throwable)e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSChangeProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */