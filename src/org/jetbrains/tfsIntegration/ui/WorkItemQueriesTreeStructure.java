/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.Disposable;
/*    */ import com.intellij.ui.treeStructure.SimpleTreeStructure;
/*    */ import com.microsoft.tfs.core.TFSTeamProjectCollection;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
/*    */ import org.jetbrains.tfsIntegration.core.TfsSdkManager;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil;
/*    */ 
/*    */ public class WorkItemQueriesTreeStructure
/*    */   extends SimpleTreeStructure
/*    */   implements QueriesTreeContext, Disposable {
/*    */   @NotNull
/*    */   private final WorkItemQueriesTreeRootNode myRootNode;
/*    */   @NotNull
/*    */   private final TFSTeamProjectCollection myProjectCollection;
/*    */   
/*    */   public WorkItemQueriesTreeStructure(@NotNull WorkItemsPanel panel) {
/* 21 */     this.myPanel = panel;
/* 22 */     this.myState = panel.getState();
/* 23 */     this.myServer = panel.getServer();
/* 24 */     this.myRootNode = new WorkItemQueriesTreeRootNode(this);
/* 25 */     this.myProjectCollection = new TFSTeamProjectCollection(this.myServer.getUri(), TfsSdkManager.getInstance().getCredentials(this.myServer));
/*    */   } @NotNull
/*    */   private final CheckinParameters myState; @NotNull
/*    */   private final ServerInfo myServer; @NotNull
/*    */   private final WorkItemsPanel myPanel; @NotNull
/* 30 */   public PredefinedQueriesGroupNode getPredefinedQueriesGroupNode() {      return this.myRootNode.getPredefinedQueriesGroupNode(); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 36 */   public Object getRootElement() {    return this.myRootNode; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 41 */   public boolean isToBuildChildrenInBackground(@NotNull Object element) {    return (element instanceof SavedQueryFolderNode && ((SavedQueryFolderNode)element).isProject()); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 47 */   public CheckinParameters getState() {    return this.myState; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 53 */   public ServerInfo getServer() {    return this.myServer; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 59 */   public TFSTeamProjectCollection getProjectCollection() {    return this.myProjectCollection; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 64 */   public void queryWorkItems(@NotNull TfsExecutionUtil.Process<WorkItemsQueryResult> query) {    this.myPanel.queryWorkItems(query); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 69 */   public void dispose() { this.myProjectCollection.close(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\WorkItemQueriesTreeStructure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */