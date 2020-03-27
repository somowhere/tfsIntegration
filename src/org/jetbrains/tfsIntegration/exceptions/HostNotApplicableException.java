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
/*    */ public class HostNotApplicableException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 26 */   public HostNotApplicableException(AxisFault cause) { super((Throwable)cause); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 31 */   public String getMessage() { return "Host contacted, but no TFS service found"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\HostNotApplicableException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */