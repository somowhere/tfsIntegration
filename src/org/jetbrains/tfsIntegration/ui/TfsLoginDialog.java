/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogWrapper;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.ui.ValidationInfo;
/*     */ import com.intellij.openapi.util.Condition;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.util.net.HttpConfigurable;
/*     */ import java.net.URI;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.Credentials;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TfsLoginDialog
/*     */   extends DialogWrapper
/*     */ {
/*  41 */   private static final Logger LOG = Logger.getInstance(TfsLoginDialog.class.getName());
/*     */   
/*     */   private TfsLoginForm myLoginForm;
/*     */   
/*     */   private String lastMessage;
/*     */   
/*     */   @Nullable
/*     */   private Condition<TfsLoginDialog> myOkActionCallback;
/*     */ 
/*     */   
/*     */   public TfsLoginDialog(Project project, URI initialUri, Credentials initialCredentials, boolean allowAddressChange, @Nullable Condition<TfsLoginDialog> okActionCallback) {
/*  52 */     super(project, true);
/*  53 */     doInit(initialUri, initialCredentials, allowAddressChange, okActionCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TfsLoginDialog(JComponent parentComponent, URI initialUri, Credentials initialCredentials, boolean allowAddressChange, @Nullable Condition<TfsLoginDialog> okActionCallback) {
/*  61 */     super(parentComponent, true);
/*  62 */     doInit(initialUri, initialCredentials, allowAddressChange, okActionCallback);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doInit(URI initialUri, Credentials initialCredentials, boolean allowAddressChange, Condition<TfsLoginDialog> okActionCallback) {
/*  69 */     this.myOkActionCallback = okActionCallback;
/*  70 */     setTitle(TFSBundle.message(allowAddressChange ? "logindialog.title.connect" : "logindialog.title.login", new Object[0]));
/*     */     
/*  72 */     this.myLoginForm = new TfsLoginForm(initialUri, initialCredentials, allowAddressChange);
/*  73 */     this.myLoginForm.addListener(e -> {
/*  74 */           this.lastMessage = null;
/*  75 */           setOKActionEnabled(true);
/*     */         });
/*     */     
/*  78 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  83 */   protected JComponent createCenterPanel() { return this.myLoginForm.getContentPane(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   public JComponent getPreferredFocusedComponent() { return this.myLoginForm.getPreferredFocusedComponent(); }
/*     */ 
/*     */   
/*     */   public URI getUri() {
/*  92 */     URI uri = TfsUtil.getUrl(this.myLoginForm.getUrl(), false, false);
/*  93 */     LOG.assertTrue((uri != null));
/*  94 */     return uri;
/*     */   }
/*     */ 
/*     */   
/*  98 */   public Credentials getCredentials() { return this.myLoginForm.getCredentials(); }
/*     */ 
/*     */   
/*     */   public void setMessage(@Nullable String message) {
/* 102 */     if (message != null && !message.endsWith(".")) {
/* 103 */       message = message + ".";
/*     */     }
/*     */     
/* 106 */     this.lastMessage = message;
/* 107 */     setErrorText(this.lastMessage);
/* 108 */     setOKActionEnabled((this.lastMessage == null));
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/* 113 */   public List<ValidationInfo> doValidateAll() { return (this.lastMessage != null) ? Collections.singletonList(new ValidationInfo(this.lastMessage, null)) : this.myLoginForm.validate(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 118 */   protected String getDimensionServiceKey() { return "TFS.Login"; }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doOKAction() {
/* 123 */     if (shouldPromptForProxyPassword(false)) {
/* 124 */       HttpConfigurable hc = HttpConfigurable.getInstance();
/* 125 */       hc.setPlainProxyPassword(this.myLoginForm.getProxyPassword());
/*     */     } 
/*     */     
/* 128 */     if (this.myLoginForm.getCredentials().getType() == Credentials.Type.Alternate && "http".equals(getUri().getScheme()) && 
/* 129 */       Messages.showYesNoDialog(this.myLoginForm.getContentPane(), "You're about to send your credentials over unsecured HTTP connection. Continue?", 
/* 130 */         getTitle(), null) != 0) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 136 */     if (this.myOkActionCallback == null || this.myOkActionCallback.value(this)) {
/* 137 */       super.doOKAction();
/*     */     }
/*     */   }
/*     */   
/*     */   public static boolean shouldPromptForProxyPassword(boolean strictOnly) {
/* 142 */     HttpConfigurable hc = HttpConfigurable.getInstance();
/* 143 */     return (TFSConfigurationManager.getInstance().useIdeaHttpProxy() && hc.USE_HTTP_PROXY && hc.PROXY_AUTHENTICATION && !hc.KEEP_PROXY_PASSWORD && (!strictOnly || 
/*     */ 
/*     */       
/* 146 */       StringUtil.isEmpty(hc.getPlainProxyPassword())));
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\TfsLoginDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */