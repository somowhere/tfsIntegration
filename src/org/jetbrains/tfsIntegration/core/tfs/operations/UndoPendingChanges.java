/*    */ package org.jetbrains.tfsIntegration.core.tfs.operations;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import com.intellij.openapi.vcs.VcsException;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Failure;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
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
/*    */ public class UndoPendingChanges
/*    */ {
/*    */   @NonNls
/*    */   private static final String ITEM_NOT_CHECKED_OUT_FAILURE = "ItemNotCheckedOutException";
/*    */   
/*    */   public static class UndoPendingChangesResult
/*    */   {
/*    */     public final Collection<VcsException> errors;
/*    */     public final Map<ItemPath, ItemPath> undonePaths;
/*    */     
/*    */     public UndoPendingChangesResult(Map<ItemPath, ItemPath> undonePaths, Collection<VcsException> errors) {
/* 44 */       this.undonePaths = undonePaths;
/* 45 */       this.errors = errors;
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static UndoPendingChangesResult execute(Project project, WorkspaceInfo workspace, Collection<String> serverPaths, boolean forbidDownload, @NotNull ApplyProgress progress, boolean tolerateNoChangesFailure) {
/* 55 */        if (serverPaths.isEmpty()) {
/* 56 */       return new UndoPendingChangesResult(Collections.emptyMap(), Collections.emptyList());
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     try {
/* 63 */       ResultWithFailures<GetOperation> result = workspace.getServer().getVCS().undoPendingChanges(workspace.getName(), workspace.getOwnerName(), serverPaths, project, TFSBundle.message("reverting", new Object[0]));
/*    */       
/* 65 */       Collection<Failure> failures = result.getFailures();
/* 66 */       if (tolerateNoChangesFailure) {
/* 67 */         for (Iterator<Failure> i = failures.iterator(); i.hasNext();) {
/* 68 */           if ("ItemNotCheckedOutException".equals(((Failure)i.next()).getCode())) {
/* 69 */             i.remove();
/*    */           }
/*    */         } 
/*    */       }
/*    */       
/* 74 */       Collection<VcsException> errors = new ArrayList<>(TfsUtil.getVcsExceptions(failures));
/*    */ 
/*    */       
/* 77 */       Map<ItemPath, ItemPath> undonePaths = new HashMap<>();
/* 78 */       for (GetOperation getOperation : result.getResult()) {
/* 79 */         if (getOperation.getSlocal() != null && getOperation.getTlocal() != null) {
/*    */           
/* 81 */           FilePath sourcePath = VersionControlPath.getFilePath(getOperation.getSlocal(), (getOperation.getType() == ItemType.Folder));
/*    */           
/* 83 */           FilePath targetPath = VersionControlPath.getFilePath(getOperation.getTlocal(), (getOperation.getType() == ItemType.Folder));
/* 84 */           undonePaths.put(new ItemPath(sourcePath, workspace.findServerPathsByLocalPath(sourcePath, false, project).iterator().next()), new ItemPath(targetPath, workspace
/* 85 */                 .findServerPathsByLocalPath(targetPath, false, project).iterator().next()));
/*    */         } 
/*    */       } 
/*    */ 
/*    */       
/* 90 */       ApplyGetOperations.DownloadMode downloadMode = forbidDownload ? ApplyGetOperations.DownloadMode.FORBID : ApplyGetOperations.DownloadMode.FORCE;
/*    */ 
/*    */       
/* 93 */       Collection<VcsException> applyingErrors = ApplyGetOperations.execute(project, workspace, result.getResult(), progress, null, downloadMode);
/* 94 */       errors.addAll(applyingErrors);
/* 95 */       return new UndoPendingChangesResult(undonePaths, errors);
/*    */     }
/* 97 */     catch (TfsException e) {
/* 98 */       return new UndoPendingChangesResult(Collections.emptyMap(), Collections.singletonList(new VcsException((Throwable)e)));
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\operations\UndoPendingChanges.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */