/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.actionSystem.AnAction;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsConfiguration;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vcs.history.DiffFromHistoryHandler;
/*     */ import com.intellij.openapi.vcs.history.HistoryAsTreeProvider;
/*     */ import com.intellij.openapi.vcs.history.VcsAbstractHistorySession;
/*     */ import com.intellij.openapi.vcs.history.VcsAppendableHistorySessionPartner;
/*     */ import com.intellij.openapi.vcs.history.VcsDependentHistoryComponents;
/*     */ import com.intellij.openapi.vcs.history.VcsFileRevision;
/*     */ import com.intellij.openapi.vcs.history.VcsHistoryProvider;
/*     */ import com.intellij.openapi.vcs.history.VcsHistorySession;
/*     */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Changeset;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TFSHistoryProvider
/*     */   implements VcsHistoryProvider
/*     */ {
/*     */   @NotNull
/*     */   private final Project myProject;
/*     */   
/*  50 */   public TFSHistoryProvider(@NotNull Project project) { this.myProject = project; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  55 */   public VcsDependentHistoryComponents getUICustomization(VcsHistorySession session, JComponent forShortcutRegistration) { return VcsDependentHistoryComponents.createOnlyColumns(ColumnInfo.EMPTY_ARRAY); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   public AnAction[] getAdditionalActions(Runnable refresher) { return AnAction.EMPTY_ARRAY; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   public boolean isDateOmittable() { return false; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   @NonNls
/*  72 */   public String getHelpId() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public VcsHistorySession createSessionFor(FilePath filePath) throws VcsException {
/*     */     try {
/*  80 */       Pair<WorkspaceInfo, ExtendedItem> workspaceAndItem = TfsUtil.getWorkspaceAndExtendedItem(filePath, this.myProject, TFSBundle.message("loading.item", new Object[0]));
/*  81 */       if (workspaceAndItem == null || workspaceAndItem.second == null) {
/*  82 */         return null;
/*     */       }
/*     */ 
/*     */       
/*  86 */       List<TFSFileRevision> revisions = getRevisions(this.myProject, ((ExtendedItem)workspaceAndItem.second).getSitem(), filePath.isDirectory(), (WorkspaceInfo)workspaceAndItem.first, (VersionSpecBase)LatestVersionSpec.INSTANCE);
/*     */       
/*  88 */       if (revisions.isEmpty()) {
/*  89 */         return null;
/*     */       }
/*     */       
/*  92 */       return (VcsHistorySession)createSession(workspaceAndItem, (List)revisions);
/*     */     }
/*  94 */     catch (TfsException e) {
/*  95 */       throw new VcsException((Throwable)e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static VcsAbstractHistorySession createSession(final Pair<WorkspaceInfo, ExtendedItem> workspaceAndItem, List<? extends VcsFileRevision> revisions) {
/* 101 */     return new VcsAbstractHistorySession(revisions)
/*     */       {
/*     */         public VcsRevisionNumber calcCurrentRevisionNumber() {
/* 104 */           return TfsUtil.getCurrentRevisionNumber((ExtendedItem)workspaceAndItem.second);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 109 */         public HistoryAsTreeProvider getHistoryAsTreeProvider() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 114 */         public VcsHistorySession copy() { return (VcsHistorySession)TFSHistoryProvider.createSession(workspaceAndItem, getRevisionList()); }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 119 */         public boolean isContentAvailable(VcsFileRevision revision) { return (((ExtendedItem)workspaceAndItem.second).getType() == ItemType.File); }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reportAppendableHistory(FilePath path, VcsAppendableHistorySessionPartner partner) throws VcsException {
/* 127 */     VcsHistorySession session = createSessionFor(path);
/* 128 */     partner.reportCreatedEmptySession((VcsAbstractHistorySession)session);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<TFSFileRevision> getRevisions(Project project, String serverPath, boolean isDirectory, WorkspaceInfo workspace, VersionSpecBase versionTo) throws TfsException {
/* 136 */     VcsConfiguration vcsConfiguration = VcsConfiguration.getInstance(project);
/* 137 */     int maxCount = vcsConfiguration.LIMIT_HISTORY ? vcsConfiguration.MAXIMUM_HISTORY_ROWS : Integer.MAX_VALUE;
/*     */     
/* 139 */     List<Changeset> changesets = workspace.getServer().getVCS().queryHistory(workspace, serverPath, isDirectory, null, (VersionSpec)new ChangesetVersionSpec(1), (VersionSpec)versionTo, project, 
/* 140 */         TFSBundle.message("loading.item", new Object[0]), maxCount);
/*     */     
/* 142 */     List<TFSFileRevision> revisions = new ArrayList<>(changesets.size());
/* 143 */     for (Changeset changeset : changesets) {
/* 144 */       Item item = changeset.getChanges().getChange()[0].getItem();
/* 145 */       revisions.add(new TFSFileRevision(project, workspace, item
/* 146 */             .getItemid(), changeset.getDate().getTime(), changeset.getComment(), changeset
/* 147 */             .getOwner(), changeset
/* 148 */             .getCset()));
/*     */     } 
/* 150 */     return revisions;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 155 */   public boolean supportsHistoryForDirectories() { return true; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 160 */   public DiffFromHistoryHandler getHistoryDiffHandler() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 165 */   public boolean canShowHistoryFor(@NotNull VirtualFile file) {    return true; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSHistoryProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */