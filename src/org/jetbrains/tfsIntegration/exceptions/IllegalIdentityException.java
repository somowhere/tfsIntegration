/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import javax.xml.namespace.QName;
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
/*    */ 
/*    */ public class IllegalIdentityException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final String CODE = "IllegalIdentityException";
/*    */   private final String myIdentityName;
/*    */   
/*    */   public IllegalIdentityException(AxisFault cause) {
/* 32 */     super((Throwable)cause);
/* 33 */     if (cause.getDetail() != null) {
/* 34 */       this.myIdentityName = cause.getDetail().getAttributeValue(new QName("IdentityName"));
/*    */     } else {
/*    */       
/* 37 */       this.myIdentityName = null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/* 42 */   public String getIdentityName() { return this.myIdentityName; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\IllegalIdentityException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */