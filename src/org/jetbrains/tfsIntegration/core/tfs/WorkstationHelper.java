/*    */ package org.jetbrains.tfsIntegration.core.tfs;
/*    */ 
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ public class WorkstationHelper
/*    */ {
/*    */   public static List<FilePath> processByWorkspaces(Collection<? extends FilePath> localPaths, boolean considerChildMappings, Object projectOrComponent, VoidProcessDelegate processor) throws TfsException {
/* 45 */     List<FilePath> orphanPaths = new ArrayList<>();
/* 46 */     Map<WorkspaceInfo, List<FilePath>> workspace2localPaths = new HashMap<>();
/* 47 */     for (FilePath localPath : localPaths) {
/* 48 */       Collection<WorkspaceInfo> workspaces = Workstation.getInstance().findWorkspaces(localPath, considerChildMappings, projectOrComponent);
/* 49 */       if (!workspaces.isEmpty()) {
/* 50 */         for (WorkspaceInfo workspace : workspaces) {
/* 51 */           List<FilePath> workspaceLocalPaths = workspace2localPaths.get(workspace);
/* 52 */           if (workspaceLocalPaths == null) {
/* 53 */             workspaceLocalPaths = new ArrayList<>();
/* 54 */             workspace2localPaths.put(workspace, workspaceLocalPaths);
/*    */           } 
/* 56 */           workspaceLocalPaths.add(localPath);
/*    */         } 
/*    */         continue;
/*    */       } 
/* 60 */       orphanPaths.add(localPath);
/*    */     } 
/*    */ 
/*    */     
/* 64 */     for (WorkspaceInfo workspace : workspace2localPaths.keySet()) {
/* 65 */       List<FilePath> currentLocalPaths = workspace2localPaths.get(workspace);
/* 66 */       List<ItemPath> currentItemPaths = new ArrayList<>(currentLocalPaths.size());
/* 67 */       for (FilePath localPath : currentLocalPaths) {
/* 68 */         Collection<String> serverPaths = workspace.findServerPathsByLocalPath(localPath, considerChildMappings, projectOrComponent);
/* 69 */         if (!considerChildMappings) {
/* 70 */           currentItemPaths.add(new ItemPath(localPath, serverPaths.iterator().next()));
/*    */           continue;
/*    */         } 
/* 73 */         for (String serverPath : serverPaths)
/*    */         {
/* 75 */           currentItemPaths.add(new ItemPath(workspace
/* 76 */                 .findLocalPathByServerPath(serverPath, localPath.isDirectory(), projectOrComponent), serverPath));
/*    */         }
/*    */       } 
/*    */       
/* 80 */       processor.executeRequest(workspace, currentItemPaths);
/*    */     } 
/* 82 */     return orphanPaths;
/*    */   }
/*    */   
/*    */   public static interface VoidProcessDelegate {
/*    */     void executeRequest(WorkspaceInfo param1WorkspaceInfo, List<ItemPath> param1List) throws TfsException;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\WorkstationHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */