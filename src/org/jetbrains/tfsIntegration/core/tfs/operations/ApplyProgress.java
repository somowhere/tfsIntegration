/*    */ package org.jetbrains.tfsIntegration.core.tfs.operations;
/*    */ 
/*    */ import com.intellij.openapi.progress.ProgressIndicator;
/*    */ import com.intellij.openapi.vcs.rollback.RollbackProgressListener;
/*    */ import java.io.File;
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
/*    */ public interface ApplyProgress
/*    */ {
/* 35 */   public static final ApplyProgress EMPTY = new ApplyProgress()
/*    */     {
/*    */       public void setText(String text) {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 43 */       public boolean isCancelled() { return false; }
/*    */       
/*    */       public void setFraction(double fraction) {}
/*    */     };
/*    */   
/*    */   void setText(String paramString);
/*    */   
/*    */   boolean isCancelled();
/*    */   
/*    */   void setFraction(double paramDouble);
/*    */   
/*    */   public static class ProgressIndicatorWrapper
/*    */     implements ApplyProgress
/*    */   {
/* 57 */     public ProgressIndicatorWrapper(@Nullable ProgressIndicator progressIndicator) { this.myProgressIndicator = progressIndicator; }
/*    */     @Nullable
/*    */     private final ProgressIndicator myProgressIndicator;
/*    */     
/*    */     public void setText(String text) {
/* 62 */       if (this.myProgressIndicator != null) {
/* 63 */         this.myProgressIndicator.setText(text);
/*    */       }
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 69 */     public boolean isCancelled() { return (this.myProgressIndicator != null && this.myProgressIndicator.isCanceled()); }
/*    */ 
/*    */ 
/*    */     
/*    */     public void setFraction(double fraction) {
/* 74 */       if (this.myProgressIndicator != null)
/* 75 */         this.myProgressIndicator.setFraction(fraction); 
/*    */     }
/*    */   }
/*    */   
/*    */   public static class RollbackProgressWrapper
/*    */     implements ApplyProgress
/*    */   {
/*    */     @NotNull
/*    */     private final RollbackProgressListener myListener;
/*    */     
/* 85 */     public RollbackProgressWrapper(@NotNull RollbackProgressListener listener) { this.myListener = listener; }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 90 */     public void setText(String text) { this.myListener.accept(new File(text)); }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 95 */     public boolean isCancelled() { return false; }
/*    */     
/*    */     public void setFraction(double fraction) {}
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\operations\ApplyProgress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */