/*     */ package org.jetbrains.tfsIntegration.ui.servertree;
/*     */ 
/*     */ import com.intellij.ide.projectView.PresentationData;
/*     */ import com.intellij.openapi.fileTypes.FileTypeManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Condition;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.ui.SimpleTextAttributes;
/*     */ import com.intellij.ui.treeStructure.SimpleNode;
/*     */ import com.intellij.util.PlatformIcons;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.ui.UiConstants;
/*     */ 
/*     */ public class TfsTreeNode
/*     */   extends SimpleNode
/*     */ {
/*  26 */   private static final SimpleTextAttributes VIRTUAL_ATTRS = SimpleTextAttributes.SYNTHETIC_ATTRIBUTES;
/*     */   
/*     */   private final TfsTreeContext myTreeContext;
/*     */   private final String myPath;
/*     */   private final boolean myIsDirectory;
/*     */   private final boolean myVirtual;
/*  32 */   private final Collection<TfsTreeNode> myVirtualChildren = new ArrayList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TfsTreeNode(@NotNull Object projectOrComponent, ServerInfo server, boolean foldersOnly, @Nullable Condition<String> pathFilter) {
/*  39 */     super((projectOrComponent instanceof Project) ? (Project)projectOrComponent : null);
/*  40 */     this.myTreeContext = new TfsTreeContext(server, foldersOnly, projectOrComponent, pathFilter);
/*  41 */     this.myPath = "$/";
/*  42 */     this.myIsDirectory = true;
/*  43 */     this.myVirtual = false;
/*     */   }
/*     */ 
/*     */   
/*     */   private TfsTreeNode(TfsTreeNode parent, String path, boolean isDirectory, boolean virtual) {
/*  48 */     super(parent);
/*  49 */     this.myPath = path;
/*  50 */     this.myIsDirectory = isDirectory;
/*  51 */     this.myVirtual = virtual;
/*  52 */     this.myTreeContext = parent.myTreeContext;
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public SimpleNode[] getChildren() {
/*     */     List<Item> children;
/*  58 */     if (!this.myIsDirectory) {
/*  59 */        return NO_CHILDREN;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/*  64 */       children = this.myTreeContext.getChildItems(this.myPath);
/*     */     }
/*  66 */     catch (TfsException e) {
/*  67 */       (new SimpleNode[1])[0] = new TfsErrorTreeNode(this, e.getMessage()); return new SimpleNode[1];
/*     */     } 
/*     */     
/*  70 */     List<TfsTreeNode> result = new ArrayList<>(this.myVirtualChildren);
/*  71 */     for (Item childItem : children) {
/*  72 */       result.add(new TfsTreeNode(this, childItem.getItem(), (childItem.getType() == ItemType.Folder), false));
/*     */     }
/*  74 */     return result.toArray(new SimpleNode[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void update(@NotNull PresentationData presentation) {
/*  79 */       if (isRoot()) {
/*  80 */       presentation.addText(this.myTreeContext.myServer.getPresentableUri(), getPlainAttributes());
/*  81 */       presentation.setIcon(UiConstants.ICON_SERVER);
/*     */     } else {
/*     */       SimpleTextAttributes attrs;
/*  84 */       if (isDirectory()) {
/*  85 */         presentation.setIcon(PlatformIcons.FOLDER_ICON);
/*     */       } else {
/*     */         
/*  88 */         presentation.setIcon(FileTypeManager.getInstance().getFileTypeByFileName(getFileName()).getIcon());
/*     */       } 
/*     */       
/*  91 */       if (this.myVirtual) {
/*  92 */         attrs = VIRTUAL_ATTRS;
/*     */       }
/*  94 */       else if (this.myTreeContext.isAccepted(this.myPath)) {
/*  95 */         attrs = getPlainAttributes();
/*     */       } else {
/*     */         
/*  98 */         attrs = SimpleTextAttributes.GRAYED_ATTRIBUTES;
/*     */       } 
/* 100 */       presentation.addText(getFileName(), attrs);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/* 105 */   public String getFileName() { return VersionControlPath.getLastComponent(this.myPath); }
/*     */ 
/*     */ 
/*     */   
/* 109 */   public boolean isDirectory() { return this.myIsDirectory; }
/*     */ 
/*     */ 
/*     */   
/* 113 */   public boolean isRoot() { return "$/".equals(this.myPath); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/* 118 */   public String getPath() {  return this.myPath; }
/*     */ 
/*     */   
/*     */   private TfsTreeNode createFakeChild(String name) {
/* 122 */     String childPath = VersionControlPath.getCombinedServerPath(this.myPath, name);
/* 123 */     return new TfsTreeNode(this, childPath, false, false);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/* 129 */   public Object[] getEqualityObjects() { (new Object[1])[0] = this.myPath;   return new Object[1]; }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public TfsTreeNode createForSelection(String serverPath) {
/* 134 */     if (StringUtil.isEmpty(serverPath) || "$/".equals(serverPath)) {
/* 135 */       return this;
/*     */     }
/*     */     
/* 138 */     TfsTreeNode result = this;
/* 139 */     String[] components = VersionControlPath.getPathComponents(serverPath);
/* 140 */     for (int i = 1; i < components.length; i++) {
/* 141 */       result = result.createFakeChild(components[i]);
/*     */     }
/* 143 */     return result;
/*     */   }
/*     */   
/*     */   public TfsTreeNode createVirtualSubfolder(String folderName) {
/* 147 */     String childPath = VersionControlPath.getCombinedServerPath(this.myPath, folderName);
/* 148 */     TfsTreeNode child = new TfsTreeNode(this, childPath, true, true);
/* 149 */     this.myVirtualChildren.add(child);
/* 150 */     return child;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\servertree\TfsTreeNode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */