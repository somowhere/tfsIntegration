/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.util.ui.UIUtil;
/*    */ import java.awt.Color;
/*    */ import java.awt.Component;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.JTextArea;
/*    */ import javax.swing.UIManager;
/*    */ import javax.swing.table.TableCellRenderer;
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
/*    */ public abstract class MultiLineTableRenderer
/*    */   extends JTextArea
/*    */   implements TableCellRenderer
/*    */ {
/*    */   public MultiLineTableRenderer() {
/* 28 */     setLineWrap(true);
/* 29 */     setWrapStyleWord(true);
/* 30 */     setBorder(UIManager.getBorder("Table.cellNoFocusBorder"));
/*    */   }
/*    */ 
/*    */   
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/* 35 */     if (isSelected) {
/* 36 */       setForeground(table.getSelectionForeground());
/* 37 */       setBackground(table.getSelectionBackground());
/*    */     } else {
/*    */       
/* 40 */       Color background = table.getBackground();
/* 41 */       if (background == null || background instanceof javax.swing.plaf.UIResource) {
/* 42 */         Color alternateColor = UIManager.getColor("Table.alternateRowColor");
/* 43 */         if (alternateColor != null && row % 2 == 0) background = alternateColor; 
/*    */       } 
/* 45 */       setForeground(table.getForeground());
/* 46 */       setBackground(background);
/*    */     } 
/*    */     
/* 49 */     setFont(table.getFont());
/*    */     
/* 51 */     if (hasFocus && 
/* 52 */       !isSelected && table.isCellEditable(row, column)) {
/*    */       
/* 54 */       Color col = UIUtil.getTableFocusCellForeground();
/* 55 */       if (col != null) {
/* 56 */         setForeground(col);
/*    */       }
/* 58 */       col = UIUtil.getTableFocusCellBackground();
/* 59 */       if (col != null) {
/* 60 */         setBackground(col);
/*    */       }
/*    */     } 
/*    */ 
/*    */     
/* 65 */     customize(table, this, isSelected, value);
/*    */     
/* 67 */     setSize(table.getColumnModel().getColumn(column).getWidth(), 100000);
/* 68 */     table.setRowHeight(row, (getPreferredSize()).height);
/* 69 */     return this;
/*    */   }
/*    */   
/*    */   protected abstract void customize(JTable paramJTable, JTextArea paramJTextArea, boolean paramBoolean, Object paramObject);
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\MultiLineTableRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */