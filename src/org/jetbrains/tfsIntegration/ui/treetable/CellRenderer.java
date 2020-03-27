/*    */ package org.jetbrains.tfsIntegration.ui.treetable;
/*    */ 
/*    */ import javax.swing.JLabel;
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
/*    */ public class CellRenderer<T>
/*    */ {
/* 23 */   protected void render(CustomTreeTable<T> treeTable, TreeTableColumn<T> column, T value, JLabel cell) { cell.setText(column.getPresentableString(value)); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\treetable\CellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */