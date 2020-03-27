/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import org.apache.axis2.AxisFault;
/*    */ import org.jetbrains.annotations.NonNls;
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
/*    */ public class InvalidPathException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   @NonNls
/*    */   public static final String CODE = "InvalidPathException";
/*    */   private final String myPath;
/*    */   
/*    */   public InvalidPathException(AxisFault cause) {
/* 31 */     super((Throwable)cause);
/* 32 */     this.myPath = cause.getMessage();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 37 */   public String getMessage() { return "Invalid path: " + this.myPath; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\InvalidPathException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */