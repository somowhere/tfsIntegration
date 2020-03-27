/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.microsoft.tfs.core.clients.workitem.query.WorkItemLinkInfo;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
/*    */ 
/*    */ public class WorkItemsQueryResult
/*    */ {
/*    */   @NotNull
/*    */   private final List<WorkItem> myWorkItems;
/*    */   @Nullable
/*    */   private final List<WorkItemLinkInfo> myLinks;
/*    */   
/* 16 */   public WorkItemsQueryResult(@NotNull List<WorkItem> workItems) { this(workItems, null); }
/*    */ 
/*    */   
/*    */   public WorkItemsQueryResult(@NotNull List<WorkItem> items, @Nullable List<WorkItemLinkInfo> links) {
/* 20 */     this.myWorkItems = items;
/* 21 */     this.myLinks = links;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 26 */   public List<WorkItem> getWorkItems() {   return this.myWorkItems; }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 31 */   public List<WorkItemLinkInfo> getLinks() { return this.myLinks; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\WorkItemsQueryResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */