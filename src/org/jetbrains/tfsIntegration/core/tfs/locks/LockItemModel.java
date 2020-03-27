/*    */ package org.jetbrains.tfsIntegration.core.tfs.locks;
/*    */ 
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LockLevel;
/*    */ import java.util.Comparator;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
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
/*    */ public class LockItemModel
/*    */ {
/*    */   @NotNull
/*    */   private final ExtendedItem myExtendedItem;
/*    */   @NotNull
/*    */   private final WorkspaceInfo myWorkspace;
/*    */   @Nullable
/*    */   private Boolean mySelectionStatus;
/*    */   
/*    */   public LockItemModel(@NotNull ExtendedItem item, @NotNull WorkspaceInfo workspace) {
/* 38 */     this.myExtendedItem = item;
/* 39 */     this.myWorkspace = workspace;
/* 40 */     this.mySelectionStatus = (canBeLocked() || canBeUnlocked()) ? Boolean.FALSE : null;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 45 */   public ExtendedItem getExtendedItem() {    return this.myExtendedItem; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 50 */   public WorkspaceInfo getWorkspace() {    return this.myWorkspace; }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 55 */   public Boolean getSelectionStatus() { return this.mySelectionStatus; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setSelectionStatus(@NotNull Boolean selectionStatus) {
/* 62 */        if (this.mySelectionStatus == null) {
/* 63 */       throw new IllegalArgumentException("State of items locked by another user cannot be changed.");
/*    */     }
/* 65 */     this.mySelectionStatus = selectionStatus;
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/* 70 */   public String getLockOwner() { return (this.myExtendedItem.getLowner() != null) ? TfsUtil.getNameWithoutDomain(this.myExtendedItem.getLowner()) : null; }
/*    */ 
/*    */ 
/*    */   
/* 74 */   public static final Comparator<LockItemModel> LOCK_ITEM_PARENT_FIRST = (o1, o2) -> VersionControlPath.compareParentToChild(o1.getExtendedItem().getSitem(), (o1.getExtendedItem().getType() == ItemType.Folder), o2
/* 75 */       .getExtendedItem().getSitem(), (o2.getExtendedItem().getType() == ItemType.Folder));
/*    */ 
/*    */   
/* 78 */   public boolean canBeLocked() { return (this.myExtendedItem.getLock() == null || this.myExtendedItem.getLock() == LockLevel.None); }
/*    */ 
/*    */ 
/*    */   
/* 82 */   public boolean canBeUnlocked() { return this.myWorkspace.isWorkspaceOwner(this.myExtendedItem.getLowner()); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\locks\LockItemModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */