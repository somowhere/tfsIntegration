/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
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
/*     */ import java.net.URI;
/*     */ import java.text.MessageFormat;
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
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProxySettingsForm
/*     */ {
/*     */   private JPanel myContentPane;
/*     */   private JRadioButton myNoProxyRadioButton;
/*     */   private JRadioButton myProxyServerRadioButton;
/*     */   private JTextField myProxyServerTextField;
/*     */   private JLabel myMessageLabel;
/*     */   private JLabel myInfoLabel;
/*     */   private JLabel myProxyUrlLabel;
/*     */   private final EventDispatcher<Listener> myEventDispatcher;
/*     */   
/*     */   public ProxySettingsForm(@Nullable URI initialProxyUri, @Nullable String serverQualifiedUsername) {
/*  49 */         this.myEventDispatcher = EventDispatcher.create(Listener.class);
/*     */ 
/*     */     
/*  52 */     if (initialProxyUri == null) {
/*  53 */       this.myNoProxyRadioButton.setSelected(true);
/*     */     } else {
/*     */       
/*  56 */       this.myProxyServerRadioButton.setSelected(true);
/*  57 */       this.myProxyServerTextField.setText(initialProxyUri.toString());
/*     */     } 
/*     */     
/*  60 */     ActionListener radioButtonListener = new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/*  63 */           ProxySettingsForm.this.updateContols();
/*  64 */           ((ProxySettingsForm.Listener)ProxySettingsForm.this.myEventDispatcher.getMulticaster()).stateChanged();
/*     */         }
/*     */       };
/*     */     
/*  68 */     this.myNoProxyRadioButton.addActionListener(radioButtonListener);
/*  69 */     this.myProxyServerRadioButton.addActionListener(radioButtonListener);
/*  70 */     this.myProxyServerTextField.getDocument().addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  73 */                ((ProxySettingsForm.Listener)ProxySettingsForm.this.myEventDispatcher.getMulticaster()).stateChanged();
/*     */           }
/*     */         });
/*     */ 
/*     */     
/*  78 */     String infoMessage = MessageFormat.format("Credentials to connect to the proxy: {0}", new Object[] { (serverQualifiedUsername != null) ? serverQualifiedUsername : "(not specified)" });
/*  79 */     this.myInfoLabel.setText(infoMessage);
/*     */     
/*  81 */     updateContols();
/*     */   }
/*     */   
/*     */   private void updateContols() {
/*  85 */     this.myProxyServerTextField.setEnabled(this.myProxyServerRadioButton.isSelected());
/*  86 */     this.myInfoLabel.setEnabled(this.myProxyServerRadioButton.isSelected());
/*  87 */     this.myProxyUrlLabel.setEnabled(this.myProxyServerRadioButton.isSelected());
/*     */     
/*  89 */     if (this.myProxyServerRadioButton.isSelected()) {
/*  90 */       IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(this.myProxyServerTextField, true));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*  95 */   public JComponent getContentPane() { return this.myContentPane; }
/*     */ 
/*     */ 
/*     */   
/*  99 */   public void addListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 103 */   public void removeListener(Listener listener) { this.myEventDispatcher.removeListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 107 */   public boolean isValid() { return (this.myNoProxyRadioButton.isSelected() || TfsUtil.getUrl(this.myProxyServerTextField.getText(), true, true) != null); }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public URI getProxyUri() {
/* 112 */     if (this.myNoProxyRadioButton.isSelected()) {
/* 113 */       return null;
/*     */     }
/*     */     
/* 116 */     return TfsUtil.getUrl(this.myProxyServerTextField.getText(), true, true);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 121 */   public void setMessage(@Nullable String message) { this.myMessageLabel.setText(message); }
/*     */   
/*     */   public static interface Listener extends EventListener {
/*     */     void stateChanged();
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ProxySettingsForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */