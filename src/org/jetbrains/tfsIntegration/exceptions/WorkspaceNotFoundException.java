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
/*    */ 
/*    */ public class WorkspaceNotFoundException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final String CODE = "WorkspaceNotFoundException";
/*    */   
/* 28 */   public WorkspaceNotFoundException(String message) { super(message); }
/*    */ 
/*    */ 
/*    */   
/* 32 */   public WorkspaceNotFoundException(AxisFault cause) { super((Throwable)cause); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\WorkspaceNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */