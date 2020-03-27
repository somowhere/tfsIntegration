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
/*    */ 
/*    */ public class PolicyParseException
/*    */   extends Exception
/*    */ {
/* 22 */   PolicyParseException(Throwable cause) { super(cause.getMessage(), cause); }
/*    */ 
/*    */ 
/*    */   
/* 26 */   PolicyParseException(String message) { super(message); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\PolicyParseException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */