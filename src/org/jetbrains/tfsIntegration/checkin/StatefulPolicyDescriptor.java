/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.jdom.Element;
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
/*    */ public class StatefulPolicyDescriptor
/*    */   extends PolicyDescriptor
/*    */ {
/*    */   public static final String DEFAULT_PRIORITY = "0";
/*    */   @NotNull
/*    */   private Element myConfiguration;
/*    */   private final List<String> myScope;
/*    */   @NotNull
/*    */   private final String myPriority;
/*    */   @Nullable
/*    */   private final String myLongDescription;
/*    */   
/*    */   public StatefulPolicyDescriptor(@NotNull PolicyType type, boolean enabled, @NotNull Element configuration, List<String> scope, @NotNull String priority, @Nullable String longDescription) {
/* 41 */     super(type, enabled);
/* 42 */     this.myConfiguration = configuration;
/* 43 */     this.myScope = scope;
/* 44 */     this.myPriority = priority;
/* 45 */     this.myLongDescription = longDescription;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 50 */   public Element getConfiguration() {   return this.myConfiguration; }
/*    */ 
/*    */ 
/*    */   
/* 54 */   public List<String> getScope() { return this.myScope; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 59 */   public String getPriority() {  return this.myPriority; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 64 */   public String getLongDescription() {  return (this.myLongDescription != null) ? this.myLongDescription : "(No description)"; }
/*    */ 
/*    */ 
/*    */   
/* 68 */   public void setConfiguration(@NotNull Element configuration) { this.myConfiguration = configuration; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\StatefulPolicyDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */