/*     */ package org.jetbrains.tfsIntegration.webservice.compatibility;
/*     */ 
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.apache.axiom.om.OMAbstractFactory;
/*     */ import org.apache.axiom.om.OMContainer;
/*     */ import org.apache.axiom.om.OMDocument;
/*     */ import org.apache.axiom.om.OMElement;
/*     */ import org.apache.axiom.om.OMException;
/*     */ import org.apache.axiom.om.OMFactory;
/*     */ import org.apache.axiom.om.OMNamespace;
/*     */ import org.apache.axiom.om.OMNode;
/*     */ import org.apache.axiom.om.OMXMLParserWrapper;
/*     */ import org.apache.axiom.om.impl.OMContainerEx;
/*     */ import org.apache.axiom.om.impl.OMNodeEx;
/*     */ import org.apache.axiom.om.impl.builder.CustomBuilder;
/*     */ import org.apache.axiom.om.impl.builder.StAXOMBuilder;
/*     */ import org.apache.axiom.soap.SOAPBody;
/*     */ import org.apache.axiom.soap.SOAPEnvelope;
/*     */ import org.apache.axiom.soap.SOAPFactory;
/*     */ import org.apache.axiom.soap.SOAPHeader;
/*     */ import org.apache.axiom.soap.SOAPMessage;
/*     */ import org.apache.axiom.soap.SOAPProcessingException;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
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
/*     */ public class CustomStAXSOAPModelBuilder
/*     */   extends StAXOMBuilder
/*     */ {
/*     */   SOAPMessage soapMessage;
/*     */   private SOAPEnvelope envelope;
/*     */   private OMNamespace envelopeNamespace;
/*     */   private String namespaceURI;
/*     */   private SOAPFactory soapFactory;
/*     */   private boolean headerPresent = false;
/*     */   private boolean bodyPresent = false;
/*  59 */   private static final Log log = LogFactory.getLog(CustomStAXSOAPModelBuilder.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean processingFault = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean processingDetailElements = false;
/*     */ 
/*     */   
/*     */   private CustomSOAPBuilderHelper builderHelper;
/*     */ 
/*     */   
/*  73 */   private String parserVersion = null;
/*  74 */   private static final boolean isDebugEnabled = log.isDebugEnabled();
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
/*     */   public CustomStAXSOAPModelBuilder(XMLStreamReader parser, String soapVersion) {
/*  87 */     super(parser);
/*  88 */     this.parserVersion = parser.getVersion();
/*  89 */     identifySOAPVersion(soapVersion);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CustomStAXSOAPModelBuilder(XMLStreamReader parser) {
/*  99 */     super(parser);
/* 100 */     this.parserVersion = parser.getVersion();
/* 101 */     SOAPEnvelope soapEnvelope = getSOAPEnvelope();
/* 102 */     this.envelopeNamespace = soapEnvelope.getNamespace();
/*     */   }
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
/*     */   public CustomStAXSOAPModelBuilder(XMLStreamReader parser, SOAPFactory factory, String soapVersion) {
/* 115 */     super((OMFactory)factory, parser);
/* 116 */     this.soapFactory = factory;
/* 117 */     this.parserVersion = parser.getVersion();
/* 118 */     identifySOAPVersion(soapVersion);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void identifySOAPVersion(String soapVersionURIFromTransport) {
/* 124 */     SOAPEnvelope soapEnvelope = getSOAPEnvelope();
/* 125 */     if (soapEnvelope == null) {
/* 126 */       throw new SOAPProcessingException("SOAP Message does not contain an Envelope", "VersionMismatch");
/*     */     }
/*     */ 
/*     */     
/* 130 */     this.envelopeNamespace = soapEnvelope.getNamespace();
/*     */     
/* 132 */     if (soapVersionURIFromTransport != null) {
/* 133 */       String namespaceName = this.envelopeNamespace.getNamespaceURI();
/* 134 */       if (!soapVersionURIFromTransport.equals(namespaceName)) {
/* 135 */         throw new SOAPProcessingException("Transport level information does not match with SOAP Message namespace URI", this.envelopeNamespace
/*     */             
/* 137 */             .getPrefix() + ":" + "VersionMismatch");
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SOAPEnvelope getSOAPEnvelope() throws OMException {
/* 151 */     while (this.envelope == null && !this.done) {
/* 152 */       next();
/*     */     }
/* 154 */     return this.envelope;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected OMNode createNextOMElement() {
/* 164 */     OMNode newElement = null;
/*     */ 
/*     */     
/* 167 */     if (this.elementLevel == 3 && this.customBuilderForPayload != null) {
/*     */ 
/*     */       
/* 170 */       OMNode parent = this.lastNode;
/* 171 */       if (parent != null && parent.isComplete()) {
/* 172 */         parent = (OMNode)this.lastNode.getParent();
/*     */       }
/* 174 */       if (parent instanceof SOAPBody) {
/* 175 */         newElement = createWithCustomBuilder(this.customBuilderForPayload, (OMFactory)this.soapFactory);
/*     */       }
/*     */     } 
/* 178 */     if (newElement == null && this.customBuilders != null && this.elementLevel <= this.maxDepthForCustomBuilders) {
/*     */       
/* 180 */       String namespace = this.parser.getNamespaceURI();
/* 181 */       String localPart = this.parser.getLocalName();
/* 182 */       CustomBuilder customBuilder = getCustomBuilder(namespace, localPart);
/* 183 */       if (customBuilder != null) {
/* 184 */         newElement = createWithCustomBuilder(customBuilder, (OMFactory)this.soapFactory);
/*     */       }
/*     */     } 
/* 187 */     if (newElement == null) {
/* 188 */       newElement = createOMElement();
/*     */     } else {
/* 190 */       this.elementLevel--;
/*     */     } 
/* 192 */     return newElement;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected OMNode createOMElement() throws OMException {
/*     */     OMElement node;
/* 205 */     String elementName = this.parser.getLocalName();
/* 206 */     if (this.lastNode == null) {
/* 207 */       node = constructNode(null, elementName, true);
/* 208 */       setSOAPEnvelope(node);
/* 209 */     } else if (this.lastNode.isComplete()) {
/* 210 */       OMContainer parent = this.lastNode.getParent();
/* 211 */       if (parent == this.document) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 216 */         this.lastNode = null;
/* 217 */         node = constructNode(null, elementName, true);
/* 218 */         setSOAPEnvelope(node);
/*     */       } else {
/* 220 */         node = constructNode((OMElement)parent, elementName, false);
/*     */ 
/*     */         
/* 223 */         ((OMNodeEx)this.lastNode).setNextOMSibling((OMNode)node);
/* 224 */         ((OMNodeEx)node).setPreviousOMSibling(this.lastNode);
/*     */       } 
/*     */     } else {
/* 227 */       OMContainerEx e = (OMContainerEx)this.lastNode;
/* 228 */       node = constructNode((OMElement)this.lastNode, elementName, false);
/* 229 */       e.setFirstChild((OMNode)node);
/*     */     } 
/*     */     
/* 232 */     if (isDebugEnabled) {
/* 233 */       log.debug("Build the OMElement " + node.getLocalName() + " by the StaxSOAPModelBuilder");
/*     */     }
/*     */     
/* 236 */     return (OMNode)node;
/*     */   }
/*     */   
/*     */   protected void setSOAPEnvelope(OMElement node) {
/* 240 */     this.soapMessage.setSOAPEnvelope((SOAPEnvelope)node);
/* 241 */     this.soapMessage.setXMLVersion(this.parserVersion);
/* 242 */     this.soapMessage.setCharsetEncoding(this.charEncoding);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected OMElement constructNode(OMElement parent, String elementName, boolean isEnvelope) {
/*     */     Object element;
/* 255 */     if (parent == null) {
/*     */ 
/*     */ 
/*     */       
/* 259 */       if (!elementName.equals("Envelope")) {
/* 260 */         throw new SOAPProcessingException("First Element must contain the local name, Envelope , but found " + elementName, "");
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 266 */       if (this.soapFactory == null) {
/* 267 */         this.namespaceURI = this.parser.getNamespaceURI();
/* 268 */         if ("http://www.w3.org/2003/05/soap-envelope".equals(this.namespaceURI)) {
/* 269 */           this.soapFactory = OMAbstractFactory.getSOAP12Factory();
/* 270 */           if (isDebugEnabled) {
/* 271 */             log.debug("Starting to process SOAP 1.2 message");
/*     */           }
/* 273 */         } else if ("http://schemas.xmlsoap.org/soap/envelope/".equals(this.namespaceURI)) {
/* 274 */           this.soapFactory = OMAbstractFactory.getSOAP11Factory();
/* 275 */           if (isDebugEnabled) {
/* 276 */             log.debug("Starting to process SOAP 1.1 message");
/*     */           }
/*     */         } else {
/* 279 */           throw new SOAPProcessingException("Only SOAP 1.1 or SOAP 1.2 messages are supported in the system", "VersionMismatch");
/*     */         }
/*     */       
/*     */       } else {
/*     */         
/* 284 */         this.namespaceURI = this.soapFactory.getSoapVersionURI();
/*     */       } 
/*     */ 
/*     */       
/* 288 */       this.soapMessage = this.soapFactory.createSOAPMessage((OMXMLParserWrapper)this);
/* 289 */       this.document = (OMDocument)this.soapMessage;
/* 290 */       if (this.charEncoding != null) {
/* 291 */         this.document.setCharsetEncoding(this.charEncoding);
/*     */       }
/*     */       
/* 294 */       this.envelope = this.soapFactory.createSOAPEnvelope((OMXMLParserWrapper)this);
/* 295 */       element = this.envelope;
/* 296 */       processNamespaceData((OMElement)element, true);
/*     */       
/* 298 */       processAttributes((OMElement)element);
/*     */     }
/* 300 */     else if (this.elementLevel == 2) {
/*     */       
/* 302 */       String elementNS = this.parser.getNamespaceURI();
/*     */       
/* 304 */       if (!this.namespaceURI.equals(elementNS) && (
/* 305 */         !this.bodyPresent || 
/* 306 */         !"http://schemas.xmlsoap.org/soap/envelope/".equals(this.namespaceURI))) {
/* 307 */         throw new SOAPProcessingException("Disallowed element found inside Envelope : {" + elementNS + "}" + elementName);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 313 */       if (elementName.equals("Header")) {
/* 314 */         if (this.headerPresent) {
/* 315 */           throw new SOAPProcessingException("Multiple headers encountered!", 
/* 316 */               getSenderFaultCode());
/*     */         }
/* 318 */         if (this.bodyPresent) {
/* 319 */           throw new SOAPProcessingException("Header Body wrong order!", 
/* 320 */               getSenderFaultCode());
/*     */         }
/* 322 */         this.headerPresent = true;
/*     */         
/* 324 */         element = this.soapFactory.createSOAPHeader((SOAPEnvelope)parent, (OMXMLParserWrapper)this);
/*     */ 
/*     */         
/* 327 */         processNamespaceData((OMElement)element, true);
/* 328 */         processAttributes((OMElement)element);
/*     */       }
/* 330 */       else if (elementName.equals("Body")) {
/* 331 */         if (this.bodyPresent) {
/* 332 */           throw new SOAPProcessingException("Multiple body elements encountered", 
/* 333 */               getSenderFaultCode());
/*     */         }
/* 335 */         this.bodyPresent = true;
/*     */         
/* 337 */         element = this.soapFactory.createSOAPBody((SOAPEnvelope)parent, (OMXMLParserWrapper)this);
/*     */ 
/*     */         
/* 340 */         processNamespaceData((OMElement)element, true);
/* 341 */         processAttributes((OMElement)element);
/*     */       } else {
/* 343 */         throw new SOAPProcessingException(elementName + " is not supported here. Envelope can not have elements other than Header and Body.", 
/*     */ 
/*     */             
/* 346 */             getSenderFaultCode());
/*     */       } 
/* 348 */     } else if (this.elementLevel == 3 && parent
/*     */       
/* 350 */       .getLocalName().equals("Header")) {
/*     */ 
/*     */       
/*     */       try {
/*     */         
/* 355 */         element = this.soapFactory.createSOAPHeaderBlock(elementName, null, (SOAPHeader)parent, (OMXMLParserWrapper)this);
/*     */       }
/* 357 */       catch (SOAPProcessingException e) {
/* 358 */         throw new SOAPProcessingException("Can not create SOAPHeader block", 
/* 359 */             getReceiverFaultCode(), (Throwable)e);
/*     */       } 
/* 361 */       processNamespaceData((OMElement)element, false);
/* 362 */       processAttributes((OMElement)element);
/*     */     }
/* 364 */     else if (this.elementLevel == 3 && parent
/* 365 */       .getLocalName().equals("Body") && elementName
/* 366 */       .equals("Fault")) {
/*     */       
/* 368 */       element = this.soapFactory.createSOAPFault((SOAPBody)parent, (OMXMLParserWrapper)this);
/* 369 */       processNamespaceData((OMElement)element, false);
/* 370 */       processAttributes((OMElement)element);
/*     */ 
/*     */       
/* 373 */       this.processingFault = true;
/* 374 */       if ("http://www.w3.org/2003/05/soap-envelope"
/* 375 */         .equals(this.envelopeNamespace.getNamespaceURI()))
/*     */       {
/* 377 */         this.builderHelper = new CustomSOAP12BuilderHelper(this);
/*     */       }
/* 379 */       else if ("http://schemas.xmlsoap.org/soap/envelope/"
/* 380 */         .equals(this.envelopeNamespace.getNamespaceURI()))
/*     */       {
/* 382 */         this.builderHelper = new CustomSOAP11BuilderHelper(this);
/*     */       }
/*     */     
/*     */     }
/* 386 */     else if (this.elementLevel > 3 && this.processingFault) {
/* 387 */       element = this.builderHelper.handleEvent(this.parser, parent, this.elementLevel);
/*     */     } else {
/*     */       
/* 390 */       element = this.soapFactory.createOMElement(elementName, null, (OMContainer)parent, (OMXMLParserWrapper)this);
/*     */       
/* 392 */       processNamespaceData((OMElement)element, false);
/* 393 */       processAttributes((OMElement)element);
/*     */     } 
/*     */     
/* 396 */     return (OMElement)element;
/*     */   }
/*     */ 
/*     */   
/* 400 */   private String getSenderFaultCode() { return this.envelope.getVersion().getSenderFaultCode().getLocalPart(); }
/*     */ 
/*     */ 
/*     */   
/* 404 */   private String getReceiverFaultCode() { return this.envelope.getVersion().getReceiverFaultCode().getLocalPart(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public void endElement() {
/* 409 */     if (this.lastNode.isComplete()) {
/* 410 */       OMElement parent = (OMElement)this.lastNode.getParent();
/* 411 */       ((OMNodeEx)parent).setComplete(true);
/* 412 */       this.lastNode = (OMNode)parent;
/*     */     } else {
/* 414 */       OMNode e = this.lastNode;
/* 415 */       ((OMNodeEx)e).setComplete(true);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 422 */   protected OMNode createDTD() throws OMException { throw new OMException("SOAP message MUST NOT contain a Document Type Declaration(DTD)"); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 428 */   protected OMNode createPI() throws OMException { throw new OMException("SOAP message MUST NOT contain Processing Instructions(PI)"); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 438 */   public OMElement getDocumentElement() { return (this.envelope != null) ? (OMElement)this.envelope : (OMElement)getSOAPEnvelope(); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void processNamespaceData(OMElement node, boolean isSOAPElement) {
/* 449 */     processNamespaceData(node);
/*     */     
/* 451 */     if (isSOAPElement && 
/* 452 */       node.getNamespace() != null && 
/*     */       
/* 454 */       !node.getNamespace().getNamespaceURI().equals("http://schemas.xmlsoap.org/soap/envelope/") && 
/*     */       
/* 456 */       !node.getNamespace().getNamespaceURI().equals("http://www.w3.org/2003/05/soap-envelope")) {
/* 457 */       throw new SOAPProcessingException("invalid SOAP namespace URI. Only http://schemas.xmlsoap.org/soap/envelope/ and http://www.w3.org/2003/05/soap-envelope are supported.", "Sender");
/*     */     }
/*     */   }
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
/* 470 */   public OMNamespace getEnvelopeNamespace() { return this.envelopeNamespace; }
/*     */ 
/*     */ 
/*     */   
/* 474 */   public boolean isProcessingDetailElements() { return this.processingDetailElements; }
/*     */ 
/*     */ 
/*     */   
/* 478 */   public void setProcessingDetailElements(boolean value) { this.processingDetailElements = value; }
/*     */ 
/*     */ 
/*     */   
/* 482 */   public SOAPMessage getSoapMessage() { return this.soapMessage; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 487 */   public OMDocument getDocument() { return (OMDocument)this.soapMessage; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 492 */   protected SOAPFactory getSoapFactory() { return this.soapFactory; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 502 */   void adjustElementLevel(int value) { this.elementLevel += value; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\compatibility\CustomStAXSOAPModelBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */