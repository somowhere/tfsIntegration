/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import java.util.List;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.tfsIntegration.checkin.PolicyFailure;
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
/*    */ public class OverridePolicyWarningsDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private OverridePolicyWarningsForm myForm;
/*    */   private final Project myProject;
/*    */   private final List<PolicyFailure> myFailures;
/*    */   
/*    */   public OverridePolicyWarningsDialog(Project project, List<PolicyFailure> failures) {
/* 33 */     super(project, false);
/* 34 */     this.myProject = project;
/* 35 */     this.myFailures = failures;
/* 36 */     setTitle("Checkin: Policy Warnings");
/* 37 */     init();
/* 38 */     getOKAction().setEnabled(false);
/* 39 */     setSize(500, 500);
/*    */   }
/*    */ 
/*    */   
/*    */   protected JComponent createCenterPanel() {
/* 44 */     this.myForm = new OverridePolicyWarningsForm(this.myProject, this.myFailures);
/* 45 */     this.myForm.addListener(new OverridePolicyWarningsForm.Listener()
/*    */         {
/*    */           public void stateChanged() {
/* 48 */             OverridePolicyWarningsDialog.this.getOKAction().setEnabled(StringUtil.isNotEmpty(OverridePolicyWarningsDialog.this.myForm.getReason()));
/*    */           }
/*    */         });
/* 51 */     return this.myForm.getContentPane();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 56 */   protected String getDimensionServiceKey() { return "TFS.CheckIn.OverridePolicyWarnings"; }
/*    */ 
/*    */ 
/*    */   
/* 60 */   public String getReason() { return this.myForm.getReason(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\OverridePolicyWarningsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */