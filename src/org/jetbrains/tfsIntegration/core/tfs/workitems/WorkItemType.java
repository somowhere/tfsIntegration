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
/*    */ public class WorkItemType
/*    */ {
/* 27 */   private static final Map<String, WorkItemType> ourAllTypes = new HashMap<>();
/*    */   
/* 29 */   public static final WorkItemType BUG = register("Bug");
/* 30 */   public static final WorkItemType TASK = register("Task");
/*    */   @NonNls
/*    */   @NotNull
/*    */   private final String myName;
/*    */   
/* 35 */   private WorkItemType(@NonNls @NotNull String name) { this.myName = name; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 40 */   public String getName() {    return this.myName; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 45 */   public static WorkItemType from(@NotNull String typeName) {         return register(typeName); }
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   private static synchronized WorkItemType register(@NotNull String typeName) {
/* 50 */        WorkItemType result = ourAllTypes.get(typeName);
/*    */     
/* 52 */     if (result == null) {
/* 53 */       result = new WorkItemType(typeName);
/* 54 */       ourAllTypes.put(typeName, result);
/*    */     } 
/*    */     
/* 57 */        return result;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 62 */   public String toString() { return this.myName; }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 67 */     if (this == o) return true; 
/* 68 */     if (!(o instanceof WorkItemType)) return false;
/*    */     
/* 70 */     WorkItemType type = (WorkItemType)o;
/*    */     
/* 72 */     return this.myName.equals(type.myName);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 77 */   public int hashCode() { return this.myName.hashCode(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\workitems\WorkItemType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */