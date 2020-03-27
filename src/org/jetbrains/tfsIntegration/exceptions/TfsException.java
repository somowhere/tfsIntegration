/*    */ package org.jetbrains.tfsIntegration.exceptions;
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
/*    */ public class TfsException
/*    */   extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 24 */   public TfsException(String message, Throwable cause) { super(message, cause); }
/*    */ 
/*    */ 
/*    */   
/* 28 */   public TfsException(Throwable cause) { super((cause != null) ? cause.getMessage() : null, cause); }
/*    */ 
/*    */ 
/*    */   
/* 32 */   public TfsException(String message) { super(message); }
/*    */   
/*    */   public TfsException() {}
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\TfsException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */