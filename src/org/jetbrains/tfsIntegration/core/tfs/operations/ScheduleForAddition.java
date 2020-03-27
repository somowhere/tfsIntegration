/*    */ package org.jetbrains.tfsIntegration.core.tfs.operations;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vcs.VcsException;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
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
/*    */ public class ScheduleForAddition
/*    */ {
/*    */   public static Collection<VcsException> execute(Project project, WorkspaceInfo workspace, List<ItemPath> paths) {
/*    */     try {
/* 37 */       ResultWithFailures<GetOperation> serverResults = workspace.getServer().getVCS().scheduleForAddition(workspace.getName(), workspace.getOwnerName(), paths, project, TFSBundle.message("scheduling.for.addition", new Object[0]));
/* 38 */       for (GetOperation getOp : serverResults.getResult()) {
/* 39 */         VirtualFile file = VersionControlPath.getVirtualFile(getOp.getTlocal());
/* 40 */         if (file != null && file.isValid()) {
/* 41 */           TfsFileUtil.markFileDirty(project, file);
/*    */         }
/*    */       } 
/* 44 */       return TfsUtil.getVcsExceptions(serverResults.getFailures());
/*    */     }
/* 46 */     catch (TfsException e) {
/* 47 */       return Collections.singletonList(new VcsException((Throwable)e));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\operations\ScheduleForAddition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */