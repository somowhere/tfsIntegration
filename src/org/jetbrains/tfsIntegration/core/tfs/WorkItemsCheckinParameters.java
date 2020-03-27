/*    */ package org.jetbrains.tfsIntegration.core.tfs;
/*    */ 
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinWorkItemAction;
/*    */ import com.microsoft.tfs.core.clients.workitem.query.WorkItemLinkInfo;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
/*    */ import org.jetbrains.tfsIntegration.ui.WorkItemsQueryResult;
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
/*    */ public class WorkItemsCheckinParameters
/*    */ {
/*    */   @NotNull
/*    */   private List<WorkItem> myWorkItems;
/*    */   @NotNull
/*    */   private Map<WorkItem, CheckinWorkItemAction> myActions;
/*    */   @Nullable
/*    */   private List<WorkItemLinkInfo> myLinks;
/*    */   
/*    */   private WorkItemsCheckinParameters(@NotNull List<WorkItem> workItems, @NotNull Map<WorkItem, CheckinWorkItemAction> actions, @Nullable List<WorkItemLinkInfo> links) {
/* 37 */     this.myWorkItems = workItems;
/* 38 */     this.myActions = actions;
/* 39 */     this.myLinks = links;
/*    */   }
/*    */ 
/*    */   
/* 43 */   public WorkItemsCheckinParameters() { this(Collections.emptyList(), new HashMap<>(), null); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 48 */   public CheckinWorkItemAction getAction(@NotNull WorkItem workItem) {    return this.myActions.get(workItem); }
/*    */ 
/*    */ 
/*    */   
/* 52 */   public void setAction(@NotNull WorkItem workItem, @NotNull CheckinWorkItemAction action) {       this.myActions.put(workItem, action); }
/*    */ 
/*    */ 
/*    */   
/* 56 */   public void removeAction(@NotNull WorkItem workItem) {    this.myActions.remove(workItem); }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 61 */   public List<WorkItem> getWorkItems() {      return Collections.unmodifiableList(this.myWorkItems); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 66 */   public List<WorkItemLinkInfo> getLinks() { return (this.myLinks != null) ? Collections.unmodifiableList(this.myLinks) : null; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 71 */   public WorkItemsCheckinParameters createCopy() {  return new WorkItemsCheckinParameters(new ArrayList<>(this.myWorkItems), new HashMap<>(this.myActions), getLinks()); }
/*    */ 
/*    */   
/*    */   public void update(@NotNull WorkItemsQueryResult queryResult) {
/* 75 */        this.myWorkItems = queryResult.getWorkItems();
/* 76 */     this.myLinks = queryResult.getLinks();
/* 77 */     this.myActions.clear();
/*    */   }
/*    */   
/*    */   public void update(@NotNull WorkItemsCheckinParameters parameters) {
/* 81 */        this.myWorkItems = parameters.myWorkItems;
/* 82 */     this.myLinks = parameters.myLinks;
/* 83 */     this.myActions = parameters.myActions;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 88 */   public Map<WorkItem, CheckinWorkItemAction> getWorkItemsActions() {      return Collections.unmodifiableMap(this.myActions); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\WorkItemsCheckinParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */