/*    */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*    */ 
/*    */ import com.intellij.uiDesigner.core.GridConstraints;
/*    */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*    */ import com.intellij.uiDesigner.core.Spacer;
/*    */ import java.awt.Component;
/*    */ import java.awt.Insets;
/*    */ import java.awt.LayoutManager;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JLabel;
/*    */ import javax.swing.JPanel;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ 
/*    */ public class SummaryForm
/*    */ {
/*    */   private JLabel myServerLabel;
/*    */   private JLabel myWorkspaceLabel;
/*    */   private JLabel mySourceLabel;
/*    */   private JLabel myDestinationLabel;
/*    */   private JPanel myContentPanel;
/*    */   private JLabel myWorkspaceTypeLabel;
/*    */   
/* 25 */   public SummaryForm() { }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 34 */   public void setServer(ServerInfo server) { this.myServerLabel.setText(server.getPresentableUri()); }
/*    */ 
/*    */   
/*    */   public void setWorkspace(WorkspaceInfo workspace) {
/* 38 */     this.myWorkspaceLabel.setText(workspace.getName());
/* 39 */     this.myWorkspaceTypeLabel.setText("Existing workspace to be used:");
/*    */   }
/*    */   
/*    */   public void setNewWorkspaceName(String newWorkspaceName) {
/* 43 */     this.myWorkspaceLabel.setText(newWorkspaceName);
/* 44 */     this.myWorkspaceTypeLabel.setText("Workspace to be created:");
/*    */   }
/*    */ 
/*    */   
/* 48 */   public void setServerPath(String path) { this.mySourceLabel.setText(path); }
/*    */ 
/*    */ 
/*    */   
/* 52 */   public void setLocalPath(@NotNull String path) {  this.myDestinationLabel.setText(path); }
/*    */ 
/*    */ 
/*    */   
/* 56 */   public JPanel getContentPanel() { return this.myContentPanel; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\SummaryForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */