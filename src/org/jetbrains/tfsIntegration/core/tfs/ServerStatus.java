/*     */ package org.jetbrains.tfsIntegration.core.tfs;
/*     */ 
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingChange;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class ServerStatus
/*     */ {
/*     */   public final int localVer;
/*     */   public final int itemId;
/*     */   public final boolean isDirectory;
/*     */   @Nullable
/*     */   public final String sourceItem;
/*     */   @Nullable
/*     */   public final String targetItem;
/*     */   
/*     */   protected ServerStatus(int localVer, int itemId, boolean isDirectory, String sourceItem, String targetItem) {
/*  39 */     this.localVer = localVer;
/*  40 */     this.itemId = itemId;
/*  41 */     this.isDirectory = isDirectory;
/*  42 */     this.sourceItem = sourceItem;
/*  43 */     this.targetItem = targetItem;
/*     */   }
/*     */   
/*     */   protected ServerStatus(@NotNull PendingChange pendingChange) {
/*  47 */     this(pendingChange.getVer(), pendingChange.getItemid(), (pendingChange.getType() == ItemType.Folder), pendingChange.getSrcitem(), pendingChange
/*  48 */         .getItem());
/*     */   }
/*     */   
/*     */   protected ServerStatus(@NotNull ExtendedItem extendedItem) {
/*  52 */     this(extendedItem.getLver(), extendedItem.getItemid(), (extendedItem.getType() == ItemType.Folder), extendedItem.getSitem(), extendedItem
/*  53 */         .getTitem());
/*     */   }
/*     */ 
/*     */   
/*     */   public abstract void visitBy(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull StatusVisitor paramStatusVisitor) throws TfsException;
/*     */ 
/*     */   
/*  60 */   public String toString() { return getClass().getName().substring(getClass().getName().lastIndexOf("$") + 1); }
/*     */   
/*     */   public static class Unversioned
/*     */     extends ServerStatus
/*     */   {
/*  65 */     public static final ServerStatus INSTANCE = new Unversioned();
/*     */ 
/*     */     
/*  68 */     private Unversioned() { super(0, 0, false, null, null); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {    statusVisitor.unversioned(localPath, localItemExists, this); }
/*     */   }
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
/*     */   public static class CheckedOutForEdit
/*     */     extends ServerStatus
/*     */   {
/*  91 */     protected CheckedOutForEdit(@NotNull PendingChange pendingChange) { super(pendingChange); }
/*     */ 
/*     */ 
/*     */     
/*  95 */     public CheckedOutForEdit(@NotNull ExtendedItem item) { super(item); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 101 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.checkedOutForEdit(localPath, localItemExists, this); }
/*     */   }
/*     */   
/*     */   public static class ScheduledForAddition
/*     */     extends ServerStatus
/*     */   {
/* 107 */     protected ScheduledForAddition(@NotNull PendingChange pendingChange) { super(pendingChange); }
/*     */ 
/*     */ 
/*     */     
/* 111 */     protected ScheduledForAddition(@NotNull ExtendedItem extendedItem) { super(extendedItem); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 117 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.scheduledForAddition(localPath, localItemExists, this); }
/*     */   }
/*     */   
/*     */   public static class ScheduledForDeletion
/*     */     extends ServerStatus
/*     */   {
/* 123 */     protected ScheduledForDeletion(@NotNull PendingChange pendingChange) { super(pendingChange); }
/*     */ 
/*     */ 
/*     */     
/* 127 */     public ScheduledForDeletion(@NotNull ExtendedItem item) { super(item); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 133 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.scheduledForDeletion(localPath, localItemExists, this); }
/*     */   }
/*     */   
/*     */   public static class OutOfDate
/*     */     extends ServerStatus
/*     */   {
/* 139 */     protected OutOfDate(@NotNull ExtendedItem extendedItem) { super(extendedItem); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.outOfDate(localPath, localItemExists, this); }
/*     */   }
/*     */   
/*     */   public static class UpToDate
/*     */     extends ServerStatus
/*     */   {
/* 151 */     protected UpToDate(@NotNull ExtendedItem extendedItem) { super(extendedItem); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 157 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.upToDate(localPath, localItemExists, this); }
/*     */   }
/*     */   
/*     */   public static class Renamed
/*     */     extends ServerStatus
/*     */   {
/* 163 */     protected Renamed(@NotNull PendingChange pendingChange) { super(pendingChange); }
/*     */ 
/*     */ 
/*     */     
/* 167 */     public Renamed(@NotNull ExtendedItem item) { super(item); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 173 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.renamed(localPath, localItemExists, this); }
/*     */   }
/*     */   
/*     */   public static class RenamedCheckedOut
/*     */     extends ServerStatus
/*     */   {
/* 179 */     protected RenamedCheckedOut(@NotNull PendingChange pendingChange) { super(pendingChange); }
/*     */ 
/*     */ 
/*     */     
/* 183 */     public RenamedCheckedOut(@NotNull ExtendedItem item) { super(item); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 189 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.renamedCheckedOut(localPath, localItemExists, this); }
/*     */   }
/*     */   
/*     */   public static class Undeleted
/*     */     extends ServerStatus
/*     */   {
/* 195 */     protected Undeleted(@NotNull PendingChange pendingChange) { super(pendingChange); }
/*     */ 
/*     */ 
/*     */     
/* 199 */     public Undeleted(@NotNull ExtendedItem item) { super(item); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 205 */     public void visitBy(@NotNull FilePath localPath, boolean localItemExists, @NotNull StatusVisitor statusVisitor) throws TfsException {       statusVisitor.undeleted(localPath, localItemExists, this); }
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\ServerStatus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */