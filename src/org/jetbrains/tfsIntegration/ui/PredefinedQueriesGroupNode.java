/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.ide.projectView.PresentationData;
/*    */ import com.intellij.ui.SimpleTextAttributes;
/*    */ import com.intellij.ui.treeStructure.SimpleNode;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItemsQuery;
/*    */ 
/*    */ 
/*    */ public class PredefinedQueriesGroupNode
/*    */   extends BaseQueryNode
/*    */ {
/* 15 */   public PredefinedQueriesGroupNode(@NotNull QueriesTreeContext context) { super(context); }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void doUpdate() {
/* 20 */     PresentationData presentation = getPresentation();
/*    */     
/* 22 */     presentation.addText("Predefined Queries", SimpleTextAttributes.REGULAR_ATTRIBUTES);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 28 */   public Object[] getEqualityObjects() { (new Object[2])[0] = getServer().getUri(); (new Object[2])[1] = WorkItemsQuery.class;return new Object[2]; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public SimpleNode[] getChildren() {
/* 34 */     List<SimpleNode> result = new ArrayList<>();
/*    */     
/* 36 */     for (WorkItemsQuery query : WorkItemsQuery.values()) {
/* 37 */       result.add(new PredefinedQueryNode(this.myQueriesTreeContext, query));
/*    */     }
/*    */     
/* 40 */       return result.toArray(new SimpleNode[0]);
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\PredefinedQueriesGroupNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */