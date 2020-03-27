/*    */ package org.jetbrains.tfsIntegration.core.tfs.labels;
/*    */ 
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemSpec;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelItemSpec;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
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
/*    */ public class LabelItemSpecWithItems
/*    */ {
/*    */   private final LabelItemSpec myLabelItemSpec;
/*    */   private final List<Item> myItemsList;
/*    */   
/*    */   private LabelItemSpecWithItems(@NotNull LabelItemSpec labelItemSpec, @NotNull List<Item> itemsList) {
/* 33 */     this.myLabelItemSpec = labelItemSpec;
/* 34 */     this.myItemsList = itemsList;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public static LabelItemSpecWithItems createForAdd(@NotNull ItemSpec item, @NotNull VersionSpecBase version, @NotNull List<Item> itemsList) {
/* 40 */              LabelItemSpec labelItemSpec = new LabelItemSpec();
/* 41 */     labelItemSpec.setItemSpec(item);
/* 42 */     labelItemSpec.setVersion((VersionSpec)version);
/* 43 */     labelItemSpec.setEx(false);
/* 44 */     return new LabelItemSpecWithItems(labelItemSpec, itemsList);
/*    */   }
/*    */   
/*    */   public static LabelItemSpecWithItems createForRemove(@NotNull ItemAndVersion item) {
/* 48 */
/* 49 */     ItemSpec itemSpec = VersionControlServer.createItemSpec(item.getItem().getItem(), (item.getItem().getType() == ItemType.Folder) ? RecursionType.Full : null);
/*    */     
/* 51 */     LabelItemSpec labelItemSpec = new LabelItemSpec();
/* 52 */     labelItemSpec.setEx(true);
/* 53 */     labelItemSpec.setItemSpec(itemSpec);
/* 54 */     labelItemSpec.setVersion(item.getVersionSpec());
/* 55 */     return new LabelItemSpecWithItems(labelItemSpec, Collections.emptyList());
/*    */   }
/*    */ 
/*    */   
/* 59 */   public LabelItemSpec getLabelItemSpec() { return this.myLabelItemSpec; }
/*    */ 
/*    */ 
/*    */   
/* 63 */   public List<Item> getItemsList() { return this.myItemsList; }
/*    */ 
/*    */ 
/*    */   
/* 67 */   public String getServerPath() { return getLabelItemSpec().getItemSpec().getItem(); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\labels\LabelItemSpecWithItems.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */