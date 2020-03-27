/*    */ package org.jetbrains.tfsIntegration.core.tfs.labels;
/*    */ 
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ 
/*    */ public class ItemAndVersion
/*    */ {
/*    */   private final Item myItem;
/*    */   private final VersionSpec myVersionSpec;
/*    */   
/*    */   public ItemAndVersion(@NotNull Item item, @NotNull VersionSpec versionSpec) {
/* 30 */     this.myItem = item;
/* 31 */     this.myVersionSpec = versionSpec;
/*    */   }
/*    */ 
/*    */   
/* 35 */   public Item getItem() { return this.myItem; }
/*    */ 
/*    */ 
/*    */   
/* 39 */   public VersionSpec getVersionSpec() { return this.myVersionSpec; }
/*    */ 
/*    */ 
/*    */   
/* 43 */   public String getServerPath() { return this.myItem.getItem(); }
/*    */ 
/*    */ 
/*    */   
/* 47 */   public boolean isDirectory() { return (this.myItem.getType() == ItemType.Folder); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\labels\ItemAndVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */