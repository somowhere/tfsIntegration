/*     */ package org.jetbrains.tfsIntegration.webservice.compatibility;
/*     */ 
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.apache.axiom.om.OMElement;
/*     */ import org.apache.axiom.om.OMNamespace;
/*     */ import org.apache.axiom.om.impl.exception.OMBuilderException;
/*     */ import org.apache.axiom.soap.SOAPProcessingException;
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
/*     */ public abstract class CustomSOAPBuilderHelper
/*     */ {
/*     */   protected CustomStAXSOAPModelBuilder builder;
/*     */   protected XMLStreamReader parser;
/*     */   
/*  40 */   protected CustomSOAPBuilderHelper(CustomStAXSOAPModelBuilder builder) { this.builder = builder; }
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract OMElement handleEvent(XMLStreamReader paramXMLStreamReader, OMElement paramOMElement, int paramInt) throws SOAPProcessingException;
/*     */ 
/*     */   
/*     */   protected void processNamespaceData(OMElement node, boolean checkSOAPNamespace) {
/*  48 */     int namespaceCount = this.parser.getNamespaceCount();
/*  49 */     for (int i = 0; i < namespaceCount; i++) {
/*  50 */       String namespaceURI = this.parser.getNamespaceURI(i);
/*  51 */       if (namespaceURI != null) {
/*  52 */         node.declareNamespace(this.parser.getNamespaceURI(i), this.parser
/*  53 */             .getNamespacePrefix(i));
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/*  58 */     String namespaceURI = this.parser.getNamespaceURI();
/*  59 */     String prefix = this.parser.getPrefix();
/*  60 */     OMNamespace namespace = null;
/*  61 */     if (namespaceURI != null && namespaceURI.length() > 0) {
/*  62 */       if (prefix == null) {
/*     */         
/*  64 */         namespace = node.findNamespace(namespaceURI, "");
/*  65 */         if (namespace == null) {
/*  66 */           namespace = node.declareNamespace(namespaceURI, "");
/*     */         }
/*     */       } else {
/*  69 */         namespace = node.findNamespace(namespaceURI, prefix);
/*     */       } 
/*  71 */       node.setNamespace(namespace);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  79 */     if (checkSOAPNamespace && 
/*  80 */       node.getNamespace() != null && 
/*  81 */       !node.getNamespace().getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/envelope/") && 
/*     */       
/*  83 */       !node.getNamespace().getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope"))
/*     */     {
/*  85 */       throw new OMBuilderException("invalid SOAP namespace URI");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void processAttributes(OMElement node) {
/*  92 */     int attribCount = this.parser.getAttributeCount();
/*  93 */     for (int i = 0; i < attribCount; i++) {
/*  94 */       OMNamespace ns = null;
/*  95 */       String uri = this.parser.getAttributeNamespace(i);
/*  96 */       if (uri != null && uri.hashCode() != 0) {
/*  97 */         ns = node.findNamespace(uri, this.parser
/*  98 */             .getAttributePrefix(i));
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 103 */       node.addAttribute(this.parser.getAttributeLocalName(i), this.parser
/* 104 */           .getAttributeValue(i), ns);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\compatibility\CustomSOAPBuilderHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */