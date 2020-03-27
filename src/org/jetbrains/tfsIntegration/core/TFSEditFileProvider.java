/*    */ package org.jetbrains.tfsIntegration.core;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vcs.EditFileProvider;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import com.intellij.openapi.vcs.VcsException;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
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
/*    */ public class TFSEditFileProvider
/*    */   implements EditFileProvider
/*    */ {
/*    */   private final Project myProject;
/*    */   
/* 37 */   public TFSEditFileProvider(Project project) { this.myProject = project; }
/*    */ 
/*    */ 
/*    */   
/*    */   public void editFiles(VirtualFile[] files) throws VcsException {
/* 42 */     final Collection<VcsException> errors = new ArrayList<>();
/*    */     
/*    */     try {
/* 45 */       Collection<FilePath> orphans = WorkstationHelper.processByWorkspaces(TfsFileUtil.getFilePaths(files), false, this.myProject, new WorkstationHelper.VoidProcessDelegate()
/*    */           {
/*    */ 
/*    */             
/*    */             public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException
/*    */             {
/* 51 */               ResultWithFailures<GetOperation> processResult = workspace.getServer().getVCS().checkoutForEdit(workspace.getName(), workspace.getOwnerName(), paths, TFSEditFileProvider.this.myProject, TFSBundle.message("checking.out", new Object[0]));
/* 52 */               Collection<VirtualFile> makeWritable = new ArrayList<>();
/* 53 */               for (GetOperation getOperation : processResult.getResult()) {
/* 54 */                 TFSVcs.assertTrue(getOperation.getSlocal().equals(getOperation.getTlocal()));
/* 55 */                 VirtualFile file = VersionControlPath.getVirtualFile(getOperation.getSlocal());
/* 56 */                 if (file != null && file.isValid() && !file.isDirectory()) {
/* 57 */                   makeWritable.add(file);
/*    */                 }
/*    */               } 
/*    */               try {
/* 61 */                 TfsFileUtil.setReadOnly(makeWritable, false);
/*    */               }
/* 63 */               catch (IOException e) {
/* 64 */                 errors.add(new VcsException(e));
/*    */               } 
/* 66 */               errors.addAll(TfsUtil.getVcsExceptions(processResult.getFailures()));
/*    */             }
/*    */           });
/*    */       
/* 70 */       if (!orphans.isEmpty()) {
/* 71 */         StringBuilder s = new StringBuilder("Mappings not found for files:\n");
/* 72 */         for (FilePath path : orphans) {
/* 73 */           s.append(path.getPresentableUrl()).append("\n");
/*    */         }
/* 75 */         throw new VcsException(s.toString());
/*    */       }
/*    */     
/* 78 */     } catch (TfsException e) {
/* 79 */       throw new VcsException((Throwable)e);
/*    */     } 
/* 81 */     if (!errors.isEmpty()) {
/* 82 */       throw TfsUtil.collectExceptions(errors);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 88 */   public String getRequestText() { return "Perform a checkout?"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSEditFileProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */