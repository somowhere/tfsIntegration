/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.ide.util.treeView.AbstractTreeStructure;
/*    */ import com.intellij.ui.treeStructure.SimpleTreeBuilder;
/*    */ import com.intellij.ui.treeStructure.SimpleTreeStructure;
/*    */ import javax.swing.JTree;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ import javax.swing.tree.DefaultTreeModel;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class WorkItemQueriesTreeBuilder
/*    */   extends SimpleTreeBuilder
/*    */ {
/* 14 */   public WorkItemQueriesTreeBuilder(@NotNull JTree tree, @NotNull SimpleTreeStructure treeStructure) { super(tree, new DefaultTreeModel(new DefaultMutableTreeNode(treeStructure.getRootElement())), (AbstractTreeStructure)treeStructure, null); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 19 */   protected boolean isSmartExpand() { return false; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\WorkItemQueriesTreeBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */