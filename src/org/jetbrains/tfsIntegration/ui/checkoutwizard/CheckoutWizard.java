/*    */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*    */ 
/*    */ import com.intellij.ide.wizard.AbstractWizardEx;
/*    */ import com.intellij.ide.wizard.AbstractWizardStepEx;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.Nullable;
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
/*    */ public class CheckoutWizard
/*    */   extends AbstractWizardEx
/*    */ {
/*    */   private final CheckoutWizardModel myModel;
/*    */   
/*    */   public CheckoutWizard(@Nullable Project project, List<CheckoutWizardStep> steps, CheckoutWizardModel model) {
/* 29 */     super("Checkout From TFS", project, steps);
/* 30 */     this.myModel = model;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 35 */   protected boolean canFinish() { return (this.myModel != null && this.myModel.isComplete() && ((AbstractWizardStepEx)getCurrentStepObject()).getNextStepId() == null); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 40 */   protected String getDimensionServiceKey() { return "TFS.CheckoutWizard"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\CheckoutWizard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */