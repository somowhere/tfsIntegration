/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.ide.projectView.PresentationData;
/*     */ import com.intellij.openapi.util.ThrowableComputable;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.SimpleNode;
/*     */ import com.intellij.util.ObjectUtils;
/*     */ import com.microsoft.tfs.core.clients.workitem.project.Project;
/*     */ import com.microsoft.tfs.core.clients.workitem.queryhierarchy.QueryDefinition;
/*     */ import com.microsoft.tfs.core.clients.workitem.queryhierarchy.QueryFolder;
/*     */ import com.microsoft.tfs.core.clients.workitem.queryhierarchy.QueryItem;
/*     */ import com.microsoft.tfs.core.ws.runtime.exceptions.ProxyException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
/*     */ import org.jetbrains.tfsIntegration.ui.servertree.TfsErrorTreeNode;
/*     */ 
/*     */ public class SavedQueryFolderNode
/*     */   extends BaseQueryNode {
/*     */   @Nullable
/*     */   private final QueryFolder myQueryFolder;
/*     */   @Nullable
/*     */   private final String myProjectName;
/*     */   
/*     */   public SavedQueryFolderNode(@NotNull QueriesTreeContext context, @NotNull QueryFolder folder) {
/*  31 */     super(context);
/*     */     
/*  33 */     this.myQueryFolder = folder;
/*  34 */     this.myProjectName = null;
/*     */   }
/*     */   
/*     */   public SavedQueryFolderNode(@NotNull QueriesTreeContext context, @NotNull String projectPath) {
/*  38 */     super(context);
/*     */     
/*  40 */     this.myQueryFolder = null;
/*  41 */     this.myProjectName = VersionControlPath.getTeamProject(projectPath);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void doUpdate() {
/*  46 */     PresentationData presentation = getPresentation();
/*     */     
/*  48 */     presentation.addText(getQueryFolderName(), SimpleTextAttributes.REGULAR_ATTRIBUTES);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  53 */   public boolean isAlwaysShowPlus() { return true; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  59 */   public Object[] getEqualityObjects() { (new Object[1])[0] = getQueryFolderName();    return new Object[1]; }
/*     */ 
/*     */ 
/*     */   
/*  63 */   public boolean isProject() { return (this.myQueryFolder == null); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public SimpleNode[] getChildren() {
/*  69 */     List<SimpleNode> result = new ArrayList<>();
/*     */     
/*     */     try {
/*  72 */       result.addAll((Collection<? extends SimpleNode>)TfsUtil.forcePluginClassLoader(new ThrowableComputable<Collection<? extends SimpleNode>, VcsException>()
/*     */             {
/*     */               public Collection<? extends SimpleNode> compute() throws VcsException {
/*  75 */                 return SavedQueryFolderNode.this.getChildren(SavedQueryFolderNode.this.getQueryFolder());
/*     */               }
/*     */             }));
/*     */     }
/*  79 */     catch (VcsException e) {
/*  80 */       result.add(buildErrorNode((Exception)e));
/*     */     }
/*  82 */     catch (ProxyException e) {
/*  83 */       result.add(buildErrorNode((Exception)e));
/*     */     } 
/*     */     
/*  86 */          return result.toArray(new SimpleNode[0]);
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   private List<SimpleNode> getChildren(@NotNull QueryFolder folder) {
/*  91 */        List<SimpleNode> result = new ArrayList<>();
/*     */     
/*  93 */     for (QueryItem item : folder.getItems()) {
/*     */       SimpleNode child;
/*     */       
/*  96 */       if (item instanceof QueryDefinition) {
/*  97 */         child = new SavedQueryDefinitionNode(this.myQueriesTreeContext, (QueryDefinition)item);
/*     */       }
/*  99 */       else if (item instanceof QueryFolder) {
/* 100 */         child = new SavedQueryFolderNode(this.myQueriesTreeContext, (QueryFolder)item);
/*     */       } else {
/*     */         
/* 103 */         throw new IllegalArgumentException("Unknown query item " + item);
/*     */       } 
/*     */       
/* 106 */       result.add(child);
/*     */     } 
/*     */     
/* 109 */        return result;
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/* 114 */   private SimpleNode buildErrorNode(@NotNull Exception e) {       return (SimpleNode)new TfsErrorTreeNode(this, e.getMessage()); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/* 119 */   private String getQueryFolderName() {   return (String)ObjectUtils.assertNotNull((this.myQueryFolder != null) ? this.myQueryFolder.getName() : this.myProjectName); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   private QueryFolder getQueryFolder() throws VcsException, ProxyException {
/*     */     Object object;
/* 126 */     if (this.myQueryFolder != null) {
/* 127 */       object = this.myQueryFolder;
/*     */     } else {
/*     */       
/* 130 */       Project project = getWorkItemClient().getProjects().get(this.myProjectName);
/*     */       
/* 132 */       if (project == null) {
/* 133 */         throw new VcsException("Could not find project " + this.myProjectName + " in " + getServer().getPresentableUri());
/*     */       }
/*     */       
/* 136 */       object = project.getQueryHierarchy();
/*     */     } 
/*     */     
/* 139 */         return (QueryFolder)object;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\SavedQueryFolderNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */