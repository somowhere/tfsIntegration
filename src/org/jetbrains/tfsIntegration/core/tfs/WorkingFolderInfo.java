/*    */ package org.jetbrains.tfsIntegration.core.tfs;
/*    */ 
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorkingFolderInfo
/*    */ {
/*    */   @NotNull
/*    */   private FilePath myLocalPath;
/*    */   @NotNull
/*    */   private String myServerPath;
/*    */   @NotNull
/*    */   private Status myStatus;
/*    */   
/*    */   public enum Status
/*    */   {
/* 27 */     Active,
/* 28 */     Cloaked;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 36 */   public WorkingFolderInfo(FilePath localPath) { this(Status.Active, localPath, ""); }
/*    */ 
/*    */   
/*    */   public WorkingFolderInfo(@NotNull Status status, @NotNull FilePath localPath, @NotNull String serverPath) {
/* 40 */     this.myStatus = status;
/* 41 */     this.myLocalPath = localPath;
/* 42 */     this.myServerPath = serverPath;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 47 */   public FilePath getLocalPath() {    return this.myLocalPath; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 52 */   public String getServerPath() {    return this.myServerPath; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 57 */   public Status getStatus() {    return this.myStatus; }
/*    */ 
/*    */ 
/*    */   
/* 61 */   public void setStatus(@NotNull Status status) {    this.myStatus = status; }
/*    */ 
/*    */ 
/*    */   
/* 65 */   public void setServerPath(@NotNull String serverPath) {    this.myServerPath = serverPath; }
/*    */ 
/*    */ 
/*    */   
/* 69 */   public void setLocalPath(@NotNull FilePath localPath) {    this.myLocalPath = localPath; }
/*    */ 
/*    */ 
/*    */   
/* 73 */   public WorkingFolderInfo getCopy() { return new WorkingFolderInfo(this.myStatus, this.myLocalPath, this.myServerPath); }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   String getServerPathByLocalPath(@NotNull FilePath localPath) {
/* 78 */        if (!StringUtil.isEmpty(getServerPath()) && localPath.isUnder(getLocalPath(), false)) {
/* 79 */       return VersionControlPath.getCombinedServerPath(getLocalPath(), getServerPath(), localPath);
/*    */     }
/* 81 */     return null;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public FilePath getLocalPathByServerPath(String serverPath, boolean isDirectory) {
/* 86 */     if (!StringUtil.isEmpty(getServerPath()) && VersionControlPath.isUnder(getServerPath(), serverPath)) {
/* 87 */       return VersionControlPath.getCombinedLocalPath(getLocalPath(), getServerPath(), serverPath, isDirectory);
/*    */     }
/* 89 */     return null;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\WorkingFolderInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */