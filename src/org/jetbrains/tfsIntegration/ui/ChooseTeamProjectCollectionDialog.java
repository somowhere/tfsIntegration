/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import java.util.Collection;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.config.TfsServerConnectionHelper;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*    */ 
/*    */ 
/*    */ public class ChooseTeamProjectCollectionDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final ChooseTeamProjectCollectionForm myForm;
/*    */   
/*    */   public ChooseTeamProjectCollectionDialog(JComponent parent, String serverAddress, Collection<TfsServerConnectionHelper.TeamProjectCollectionDescriptor> items) {
/* 19 */     super(parent, false);
/*    */     
/* 21 */     setTitle(TFSBundle.message("choose.team.project.collection.dialog.title", new Object[0]));
/* 22 */     setSize(500, 400);
/*    */     
/* 24 */     this.myForm = new ChooseTeamProjectCollectionForm(serverAddress, items);
/* 25 */     this.myForm.addChangeListener(new ChooseTeamProjectCollectionForm.Listener()
/*    */         {
/*    */           public void stateChanged(ChangeEvent e) {
/* 28 */             ChooseTeamProjectCollectionDialog.this.revalidate();
/*    */           }
/*    */ 
/*    */           
/*    */           public void selected() {
/* 33 */             if (ChooseTeamProjectCollectionDialog.this.getErrorMessage() == null) {
/* 34 */               ChooseTeamProjectCollectionDialog.this.doOKAction();
/*    */             }
/*    */           }
/*    */         });
/*    */     
/* 39 */     init();
/*    */     
/* 41 */     revalidate();
/*    */   }
/*    */   
/*    */   private void revalidate() {
/* 45 */     String message = getErrorMessage();
/* 46 */     setOKActionEnabled((message == null));
/* 47 */     this.myForm.setErrorMessage(message);
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   private String getErrorMessage() {
/* 52 */     TfsServerConnectionHelper.TeamProjectCollectionDescriptor selected = getSelectedItem();
/* 53 */     if (selected == null) {
/* 54 */       return TFSBundle.message("no.team.project.collection.selected", new Object[0]);
/*    */     }
/* 56 */     if (TFSConfigurationManager.getInstance().serverKnown(selected.instanceId)) {
/* 57 */       return TFSBundle.message("duplicate.team.project.collection", new Object[] { selected.name });
/*    */     }
/* 59 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/* 64 */   public TfsServerConnectionHelper.TeamProjectCollectionDescriptor getSelectedItem() { return this.myForm.getSelectedItem(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 69 */   protected JComponent createCenterPanel() { return this.myForm.getContentPane(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 74 */   public JComponent getPreferredFocusedComponent() { return this.myForm.getPreferredFocusedComponent(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 79 */   protected String getDimensionServiceKey() { return "TFS.ChooseTeamProjectCollection"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ChooseTeamProjectCollectionDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */