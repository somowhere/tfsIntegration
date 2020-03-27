/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
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
/*    */ public class CheckInPoliciesDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final Project myProject;
/*    */   private final Map<String, ManageWorkspacesForm.ProjectEntry> myProjectToDescriptors;
/*    */   private CheckInPoliciesForm myForm;
/*    */   
/*    */   public CheckInPoliciesDialog(Project project, ServerInfo server, Map<String, ManageWorkspacesForm.ProjectEntry> projectToDescriptors) {
/* 35 */     super(project, false);
/* 36 */     this.myProject = project;
/* 37 */     this.myProjectToDescriptors = projectToDescriptors;
/* 38 */     setTitle(TFSBundle.message("checkin.policies.dialog.title", new Object[] { server.getPresentableUri() }));
/* 39 */     init();
/* 40 */     setSize(800, 500);
/*    */   }
/*    */ 
/*    */   
/*    */   protected JComponent createCenterPanel() {
/* 45 */     this.myForm = new CheckInPoliciesForm(this.myProject, this.myProjectToDescriptors);
/* 46 */     return this.myForm.getContentPane();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 51 */   protected String getDimensionServiceKey() { return "TFS.ConfigureCheckInPolicies"; }
/*    */ 
/*    */ 
/*    */   
/* 55 */   public Map<String, ManageWorkspacesForm.ProjectEntry> getModifications() { return this.myForm.getModifications(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 60 */   protected String getHelpId() { return "project.propVCSSupport.VCSs.TFS.edit.checkin.policies"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\CheckInPoliciesDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */