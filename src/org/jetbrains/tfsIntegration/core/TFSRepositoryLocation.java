/*    */ package org.jetbrains.tfsIntegration.core;
/*    */ 
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import com.intellij.openapi.vcs.RepositoryLocation;
/*    */ import com.intellij.openapi.vcs.VcsException;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
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
/*    */ public class TFSRepositoryLocation
/*    */   implements RepositoryLocation
/*    */ {
/*    */   private final Map<WorkspaceInfo, List<FilePath>> myPathsByWorkspaces;
/*    */   
/* 33 */   public TFSRepositoryLocation(Map<WorkspaceInfo, List<FilePath>> pathsByWorkspaces) { this.myPathsByWorkspaces = pathsByWorkspaces; }
/*    */ 
/*    */ 
/*    */   
/* 37 */   public Map<WorkspaceInfo, List<FilePath>> getPathsByWorkspaces() { return this.myPathsByWorkspaces; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 42 */   public String getKey() { return toString(); }
/*    */ 
/*    */ 
/*    */   
/*    */   public void onBeforeBatch() throws VcsException {}
/*    */ 
/*    */ 
/*    */   
/*    */   public void onAfterBatch() {}
/*    */ 
/*    */ 
/*    */   
/*    */   public String toPresentableString() {
/* 55 */     if (this.myPathsByWorkspaces.size() == 1 && ((List)this.myPathsByWorkspaces.values().iterator().next()).size() == 1) {
/* 56 */       return ((FilePath)((List<FilePath>)this.myPathsByWorkspaces.values().iterator().next()).iterator().next()).getPresentableUrl();
/*    */     }
/*    */     
/* 59 */     return "Multiple paths";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     StringBuilder s = new StringBuilder();
/* 66 */     for (Collection<FilePath> paths : this.myPathsByWorkspaces.values()) {
/* 67 */       for (FilePath path : paths) {
/* 68 */         s.append(path.getPath());
/*    */       }
/*    */     } 
/* 71 */     return s.toString();
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSRepositoryLocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */