/*     */ package org.jetbrains.tfsIntegration.core.tfs;
/*     */ 
/*     */ import com.intellij.vcsUtil.VcsUtil;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.List;
/*     */ import org.apache.axis2.databinding.utils.ConverterUtil;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.TfsBeansHolder;
/*     */ import org.xml.sax.Attributes;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.SAXParseException;
/*     */ import org.xml.sax.helpers.DefaultHandler;
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
/*     */ class WorkstationCacheReader
/*     */   extends DefaultHandler
/*     */ {
/*     */   @NotNull
/*  37 */   private final List<ServerInfo> myServerInfos = new ArrayList<>();
/*     */   
/*     */   private ServerInfo myCurrentServerInfo;
/*     */   
/*     */   private WorkspaceInfo myCurrentWorkspaceInfo;
/*     */ 
/*     */   
/*  44 */   public void error(SAXParseException e) throws SAXException { throw e; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   public void fatalError(SAXParseException e) throws SAXException { throw e; }
/*     */ 
/*     */ 
/*     */   
/*     */   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
/*  54 */     if ("ServerInfo".equals(qName)) {
/*     */       try {
/*  56 */         URI serverUri = new URI(attributes.getValue("uri"));
/*  57 */         this
/*  58 */           .myCurrentServerInfo = new ServerInfo(serverUri, attributes.getValue("repositoryGuid"), new TfsBeansHolder(serverUri));
/*     */       }
/*  60 */       catch (URISyntaxException e) {
/*  61 */         throw new SAXException(e);
/*     */       }
/*     */     
/*  64 */     } else if ("WorkspaceInfo".equals(qName)) {
/*  65 */       String name = attributes.getValue("name");
/*  66 */       String owner = attributes.getValue("ownerName");
/*  67 */       String computer = attributes.getValue("computer");
/*  68 */       String comment = attributes.getValue("comment");
/*  69 */       Calendar timestamp = ConverterUtil.convertToDateTime(attributes.getValue("LastSavedCheckinTimeStamp"));
/*  70 */       String localWorkspace = attributes.getValue("isLocalWorkspace");
/*  71 */       String ownerDisplayName = attributes.getValue("ownerDisplayName");
/*  72 */       String securityToken = attributes.getValue("securityToken");
/*  73 */       int options = ConverterUtil.convertToInt(attributes.getValue("options"));
/*     */       
/*  75 */       this
/*  76 */         .myCurrentWorkspaceInfo = new WorkspaceInfo(this.myCurrentServerInfo, name, owner, computer, comment, timestamp, Boolean.parseBoolean(localWorkspace), ownerDisplayName, securityToken, options);
/*     */     
/*     */     }
/*  79 */     else if ("MappedPath".equals(qName)) {
/*  80 */       this.myCurrentWorkspaceInfo
/*  81 */         .addWorkingFolderInfo(new WorkingFolderInfo(VcsUtil.getFilePath(attributes.getValue("path"), true)));
/*     */     }
/*  83 */     else if ("OwnerAlias".equals(qName)) {
/*  84 */       this.myCurrentWorkspaceInfo.addOwnerAlias(attributes.getValue("OwnerAlias"));
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void endElement(String uri, String localName, String qName) throws SAXException {
/*  90 */     if ("ServerInfo".equals(qName)) {
/*  91 */       this.myServerInfos.add(this.myCurrentServerInfo);
/*  92 */       this.myCurrentServerInfo = null;
/*     */     }
/*  94 */     else if ("WorkspaceInfo".equals(qName)) {
/*  95 */       this.myCurrentServerInfo.addWorkspaceInfo(this.myCurrentWorkspaceInfo);
/*  96 */       this.myCurrentWorkspaceInfo = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @NotNull
/* 102 */   public List<ServerInfo> getServers() {    return this.myServerInfos; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\WorkstationCacheReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */