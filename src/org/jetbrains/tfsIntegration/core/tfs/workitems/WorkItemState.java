/*    */ package org.jetbrains.tfsIntegration.core.tfs.workitems;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class WorkItemState
/*    */ {
/* 14 */   private static final Map<String, WorkItemState> ourAllStates = new HashMap<>();
/*    */   
/* 16 */   public static final WorkItemState ACTIVE = register("Active");
/* 17 */   public static final WorkItemState RESOLVED = register("Resolved");
/* 18 */   public static final WorkItemState CLOSED = register("Closed");
/*    */   @NonNls
/*    */   @NotNull
/*    */   private final String myName;
/*    */   
/* 23 */   private WorkItemState(@NonNls @NotNull String name) { this.myName = name; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 28 */   public String getName() {    return this.myName; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 33 */   public static WorkItemState from(@NotNull String stateName) {         return register(stateName); }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   private static synchronized WorkItemState register(@NotNull String stateName) {
/* 38 */        WorkItemState result = ourAllStates.get(stateName);
/*    */     
/* 40 */     if (result == null) {
/* 41 */       result = new WorkItemState(stateName);
/* 42 */       ourAllStates.put(stateName, result);
/*    */     } 
/*    */     
/* 45 */        return result;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 50 */   public String toString() { return this.myName; }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 55 */     if (this == o) return true; 
/* 56 */     if (!(o instanceof WorkItemState)) return false;
/*    */     
/* 58 */     WorkItemState state = (WorkItemState)o;
/*    */     
/* 60 */     return this.myName.equals(state.myName);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 65 */   public int hashCode() { return this.myName.hashCode(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\workitems\WorkItemState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */