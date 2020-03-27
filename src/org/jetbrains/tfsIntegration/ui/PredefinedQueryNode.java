/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.ide.projectView.PresentationData;
/*    */ import com.intellij.openapi.vcs.VcsException;
/*    */ import com.intellij.ui.SimpleTextAttributes;
/*    */ import com.intellij.ui.treeStructure.SimpleNode;
/*    */ import com.intellij.ui.treeStructure.SimpleTree;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItemsQuery;
/*    */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*    */ 
/*    */ public class PredefinedQueryNode extends BaseQueryNode {
/*    */   @NotNull
/*    */   private final WorkItemsQuery myQuery;
/*    */   
/*    */   public PredefinedQueryNode(@NotNull QueriesTreeContext context, @NotNull WorkItemsQuery query) {
/* 18 */     super(context);
/*    */     
/* 20 */     this.myQuery = query;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void doUpdate() {
/* 25 */     PresentationData presentation = getPresentation();
/*    */     
/* 27 */     presentation.addText(this.myQuery.toString(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 33 */   public Object[] getEqualityObjects() { (new Object[1])[0] = this.myQuery;  return new Object[1]; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 39 */   public SimpleNode[] getChildren() {   return NO_CHILDREN; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 44 */   public boolean isAlwaysLeaf() { return true; }
/*    */ 
/*    */ 
/*    */   
/*    */   public void handleSelection(@NotNull final SimpleTree tree) {
/* 49 */      this.myQueriesTreeContext.queryWorkItems(new TfsExecutionUtil.Process<WorkItemsQueryResult>()
/*    */         {
/*    */           @NotNull
/*    */           public WorkItemsQueryResult run() throws TfsException, VcsException {
/* 53 */              return new WorkItemsQueryResult(PredefinedQueryNode.this.myQuery.queryWorkItems(PredefinedQueryNode.this.getServer(), tree, null));
/*    */           }
/*    */         });
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\PredefinedQueryNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */