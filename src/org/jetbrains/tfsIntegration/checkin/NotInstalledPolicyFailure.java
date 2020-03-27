/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
/*    */ import java.text.MessageFormat;
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
/*    */ 
/*    */ public class NotInstalledPolicyFailure
/*    */   extends PolicyFailure
/*    */ {
/*    */   private final PolicyType myPolicyType;
/*    */   
/*    */   public NotInstalledPolicyFailure(@NotNull PolicyType policyType, boolean reportId) {
/* 27 */     super(CheckinPoliciesManager.DUMMY_POLICY, getMessage(policyType), getTooltip(policyType, reportId));
/* 28 */     this.myPolicyType = policyType;
/*    */   }
/*    */ 
/*    */   
/* 32 */   private static String getMessage(PolicyType type) { return MessageFormat.format("Checkin policy ''{0}'' is not installed", new Object[] { type.getName() }); }
/*    */ 
/*    */   
/*    */   private static String getTooltip(PolicyType type, boolean reportId) {
/* 36 */     if (reportId) {
/* 37 */       return MessageFormat.format("Policy id: {0}\n{1}", new Object[] { type.getId(), type.getInstallationInstructions() });
/*    */     }
/*    */     
/* 40 */     return type.getInstallationInstructions();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 46 */   public String getPolicyName() { return this.myPolicyType.getName(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\NotInstalledPolicyFailure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */