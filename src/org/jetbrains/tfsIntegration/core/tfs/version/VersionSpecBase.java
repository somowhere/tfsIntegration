/*     */ package org.jetbrains.tfsIntegration.core.tfs.version;
/*     */ 
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamWriter;
/*     */ import org.apache.axiom.om.OMFactory;
/*     */ import org.apache.axis2.databinding.utils.BeanUtil;
/*     */ import org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter;
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
/*     */ public abstract class VersionSpecBase
/*     */   extends VersionSpec
/*     */ {
/*     */   public void serialize(QName parentQName, OMFactory factory, MTOMAwareXMLStreamWriter xmlWriter) throws XMLStreamException {
/*  37 */     String prefix = parentQName.getPrefix();
/*  38 */     String namespace = parentQName.getNamespaceURI();
/*     */     
/*  40 */     if (namespace != null) {
/*  41 */       String writerPrefix = xmlWriter.getPrefix(namespace);
/*     */       
/*  43 */       if (writerPrefix != null) {
/*  44 */         xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
/*     */       } else {
/*     */         
/*  47 */         if (prefix == null) {
/*  48 */           prefix = genPrefix(namespace);
/*     */         }
/*     */         
/*  51 */         xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
/*  52 */         xmlWriter.writeNamespace(prefix, namespace);
/*  53 */         xmlWriter.setPrefix(prefix, namespace);
/*     */       } 
/*     */     } else {
/*     */       
/*  57 */       xmlWriter.writeStartElement(parentQName.getLocalPart());
/*     */     } 
/*  59 */     writeAttributes(parentQName, factory, xmlWriter);
/*  60 */     xmlWriter.writeEndElement();
/*     */   }
/*     */   
/*     */   private static String genPrefix(String namespace) {
/*  64 */     if (namespace.equals("http://schemas.microsoft.com/TeamFoundation/2005/06/VersionControl/ClientServices/03")) {
/*  65 */       return "";
/*     */     }
/*     */     
/*  68 */     return BeanUtil.getUniquePrefix();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected abstract void writeAttributes(QName paramQName, OMFactory paramOMFactory, MTOMAwareXMLStreamWriter paramMTOMAwareXMLStreamWriter) throws XMLStreamException;
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void writeVersionAttribute(String namespace, String attName, String attValue, XMLStreamWriter xmlWriter) throws XMLStreamException {
/*  79 */     if (namespace.length() == 0) {
/*  80 */       xmlWriter.writeAttribute(attName, attValue);
/*     */     } else {
/*     */       
/*  83 */       regPrefix(xmlWriter, namespace);
/*  84 */       xmlWriter.writeAttribute(namespace, attName, attValue);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static String regPrefix(XMLStreamWriter xmlWriter, String namespace) throws XMLStreamException {
/*  92 */     String prefix = xmlWriter.getPrefix(namespace);
/*     */     
/*  94 */     if (prefix == null) {
/*  95 */       prefix = genPrefix(namespace);
/*     */       
/*  97 */       while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
/*  98 */         prefix = BeanUtil.getUniquePrefix();
/*     */       }
/*     */       
/* 101 */       xmlWriter.writeNamespace(prefix, namespace);
/* 102 */       xmlWriter.setPrefix(prefix, namespace);
/*     */     } 
/*     */     
/* 105 */     return prefix;
/*     */   }
/*     */   
/*     */   public abstract String getPresentableString();
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\version\VersionSpecBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */