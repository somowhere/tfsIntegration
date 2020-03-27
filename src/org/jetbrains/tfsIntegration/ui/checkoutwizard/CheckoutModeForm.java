/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import java.awt.BorderLayout;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ public class CheckoutModeForm
/*     */ {
/*     */   private JPanel myContentPanel;
/*     */   private JRadioButton myAutoModeButton;
/*     */   private JRadioButton myManualModeButton;
/*     */   private JTextField myWorkspaceNameField;
/*     */   private JLabel myErrorLabel;
/*     */   private final EventDispatcher<ChangeListener> myEventDispatcher;
/*     */   
/*     */   public CheckoutModeForm() {
/*  40 */     this.myEventDispatcher = EventDispatcher.create(ChangeListener.class);
/*     */ 
/*     */     
/*  43 */     this.myErrorLabel.setIcon(UIUtil.getBalloonWarningIcon());
/*     */     
/*  45 */     this.myAutoModeButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*  48 */             ((ChangeListener)CheckoutModeForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*  49 */             IdeFocusManager.findInstanceByComponent(CheckoutModeForm.this.myContentPanel).requestFocus(CheckoutModeForm.this.myWorkspaceNameField, true);
/*     */           }
/*     */         });
/*  52 */     this.myManualModeButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*  55 */             ((ChangeListener)CheckoutModeForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*     */           }
/*     */         });
/*  58 */     this.myWorkspaceNameField.getDocument().addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  61 */              CheckoutModeForm.this.myAutoModeButton.setSelected(true);
/*  62 */             ((ChangeListener)CheckoutModeForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*  68 */   public JPanel getContentPanel() { return this.myContentPanel; }
/*     */ 
/*     */ 
/*     */   
/*  72 */   public boolean isAutoModeSelected() { return this.myAutoModeButton.isSelected(); }
/*     */ 
/*     */   
/*     */   public void setAutoModeSelected(boolean selected) {
/*  76 */     if (selected) {
/*  77 */       this.myAutoModeButton.setSelected(true);
/*     */     } else {
/*     */       
/*  80 */       this.myManualModeButton.setSelected(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  85 */   public String getNewWorkspaceName() { return this.myWorkspaceNameField.getText(); }
/*     */ 
/*     */ 
/*     */   
/*  89 */   public void setNewWorkspaceName(String name) { this.myWorkspaceNameField.setText(name); }
/*     */ 
/*     */ 
/*     */   
/*  93 */   public void addListener(ChangeListener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */   
/*     */   public void setErrorMessage(String message) {
/*  97 */     this.myErrorLabel.setText(message);
/*  98 */     this.myErrorLabel.setVisible((message != null));
/*     */   }
/*     */   
/*     */   public JComponent getPreferredFocusedComponent() {
/* 102 */     if (this.myAutoModeButton.isSelected()) {
/* 103 */       return this.myWorkspaceNameField;
/*     */     }
/* 105 */     return this.myManualModeButton;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\CheckoutModeForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */