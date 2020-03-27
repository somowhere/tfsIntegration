/*    */ package org.jetbrains.tfsIntegration.actions;
/*    */ 
/*    */ import com.intellij.openapi.project.DumbAware;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.Messages;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import com.intellij.openapi.vcs.FileStatus;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.BranchRelative;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*    */ import java.text.MessageFormat;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
/*    */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*    */ import org.jetbrains.tfsIntegration.ui.ItemInfoDialog;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ItemInfoAction
/*    */   extends SingleItemAction
/*    */   implements DumbAware
/*    */ {
/* 40 */   private static final Collection<FileStatus> ALLOWED_STATUSES = Arrays.asList(new FileStatus[] { FileStatus.HIJACKED, FileStatus.MODIFIED, FileStatus.NOT_CHANGED, FileStatus.OBSOLETE, FileStatus.ADDED });
/*    */ 
/*    */ 
/*    */   
/* 44 */   protected Collection<FileStatus> getAllowedStatuses() { return ALLOWED_STATUSES; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void execute(@NotNull Project project, @NotNull WorkspaceInfo workspace, @NotNull FilePath localPath, @NotNull ExtendedItem extendedItem) throws TfsException {
/* 52 */                 if (extendedItem.getLver() == Integer.MIN_VALUE) {
/* 53 */       String itemType = localPath.isDirectory() ? "Folder" : "File";
/* 54 */       String message = MessageFormat.format("{0} ''{1}'' is unversioned", new Object[] { itemType, localPath.getPresentableUrl() });
/* 55 */       Messages.showInfoMessage(project, message, getActionTitle(localPath.isDirectory()));
/*    */       
/*    */       return;
/*    */     } 
/* 59 */     String serverPath = (extendedItem.getTitem() != null) ? extendedItem.getTitem() : extendedItem.getSitem();
/*    */     
/* 61 */     Collection<BranchRelative> branches = workspace.getServer().getVCS().queryBranches(serverPath, (VersionSpec)new ChangesetVersionSpec(extendedItem.getLver()), project, TFSBundle.message("loading.branches", new Object[0]));
/*    */     
/* 63 */     ItemInfoDialog d = new ItemInfoDialog(project, workspace, extendedItem, branches, getActionTitle(localPath.isDirectory()));
/* 64 */     d.show();
/*    */   }
/*    */ 
/*    */   
/* 68 */   private static String getActionTitle(boolean isDirectory) { return MessageFormat.format("{0} Information", new Object[] { isDirectory ? "Folder" : "File" }); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\actions\ItemInfoAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */