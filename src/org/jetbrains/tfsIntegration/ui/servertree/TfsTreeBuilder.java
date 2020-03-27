/*    */ package org.jetbrains.tfsIntegration.ui.servertree;
/*    */ 
/*    */ import com.intellij.ide.util.treeView.AbstractTreeBuilder;
/*    */ import com.intellij.ide.util.treeView.AbstractTreeStructure;
/*    */ import com.intellij.ide.util.treeView.NodeDescriptor;
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import com.intellij.ui.treeStructure.SimpleNode;
/*    */ import com.intellij.ui.treeStructure.SimpleTreeStructure;
/*    */ import java.util.Comparator;
/*    */ import javax.swing.JTree;
/*    */ import javax.swing.tree.DefaultMutableTreeNode;
/*    */ import javax.swing.tree.DefaultTreeModel;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class TfsTreeBuilder
/*    */   extends AbstractTreeBuilder {
/* 17 */   private static final Logger LOG = Logger.getInstance(TfsTreeBuilder.class.getName());
/*    */   
/*    */   private static final Comparator<NodeDescriptor> COMPARATOR = (o1, o2) -> {
/* 20 */       if (o1 instanceof TfsErrorTreeNode) {
/* 21 */         return (o2 instanceof TfsErrorTreeNode) ? ((TfsErrorTreeNode)o1).getMessage().compareTo(((TfsErrorTreeNode)o2).getMessage()) : -1;
/*    */       }
/* 23 */       if (o2 instanceof TfsErrorTreeNode) {
/* 24 */         return 1;
/*    */       }
/*    */       
/* 27 */       TfsTreeNode n1 = (TfsTreeNode)o1;
/* 28 */       TfsTreeNode n2 = (TfsTreeNode)o2;
/* 29 */       if (n1.isDirectory() && !n2.isDirectory()) {
/* 30 */         return -1;
/*    */       }
/* 32 */       if (!n1.isDirectory() && n2.isDirectory()) {
/* 33 */         return 1;
/*    */       }
/*    */       
/* 36 */       return n1.getFileName().compareToIgnoreCase(n2.getFileName());
/*    */     };
/*    */   
/*    */   public static TfsTreeBuilder createInstance(@NotNull TfsTreeNode root, @NotNull JTree tree) {
/* 40 */      DefaultTreeModel treeModel = new DefaultTreeModel(new DefaultMutableTreeNode(root));
/* 41 */     tree.setModel(treeModel);
/* 42 */     return new TfsTreeBuilder(tree, treeModel, (AbstractTreeStructure)new SimpleTreeStructure.Impl(root)
/*    */         {
/*    */           public boolean isToBuildChildrenInBackground(@NotNull Object element) {
/* 45 */              return true;
/*    */           }
/*    */ 
/*    */           
/*    */           public boolean isAlwaysLeaf(@NotNull Object element) {
/* 50 */              if (element instanceof TfsTreeNode) {
/* 51 */               return !((TfsTreeNode)element).isDirectory();
/*    */             }
/*    */             
/* 54 */             LOG.assertTrue(element instanceof TfsErrorTreeNode);
/* 55 */             return true;
/*    */           }
/*    */         });
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 62 */   public TfsTreeBuilder(JTree tree, DefaultTreeModel treeModel, AbstractTreeStructure treeStructure) { super(tree, treeModel, treeStructure, COMPARATOR); }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void runBackgroundLoading(@NotNull Runnable runnable) {
/* 67 */      if (isDisposed())
/* 68 */       return;  runnable.run();
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isAutoExpandNode(NodeDescriptor nodeDescriptor) {
/* 73 */     if (nodeDescriptor instanceof TfsErrorTreeNode) {
/* 74 */       return true;
/*    */     }
/* 76 */     if (nodeDescriptor instanceof TfsTreeNode) {
/* 77 */       return (!((TfsTreeNode)nodeDescriptor).isDirectory() || ((TfsTreeNode)nodeDescriptor).isRoot());
/*    */     }
/* 79 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isAlwaysShowPlus(NodeDescriptor descriptor) {
/* 84 */     if (descriptor instanceof TfsTreeNode) {
/* 85 */       return ((TfsTreeNode)descriptor).isDirectory();
/*    */     }
/*    */     
/* 88 */     LOG.assertTrue(descriptor instanceof TfsErrorTreeNode);
/* 89 */     return false;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\servertree\TfsTreeBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */