/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.BranchRelative;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JScrollPane;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ChangeTypeMask;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.CellRenderer;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.ContentProvider;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.CustomTreeTable;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.TreeTableColumn;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ItemInfoForm
/*     */ {
/*     */   private JLabel myServerNameLabel;
/*     */   private JLabel myLocalNameLabel;
/*     */   private JLabel myLatestVersionLabel;
/*     */   private JLabel myWorkspaceVersionLabel;
/*     */   private JLabel myEncodingLabel;
/*     */   private JLabel myPendingChangesLabel;
/*     */   private JLabel myBranchesLabel;
/*     */   private CustomTreeTable<BranchRelative> myBranchesTree;
/*     */   private JPanel myPanel;
/*     */   private JLabel myDeletionIdLabel;
/*     */   private JLabel myLockLabel;
/*     */   private JScrollPane myTreePane;
/*     */   private JLabel myWorkspaceLabel;
/*     */   private final Collection<? extends BranchRelative> myBranches;
/*     */   
/*  56 */   private static final TreeTableColumn<BranchRelative> SERVER_PATH_COLUMN = new TreeTableColumn<BranchRelative>("Server path", 350)
/*     */     {
/*     */       public String getPresentableString(BranchRelative value) {
/*  59 */         return value.getBranchToItem().getItem();
/*     */       }
/*     */     };
/*     */   
/*  63 */   private static final TreeTableColumn<BranchRelative> TREE_TABLE_COLUMN = new TreeTableColumn<BranchRelative>("Branched from version", 150)
/*     */     {
/*     */       public String getPresentableString(BranchRelative value)
/*     */       {
/*  67 */         if (value.getBranchFromItem() != null) {
/*  68 */           return MessageFormat.format("{0}", new Object[] { Integer.valueOf(value.getBranchFromItem().getCs()) });
/*     */         }
/*  70 */         return "";
/*     */       }
/*     */     };
/*     */   
/*     */   public ItemInfoForm(WorkspaceInfo workspace, ExtendedItem item, Collection<? extends BranchRelative> branches) {
/*  75 */     this.myBranches = branches;
/*  76 */     this.myServerNameLabel.setText((item.getTitem() != null) ? item.getTitem() : item.getSitem());
/*  77 */     this.myLocalNameLabel.setText(VersionControlPath.localPathFromTfsRepresentation(item.getLocal()));
/*  78 */     this.myLatestVersionLabel.setText(String.valueOf(item.getLatest()));
/*  79 */     this.myWorkspaceVersionLabel.setText(String.valueOf(item.getLver()));
/*  80 */     this.myEncodingLabel
/*  81 */       .setText((item.getEnc() != Integer.MIN_VALUE) ? String.valueOf(item.getEnc()) : TFSBundle.message("encoding.not.applicable", new Object[0]));
/*  82 */     this.myPendingChangesLabel.setText((new ChangeTypeMask(item.getChg())).toString());
/*  83 */     this.myDeletionIdLabel.setText((item.getDid() != Integer.MIN_VALUE) ? String.valueOf(item.getDid()) : TFSBundle.message("not.deleted", new Object[0]));
/*  84 */     this.myLockLabel.setText(
/*  85 */         (item.getLock() != null) ? 
/*  86 */         TFSBundle.message("locked.for.by", new Object[] { item.getLock().getValue(), item.getLowner()
/*  87 */           }) : TFSBundle.message("locked.none", new Object[0]));
/*  88 */     this.myWorkspaceLabel.setText(MessageFormat.format("{0} on server {1}", new Object[] { workspace.getName(), workspace.getServer().getPresentableUri() }));
/*     */     
/*  90 */     if (this.myBranches.size() < 2) {
/*  91 */       this.myTreePane.setVisible(false);
/*  92 */       this.myBranchesLabel.setText("No branches");
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  97 */   public JComponent getPanel() { return this.myPanel; }
/*     */ 
/*     */   
/*     */   private void createUIComponents() {
/* 101 */     List<TreeTableColumn<BranchRelative>> columns = Arrays.asList((TreeTableColumn<BranchRelative>[])new TreeTableColumn[] { SERVER_PATH_COLUMN, TREE_TABLE_COLUMN });
/* 102 */     this.myBranchesTree = new CustomTreeTable(columns, new ContentProviderImpl(), new CellRendererImpl(), false, false);
/* 103 */     this.myBranchesTree.expandAll();
/*     */   }
/*     */   
/*     */   private class ContentProviderImpl implements ContentProvider<BranchRelative> { private ContentProviderImpl() {}
/*     */     
/*     */     public Collection<BranchRelative> getRoots() {
/* 109 */       for (BranchRelative branch : ItemInfoForm.this.myBranches) {
/* 110 */         if (branch.getRelfromid() == 0) {
/* 111 */           return Collections.singletonList(branch);
/*     */         }
/*     */       } 
/* 114 */       return Collections.emptyList();
/*     */     }
/*     */ 
/*     */     
/*     */     public Collection<BranchRelative> getChildren(@NotNull BranchRelative parent) {
/* 119 */        Collection<BranchRelative> children = new ArrayList<>();
/* 120 */       for (BranchRelative branch : ItemInfoForm.this.myBranches) {
/* 121 */         if (branch.getRelfromid() == parent.getReltoid()) {
/* 122 */           children.add(branch);
/*     */         }
/*     */       } 
/* 125 */       return children;
/*     */     } }
/*     */ 
/*     */   
/*     */   private static class CellRendererImpl
/*     */     extends CellRenderer<BranchRelative>
/*     */   {
/*     */     private CellRendererImpl() {}
/*     */     
/*     */     public void render(CustomTreeTable<BranchRelative> treeTable, TreeTableColumn<BranchRelative> column, BranchRelative value, JLabel cell) {
/* 135 */       super.render(treeTable, column, value, cell);
/* 136 */       Font defaultFont = treeTable.getFont();
/* 137 */       Font boldFont = new Font(defaultFont.getName(), 1, defaultFont.getSize());
/* 138 */       cell.setFont(value.getReqstd() ? boldFont : defaultFont);
/* 139 */       if (column == SERVER_PATH_COLUMN)
/* 140 */         cell.setIcon((value.getBranchToItem().getType() == ItemType.Folder) ? UiConstants.ICON_FOLDER : UiConstants.ICON_FILE); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ItemInfoForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */