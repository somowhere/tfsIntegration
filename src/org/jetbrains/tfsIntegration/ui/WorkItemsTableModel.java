/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.ui.ComboBox;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTable;
/*     */ import com.intellij.ui.treeStructure.treetable.TreeTableModel;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinWorkItemAction;
/*     */ import com.microsoft.tfs.core.clients.workitem.query.WorkItemLinkInfo;
/*     */ import java.awt.Component;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.DefaultCellEditor;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.table.TableCellEditor;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import javax.swing.tree.TreeNode;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkItemsCheckinParameters;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItemState;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItemType;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class WorkItemsTableModel
/*     */   extends ListTreeTableModelOnColumns
/*     */ {
/*  48 */   private static final Logger LOG = Logger.getInstance(WorkItemsTableModel.class);
/*     */   
/*     */   @NotNull
/*     */   private final DefaultMutableTreeNode myRoot;
/*     */   
/*     */   WorkItemsTableModel(@NotNull WorkItemsCheckinParameters content) {
/*  54 */     super(null, new ColumnInfo[] { new CheckBoxColumn(content), TYPE, ID, TITLE, STATE, new CheckInActionColumn(content) });
/*     */     
/*  56 */     this.myContent = content;
/*     */     
/*  58 */     this.myRoot = new DefaultMutableTreeNode();
/*  59 */     setRoot(this.myRoot);
/*     */   }
/*     */   @NotNull
/*     */   private final WorkItemsCheckinParameters myContent;
/*     */   @Nullable
/*  64 */   public CheckinWorkItemAction getAction(@NotNull WorkItem workItem) {    return this.myContent.getAction(workItem); }
/*     */ 
/*     */   
/*     */   public void setContent(@NotNull WorkItemsCheckinParameters content) {
/*  68 */        this.myContent.update(content);
/*     */     
/*  70 */     this.myRoot.removeAllChildren();
/*  71 */     buildModel();
/*  72 */     reload(this.myRoot);
/*     */   }
/*     */   
/*     */   private void buildModel() {
/*  76 */     List<WorkItemLinkInfo> links = this.myContent.getLinks();
/*     */     
/*  78 */     if (!ContainerUtil.isEmpty(links)) {
/*  79 */       buildTreeModel(links);
/*     */     } else {
/*     */       
/*  82 */       buildFlatModel();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void buildTreeModel(@NotNull List<WorkItemLinkInfo> links) {
/*  87 */        validateLinksStructure(links);
/*     */ 
/*     */     
/*  90 */     Map<Integer, DefaultMutableTreeNode> workItemsMap = ContainerUtil.map2Map(this.myContent.getWorkItems(), workItem -> Pair.create(Integer.valueOf(workItem.getId()), new DefaultMutableTreeNode(workItem)));
/*  91 */     workItemsMap.put(Integer.valueOf(0), this.myRoot);
/*     */     
/*  93 */     for (WorkItemLinkInfo link : links) {
/*  94 */       DefaultMutableTreeNode parentNode = workItemsMap.get(Integer.valueOf(link.getSourceID()));
/*  95 */       DefaultMutableTreeNode childNode = workItemsMap.get(Integer.valueOf(link.getTargetID()));
/*     */       
/*  97 */       if (parentNode != null && childNode != null) {
/*  98 */         parentNode.add(childNode);
/*     */         continue;
/*     */       } 
/* 101 */       LOG.info("Could not resolve work item link " + link.getSourceID() + "-" + link.getTargetID());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void validateLinksStructure(@NotNull List<WorkItemLinkInfo> links) {
/* 107 */        if (links.size() != this.myContent.getWorkItems().size()) {
/* 108 */       String linksValue = StringUtil.join(links, info -> info.getSourceID() + " - " + info.getTargetID(), ", ");
/* 109 */       String workItemIdsValue = StringUtil.join(this.myContent.getWorkItems(), workItem -> String.valueOf(workItem.getId()), ", ");
/*     */       
/* 111 */       LOG.error("Unknown work item links structure\nLinks: " + linksValue + "\nWork Items: " + workItemIdsValue);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void buildFlatModel() {
/* 116 */     for (WorkItem workItem : this.myContent.getWorkItems()) {
/* 117 */       this.myRoot.add(new DefaultMutableTreeNode(workItem));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValueAt(Object aValue, Object node, int column) {
/* 123 */     super.setValueAt(aValue, node, column);
/*     */     
/* 125 */     nodeChanged((TreeNode)node);
/*     */   }
/*     */   
/*     */   static abstract class WorkItemFieldColumn<Aspect>
/*     */     extends ColumnInfo<DefaultMutableTreeNode, Aspect> {
/*     */     private final int myWidth;
/*     */     
/*     */     WorkItemFieldColumn(@NotNull String name, int width) {
/* 133 */       super(name);
/*     */       
/* 135 */       this.myWidth = width;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/* 141 */     public String getPreferredStringValue() { return ""; }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 146 */     public int getAdditionalWidth() { return this.myWidth; }
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Aspect valueOf(@NotNull DefaultMutableTreeNode node) {
/* 152 */          Object userObject = node.getUserObject();
/*     */       
/* 154 */       return (userObject instanceof WorkItem) ? valueOf((WorkItem)userObject) : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setValue(@NotNull DefaultMutableTreeNode node, @NotNull Aspect value) {
/* 159 */             if (node.getUserObject() instanceof WorkItem) {
/* 160 */         setValue((WorkItem)node.getUserObject(), value);
/*     */       }
/*     */     }
/*     */ 
/*     */     
/* 165 */     public void setValue(@NotNull WorkItem workItem, @NotNull Aspect value) {       }
/*     */     
/*     */     @Nullable
/*     */     public abstract Aspect valueOf(@NotNull WorkItem param1WorkItem);
/*     */   }
/*     */   
/*     */   static class CheckBoxColumn
/*     */     extends WorkItemFieldColumn<Boolean> {
/*     */     @NotNull
/*     */     private final WorkItemsCheckinParameters myContent;
/* 175 */     private final TableCellRenderer myRenderer = new NoBackgroundBooleanTableCellRenderer();
/*     */     
/*     */     CheckBoxColumn(@NotNull WorkItemsCheckinParameters content) {
/* 178 */       super(" ", 50);
/*     */       
/* 180 */       this.myContent = content;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public Boolean valueOf(@NotNull WorkItem workItem) {
/* 186 */          CheckinWorkItemAction action = this.myContent.getAction(workItem);
/*     */       
/* 188 */       return Boolean.valueOf((action != null && action != CheckinWorkItemAction.None));
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 193 */     public Class<?> getColumnClass() { return Boolean.class; }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 198 */     public boolean isCellEditable(@NotNull DefaultMutableTreeNode node) {    return true; }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/* 204 */     public TableCellRenderer getRenderer(@NotNull DefaultMutableTreeNode node) {    return this.myRenderer; }
/*     */ 
/*     */ 
/*     */     
/*     */     public void setValue(@NotNull WorkItem workItem, @NotNull Boolean value) {
/* 209 */             if (value == Boolean.TRUE) {
/*     */         
/* 211 */         CheckinWorkItemAction action = workItem.isActionPossible(CheckinWorkItemAction.Resolve) ? CheckinWorkItemAction.Resolve : CheckinWorkItemAction.Associate;
/*     */         
/* 213 */         this.myContent.setAction(workItem, action);
/*     */       } else {
/*     */         
/* 216 */         this.myContent.removeAction(workItem);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/* 221 */   static WorkItemFieldColumn<WorkItemType> TYPE = new WorkItemFieldColumn<WorkItemType>("Type", 300)
/*     */     {
/*     */       @Nullable
/*     */       public WorkItemType valueOf(@NotNull WorkItem workItem) {
/* 225 */            return workItem.getType();
/*     */       }
/*     */     };
/*     */   
/* 229 */   static WorkItemFieldColumn<Integer> ID = new WorkItemFieldColumn<Integer>("Id", 200)
/*     */     {
/*     */       @Nullable
/*     */       public Integer valueOf(@NotNull WorkItem workItem) {
/* 233 */            return Integer.valueOf(workItem.getId());
/*     */       }
/*     */     };
/*     */   
/* 237 */   static WorkItemFieldColumn<String> TITLE = new WorkItemFieldColumn<String>("Title", 1500)
/*     */     {
/*     */       @Nullable
/*     */       public String valueOf(@NotNull WorkItem workItem) {
/* 241 */            return workItem.getTitle();
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 247 */       public Class<?> getColumnClass() { return TreeTableModel.class; }
/*     */     };
/*     */ 
/*     */   
/* 251 */   static WorkItemFieldColumn<WorkItemState> STATE = new WorkItemFieldColumn<WorkItemState>("State", 300)
/*     */     {
/*     */       @Nullable
/*     */       public WorkItemState valueOf(@NotNull WorkItem workItem) {
/* 255 */            return workItem.getState();
/*     */       }
/*     */     };
/*     */   
/*     */   static class CheckInActionColumn
/*     */     extends WorkItemFieldColumn<CheckinWorkItemAction> {
/* 261 */     private final ComboBox myComboBox = new ComboBox((Object[])new CheckinWorkItemAction[] { CheckinWorkItemAction.Resolve, CheckinWorkItemAction.Associate });
/* 262 */     private final TableCellEditor myCellEditor = new DefaultCellEditor((JComboBox)this.myComboBox)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/*     */         @Nullable
/*     */         public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*     */         {
/* 270 */           TreeTable treeTable = (TreeTable)table;
/* 271 */           WorkItemsTableModel model = (WorkItemsTableModel)treeTable.getTableModel();
/*     */           
/* 273 */           WorkItem workItem = (WorkItem)((DefaultMutableTreeNode)treeTable.getTree().getPathForRow(row).getLastPathComponent()).getUserObject();
/* 274 */           CheckinWorkItemAction action = model.getAction(workItem);
/*     */           
/* 276 */           if (action != null && workItem.isActionPossible(CheckinWorkItemAction.Resolve)) {
/* 277 */             WorkItemsTableModel.CheckInActionColumn.this.myComboBox.setSelectedItem(action);
/*     */             
/* 279 */             return super.getTableCellEditorComponent(table, value, isSelected, row, column);
/*     */           } 
/*     */           
/* 282 */           return null;
/*     */         }
/*     */       };
/*     */     
/*     */     @NotNull
/*     */     private final WorkItemsCheckinParameters myContent;
/*     */     
/*     */     CheckInActionColumn(@NotNull WorkItemsCheckinParameters content) {
/* 290 */       super("Checkin Action", 400);
/*     */       
/* 292 */       this.myContent = content;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nullable
/*     */     public CheckinWorkItemAction valueOf(@NotNull WorkItem workItem) {
/* 298 */          CheckinWorkItemAction action = this.myContent.getAction(workItem);
/*     */       
/* 300 */       return CheckinWorkItemAction.None.equals(action) ? null : action;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 305 */     public boolean isCellEditable(@NotNull DefaultMutableTreeNode node) {    return true; }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Nullable
/* 311 */     public TableCellEditor getEditor(@NotNull DefaultMutableTreeNode node) {    return this.myCellEditor; }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 316 */     public void setValue(@NotNull WorkItem workItem, @NotNull CheckinWorkItemAction value) {       this.myContent.setAction(workItem, value); }
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\WorkItemsTableModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */