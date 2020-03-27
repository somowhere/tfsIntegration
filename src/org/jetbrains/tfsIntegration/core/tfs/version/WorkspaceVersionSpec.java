/*    */ package org.jetbrains.tfsIntegration.core.tfs.version;
/*    */ 
/*    */ import javax.xml.namespace.QName;
/*    */ import javax.xml.stream.XMLStreamException;
/*    */ import javax.xml.stream.XMLStreamWriter;
/*    */ import org.apache.axiom.om.OMFactory;
/*    */ import org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter;
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
/*    */ public class WorkspaceVersionSpec
/*    */   extends VersionSpecBase
/*    */ {
/*    */   private final String workspaceName;
/*    */   private final String workspaceOwnerName;
/*    */   
/*    */   public WorkspaceVersionSpec(String workspaceName, String workspaceOwnerName) {
/* 32 */     this.workspaceName = workspaceName;
/* 33 */     this.workspaceOwnerName = workspaceOwnerName;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void writeAttributes(QName parentQName, OMFactory factory, MTOMAwareXMLStreamWriter xmlWriter) throws XMLStreamException {
/* 39 */     writeVersionAttribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", (XMLStreamWriter)xmlWriter);
/* 40 */     writeVersionAttribute("", "xsi:type", "WorkspaceVersionSpec", (XMLStreamWriter)xmlWriter);
/* 41 */     writeVersionAttribute("", "name", this.workspaceName, (XMLStreamWriter)xmlWriter);
/* 42 */     writeVersionAttribute("", "owner", this.workspaceOwnerName, (XMLStreamWriter)xmlWriter);
/*    */   }
/*    */ 
/*    */   
/* 46 */   public String getWorkspaceName() { return this.workspaceName; }
/*    */ 
/*    */ 
/*    */   
/* 50 */   public String getWorkspaceOwnerName() { return this.workspaceOwnerName; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 55 */   public String getPresentableString() { return this.workspaceName + ';' + this.workspaceOwnerName; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\version\WorkspaceVersionSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */