/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.ui.IdeBorderFactory;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import java.awt.Component;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProjectConfigurableForm
/*     */ {
/*     */   private JButton myManageButton;
/*     */   private JButton myResetPasswordsButton;
/*     */   private final Project myProject;
/*     */   private JComponent myContentPane;
/*     */   private JCheckBox myUseIdeaHttpProxyCheckBox;
/*     */   private JCheckBox myTFSCheckBox;
/*     */   private JCheckBox myStatefulCheckBox;
/*     */   private JCheckBox myReportNotInstalledPoliciesCheckBox;
/*     */   
/*     */   public ProjectConfigurableForm(Project project) {
/*  38 */     this.myProject = project;
/*     */     
/*  40 */ this.myManageButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*  43 */             ManageWorkspacesDialog d = new ManageWorkspacesDialog(ProjectConfigurableForm.this.myProject);
/*  44 */             d.show();
/*     */           }
/*     */         });
/*     */     
/*  48 */     this.myUseIdeaHttpProxyCheckBox.setSelected(TFSConfigurationManager.getInstance().useIdeaHttpProxy());
/*     */     
/*  50 */     this.myResetPasswordsButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*  53 */             String title = "Reset Stored Passwords";
/*  54 */             if (Messages.showYesNoDialog(ProjectConfigurableForm.this.myProject, "Do you want to reset all stored passwords?", "Reset Stored Passwords", Messages.getQuestionIcon()) == 0) {
/*  55 */               TFSConfigurationManager.getInstance().resetStoredPasswords();
/*  56 */               Messages.showInfoMessage(ProjectConfigurableForm.this.myProject, "Passwords reset successfully.", "Reset Stored Passwords");
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/*  61 */     ActionListener l = new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/*  64 */           ProjectConfigurableForm.this.updateNonInstalledCheckbox();
/*     */         }
/*     */       };
/*  67 */     this.myStatefulCheckBox.addActionListener(l);
/*  68 */     this.myTFSCheckBox.addActionListener(l);
/*     */   }
/*     */   
/*     */   private void updateNonInstalledCheckbox() {
/*  72 */     if (!this.myStatefulCheckBox.isSelected() && !this.myTFSCheckBox.isSelected()) {
/*  73 */       this.myReportNotInstalledPoliciesCheckBox.setSelected(false);
/*  74 */       this.myReportNotInstalledPoliciesCheckBox.setEnabled(false);
/*     */     } else {
/*     */       
/*  77 */       this.myReportNotInstalledPoliciesCheckBox.setEnabled(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  82 */   public JComponent getContentPane() { return this.myContentPane; }
/*     */ 
/*     */ 
/*     */   
/*  86 */   public boolean useProxy() { return this.myUseIdeaHttpProxyCheckBox.isSelected(); }
/*     */ 
/*     */ 
/*     */   
/*  90 */   public void setUserProxy(boolean value) { this.myUseIdeaHttpProxyCheckBox.setSelected(value); }
/*     */ 
/*     */ 
/*     */   
/*  94 */   public boolean supportTfsCheckinPolicies() { return this.myTFSCheckBox.isSelected(); }
/*     */ 
/*     */ 
/*     */   
/*  98 */   public boolean supportStatefulCheckinPolicies() { return this.myStatefulCheckBox.isSelected(); }
/*     */ 
/*     */ 
/*     */   
/* 102 */   public boolean reportNotInstalledCheckinPolicies() { return this.myReportNotInstalledPoliciesCheckBox.isSelected(); }
/*     */ 
/*     */   
/*     */   public void setSupportTfsCheckinPolicies(boolean supportTfsCheckinPolicies) {
/* 106 */     this.myTFSCheckBox.setSelected(supportTfsCheckinPolicies);
/* 107 */     updateNonInstalledCheckbox();
/*     */   }
/*     */   
/*     */   public void setSupportStatefulCheckinPolicies(boolean supportStatefulCheckinPolicies) {
/* 111 */     this.myStatefulCheckBox.setSelected(supportStatefulCheckinPolicies);
/* 112 */     updateNonInstalledCheckbox();
/*     */   }
/*     */ 
/*     */   
/* 116 */   public void setReportNotInstalledCheckinPolicies(boolean reportNotInstalledCheckinPolicies) { this.myReportNotInstalledPoliciesCheckBox.setSelected(reportNotInstalledCheckinPolicies); }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ProjectConfigurableForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */