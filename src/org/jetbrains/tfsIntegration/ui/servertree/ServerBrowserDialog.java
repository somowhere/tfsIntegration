/*    */ package org.jetbrains.tfsIntegration.ui.servertree;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.intellij.openapi.util.Disposer;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServerBrowserDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final TfsTreeForm myForm;
/*    */   
/*    */   public ServerBrowserDialog(String title, Project project, ServerInfo server, @Nullable String initialPath, boolean foldersOnly, boolean canCreateVirtualFolders) {
/* 36 */     super(project, false);
/*    */     
/* 38 */     this.myForm = new TfsTreeForm();
/* 39 */     this.myForm.initialize(server, initialPath, foldersOnly, canCreateVirtualFolders, null);
/* 40 */     this.myForm.addListener(new TfsTreeForm.SelectionListener()
/*    */         {
/*    */           public void selectionChanged() {
/* 43 */             ServerBrowserDialog.this.setOKActionEnabled((ServerBrowserDialog.this.myForm.getSelectedItem() != null));
/*    */           }
/*    */         });
/* 46 */     Disposer.register(getDisposable(), this.myForm);
/*    */     
/* 48 */     setSize(500, 600);
/*    */     
/* 50 */     setTitle(title);
/* 51 */     init();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 57 */   protected JComponent createCenterPanel() { return this.myForm.getContentPane(); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 62 */   public String getSelectedPath() { return this.myForm.getSelectedPath(); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 67 */   public TfsTreeForm.SelectedItem getSelectedItem() { return this.myForm.getSelectedItem(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 72 */   protected String getDimensionServiceKey() { return "TFS.ServerBrowser"; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 77 */   public JComponent getPreferredFocusedComponent() { return this.myForm.getPreferredFocusedComponent(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\servertree\ServerBrowserDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */