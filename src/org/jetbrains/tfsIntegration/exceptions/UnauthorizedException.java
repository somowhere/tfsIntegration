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
/*    */ public class UnauthorizedException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 26 */   public UnauthorizedException(String message) { super(message); }
/*    */ 
/*    */ 
/*    */   
/* 30 */   public UnauthorizedException(AxisFault cause) { super((Throwable)cause); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\UnauthorizedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */