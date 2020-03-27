/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.ui.ComboBox;
/*     */ import com.intellij.openapi.ui.ValidationInfo;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import com.intellij.ui.HyperlinkLabel;
/*     */ import com.intellij.ui.SimpleListCellRenderer;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import com.intellij.util.net.HttpConfigurable;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.net.URI;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JPasswordField;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.ListCellRenderer;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.event.HyperlinkEvent;
/*     */ import javax.swing.event.HyperlinkListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.Credentials;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.webservice.auth.NativeNTLM2Scheme;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TfsLoginForm
/*     */ {
/*     */   private JTextField myAddressField;
/*     */   private JTextField myUsernameField;
/*     */   private JTextField myDomainField;
/*     */   private JPasswordField myPasswordField;
/*     */   private JCheckBox myStorePasswordCheckbox;
/*     */   private JPanel myContentPane;
/*     */   private HyperlinkLabel myProxyPasswordLabel;
/*     */   
/*     */   public TfsLoginForm(URI initialUri, Credentials initialCredentials, boolean allowUrlChange) {
/*  57 */         this.myEventDispatcher = EventDispatcher.create(ChangeListener.class);
/*     */ 
/*     */     
/*  60 */     this.myAddressField.setText((initialUri != null) ? TfsUtil.getPresentableUri(initialUri) : null);
/*  61 */     this.myAddressField.setEditable(allowUrlChange);
/*  62 */     this.myUsernameField.setText((initialCredentials != null) ? initialCredentials.getUserName() : null);
/*  63 */     this.myDomainField.setText((initialCredentials != null) ? initialCredentials.getDomain() : null);
/*  64 */     this.myPasswordField.setText((initialCredentials != null) ? initialCredentials.getPassword() : null);
/*     */     
/*  66 */     DocumentAdapter documentAdapter = new DocumentAdapter()
/*     */       {
/*     */         protected void textChanged(@NotNull DocumentEvent e) {
/*  69 */              ((ChangeListener)TfsLoginForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*     */         }
/*     */       };
/*     */     
/*  73 */     this.myAddressField.getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*  74 */     this.myUsernameField.getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*  75 */     this.myDomainField.getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*  76 */     this.myPasswordField.getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*     */     
/*  78 */     this.myProxyPasswordLabel
/*  79 */       .setHyperlinkText("", TFSBundle.message("login.dialog.proxy.label.1", new Object[0]), TFSBundle.message("login.dialog.proxy.label.2", new Object[0]));
/*  80 */     this.myProxyPasswordLabel.addHyperlinkListener(new HyperlinkListener()
/*     */         {
/*     */           public void hyperlinkUpdate(HyperlinkEvent e) {
/*  83 */             if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
/*  84 */               HttpConfigurable.editConfigurable(TfsLoginForm.this.myContentPane);
/*     */             }
/*     */           }
/*     */         });
/*     */     
/*  89 */     if (TfsLoginDialog.shouldPromptForProxyPassword(false)) {
/*  90 */       HttpConfigurable hc = HttpConfigurable.getInstance();
/*  91 */       this.myProxyPasswordField.setText(hc.getPlainProxyPassword());
/*  92 */       this.myProxyPanel.setVisible(true);
/*     */     } else {
/*     */       
/*  95 */       this.myProxyPanel.setVisible(false);
/*     */     } 
/*     */     
/*  98 */     this.myTypeCombo.setRenderer((ListCellRenderer)SimpleListCellRenderer.create("", Credentials.Type::getPresentableText));
/*  99 */     if (NativeNTLM2Scheme.isAvailable()) {
/* 100 */       this.myTypeCombo.setModel(new DefaultComboBoxModel<>(new Credentials.Type[] { Credentials.Type.NtlmNative, Credentials.Type.NtlmExplicit, Credentials.Type.Alternate }));
/*     */       
/* 102 */       this.myTypeCombo.setSelectedItem((initialCredentials == null) ? Credentials.Type.NtlmNative : initialCredentials.getType());
/*     */     } else {
/*     */       
/* 105 */       this.myTypeCombo.setModel(new DefaultComboBoxModel<>(new Credentials.Type[] { Credentials.Type.NtlmExplicit, Credentials.Type.Alternate }));
/*     */     } 
/* 107 */     this.myTypeCombo.addActionListener(e -> {
/* 108 */           updateOnTypeChange();
/* 109 */           if (getCredentialsType() != Credentials.Type.NtlmNative) {
/* 110 */             IdeFocusManager.findInstanceByComponent(this.myContentPane).requestFocus(this.myUsernameField, true);
/*     */           }
/*     */         });
/* 113 */     this.myTypeCombo.addActionListener(e -> ((ChangeListener)this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this)));
/*     */     
/* 115 */     updateOnTypeChange();
/*     */   }
/*     */   private JPasswordField myProxyPasswordField; private JPanel myProxyPanel; private JLabel myUsernameLabel; private JLabel myDomainLabel; private JLabel myPasswordLabel; private ComboBox<Credentials.Type> myTypeCombo; private final EventDispatcher<ChangeListener> myEventDispatcher;
/*     */   
/* 119 */   private Credentials.Type getCredentialsType() { return (Credentials.Type)this.myTypeCombo.getSelectedItem(); }
/*     */ 
/*     */   
/*     */   private void updateOnTypeChange() {
/* 123 */     boolean isNative = (getCredentialsType() == Credentials.Type.NtlmNative);
/* 124 */     this.myUsernameLabel.setEnabled(!isNative);
/* 125 */     this.myUsernameField.setEnabled(!isNative);
/* 126 */     this.myDomainLabel.setEnabled((getCredentialsType() == Credentials.Type.NtlmExplicit));
/* 127 */     this.myDomainField.setEnabled((getCredentialsType() == Credentials.Type.NtlmExplicit));
/* 128 */     this.myPasswordLabel.setEnabled(!isNative);
/* 129 */     this.myPasswordField.setEnabled(!isNative);
/* 130 */     this.myStorePasswordCheckbox.setEnabled(!isNative);
/* 131 */     ((ChangeListener)this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*     */   }
/*     */   
/*     */   public JComponent getPreferredFocusedComponent() {
/* 135 */     if (this.myAddressField.isEditable()) {
/* 136 */       return this.myAddressField;
/*     */     }
/* 138 */     if (StringUtil.isEmpty(this.myUsernameField.getText())) {
/* 139 */       return this.myUsernameField;
/*     */     }
/*     */     
/* 142 */     return this.myPasswordField;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 147 */   public JPanel getContentPane() { return this.myContentPane; }
/*     */ 
/*     */ 
/*     */   
/* 151 */   public String getUrl() { return this.myAddressField.getText(); }
/*     */ 
/*     */ 
/*     */   
/* 155 */   public String getUsername() { return this.myUsernameField.getText(); }
/*     */ 
/*     */ 
/*     */   
/* 159 */   public String getDomain() { return this.myDomainField.getText(); }
/*     */ 
/*     */ 
/*     */   
/* 163 */   public String getPassword() { return String.valueOf(this.myPasswordField.getPassword()); }
/*     */ 
/*     */   
/*     */   public Credentials getCredentials() {
/* 167 */     if (getCredentialsType() == Credentials.Type.NtlmNative) {
/* 168 */       return Credentials.createNative();
/*     */     }
/*     */     
/* 171 */     return new Credentials(getUsername(), getDomain(), getPassword(), this.myStorePasswordCheckbox.isSelected(), getCredentialsType());
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 176 */   public void addListener(ChangeListener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 180 */   public void removeListener(ChangeListener listener) { this.myEventDispatcher.removeListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 184 */   public String getProxyPassword() { return String.valueOf(this.myProxyPasswordField.getPassword()); }
/*     */ 
/*     */ 
/*     */   
/*     */   private void createUIComponents() {
/* 189 */     this.myProxyPasswordLabel = new HyperlinkLabel()
/*     */       {
/*     */         protected void applyRenderingHints(Graphics g) {}
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 198 */   public boolean isUseNative() { return (getCredentialsType() == Credentials.Type.NtlmNative); }
/*     */ 
/*     */   
/*     */   List<ValidationInfo> validate() {
/* 202 */     List<ValidationInfo> result = new ArrayList<>();
/* 203 */     if (StringUtil.isEmptyOrSpaces(getUrl())) {
/* 204 */       result.add(new ValidationInfo(TFSBundle.message("login.dialog.address.empty", new Object[0]), this.myAddressField));
/* 205 */     } else if (TfsUtil.getUrl(getUrl(), false, false) == null) {
/* 206 */       result.add(new ValidationInfo(TFSBundle.message("login.dialog.address.invalid", new Object[0]), this.myAddressField));
/*     */     } 
/*     */     
/* 209 */     if (!isUseNative() && StringUtil.isEmptyOrSpaces(getUsername())) {
/* 210 */       result.add(new ValidationInfo(TFSBundle.message("login.dialog.username.empty", new Object[0]), this.myUsernameField));
/*     */     }
/*     */     
/* 213 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\TfsLoginForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */