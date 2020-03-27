/*    */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*    */ 
/*    */ import com.intellij.ide.wizard.AbstractWizardStepEx;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ public abstract class CheckoutWizardStep
/*    */   extends AbstractWizardStepEx
/*    */ {
/*    */   @NotNull
/*    */   protected final CheckoutWizardModel myModel;
/*    */   
/*    */   public CheckoutWizardStep(String title, CheckoutWizardModel model) {
/* 27 */     super(title);
/* 28 */     this.myModel = model;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\CheckoutWizardStep.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */