/*    */ package org.jetbrains.tfsIntegration.ui.servertree;
/*    */ 
/*    */ import com.intellij.openapi.util.Condition;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*    */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TfsTreeContext
/*    */ {
/*    */   public final ServerInfo myServer;
/*    */   private final boolean myFoldersOnly;
/*    */   private final Object myProjectOrComponent;
/*    */   @Nullable
/*    */   private final Condition<? super String> myFilter;
/*    */   
/*    */   public TfsTreeContext(ServerInfo server, boolean foldersOnly, Object projectOrComponent, Condition<? super String> filter) {
/* 22 */     this.myServer = server;
/* 23 */     this.myFoldersOnly = foldersOnly;
/* 24 */     this.myFilter = filter;
/* 25 */     this.myProjectOrComponent = projectOrComponent;
/*    */   }
/*    */ 
/*    */   
/* 29 */   public boolean isAccepted(String path) { return (this.myFilter == null || this.myFilter.value(path)); }
/*    */ 
/*    */ 
/*    */   
/* 33 */   public List<Item> getChildItems(String path) throws TfsException { return this.myServer.getVCS().getChildItems(path, this.myFoldersOnly, this.myProjectOrComponent, TFSBundle.message("loading.items", new Object[0])); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\servertree\TfsTreeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */