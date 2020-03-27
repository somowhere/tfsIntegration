/*     */ package org.jetbrains.tfsIntegration.core.tfs;
/*     */ 
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vfs.VfsUtilCore;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.openapi.vfs.VirtualFileVisitor;
/*     */ import com.intellij.vcsUtil.VcsUtil;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemSpec;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingChange;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.TFSProgressUtil;
/*     */ import org.jetbrains.tfsIntegration.core.TFSVcs;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
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
/*     */ public class StatusProvider
/*     */ {
/*     */   public static void visitByStatus(@NotNull WorkspaceInfo workspace, List<? extends ItemPath> roots, boolean recursive, @Nullable ProgressIndicator progress, @NotNull StatusVisitor statusVisitor, Object projectOrComponent) throws TfsException {
/*  46 */           if (roots.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/*  50 */     List<ItemSpec> itemSpecs = new ArrayList<>(roots.size());
/*  51 */     for (ItemPath root : roots) {
/*  52 */       VirtualFile file = root.getLocalPath().getVirtualFile();
/*     */       
/*  54 */       RecursionType recursionType = (recursive && (file == null || !file.exists() || file.isDirectory())) ? RecursionType.Full : RecursionType.None;
/*  55 */       itemSpecs.add(VersionControlServer.createItemSpec(root.getLocalPath(), recursionType));
/*     */     } 
/*     */ 
/*     */     
/*  59 */     VersionControlServer.ExtendedItemsAndPendingChanges extendedItemsAndPendingChanges = workspace.getServer().getVCS().getExtendedItemsAndPendingChanges(workspace.getName(), workspace.getOwnerName(), itemSpecs, ItemType.Any, projectOrComponent, 
/*  60 */         TFSBundle.message("loading.changes", new Object[0]));
/*     */     
/*  62 */     Map<Integer, PendingChange> pendingChanges = new HashMap<>(extendedItemsAndPendingChanges.pendingChanges.size());
/*  63 */     for (PendingChange pendingChange : extendedItemsAndPendingChanges.pendingChanges) {
/*  64 */       pendingChanges.put(Integer.valueOf(pendingChange.getItemid()), pendingChange);
/*     */     }
/*     */     
/*  67 */     Map<Integer, ExtendedItem> extendedItems = new HashMap<>();
/*  68 */     for (ExtendedItem extendedItem : extendedItemsAndPendingChanges.extendedItems) {
/*  69 */       extendedItems.put(Integer.valueOf(extendedItem.getItemid()), extendedItem);
/*     */     }
/*     */     
/*  72 */     TFSProgressUtil.checkCanceled(progress);
/*     */     
/*  74 */     for (ItemPath root : roots) {
/*  75 */       Collection<FilePath> localItems = new HashSet<>();
/*  76 */       localItems.add(root.getLocalPath());
/*  77 */       if (recursive) {
/*  78 */         addExistingFilesRecursively(localItems, root.getLocalPath().getVirtualFile());
/*     */       }
/*     */ 
/*     */       
/*  82 */       for (FilePath localItem : localItems) {
/*     */         
/*  84 */         ExtendedItem extendedItem = null;
/*  85 */         PendingChange pendingChange = null;
/*     */ 
/*     */ 
/*     */         
/*  89 */         for (PendingChange candidate : pendingChanges.values()) {
/*  90 */           if (equals(localItem, VersionControlPath.localPathFromTfsRepresentation(candidate.getLocal()))) {
/*  91 */             extendedItem = extendedItems.remove(Integer.valueOf(candidate.getItemid()));
/*     */ 
/*     */ 
/*     */             
/*  95 */             pendingChange = candidate;
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/* 100 */         if (extendedItem == null) {
/* 101 */           for (ExtendedItem candidate : extendedItems.values()) {
/* 102 */             if (equals(localItem, VersionControlPath.localPathFromTfsRepresentation(candidate.getLocal()))) {
/* 103 */               extendedItem = extendedItems.remove(Integer.valueOf(candidate.getItemid()));
/*     */               
/*     */               break;
/*     */             } 
/*     */           } 
/*     */         }
/* 109 */         boolean localItemExists = TfsFileUtil.localItemExists(localItem);
/* 110 */         if (!localItemExists && extendedItem != null)
/*     */         {
/* 112 */           localItem = VcsUtil.getFilePath(localItem.getPath(), (extendedItem.getType() == ItemType.Folder));
/*     */         }
/* 114 */         determineServerStatus(pendingChange, extendedItem).visitBy(localItem, localItemExists, statusVisitor);
/*     */       } 
/* 116 */       TFSProgressUtil.checkCanceled(progress);
/*     */     } 
/*     */     
/* 119 */     if (recursive)
/*     */     {
/* 121 */       for (ExtendedItem extendedItem : extendedItems.values()) {
/* 122 */         PendingChange pendingChange = pendingChanges.get(Integer.valueOf(extendedItem.getItemid()));
/* 123 */         if (pendingChange != null || extendedItem.getLocal() != null) {
/* 124 */           FilePath localPath = VersionControlPath.getFilePath((pendingChange != null) ? pendingChange.getLocal() : extendedItem.getLocal(), 
/* 125 */               (extendedItem.getType() == ItemType.Folder));
/* 126 */           determineServerStatus(pendingChange, extendedItem).visitBy(localPath, false, statusVisitor);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private static void addExistingFilesRecursively(@NotNull final Collection<? super FilePath> result, @Nullable VirtualFile root) {
/* 133 */        if (root != null && root.exists()) {
/* 134 */       VfsUtilCore.visitChildrenRecursively(root, new VirtualFileVisitor(new VirtualFileVisitor.Option[0])
/*     */           {
/*     */             public boolean visitFile(@NotNull VirtualFile file) {
/* 137 */                  result.add(TfsFileUtil.getFilePath(file));
/* 138 */               return true;
/*     */             }
/*     */           });
/*     */     }
/*     */   }
/*     */   
/*     */   private static ServerStatus determineServerStatus(@Nullable PendingChange pendingChange, @Nullable ExtendedItem item) {
/* 145 */     if (item == null) {
/* 146 */       return ServerStatus.Unversioned.INSTANCE;
/*     */     }
/*     */     
/* 149 */     ChangeTypeMask change = new ChangeTypeMask(item.getChg());
/* 150 */     change.remove(new ChangeType_type0[] { ChangeType_type0.None, ChangeType_type0.Lock });
/*     */     
/* 152 */     if (item.getLocal() == null && change.isEmpty())
/*     */     {
/* 154 */       return ServerStatus.Unversioned.INSTANCE;
/*     */     }
/*     */     
/* 157 */     if (change.isEmpty()) {
/* 158 */       TFSVcs.assertTrue((item.getLver() != Integer.MIN_VALUE));
/* 159 */       if (item.getLver() < item.getLatest()) {
/* 160 */         return new ServerStatus.OutOfDate(item);
/*     */       }
/*     */       
/* 163 */       return new ServerStatus.UpToDate(item);
/*     */     } 
/*     */ 
/*     */     
/* 167 */     if (change.containsAny(new ChangeType_type0[] { ChangeType_type0.Add }) || (change
/* 168 */       .containsAny(new ChangeType_type0[] { ChangeType_type0.Merge, ChangeType_type0.Branch }) && item.getLatest() == Integer.MIN_VALUE)) {
/*     */       
/* 170 */       TFSVcs.assertTrue(change.containsAny(new ChangeType_type0[] { ChangeType_type0.Encoding }));
/* 171 */       TFSVcs.assertTrue((item.getLatest() == Integer.MIN_VALUE));
/* 172 */       TFSVcs.assertTrue((item.getLver() == Integer.MIN_VALUE));
/* 173 */       if (pendingChange != null) {
/* 174 */         return new ServerStatus.ScheduledForAddition(pendingChange);
/*     */       }
/*     */       
/* 177 */       return new ServerStatus.ScheduledForAddition(item);
/*     */     } 
/*     */     
/* 180 */     if (change.contains(ChangeType_type0.Delete)) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 185 */       if (pendingChange != null) {
/* 186 */         return new ServerStatus.ScheduledForDeletion(pendingChange);
/*     */       }
/*     */       
/* 189 */       return new ServerStatus.ScheduledForDeletion(item);
/*     */     } 
/*     */     
/* 192 */     if (change.containsAny(new ChangeType_type0[] { ChangeType_type0.Edit, ChangeType_type0.Merge }) && !change.contains(ChangeType_type0.Rename)) {
/* 193 */       TFSVcs.assertTrue((item.getLatest() != Integer.MIN_VALUE));
/* 194 */       if (item.getLver() != Integer.MIN_VALUE) {
/* 195 */         TFSVcs.assertTrue((item.getLocal() != null));
/* 196 */         if (pendingChange != null) {
/* 197 */           return new ServerStatus.CheckedOutForEdit(pendingChange);
/*     */         }
/*     */         
/* 200 */         return new ServerStatus.CheckedOutForEdit(item);
/*     */       } 
/*     */ 
/*     */       
/* 204 */       return new ServerStatus.ScheduledForAddition(item);
/*     */     } 
/*     */     
/* 207 */     if (change.containsAny(new ChangeType_type0[] { ChangeType_type0.Merge, ChangeType_type0.Rename }) && !change.contains(ChangeType_type0.Edit)) {
/* 208 */       if (pendingChange != null) {
/* 209 */         return new ServerStatus.Renamed(pendingChange);
/*     */       }
/*     */       
/* 212 */       return new ServerStatus.Renamed(item);
/*     */     } 
/*     */     
/* 215 */     if (change.containsAll(new ChangeType_type0[] { ChangeType_type0.Rename, ChangeType_type0.Edit })) {
/* 216 */       TFSVcs.assertTrue((item.getLatest() != Integer.MIN_VALUE));
/* 217 */       TFSVcs.assertTrue((item.getLver() != Integer.MIN_VALUE));
/* 218 */       TFSVcs.assertTrue((item.getLocal() != null));
/* 219 */       if (pendingChange != null) {
/* 220 */         return new ServerStatus.RenamedCheckedOut(pendingChange);
/*     */       }
/*     */       
/* 223 */       return new ServerStatus.RenamedCheckedOut(item);
/*     */     } 
/*     */     
/* 226 */     if (change.contains(ChangeType_type0.Undelete)) {
/* 227 */       if (pendingChange != null) {
/* 228 */         return new ServerStatus.Undeleted(pendingChange);
/*     */       }
/*     */       
/* 231 */       return new ServerStatus.Undeleted(item);
/*     */     } 
/*     */ 
/*     */     
/* 235 */     TFSVcs.LOG.error("Uncovered case for item " + (
/* 236 */         (item.getLocal() != null) ? VersionControlPath.localPathFromTfsRepresentation(item.getLocal()) : item.getTitem()));
/* 237 */     return null;
/*     */   }
/*     */   
/*     */   private static boolean equals(FilePath path1, String path2) {
/* 241 */     if (path2 == null) {
/* 242 */       return (path1 == null);
/*     */     }
/*     */     
/* 245 */     return path1.getIOFile().equals(new File(path2));
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\StatusProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */