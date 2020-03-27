/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import java.util.List;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.tfsIntegration.checkin.PolicyBase;
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
/*    */ public class ChooseCheckinPolicyDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private ChooseCheckinPolicyForm myForm;
/*    */   private final List<PolicyBase> myPolicies;
/*    */   
/*    */   public ChooseCheckinPolicyDialog(Project project, List<PolicyBase> policies) {
/* 31 */     super(project, false);
/* 32 */     this.myPolicies = policies;
/* 33 */     setTitle("Add Checkin Policy");
/* 34 */     init();
/*    */     
/* 36 */     setSize(450, 500);
/*    */     
/* 38 */     getOKAction().setEnabled(false);
/*    */   }
/*    */ 
/*    */   
/*    */   protected JComponent createCenterPanel() {
/* 43 */     this.myForm = new ChooseCheckinPolicyForm(this.myPolicies);
/* 44 */     this.myForm.addListener(new ChooseCheckinPolicyForm.Listener()
/*    */         {
/*    */           public void stateChanged() {
/* 47 */             ChooseCheckinPolicyDialog.this.getOKAction().setEnabled((ChooseCheckinPolicyDialog.this.myForm.getSelectedPolicy() != null));
/*    */           }
/*    */ 
/*    */           
/*    */           public void close() {
/* 52 */             ChooseCheckinPolicyDialog.this.doOKAction();
/*    */           }
/*    */         });
/*    */     
/* 56 */     return this.myForm.getContentPane();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 61 */   protected String getDimensionServiceKey() { return "TFS.ChooseCheckInPolicy"; }
/*    */ 
/*    */ 
/*    */   
/* 65 */   public PolicyBase getSelectedPolicy() { return this.myForm.getSelectedPolicy(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ChooseCheckinPolicyDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */