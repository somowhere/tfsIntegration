/*    */ package org.jetbrains.tfsIntegration.checkin;
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
/*    */ public class DuplicatePolicyIdException
/*    */   extends Exception
/*    */ {
/*    */   private final String myId;
/*    */   
/* 23 */   public DuplicatePolicyIdException(String duplicateId) { this.myId = duplicateId; }
/*    */ 
/*    */ 
/*    */   
/* 27 */   public String getDuplicateId() { return this.myId; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\DuplicatePolicyIdException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */