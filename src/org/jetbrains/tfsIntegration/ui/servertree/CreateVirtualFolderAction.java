/*    */ package org.jetbrains.tfsIntegration.ui.servertree;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.ActionPlaces;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.openapi.ui.Messages;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ 
/*    */ public class CreateVirtualFolderAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   public void update(@NotNull AnActionEvent e) {
/* 15 */      boolean isEnabled = isEnabled(e);
/* 16 */     if (ActionPlaces.isPopupPlace(e.getPlace())) {
/* 17 */       e.getPresentation().setVisible(isEnabled);
/*    */     } else {
/*    */       
/* 20 */       e.getPresentation().setEnabled(isEnabled);
/*    */     } 
/*    */   }
/*    */   
/*    */   private static boolean isEnabled(AnActionEvent e) {
/* 25 */     TfsTreeForm form = (TfsTreeForm)e.getData(TfsTreeForm.KEY);
/* 26 */     return (form != null && form.getSelectedItem() != null && form.canCreateVirtualFolders());
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 31 */      TfsTreeForm form = (TfsTreeForm)e.getData(TfsTreeForm.KEY);
/*    */     
/* 33 */     String folderName = Messages.showInputDialog(form.getContentPane(), TFSBundle.message("create.subfolder.prompt", new Object[0]), TFSBundle.message("create.subfolder.title", new Object[0]), null);
/*    */     
/* 35 */     if (StringUtil.isEmpty(folderName)) {
/*    */       return;
/*    */     }
/* 38 */     form.createVirtualFolder(folderName);
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\servertree\CreateVirtualFolderAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */