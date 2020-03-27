/*    */ package org.jetbrains.tfsIntegration.core.tfs.conflicts;
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
/*    */ public class ConflictsEnvironment
/*    */ {
/* 21 */   private static ConflictsHandler ourConflictsHandler = new DialogConflictsHandler();
/* 22 */   private static NameMerger ourNameMerger = new DialogNameMerger();
/* 23 */   private static ContentMerger ourContentMerger = new DialogContentMerger();
/*    */ 
/*    */   
/* 26 */   public static NameMerger getNameMerger() { return ourNameMerger; }
/*    */ 
/*    */ 
/*    */   
/* 30 */   public static void setNameMerger(NameMerger nameMerger) { ourNameMerger = nameMerger; }
/*    */ 
/*    */ 
/*    */   
/* 34 */   public static ContentMerger getContentMerger() { return ourContentMerger; }
/*    */ 
/*    */ 
/*    */   
/* 38 */   public static void setContentMerger(ContentMerger contentMerger) { ourContentMerger = contentMerger; }
/*    */ 
/*    */ 
/*    */   
/* 42 */   public static void setConflictsHandler(ConflictsHandler conflictsHandler) { ourConflictsHandler = conflictsHandler; }
/*    */ 
/*    */ 
/*    */   
/* 46 */   public static ConflictsHandler getConflictsHandler() { return ourConflictsHandler; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\conflicts\ConflictsEnvironment.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */