/*    */ package org.jetbrains.tfsIntegration.ui.treetable;
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
/*    */ public abstract class TreeTableColumn<T>
/*    */ {
/*    */   private final String myCaption;
/*    */   private final int myWidth;
/*    */   
/*    */   public TreeTableColumn(String caption, int width) {
/* 24 */     this.myCaption = caption;
/* 25 */     this.myWidth = width;
/*    */   }
/*    */ 
/*    */   
/*    */   public abstract String getPresentableString(T paramT);
/*    */   
/* 31 */   public String getCaption() { return this.myCaption; }
/*    */ 
/*    */ 
/*    */   
/* 35 */   public int getWidth() { return this.myWidth; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\treetable\TreeTableColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */