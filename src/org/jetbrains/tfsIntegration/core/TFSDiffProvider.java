/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.vcs.AbstractVcsHelper;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vcs.changes.ContentRevision;
/*     */ import com.intellij.openapi.vcs.diff.DiffProvider;
/*     */ import com.intellij.openapi.vcs.diff.ItemLatestState;
/*     */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.DeletedState;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*     */ import java.util.Collection;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.revision.TFSContentRevision;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.Workstation;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
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
/*     */ public class TFSDiffProvider
/*     */   implements DiffProvider
/*     */ {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   
/*  46 */   public TFSDiffProvider(@NotNull Project project) { this.myProject = project; }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ItemLatestState getLastRevision(VirtualFile virtualFile) {
/*  52 */     FilePath localPath = TfsFileUtil.getFilePath(virtualFile);
/*  53 */     return getLastRevision(localPath);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public ContentRevision createFileContent(VcsRevisionNumber vcsRevisionNumber, VirtualFile virtualFile) {
/*  59 */     if (VcsRevisionNumber.NULL.equals(vcsRevisionNumber)) {
/*  60 */       return null;
/*     */     }
/*     */     
/*  63 */     FilePath path = TfsFileUtil.getFilePath(virtualFile);
/*     */     
/*     */     try {
/*  66 */       Pair<WorkspaceInfo, ExtendedItem> workspaceAndItem = TfsUtil.getWorkspaceAndExtendedItem(path, this.myProject, TFSBundle.message("loading.item", new Object[0]));
/*  67 */       if (workspaceAndItem == null || workspaceAndItem.second == null) {
/*  68 */         return null;
/*     */       }
/*  70 */       TfsRevisionNumber revisionNumber = (TfsRevisionNumber)vcsRevisionNumber;
/*     */       
/*  72 */       int itemId = (revisionNumber.getItemId() != Integer.MIN_VALUE) ? revisionNumber.getItemId() : ((ExtendedItem)workspaceAndItem.second).getItemid();
/*  73 */       return (ContentRevision)TFSContentRevision.create(this.myProject, (WorkspaceInfo)workspaceAndItem.first, revisionNumber.getValue(), itemId);
/*     */     }
/*  75 */     catch (TfsException e) {
/*  76 */       AbstractVcsHelper.getInstance(this.myProject).showError(new VcsException((Throwable)e), "TFS");
/*  77 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  85 */   public VcsRevisionNumber getCurrentRevision(VirtualFile virtualFile) { return TfsUtil.getCurrentRevisionNumber(TfsFileUtil.getFilePath(virtualFile), this.myProject, TFSBundle.message("loading.item", new Object[0])); }
/*     */ 
/*     */ 
/*     */   
/*     */   public ItemLatestState getLastRevision(FilePath localPath) {
/*     */     try {
/*  91 */       Collection<WorkspaceInfo> workspaces = Workstation.getInstance().findWorkspaces(localPath, false, this.myProject);
/*  92 */       if (workspaces.isEmpty()) {
/*  93 */         return new ItemLatestState(VcsRevisionNumber.NULL, false, false);
/*     */       }
/*  95 */       WorkspaceInfo workspace = workspaces.iterator().next();
/*     */       
/*  97 */       ExtendedItem extendedItem = workspace.getServer().getVCS().getExtendedItem(workspace.getName(), workspace.getOwnerName(), localPath, RecursionType.None, DeletedState.Any, this.myProject, 
/*  98 */           TFSBundle.message("loading.item", new Object[0]));
/*  99 */       if (extendedItem == null) {
/* 100 */         return new ItemLatestState(VcsRevisionNumber.NULL, false, false);
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 105 */       Item item = workspace.getServer().getVCS().queryItem(workspace.getName(), workspace.getOwnerName(), extendedItem.getSitem(), (VersionSpec)LatestVersionSpec.INSTANCE, DeletedState.Any, false, this.myProject, 
/* 106 */           TFSBundle.message("loading.item", new Object[0]));
/* 107 */       if (item != null) {
/* 108 */         TfsRevisionNumber tfsRevisionNumber = new TfsRevisionNumber(item.getCs(), item.getItemid());
/* 109 */         return new ItemLatestState((VcsRevisionNumber)tfsRevisionNumber, (item.getDid() == Integer.MIN_VALUE), false);
/*     */       } 
/*     */       
/* 112 */       return new ItemLatestState(VcsRevisionNumber.NULL, false, false);
/*     */     
/*     */     }
/* 115 */     catch (TfsException e) {
/* 116 */       AbstractVcsHelper.getInstance(this.myProject).showError(new VcsException(e.getMessage(), (Throwable)e), "TFS");
/* 117 */       return new ItemLatestState(VcsRevisionNumber.NULL, false, false);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 124 */   public VcsRevisionNumber getLatestCommittedRevision(VirtualFile vcsRoot) { return null; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSDiffProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */