/*    */ package org.jetbrains.tfsIntegration.actions;
/*    */ 
/*    */ import com.intellij.openapi.actionSystem.ActionPlaces;
/*    */ import com.intellij.openapi.actionSystem.AnActionEvent;
/*    */ import com.intellij.openapi.project.DumbAwareAction;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.ui.ManageWorkspacesDialog;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TfsEditConfigurationAction
/*    */   extends DumbAwareAction
/*    */ {
/*    */   public void update(@NotNull AnActionEvent e) {
/* 17 */        Project project = e.getProject();
/* 18 */     if (ActionPlaces.isPopupPlace(e.getPlace())) {
/* 19 */       e.getPresentation().setVisible((project != null));
/*    */     } else {
/*    */       
/* 22 */       e.getPresentation().setEnabled((project != null));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void actionPerformed(@NotNull AnActionEvent e) {
/* 28 */        Project project = e.getProject();
/* 29 */     ManageWorkspacesDialog d = new ManageWorkspacesDialog(project);
/* 30 */     d.show();
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\actions\TfsEditConfigurationAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */