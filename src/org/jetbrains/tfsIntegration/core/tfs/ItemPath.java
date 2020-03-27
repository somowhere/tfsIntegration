/*    */ package org.jetbrains.tfsIntegration.core.tfs;
/*    */ 
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ public class ItemPath
/*    */ {
/*    */   @NotNull
/*    */   private final FilePath myLocalPath;
/*    */   private final String myServerPath;
/*    */   
/*    */   public ItemPath(@NotNull FilePath localPath, String serverPath) {
/* 29 */     this.myLocalPath = localPath;
/* 30 */     this.myServerPath = serverPath;
/*    */   }
/*    */   
/*    */   @NotNull
/* 34 */   public FilePath getLocalPath() {    return this.myLocalPath; }
/*    */ 
/*    */ 
/*    */   
/* 38 */   public String getServerPath() { return this.myServerPath; }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 42 */     if (this == o) return true; 
/* 43 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 45 */     ItemPath itemPath = (ItemPath)o;
/*    */     
/* 47 */     return FileUtil.pathsEqual(getLocalPath().getPath(), itemPath.getLocalPath().getPath());
/*    */   }
/*    */ 
/*    */   
/* 51 */   public int hashCode() { return getLocalPath().hashCode(); }
/*    */ 
/*    */ 
/*    */   
/* 55 */   public String toString() { return "local: " + getLocalPath() + ", server: " + getServerPath(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\ItemPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */