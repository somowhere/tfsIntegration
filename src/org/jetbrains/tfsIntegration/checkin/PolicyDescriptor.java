/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
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
/*    */ 
/*    */ public class PolicyDescriptor
/*    */ {
/*    */   private boolean myEnabled;
/*    */   @NotNull
/*    */   private final PolicyType myType;
/*    */   
/*    */   public PolicyDescriptor(@NotNull PolicyType type, boolean enabled) {
/* 28 */     this.myEnabled = enabled;
/* 29 */     this.myType = type;
/*    */   }
/*    */ 
/*    */   
/* 33 */   public boolean isEnabled() { return this.myEnabled; }
/*    */ 
/*    */ 
/*    */   
/* 37 */   public void setEnabled(Boolean value) { this.myEnabled = value.booleanValue(); }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 42 */   public PolicyType getType() {   return this.myType; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\PolicyDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */