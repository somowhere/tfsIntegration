/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.ui.treeStructure.SimpleNode;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class WorkItemQueriesTreeRootNode
/*    */   extends BaseQueryNode {
/*    */   @NotNull
/*    */   private final PredefinedQueriesGroupNode myPredefinedQueriesGroupNode;
/*    */   
/*    */   public WorkItemQueriesTreeRootNode(@NotNull QueriesTreeContext context) {
/* 14 */     super(context);
/*    */     
/* 16 */     this.myPredefinedQueriesGroupNode = new PredefinedQueriesGroupNode(this.myQueriesTreeContext);
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 21 */   public PredefinedQueriesGroupNode getPredefinedQueriesGroupNode() {    return this.myPredefinedQueriesGroupNode; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 27 */   public Object[] getEqualityObjects() { (new Object[1])[0] = getServer().getUri();    return new Object[1]; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public SimpleNode[] getChildren() {
/* 33 */     List<SimpleNode> result = new ArrayList<>();
/*    */     
/* 35 */     result.add(this.myPredefinedQueriesGroupNode);
/* 36 */     for (String projectPath : getState().getProjectPaths(getServer())) {
/* 37 */       result.add(new SavedQueryFolderNode(this.myQueriesTreeContext, projectPath));
/*    */     }
/*    */     
/* 40 */          return result.toArray(new SimpleNode[0]);
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\WorkItemQueriesTreeRootNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */