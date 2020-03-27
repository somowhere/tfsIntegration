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
/*    */ public class LatestVersionSpec
/*    */   extends VersionSpecBase
/*    */ {
/* 27 */   public static final LatestVersionSpec INSTANCE = new LatestVersionSpec();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected void writeAttributes(QName parentQName, OMFactory factory, MTOMAwareXMLStreamWriter xmlWriter) throws XMLStreamException {
/* 35 */     writeVersionAttribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", (XMLStreamWriter)xmlWriter);
/* 36 */     writeVersionAttribute("", "xsi:type", "LatestVersionSpec", (XMLStreamWriter)xmlWriter);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 41 */   public String getPresentableString() { return "Latest"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\version\LatestVersionSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */