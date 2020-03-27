/*    */ package org.jetbrains.tfsIntegration.core.tfs;
/*    */ 
/*    */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ public class TfsRevisionNumber
/*    */   extends VcsRevisionNumber.Int
/*    */ {
/*    */   private static final String SEPARATOR = ":";
/*    */   public static final int UNDEFINED_ID = -2147483648;
/*    */   private final int myItemId;
/*    */   
/*    */   public TfsRevisionNumber(int value, int itemId) {
/* 15 */     super(value);
/* 16 */     this.myItemId = itemId;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public String asString() {
/* 22 */     if (this.myItemId != Integer.MIN_VALUE) {
/* 23 */           return getValue() + ":" + this.myItemId;
/*    */     } 
/*    */     
/* 26 */          return String.valueOf(getValue());
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 31 */   public TfsRevisionNumber(int value) { this(value, -2147483648); }
/*    */ 
/*    */ 
/*    */   
/* 35 */   public int getItemId() { return this.myItemId; }
/*    */ 
/*    */   
/*    */   public static VcsRevisionNumber tryParse(String s) {
/*    */     try {
/* 40 */       int i = s.indexOf(":");
/* 41 */       if (i != -1) {
/* 42 */         String revisionNumberString = s.substring(0, i);
/* 43 */         String itemIdString = s.substring(i + 1);
/* 44 */         int revisionNumber = Integer.parseInt(revisionNumberString);
/* 45 */         int changeset = Integer.parseInt(itemIdString);
/* 46 */         return (VcsRevisionNumber)new TfsRevisionNumber(revisionNumber, changeset);
/*    */       } 
/*    */       
/* 49 */       int revisionNumber = Integer.parseInt(s);
/* 50 */       return (VcsRevisionNumber)new TfsRevisionNumber(revisionNumber);
/*    */     
/*    */     }
/* 53 */     catch (NumberFormatException e) {
/* 54 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/* 59 */   public String getChangesetString() { return String.valueOf(getValue()); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\TfsRevisionNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */