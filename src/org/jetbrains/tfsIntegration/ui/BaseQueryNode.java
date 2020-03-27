/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.ui.treeStructure.SimpleNode;
/*    */ import com.microsoft.tfs.core.clients.workitem.WorkItemClient;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*    */ 
/*    */ public abstract class BaseQueryNode
/*    */   extends SimpleNode {
/*    */   @NotNull
/*    */   protected final QueriesTreeContext myQueriesTreeContext;
/*    */   
/* 14 */   protected BaseQueryNode(@NotNull QueriesTreeContext context) { this.myQueriesTreeContext = context; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 19 */   protected WorkItemClient getWorkItemClient() {   return this.myQueriesTreeContext.getProjectCollection().getWorkItemClient(); }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 24 */   protected ServerInfo getServer() {  return this.myQueriesTreeContext.getServer(); }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 29 */   protected CheckinParameters getState() {   return this.myQueriesTreeContext.getState(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\BaseQueryNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */