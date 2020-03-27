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
/*    */ public class WrongConnectionException
/*    */   extends UnauthorizedException
/*    */ {
/* 22 */   public WrongConnectionException(String actualUsername) { super("Existing connection to server was started by another user: " + actualUsername); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\WrongConnectionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */