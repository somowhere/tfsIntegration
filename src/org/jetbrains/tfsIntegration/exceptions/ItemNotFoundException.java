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
/*    */ public class ItemNotFoundException
/*    */   extends TfsException
/*    */ {
/*    */   public static final String CODE = "ItemNotFoundException";
/*    */   private final String myServerItem;
/*    */   
/*    */   public ItemNotFoundException(AxisFault cause) {
/* 30 */     super((Throwable)cause);
/*    */     
/* 32 */     this.myServerItem = cause.getDetail().getAttributeValue(new QName("ServerItem"));
/*    */   }
/*    */ 
/*    */   
/* 36 */   public String getServerItem() { return this.myServerItem; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\ItemNotFoundException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */