/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.ComponentWithBrowseButton;
/*    */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*    */ import com.intellij.util.ui.AbstractTableCellEditor;
/*    */ import com.intellij.util.ui.CellEditorComponentWithBrowseButton;
/*    */ import java.awt.Component;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JTable;
/*    */ import javax.swing.JTextField;
/*    */ import javax.swing.table.TableCellEditor;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*    */ import org.jetbrains.tfsIntegration.ui.servertree.ServerBrowserDialog;
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
/*    */ public class ServerPathCellEditor
/*    */   extends AbstractTableCellEditor
/*    */ {
/*    */   private final String myTitle;
/*    */   private CellEditorComponentWithBrowseButton<JTextField> myComponent;
/*    */   private final Project myProject;
/*    */   private final ServerInfo myServer;
/*    */   
/*    */   public ServerPathCellEditor(String title, Project project, ServerInfo server) {
/* 38 */     this.myTitle = title;
/* 39 */     this.myProject = project;
/* 40 */     this.myServer = server;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 45 */   public Object getCellEditorValue() { return ((JTextField)this.myComponent.getChildComponent()).getText(); }
/*    */ 
/*    */ 
/*    */   
/*    */   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
/* 50 */     ActionListener listener = new ActionListener()
/*    */       {
/*    */         public void actionPerformed(ActionEvent e) {
/* 53 */           ServerBrowserDialog d = new ServerBrowserDialog(ServerPathCellEditor.this.myTitle, ServerPathCellEditor.this.myProject, ServerPathCellEditor.this.myServer, (String)ServerPathCellEditor.this.getCellEditorValue(), true, false);
/* 54 */           if (d.showAndGet()) {
/* 55 */             ((JTextField)ServerPathCellEditor.this.myComponent.getChildComponent()).setText(d.getSelectedPath());
/*    */           }
/*    */         }
/*    */       };
/* 59 */     this.myComponent = new CellEditorComponentWithBrowseButton((ComponentWithBrowseButton)new TextFieldWithBrowseButton(listener), (TableCellEditor)this);
/* 60 */     ((JTextField)this.myComponent.getChildComponent()).setText((String)value);
/* 61 */     return (Component)this.myComponent;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ServerPathCellEditor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */