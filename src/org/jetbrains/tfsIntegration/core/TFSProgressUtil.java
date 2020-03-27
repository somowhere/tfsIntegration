/*    */ package org.jetbrains.tfsIntegration.core;
/*    */ 
/*    */ import com.intellij.openapi.progress.ProcessCanceledException;
/*    */ import com.intellij.openapi.progress.ProgressIndicator;
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
/*    */ public class TFSProgressUtil
/*    */ {
/*    */   public static void checkCanceled(@Nullable ProgressIndicator progressIndicator) throws ProcessCanceledException {
/* 26 */     if (progressIndicator != null && progressIndicator.isCanceled()) {
/* 27 */       throw new ProcessCanceledException();
/*    */     }
/*    */   }
/*    */ 
/*    */   
/* 32 */   public static boolean isCanceled(@Nullable ProgressIndicator progressIndicator) { return (progressIndicator != null && progressIndicator.isCanceled()); }
/*    */ 
/*    */   
/*    */   public static void setProgressText(@Nullable ProgressIndicator progressIndicator, @Nullable String text) {
/* 36 */     if (progressIndicator != null && text != null) {
/* 37 */       progressIndicator.setText(text);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void setProgressText2(@Nullable ProgressIndicator progressIndicator, @Nullable String text) {
/* 42 */     if (progressIndicator != null && text != null) {
/* 43 */       progressIndicator.setText2(text);
/*    */     }
/*    */   }
/*    */   
/*    */   public static void setIndeterminate(@Nullable ProgressIndicator progressIndicator, boolean indeterminate) {
/* 48 */     if (progressIndicator != null)
/* 49 */       progressIndicator.setIndeterminate(indeterminate); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSProgressUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */