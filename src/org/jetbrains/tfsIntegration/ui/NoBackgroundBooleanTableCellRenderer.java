/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import java.awt.Component;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTable;
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
/*    */ public class NoBackgroundBooleanTableCellRenderer
/*    */   extends JCheckBox
/*    */   implements TableCellRenderer
/*    */ {
/*    */   private final JPanel myPanel;
/*    */   
/*    */   public NoBackgroundBooleanTableCellRenderer() {
/* 24 */     this.myPanel = new JPanel();
/*    */ 
/*    */ 
/*    */     
/* 28 */     setHorizontalAlignment(0);
/*    */   }
/*    */ 
/*    */   
/*    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/* 33 */     if (value == null) {
/* 34 */       this.myPanel.setBackground(table.getBackground());
/* 35 */       return this.myPanel;
/*    */     } 
/*    */     
/* 38 */     setForeground(table.getForeground());
/* 39 */     setBackground(table.getBackground());
/* 40 */     setSelected(((Boolean)value).booleanValue());
/* 41 */     return this;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\NoBackgroundBooleanTableCellRenderer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */