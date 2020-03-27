/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.ui.IdeBorderFactory;
/*    */ import com.intellij.ui.components.JBScrollPane;
/*    */ import com.intellij.ui.table.JBTable;
/*    */ import com.intellij.uiDesigner.core.GridConstraints;
/*    */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LockLevel;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Component;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Insets;
/*    */ import java.awt.LayoutManager;
/*    */ import java.util.List;
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.ButtonGroup;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JRadioButton;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.table.DefaultTableCellRenderer;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.locks.LockItemModel;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LockItemsForm
/*    */ {
/*    */   private JPanel myContentPane;
/*    */   private JTable myLockItemsTable;
/*    */   private JRadioButton myLockCheckOutRadioButton;
/*    */   private JRadioButton myLockCheckInRadioButton;
/*    */   private final LockItemsTableModel myLockItemsTableModel;
/*    */   
/*    */   public LockItemsForm(List<LockItemModel> items) {
/* 41 */     this.myLockCheckOutRadioButton.setSelected(true);
/*    */     
/* 43 */     this.myLockItemsTableModel = new LockItemsTableModel(items);
/* 44 */     this.myLockItemsTable.setModel(this.myLockItemsTableModel);
/* 45 */     for (int i = 0; i < (LockItemsTableModel.Column.values()).length; i++) {
/* 46 */       this.myLockItemsTable.getColumnModel().getColumn(i).setPreferredWidth(LockItemsTableModel.Column.values()[i].getWidth());
/*    */     }
/* 48 */     this.myLockItemsTable.setSelectionMode(0);
/* 49 */     this.myLockItemsTable.setDefaultRenderer(Boolean.class, new NoBackgroundBooleanTableCellRenderer());
/* 50 */     this.myLockItemsTable.setDefaultRenderer(ExtendedItem.class, new DefaultTableCellRenderer()
/*    */         {
/*    */ 
/*    */ 
/*    */ 
/*    */           
/*    */           public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*    */           {
/* 58 */             ExtendedItem item = (ExtendedItem)value;
/* 59 */             super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*    */             
/* 61 */             setIcon((item.getType() == ItemType.Folder) ? UiConstants.ICON_FOLDER : UiConstants.ICON_FILE);
/* 62 */             setValue(item.getSitem());
/* 63 */             return this;
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */   
/* 69 */   public JPanel getContentPane() { return this.myContentPane; }
/*    */ 
/*    */   
/*    */   public void setRadioButtonsEnabled(boolean isEnabled) {
/* 73 */     this.myLockCheckInRadioButton.setEnabled(isEnabled);
/* 74 */     this.myLockCheckOutRadioButton.setEnabled(isEnabled);
/*    */   }
/*    */ 
/*    */   
/* 78 */   public List<LockItemModel> getSelectedItems() { return this.myLockItemsTableModel.getSelectedItems(); }
/*    */ 
/*    */   
/*    */   public LockLevel getLockLevel() {
/* 82 */     if (this.myLockCheckInRadioButton.isEnabled() && this.myLockCheckInRadioButton.isSelected())
/* 83 */       return LockLevel.Checkin; 
/* 84 */     if (this.myLockCheckOutRadioButton.isEnabled() && this.myLockCheckOutRadioButton.isSelected()) {
/* 85 */       return LockLevel.CheckOut;
/*    */     }
/* 87 */     return LockLevel.None;
/*    */   }
/*    */ 
/*    */   
/* 91 */   public void addListener(LockItemsTableModel.Listener listener) { this.myLockItemsTableModel.addListener(listener); }
/*    */ 
/*    */ 
/*    */   
/* 95 */   public void removeListener(LockItemsTableModel.Listener listener) { this.myLockItemsTableModel.removeListener(listener); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\LockItemsForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */