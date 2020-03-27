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
/*    */ public class LabelVersionSpec
/*    */   extends VersionSpecBase
/*    */ {
/*    */   private final String myLabel;
/*    */   private final String myScope;
/*    */   
/*    */   public LabelVersionSpec(String label, String scope) {
/* 31 */     this.myLabel = label;
/* 32 */     this.myScope = scope;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeAttributes(QName parentQName, OMFactory factory, MTOMAwareXMLStreamWriter xmlWriter) throws XMLStreamException {
/* 37 */     writeVersionAttribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", (XMLStreamWriter)xmlWriter);
/* 38 */     writeVersionAttribute("", "xsi:type", "LabelVersionSpec", (XMLStreamWriter)xmlWriter);
/* 39 */     writeVersionAttribute("", "label", this.myLabel, (XMLStreamWriter)xmlWriter);
/* 40 */     if (this.myScope != null) {
/* 41 */       writeVersionAttribute("", "scope", this.myScope, (XMLStreamWriter)xmlWriter);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 47 */   public String getPresentableString() { return (this.myScope != null) ? (this.myLabel + "@" + this.myScope) : this.myLabel; }
/*    */ 
/*    */ 
/*    */   
/* 51 */   public String getLabel() { return this.myLabel; }
/*    */ 
/*    */ 
/*    */   
/* 55 */   public String getScope() { return this.myScope; }
/*    */ 
/*    */   
/*    */   public static LabelVersionSpec fromStringRepresentation(String stringRepresentation) {
/* 59 */     int atPos = stringRepresentation.indexOf('@');
/* 60 */     if (atPos != -1) {
/* 61 */       return new LabelVersionSpec(stringRepresentation.substring(0, atPos), stringRepresentation.substring(atPos + 1));
/*    */     }
/*    */     
/* 64 */     return new LabelVersionSpec(stringRepresentation, null);
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\version\LabelVersionSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */