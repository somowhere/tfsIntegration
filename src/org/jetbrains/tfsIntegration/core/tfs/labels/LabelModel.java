/*    */ package org.jetbrains.tfsIntegration.core.tfs.labels;
/*    */ 
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelItemSpec;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.Iterator;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
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
/*    */ public class LabelModel
/*    */ {
/* 28 */   private final List<LabelItemSpecWithItems> myLabelSpecs = new ArrayList<>();
/*    */ 
/*    */ 
/*    */   
/*    */   public void add(@NotNull LabelItemSpecWithItems newSpec) {
/* 33 */        for (Iterator<LabelItemSpecWithItems> iterator = this.myLabelSpecs.iterator(); iterator.hasNext(); ) {
/* 34 */       LabelItemSpecWithItems existingSpec = iterator.next();
/* 35 */       if (VersionControlPath.isUnder(newSpec.getServerPath(), existingSpec.getServerPath())) {
/* 36 */         iterator.remove();
/*    */       }
/*    */     } 
/* 39 */     this.myLabelSpecs.add(newSpec);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void addAll(List<LabelItemSpecWithItems> newSpecs) {
/* 45 */     Collections.sort(newSpecs, ITEM_SPEC_CHILDREN_FIRST);
/* 46 */     for (LabelItemSpecWithItems spec : newSpecs) {
/* 47 */       add(spec);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public List<ItemAndVersion> calculateItemsToDisplay() {
/* 53 */     Collections.sort(this.myLabelSpecs, ITEM_SPEC_PARENT_FIRST);
/* 54 */     List<ItemAndVersion> result = new ArrayList<>();
/*    */     
/* 56 */     for (int i = 0; i < this.myLabelSpecs.size(); i++) {
/* 57 */       LabelItemSpecWithItems labelSpec = this.myLabelSpecs.get(i);
/*    */       
/* 59 */       for (Item item : labelSpec.getItemsList()) {
/* 60 */         boolean appearsUnderChild = false;
/* 61 */         for (int j = i + 1; j < this.myLabelSpecs.size(); j++) {
/* 62 */           if (VersionControlPath.isUnder(((LabelItemSpecWithItems)this.myLabelSpecs.get(j)).getServerPath(), item.getItem())) {
/* 63 */             appearsUnderChild = true;
/*    */             break;
/*    */           } 
/*    */         } 
/* 67 */         if (!appearsUnderChild) {
/* 68 */           result.add(new ItemAndVersion(item, labelSpec.getLabelItemSpec().getVersion()));
/*    */         }
/*    */       } 
/*    */     } 
/*    */     
/* 73 */     Collections.sort(result, ITEM_AND_VERSION_PARENT_FIRST);
/* 74 */     return result;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public List<LabelItemSpec> getLabelItemSpecs() {
/* 81 */     Collections.sort(this.myLabelSpecs, ITEM_SPEC_PARENT_FIRST);
/* 82 */     List<LabelItemSpec> result = new ArrayList<>(this.myLabelSpecs.size());
/* 83 */     for (LabelItemSpecWithItems labelSpec : this.myLabelSpecs) {
/* 84 */       result.add(labelSpec.getLabelItemSpec());
/*    */     }
/* 86 */     return result;
/*    */   }
/*    */ 
/*    */   
/* 90 */   private static final Comparator<LabelItemSpecWithItems> ITEM_SPEC_PARENT_FIRST = (o1, o2) -> VersionControlPath.compareParentToChild(o1.getServerPath(), o2.getServerPath());
/*    */ 
/*    */   
/* 93 */   private static final Comparator<LabelItemSpecWithItems> ITEM_SPEC_CHILDREN_FIRST = (o1, o2) -> -VersionControlPath.compareParentToChild(o1.getServerPath(), o2.getServerPath());
/*    */ 
/*    */   
/* 96 */   private static final Comparator<ItemAndVersion> ITEM_AND_VERSION_PARENT_FIRST = (o1, o2) -> VersionControlPath.compareParentToChild(o1.getServerPath(), o1.isDirectory(), o2.getServerPath(), o2.isDirectory());
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\labels\LabelModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */