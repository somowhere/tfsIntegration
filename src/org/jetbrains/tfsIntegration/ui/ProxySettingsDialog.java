/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import java.net.URI;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.configuration.Credentials;
/*    */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ProxySettingsDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private ProxySettingsForm myForm;
/*    */   @NotNull
/*    */   private final URI myServerUri;
/*    */   
/*    */   public ProxySettingsDialog(Project project, @NotNull URI serverUri) {
/* 37 */     super(project, true);
/* 38 */     this.myServerUri = serverUri;
/* 39 */     String title = TFSBundle.message("proxy.dialog.title", new Object[] { TfsUtil.getPresentableUri(serverUri) });
/* 40 */     setTitle(title);
/*    */     
/* 42 */     init();
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createCenterPanel() {
/* 48 */     Credentials credentials = TFSConfigurationManager.getInstance().getCredentials(this.myServerUri);
/* 49 */     this
/* 50 */       .myForm = new ProxySettingsForm(TFSConfigurationManager.getInstance().getProxyUri(this.myServerUri), (credentials != null) ? credentials.getQualifiedUsername() : null);
/*    */     
/* 52 */     return this.myForm.getContentPane();
/*    */   }
/*    */   
/*    */   private void updateButtons() {
/* 56 */     String errorMessage = this.myForm.isValid() ? null : "Please enter valid proxy address.";
/* 57 */     this.myForm.setMessage(errorMessage);
/* 58 */     setOKActionEnabled(this.myForm.isValid());
/*    */   }
/*    */ 
/*    */   
/*    */   protected void doOKAction() {
/* 63 */     if (this.myForm.isValid()) {
/* 64 */       super.doOKAction();
/*    */     } else {
/*    */       
/* 67 */       updateButtons();
/* 68 */       this.myForm.addListener(new ProxySettingsForm.Listener()
/*    */           {
/*    */             public void stateChanged() {
/* 71 */               ProxySettingsDialog.this.updateButtons();
/*    */             }
/*    */           });
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/* 79 */   public URI getProxyUri() { return this.myForm.getProxyUri(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 84 */   protected String getDimensionServiceKey() { return "TFS.ProxySettings"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ProxySettingsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */