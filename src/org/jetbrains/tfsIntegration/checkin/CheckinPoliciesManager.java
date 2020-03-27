/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
/*    */ import com.intellij.openapi.progress.ProgressIndicator;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import java.util.HashSet;
/*    */ import java.util.List;
/*    */ import java.util.Set;
/*    */ import org.jdom.Element;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.TFSVcs;
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
/*    */ public class CheckinPoliciesManager
/*    */ {
/*    */   private static List<PolicyBase> ourInstalledPolicies;
/* 33 */   public static final PolicyBase DUMMY_POLICY = new PolicyBase() {
/* 34 */       final PolicyType DUMMY = new PolicyType("DUMMY_POLICY", "", "", "");
/*    */ 
/*    */ 
/*    */       
/*    */       @NotNull
/* 39 */       public PolicyType getPolicyType() {  return this.DUMMY; }
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 44 */       public PolicyFailure[] evaluate(@NotNull PolicyContext policycontext, @NotNull ProgressIndicator progressIndicator) {  return new PolicyFailure[0]; }
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 49 */       public boolean canEdit() { return false; }
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 54 */       public boolean edit(Project project) { return false; }
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 59 */       public void loadState(@NotNull Element element) {
/*    */          }
/*    */ 
/*    */       
/* 63 */       public void saveState(@NotNull Element element) {   }
/*    */     };
/*    */   
/*    */   public static List<PolicyBase> getInstalledPolicies() throws DuplicatePolicyIdException {
/* 67 */     if (ourInstalledPolicies == null) {
/* 68 */       List<PolicyBase> installedPolicies = PolicyBase.EP_NAME.getExtensionList();
/* 69 */       Set<PolicyType> types = new HashSet<>(installedPolicies.size());
/* 70 */       for (PolicyBase policy : installedPolicies) {
/* 71 */         if (!types.add(policy.getPolicyType())) {
/* 72 */           TFSVcs.LOG.warn("Duplicate checkin policy type: " + policy.getPolicyType().getId());
/* 73 */           throw new DuplicatePolicyIdException(policy.getPolicyType().getId());
/*    */         } 
/*    */       } 
/* 76 */       ourInstalledPolicies = installedPolicies;
/*    */     } 
/*    */     
/* 79 */     return ourInstalledPolicies;
/*    */   }
/*    */   
/*    */   @Nullable
/*    */   public static PolicyBase find(PolicyType type) throws DuplicatePolicyIdException {
/* 84 */     PolicyBase result = null;
/* 85 */     for (PolicyBase p : getInstalledPolicies()) {
/* 86 */       if (p.getPolicyType().equals(type)) {
/* 87 */         result = p;
/*    */         break;
/*    */       } 
/*    */     } 
/* 91 */     return result;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\CheckinPoliciesManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */