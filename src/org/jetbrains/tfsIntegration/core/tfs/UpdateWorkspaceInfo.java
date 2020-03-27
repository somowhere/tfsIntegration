/*    */ package org.jetbrains.tfsIntegration.core.tfs;
/*    */ 
/*    */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
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
/*    */ public class UpdateWorkspaceInfo
/*    */ {
/*    */   private VersionSpecBase myVersion;
/*    */   
/* 26 */   public UpdateWorkspaceInfo(VersionSpecBase version) { this.myVersion = version; }
/*    */ 
/*    */ 
/*    */   
/* 30 */   public VersionSpecBase getVersion() { return this.myVersion; }
/*    */ 
/*    */ 
/*    */   
/* 34 */   public void setVersion(VersionSpecBase version) { this.myVersion = version; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\UpdateWorkspaceInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */