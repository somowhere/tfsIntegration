/*    */ package org.jetbrains.tfsIntegration.actions;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.AnAction;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.actionSystem.CommonDataKeys;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.Messages;
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import com.intellij.openapi.vcs.FileStatus;
/*    */ import com.intellij.openapi.vcs.FileStatusManager;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.intellij.vcsUtil.VcsUtil;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*    */ import java.text.MessageFormat;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
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
/*    */ 
/*    */ 
/*    */ public abstract class SingleItemAction
/*    */   extends AnAction
/*    */ {
/* 47 */   private static final Collection<FileStatus> ALLOWED_STATUSES = Arrays.asList(new FileStatus[] { FileStatus.HIJACKED, FileStatus.MODIFIED, FileStatus.NOT_CHANGED, FileStatus.OBSOLETE });
/*    */ 
/*    */ 
/*    */   
/*    */   protected abstract void execute(@NotNull Project paramProject, @NotNull WorkspaceInfo paramWorkspaceInfo, @NotNull FilePath paramFilePath, @NotNull ExtendedItem paramExtendedItem) throws TfsException;
/*    */ 
/*    */ 
/*    */   
/* 55 */   protected Collection<FileStatus> getAllowedStatuses() { return ALLOWED_STATUSES; }
/*    */ 
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 60 */     Project project = (Project)e.getData(CommonDataKeys.PROJECT);
/* 61 */     VirtualFile file = VcsUtil.getOneVirtualFile(e);
/*    */ 
/*    */ 
/*    */     
/* 65 */     FilePath localPath = TfsFileUtil.getFilePath(file);
/*    */     
/* 67 */     String actionTitle = StringUtil.trimEnd(e.getPresentation().getText(), "...");
/*    */     
/*    */     try {
/* 70 */       Pair<WorkspaceInfo, ExtendedItem> workspaceAndItem = TfsUtil.getWorkspaceAndExtendedItem(localPath, project, TFSBundle.message("loading.item", new Object[0]));
/* 71 */       if (workspaceAndItem == null) {
/* 72 */         String itemType = localPath.isDirectory() ? "folder" : "file";
/* 73 */         String message = MessageFormat.format("No mapping found for {0} ''{1}''", new Object[] { itemType, localPath.getPresentableUrl() });
/* 74 */         Messages.showErrorDialog(project, message, actionTitle);
/*    */         return;
/*    */       } 
/* 77 */       if (workspaceAndItem.second == null) {
/* 78 */         String itemType = localPath.isDirectory() ? "Folder" : "File";
/* 79 */         String message = MessageFormat.format("{0} ''{1}'' is unversioned", new Object[] { itemType, localPath.getPresentableUrl() });
/* 80 */         Messages.showErrorDialog(project, message, actionTitle);
/*    */         
/*    */         return;
/*    */       } 
/* 84 */       execute(project, (WorkspaceInfo)workspaceAndItem.first, localPath, (ExtendedItem)workspaceAndItem.second);
/*    */     }
/* 86 */     catch (TfsException ex) {
/* 87 */       Messages.showErrorDialog(project, ex.getMessage(), actionTitle);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 93 */   public void update(@NotNull AnActionEvent e) { e.getPresentation().setEnabled(isEnabled(e.getProject(), VcsUtil.getOneVirtualFile(e))); }
/*    */ 
/*    */ 
/*    */   
/* 97 */   protected final boolean isEnabled(@Nullable Project project, @Nullable VirtualFile file) { return (project != null && file != null && getAllowedStatuses().contains(FileStatusManager.getInstance(project).getStatus(file))); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\actions\SingleItemAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */