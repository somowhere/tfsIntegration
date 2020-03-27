/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PolicyFailure
/*    */ {
/*    */   @NotNull
/*    */   private final PolicyBase myPolicy;
/*    */   @NotNull
/*    */   private final String myMessage;
/*    */   @Nullable
/*    */   private final String myTooltipText;
/*    */   
/* 36 */   public PolicyFailure(@NotNull PolicyBase policy, @NotNull String message) { this(policy, message, null); }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PolicyFailure(@NotNull PolicyBase policy, @NotNull String message, @Nullable String tooltipText) {
/* 46 */     this.myPolicy = policy;
/* 47 */     this.myMessage = message;
/* 48 */     this.myTooltipText = tooltipText;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 53 */   public String getMessage() {  return this.myMessage; }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 58 */   public String getTooltipText() { return this.myTooltipText; }
/*    */ 
/*    */ 
/*    */   
/* 62 */   public void activate(@NotNull Project project) {   this.myPolicy.activate(project, this); }
/*    */ 
/*    */ 
/*    */   
/* 66 */   public String getPolicyName() { return this.myPolicy.getPolicyType().getName(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\PolicyFailure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */