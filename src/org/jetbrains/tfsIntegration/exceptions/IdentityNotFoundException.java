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
/*    */ public class IdentityNotFoundException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final String CODE = "IdentityNotFoundException";
/*    */   private final String myIdentityName;
/*    */   
/*    */   public IdentityNotFoundException(AxisFault cause) {
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


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\IdentityNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */