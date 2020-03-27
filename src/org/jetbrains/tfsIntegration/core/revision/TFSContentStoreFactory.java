/*    */ package org.jetbrains.tfsIntegration.core.revision;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.jetbrains.annotations.Nullable;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TFSContentStoreFactory
/*    */ {
/* 27 */   public static TFSContentStore create(String serverUri, int itemId, int revision) throws IOException { return new TFSTmpFileStore(serverUri, itemId, revision); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 32 */   public static TFSContentStore find(String serverUri, int itemId, int revision) throws IOException { return TFSTmpFileStore.find(serverUri, itemId, revision); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\revision\TFSContentStoreFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */