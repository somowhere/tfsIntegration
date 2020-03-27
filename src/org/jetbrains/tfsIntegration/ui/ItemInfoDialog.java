/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.BranchRelative;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*    */ import java.util.Collection;
/*    */ import javax.swing.Action;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
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
/*    */ public class ItemInfoDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final ExtendedItem myExtendedItem;
/*    */   private final WorkspaceInfo myWorkspace;
/*    */   private final Collection<BranchRelative> myBranches;
/*    */   
/*    */   public ItemInfoDialog(Project project, WorkspaceInfo workspace, ExtendedItem extendedItem, Collection<BranchRelative> branches, String title) {
/* 40 */     super(project, false);
/* 41 */     this.myWorkspace = workspace;
/* 42 */     this.myBranches = branches;
/* 43 */     this.myExtendedItem = extendedItem;
/*    */     
/* 45 */     setTitle(title);
/* 46 */     setResizable(true);
/* 47 */     init();
/* 48 */     setOKButtonText("Close");
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 54 */   protected Action[] createActions() { (new Action[1])[0] = getOKAction(); return new Action[1]; }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   protected JComponent createCenterPanel() {
/* 60 */     ItemInfoForm itemInfoForm = new ItemInfoForm(this.myWorkspace, this.myExtendedItem, this.myBranches);
/* 61 */     return itemInfoForm.getPanel();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 66 */   protected String getDimensionServiceKey() { return "TFS.ItemInfo"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ItemInfoDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */