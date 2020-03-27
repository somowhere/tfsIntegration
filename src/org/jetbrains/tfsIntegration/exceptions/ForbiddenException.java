/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import org.apache.axis2.AxisFault;
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
/*    */ public class ForbiddenException
/*    */   extends TfsException
/*    */ {
/* 24 */   public ForbiddenException(AxisFault cause) { super((Throwable)cause); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 29 */   public String getMessage() { return "Forbidden"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\ForbiddenException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */