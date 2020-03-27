/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.vcs.versionBrowser.ChangeBrowserSettings;
/*    */ import com.intellij.openapi.vcs.versionBrowser.StandardVersionFilterComponent;
/*    */ import com.intellij.uiDesigner.core.GridConstraints;
/*    */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*    */ import java.awt.BorderLayout;
/*    */ import java.awt.Dimension;
/*    */ import java.awt.Insets;
/*    */ import java.awt.LayoutManager;
/*    */ import java.awt.event.ActionEvent;
/*    */ import java.awt.event.ActionListener;
/*    */ import javax.swing.JCheckBox;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.JTextField;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TFSVersionFilterComponent
/*    */   extends StandardVersionFilterComponent<ChangeBrowserSettings>
/*    */ {
/*    */   private JPanel myPanel;
/*    */   private JCheckBox myUseAuthorFilter;
/*    */   private JTextField myAuthorField;
/*    */   private JPanel myStandardPanel;
/*    */   
/*    */   public TFSVersionFilterComponent(boolean showDateFilter) {
/* 35 */     super(showDateFilter);
/* 36 */         this.myStandardPanel.setLayout(new BorderLayout());
/* 37 */     this.myStandardPanel.add(getStandardPanel(), "Center");
/* 38 */     init(new ChangeBrowserSettings());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void updateAllEnabled(ActionEvent e) {
/* 43 */     super.updateAllEnabled(e);
/* 44 */     updatePair(this.myUseAuthorFilter, this.myAuthorField, e);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void initValues(ChangeBrowserSettings settings) {
/* 49 */     super.initValues(settings);
/* 50 */     this.myUseAuthorFilter.setSelected(settings.USE_USER_FILTER);
/* 51 */     this.myAuthorField.setText(settings.USER);
/*    */   }
/*    */ 
/*    */   
/*    */   public void saveValues(ChangeBrowserSettings settings) {
/* 56 */     super.saveValues(settings);
/* 57 */     settings.USER = this.myAuthorField.getText();
/* 58 */     settings.USE_USER_FILTER = this.myUseAuthorFilter.isSelected();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void installCheckBoxListener(ActionListener filterListener) {
/* 63 */     super.installCheckBoxListener(filterListener);
/* 64 */     this.myUseAuthorFilter.addActionListener(filterListener);
/* 65 */     this.myAuthorField.addActionListener(filterListener);
/*    */   }
/*    */ 
/*    */   
/* 69 */   public JPanel getPanel() { return this.myPanel; }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   public String getAuthorFilter() {
/* 74 */     if (this.myUseAuthorFilter.isSelected() && this.myAuthorField.getText().length() > 0) {
/* 75 */       return this.myAuthorField.getText();
/*    */     }
/*    */     
/* 78 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 84 */   protected String getChangeNumberTitle() { return "Revision"; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 89 */   public JComponent getComponent() { return getPanel(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\TFSVersionFilterComponent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */