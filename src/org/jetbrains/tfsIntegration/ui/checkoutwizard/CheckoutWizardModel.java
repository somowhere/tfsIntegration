/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
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
/*     */ public class CheckoutWizardModel
/*     */ {
/*     */   private ServerInfo myServer;
/*     */   
/*     */   public enum Mode
/*     */   {
/*  25 */     Auto, Manual;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  30 */   private Mode myMode = Mode.Auto;
/*     */ 
/*     */   
/*     */   private WorkspaceInfo myWorkspace;
/*     */   
/*     */   private String myNewWorkspaceName;
/*     */   
/*     */   private String myServerPath;
/*     */   
/*     */   private String myDestinationFolder;
/*     */ 
/*     */   
/*  42 */   public ServerInfo getServer() { return this.myServer; }
/*     */ 
/*     */ 
/*     */   
/*  46 */   public Mode getMode() { return this.myMode; }
/*     */ 
/*     */ 
/*     */   
/*  50 */   public WorkspaceInfo getWorkspace() { return this.myWorkspace; }
/*     */ 
/*     */ 
/*     */   
/*  54 */   public String getNewWorkspaceName() { return this.myNewWorkspaceName; }
/*     */ 
/*     */ 
/*     */   
/*  58 */   public String getServerPath() { return this.myServerPath; }
/*     */ 
/*     */ 
/*     */   
/*  62 */   public String getDestinationFolder() { return this.myDestinationFolder; }
/*     */ 
/*     */   
/*     */   public void setServer(ServerInfo server) {
/*  66 */     if (this.myServer != null && !this.myServer.getUri().equals(server.getUri())) {
/*  67 */       this.myWorkspace = null;
/*  68 */       this.myServerPath = null;
/*     */     } 
/*  70 */     this.myServer = server;
/*     */   }
/*     */ 
/*     */   
/*  74 */   public void setMode(Mode mode) { this.myMode = mode; }
/*     */ 
/*     */   
/*     */   public void setWorkspace(WorkspaceInfo workspace) {
/*  78 */     if (this.myMode != Mode.Manual) {
/*  79 */       throw new IllegalStateException("Attempt to set workspace in Auto mode");
/*     */     }
/*  81 */     this.myWorkspace = workspace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  88 */   public void setNewWorkspaceName(String newWorkspaceName) { this.myNewWorkspaceName = newWorkspaceName; }
/*     */ 
/*     */ 
/*     */   
/*  92 */   public void setServerPath(String serverPath) { this.myServerPath = serverPath; }
/*     */ 
/*     */   
/*     */   public void setDestinationFolder(String destinationFolder) {
/*  96 */     if (this.myMode != Mode.Auto) {
/*  97 */       throw new IllegalStateException("Attempt to set destination path in Manual mode");
/*     */     }
/*  99 */     this.myDestinationFolder = destinationFolder;
/*     */   }
/*     */   
/*     */   public boolean isComplete() {
/* 103 */     if (getServer() == null) {
/* 104 */       return false;
/*     */     }
/* 106 */     if (getMode() == Mode.Auto) {
/* 107 */       if (getNewWorkspaceName() == null || getNewWorkspaceName().length() == 0) {
/* 108 */         return false;
/*     */       }
/* 110 */       if (getDestinationFolder() == null) {
/* 111 */         return false;
/*     */       
/*     */       }
/*     */     }
/* 115 */     else if (getWorkspace() == null) {
/* 116 */       return false;
/*     */     } 
/*     */     
/* 119 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\CheckoutWizardModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */