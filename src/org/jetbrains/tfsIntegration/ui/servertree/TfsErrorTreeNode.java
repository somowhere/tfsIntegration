/*    */ package org.jetbrains.tfsIntegration.ui.servertree;
/*    */ 
/*    */ import com.intellij.ide.projectView.PresentationData;
/*    */ import com.intellij.ui.treeStructure.SimpleNode;
/*    */ import com.intellij.util.PlatformIcons;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ public class TfsErrorTreeNode extends SimpleNode {
/*    */   private final String myMessage;
/*    */   
/*    */   public TfsErrorTreeNode(SimpleNode parent, String message) {
/* 12 */     super(parent);
/* 13 */     this.myMessage = message;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void update(@NotNull PresentationData presentation) {
/* 18 */     super.update(presentation);
/* 19 */     presentation.addText(this.myMessage, getErrorAttributes());
/* 20 */     presentation.setIcon(PlatformIcons.ERROR_INTRODUCTION_ICON);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 26 */   public SimpleNode[] getChildren() { return NO_CHILDREN; }
/*    */ 
/*    */ 
/*    */   
/* 30 */   public String getMessage() { return this.myMessage; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\servertree\TfsErrorTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */