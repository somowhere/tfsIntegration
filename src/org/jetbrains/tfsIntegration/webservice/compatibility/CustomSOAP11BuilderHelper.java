/*     */ package org.jetbrains.tfsIntegration.webservice.compatibility;
/*     */ 
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.apache.axiom.om.OMContainer;
/*     */ import org.apache.axiom.om.OMElement;
/*     */ import org.apache.axiom.om.OMXMLParserWrapper;
/*     */ import org.apache.axiom.om.impl.OMNodeEx;
/*     */ import org.apache.axiom.om.impl.exception.OMBuilderException;
/*     */ import org.apache.axiom.soap.SOAP11Constants;
/*     */ import org.apache.axiom.soap.SOAPFactory;
/*     */ import org.apache.axiom.soap.SOAPFault;
/*     */ import org.apache.axiom.soap.SOAPFaultCode;
/*     */ import org.apache.axiom.soap.SOAPFaultDetail;
/*     */ import org.apache.axiom.soap.SOAPFaultReason;
/*     */ import org.apache.axiom.soap.SOAPFaultRole;
/*     */ import org.apache.axiom.soap.SOAPProcessingException;
/*     */ import org.w3c.dom.Element;
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
/*     */ public class CustomSOAP11BuilderHelper
/*     */   extends CustomSOAPBuilderHelper
/*     */   implements SOAP11Constants
/*     */ {
/*     */   private final SOAPFactory factory;
/*     */   private boolean faultcodePresent = false;
/*     */   private boolean faultstringPresent = false;
/*     */   
/*     */   public CustomSOAP11BuilderHelper(CustomStAXSOAPModelBuilder builder) {
/*  39 */     super(builder);
/*  40 */     this.factory = builder.getSoapFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OMElement handleEvent(XMLStreamReader parser, OMElement parent, int elementLevel) throws SOAPProcessingException {
/*  47 */     this.parser = parser;
/*     */     
/*  49 */     OMElement element = null;
/*  50 */     String localName = parser.getLocalName();
/*     */     
/*  52 */     if (elementLevel == 4) {
/*     */       
/*  54 */       if ("faultcode".equals(localName)) {
/*     */         
/*  56 */         SOAPFaultCode code = this.factory.createSOAPFaultCode((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */         
/*  58 */         processNamespaceData((OMElement)code, false);
/*  59 */         processAttributes((OMElement)code);
/*     */         
/*  61 */         processText(parser, (OMElement)code);
/*  62 */         ((OMNodeEx)code).setComplete(true);
/*  63 */         SOAPFaultCode sOAPFaultCode = code;
/*     */         
/*  65 */         this.builder.adjustElementLevel(-1);
/*     */         
/*  67 */         this.faultcodePresent = true;
/*  68 */       } else if ("faultstring".equals(localName)) {
/*     */         
/*  70 */         SOAPFaultReason reason = this.factory.createSOAPFaultReason((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */         
/*  72 */         processNamespaceData((OMElement)reason, false);
/*  73 */         processAttributes((OMElement)reason);
/*     */         
/*  75 */         processText(parser, (OMElement)reason);
/*  76 */         ((OMNodeEx)reason).setComplete(true);
/*  77 */         SOAPFaultReason sOAPFaultReason = reason;
/*  78 */         this.builder.adjustElementLevel(-1);
/*     */ 
/*     */         
/*  81 */         this.faultstringPresent = true;
/*  82 */       } else if ("faultactor".equals(localName)) {
/*     */         
/*  84 */         SOAPFaultRole sOAPFaultRole = this.factory.createSOAPFaultRole((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */         
/*  86 */         processNamespaceData((OMElement)sOAPFaultRole, false);
/*  87 */         processAttributes((OMElement)sOAPFaultRole);
/*  88 */       } else if ("detail".equals(localName)) {
/*     */         
/*  90 */         SOAPFaultDetail sOAPFaultDetail = this.factory.createSOAPFaultDetail((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */         
/*  92 */         processNamespaceData((OMElement)sOAPFaultDetail, false);
/*  93 */         processAttributes((OMElement)sOAPFaultDetail);
/*     */       } else {
/*     */         
/*  96 */         element = this.factory.createOMElement(localName, null, (OMContainer)parent, (OMXMLParserWrapper)this.builder);
/*     */         
/*  98 */         processNamespaceData(element, false);
/*  99 */         processAttributes(element);
/*     */       }
/*     */     
/* 102 */     } else if (elementLevel == 5) {
/*     */       
/* 104 */       String parentTagName = "";
/* 105 */       if (parent instanceof Element) {
/* 106 */         parentTagName = ((Element)parent).getTagName();
/*     */       } else {
/* 108 */         parentTagName = parent.getLocalName();
/*     */       } 
/*     */       
/* 111 */       if (parentTagName.equals("faultcode")) {
/* 112 */         throw new OMBuilderException("faultcode element should not have children");
/*     */       }
/* 114 */       if (parentTagName.equals("faultstring"))
/*     */       {
/* 116 */         throw new OMBuilderException("faultstring element should not have children");
/*     */       }
/* 118 */       if (parentTagName.equals("faultactor"))
/*     */       {
/* 120 */         throw new OMBuilderException("faultactor element should not have children");
/*     */       }
/*     */ 
/*     */       
/* 124 */       element = this.factory.createOMElement(localName, null, (OMContainer)parent, (OMXMLParserWrapper)this.builder);
/*     */       
/* 126 */       processNamespaceData(element, false);
/* 127 */       processAttributes(element);
/*     */     
/*     */     }
/* 130 */     else if (elementLevel > 5) {
/*     */       
/* 132 */       element = this.factory.createOMElement(localName, null, (OMContainer)parent, (OMXMLParserWrapper)this.builder);
/*     */ 
/*     */ 
/*     */       
/* 136 */       processNamespaceData(element, false);
/* 137 */       processAttributes(element);
/*     */     } 
/*     */     
/* 140 */     return element;
/*     */   }
/*     */   
/*     */   private void processText(XMLStreamReader parser, OMElement value) {
/*     */     try {
/* 145 */       int token = parser.next();
/* 146 */       while (token != 2) {
/* 147 */         if (token == 4) {
/* 148 */           this.factory.createOMText((OMContainer)value, parser.getText());
/* 149 */         } else if (token == 12) {
/* 150 */           this.factory.createOMText((OMContainer)value, parser.getText());
/*     */         } else {
/* 152 */           throw new SOAPProcessingException("Only Characters are allowed here");
/*     */         } 
/*     */         
/* 155 */         token = parser.next();
/*     */       }
/*     */     
/*     */     }
/* 159 */     catch (XMLStreamException e) {
/* 160 */       throw new SOAPProcessingException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\compatibility\CustomSOAP11BuilderHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */