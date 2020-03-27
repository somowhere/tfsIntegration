/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.EventListener;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ 
/*     */ public class MergeNameForm
/*     */ {
/*     */   private final EventDispatcher<Listener> myEventDispatcher;
/*     */   private final String myYoursPath;
/*     */   private final String myTheirsPath;
/*     */   private JRadioButton myYoursRadioButton;
/*     */   
/*     */   public MergeNameForm(String yoursName, String theirsName) {
/*  35 */      this.myEventDispatcher = EventDispatcher.create(Listener.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  48 */     this.myYoursPath = yoursName;
/*  49 */     this.myTheirsPath = theirsName;
/*     */     
/*  51 */     this.myYoursRadioButton.setText(this.myYoursRadioButton.getText() + ": " + this.myYoursPath);
/*  52 */     this.myYoursRadioButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent ae) {
/*  55 */             MergeNameForm.this.update();
/*     */           }
/*     */         });
/*     */     
/*  59 */     this.myTheirsRadioButton.setText(this.myTheirsRadioButton.getText() + ": " + this.myTheirsPath);
/*  60 */     this.myTheirsRadioButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent ae) {
/*  63 */             MergeNameForm.this.update();
/*     */           }
/*     */         });
/*  66 */     this.myCustomPathTextField.setText(this.myYoursPath);
/*  67 */     this.myUseCustomRadioButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent ae) {
/*  70 */             MergeNameForm.this.update();
/*     */           }
/*     */         });
/*     */     
/*  74 */     this.myCustomPathTextField.setText(this.myYoursPath);
/*     */     
/*  76 */     this.myCustomPathTextField.getDocument().addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  79 */            ((MergeNameForm.Listener)MergeNameForm.this.myEventDispatcher.getMulticaster()).selectedPathChanged();
/*     */           }
/*     */         });
/*     */     
/*  83 */     this.myErrorLabel.setText(" ");
/*     */   }
/*     */   private JRadioButton myTheirsRadioButton; private JRadioButton myUseCustomRadioButton; private JTextField myCustomPathTextField; private JPanel myContentPanel; private JLabel myErrorLabel;
/*     */   private void update() {
/*  87 */     this.myCustomPathTextField.setEnabled(this.myUseCustomRadioButton.isSelected());
/*  88 */     ((Listener)this.myEventDispatcher.getMulticaster()).selectedPathChanged();
/*     */   }
/*     */ 
/*     */   
/*  92 */   public JComponent getPanel() { return this.myContentPanel; }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public String getSelectedPath() {
/*  97 */     if (this.myYoursRadioButton.isSelected()) {
/*  98 */       return this.myYoursPath;
/*     */     }
/* 100 */     if (this.myTheirsRadioButton.isSelected()) {
/* 101 */       return this.myTheirsPath;
/*     */     }
/* 103 */     if (this.myUseCustomRadioButton.isSelected()) {
/* 104 */       return this.myCustomPathTextField.getText();
/*     */     }
/* 106 */     throw new IllegalStateException("Unexpected state");
/*     */   }
/*     */ 
/*     */   
/* 110 */   public void addListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 114 */   public void removeListener(Listener listener) { this.myEventDispatcher.removeListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 118 */   public void setErrorText(String errorMessage) { this.myErrorLabel.setText(errorMessage); }
/*     */   
/*     */   public static interface Listener extends EventListener {
/*     */     void selectedPathChanged();
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\MergeNameForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */