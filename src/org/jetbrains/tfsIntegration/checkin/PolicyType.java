/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public final class PolicyType
/*    */ {
/*    */   public static final String DEFAULT_INSTALLATION_INSTRUCTIONS = "(No installation instructions provided)";
/*    */   public static final String DEFAULT_DESCRIPTION = "(No description)";
/*    */   @NotNull
/*    */   private final String myId;
/*    */   @NotNull
/*    */   private final String myName;
/*    */   @Nullable
/*    */   private final String myDescription;
/*    */   @Nullable
/*    */   private final String myInstallationInstructions;
/*    */   
/*    */   public PolicyType(@NotNull String id, @NotNull String name, @Nullable String description, @Nullable String installationInstructions) {
/* 42 */     this.myId = id;
/* 43 */     this.myName = name;
/* 44 */     this.myDescription = description;
/* 45 */     this.myInstallationInstructions = installationInstructions;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 50 */   public String getId() {  return this.myId; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 55 */   public String getName() {  return this.myName; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 60 */   public String getDescription() {  return (this.myDescription != null) ? this.myDescription : "(No description)"; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 65 */   public String getInstallationInstructions() { return (this.myInstallationInstructions != null) ? this.myInstallationInstructions : "(No installation instructions provided)"; }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 70 */     if (this == o) return true; 
/* 71 */     if (o == null || getClass() != o.getClass()) return false;
/*    */     
/* 73 */     PolicyType that = (PolicyType)o;
/*    */     
/* 75 */     if (!this.myId.equals(that.myId)) return false;
/*    */     
/* 77 */     return true;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 82 */   public int hashCode() { return this.myId.hashCode(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\PolicyType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */