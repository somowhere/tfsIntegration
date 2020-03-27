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
/*    */ public class HostNotFoundException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/* 24 */   public HostNotFoundException(Throwable cause) { super(cause); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 29 */   public String getMessage() { return "Specified host not found"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\HostNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */