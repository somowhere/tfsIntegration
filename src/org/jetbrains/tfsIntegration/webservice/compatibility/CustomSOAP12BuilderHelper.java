/*     */ package org.jetbrains.tfsIntegration.webservice.compatibility;
/*     */ 
/*     */ import java.util.Vector;
/*     */ import javax.xml.stream.XMLStreamReader;
/*     */ import org.apache.axiom.om.OMContainer;
/*     */ import org.apache.axiom.om.OMElement;
/*     */ import org.apache.axiom.om.OMXMLParserWrapper;
/*     */ import org.apache.axiom.om.impl.OMNodeEx;
/*     */ import org.apache.axiom.om.impl.exception.OMBuilderException;
/*     */ import org.apache.axiom.soap.SOAPFactory;
/*     */ import org.apache.axiom.soap.SOAPFault;
/*     */ import org.apache.axiom.soap.SOAPFaultCode;
/*     */ import org.apache.axiom.soap.SOAPFaultDetail;
/*     */ import org.apache.axiom.soap.SOAPFaultNode;
/*     */ import org.apache.axiom.soap.SOAPFaultReason;
/*     */ import org.apache.axiom.soap.SOAPFaultRole;
/*     */ import org.apache.axiom.soap.SOAPFaultSubCode;
/*     */ import org.apache.axiom.soap.SOAPFaultText;
/*     */ import org.apache.axiom.soap.SOAPFaultValue;
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
/*     */ public class CustomSOAP12BuilderHelper
/*     */   extends CustomSOAPBuilderHelper
/*     */ {
/*     */   private final SOAPFactory factory;
/*     */   private boolean codePresent = false;
/*     */   private boolean reasonPresent = false;
/*     */   private boolean nodePresent = false;
/*     */   private boolean rolePresent = false;
/*     */   private boolean detailPresent = false;
/*     */   private boolean subcodeValuePresent = false;
/*     */   private boolean subSubcodePresent = false;
/*     */   private boolean valuePresent = false;
/*     */   private boolean subcodePresent = false;
/*     */   private boolean codeprocessing = false;
/*     */   private boolean subCodeProcessing = false;
/*     */   private boolean reasonProcessing = false;
/*     */   private Vector detailElementNames;
/*     */   
/*     */   public CustomSOAP12BuilderHelper(CustomStAXSOAPModelBuilder builder) {
/*  51 */     super(builder);
/*  52 */     this.factory = builder.getSoapFactory();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OMElement handleEvent(XMLStreamReader parser, OMElement parent, int elementLevel) throws SOAPProcessingException {
/*  60 */     this.parser = parser;
/*  61 */     OMElement element = null;
/*     */     
/*  63 */     if (elementLevel == 4) {
/*  64 */       if (parser.getLocalName().equals("Code")) {
/*     */         
/*  66 */         if (this.codePresent) {
/*  67 */           throw new OMBuilderException("Multiple Code element encountered");
/*     */         }
/*     */ 
/*     */         
/*  71 */         SOAPFaultCode sOAPFaultCode = this.factory.createSOAPFaultCode((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */         
/*  73 */         this.codePresent = true;
/*  74 */         this.codeprocessing = true;
/*     */       }
/*  76 */       else if (parser.getLocalName().equals("Reason")) {
/*     */         
/*  78 */         if (!this.codeprocessing && !this.subCodeProcessing) {
/*  79 */           if (this.codePresent) {
/*  80 */             if (this.reasonPresent) {
/*  81 */               throw new OMBuilderException("Multiple Reason Element encountered");
/*     */             }
/*     */ 
/*     */             
/*  85 */             SOAPFaultReason sOAPFaultReason = this.factory.createSOAPFaultReason((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */             
/*  87 */             this.reasonPresent = true;
/*  88 */             this.reasonProcessing = true;
/*     */           } else {
/*     */             
/*  91 */             throw new OMBuilderException("Wrong element order encountred at " + parser
/*     */                 
/*  93 */                 .getLocalName());
/*     */           } 
/*     */         } else {
/*  96 */           if (this.codeprocessing) {
/*  97 */             throw new OMBuilderException("Code doesn't have a value");
/*     */           }
/*     */           
/* 100 */           throw new OMBuilderException("A subcode doesn't have a Value");
/*     */         
/*     */         }
/*     */       
/*     */       }
/* 105 */       else if (parser.getLocalName().equals("Node")) {
/*     */         
/* 107 */         if (!this.reasonProcessing) {
/* 108 */           if (this.reasonPresent && !this.rolePresent && !this.detailPresent) {
/* 109 */             if (this.nodePresent) {
/* 110 */               throw new OMBuilderException("Multiple Node element encountered");
/*     */             }
/*     */ 
/*     */             
/* 114 */             SOAPFaultNode sOAPFaultNode = this.factory.createSOAPFaultNode((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */             
/* 116 */             this.nodePresent = true;
/*     */           } else {
/*     */             
/* 119 */             throw new OMBuilderException("wrong element order encountered at " + parser
/*     */                 
/* 121 */                 .getLocalName());
/*     */           } 
/*     */         } else {
/* 124 */           throw new OMBuilderException("Reason element Should have a text");
/*     */         }
/*     */       
/* 127 */       } else if (parser.getLocalName().equals("Role")) {
/*     */         
/* 129 */         if (!this.reasonProcessing) {
/* 130 */           if (this.reasonPresent && !this.detailPresent) {
/* 131 */             if (this.rolePresent) {
/* 132 */               throw new OMBuilderException("Multiple Role element encountered");
/*     */             }
/*     */ 
/*     */             
/* 136 */             SOAPFaultRole sOAPFaultRole = this.factory.createSOAPFaultRole((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */             
/* 138 */             this.rolePresent = true;
/*     */           } else {
/*     */             
/* 141 */             throw new OMBuilderException("Wrong element order encountered at " + parser
/*     */                 
/* 143 */                 .getLocalName());
/*     */           } 
/*     */         } else {
/* 146 */           throw new OMBuilderException("Reason element should have a text");
/*     */         }
/*     */       
/*     */       }
/* 150 */       else if (parser.getLocalName().equalsIgnoreCase("Detail")) {
/*     */ 
/*     */         
/* 153 */         if (!this.reasonProcessing) {
/* 154 */           if (this.reasonPresent) {
/* 155 */             if (this.detailPresent) {
/* 156 */               throw new OMBuilderException("Multiple detail element encountered");
/*     */             }
/*     */ 
/*     */             
/* 160 */             SOAPFaultDetail sOAPFaultDetail = this.factory.createSOAPFaultDetail((SOAPFault)parent, (OMXMLParserWrapper)this.builder);
/*     */             
/* 162 */             this.detailPresent = true;
/*     */           } else {
/*     */             
/* 165 */             throw new OMBuilderException("wrong element order encountered at " + parser
/*     */                 
/* 167 */                 .getLocalName());
/*     */           } 
/*     */         } else {
/* 170 */           throw new OMBuilderException("Reason element should have a text");
/*     */         } 
/*     */       } else {
/*     */         
/* 174 */         throw new OMBuilderException(parser
/* 175 */             .getLocalName() + " unsupported element in SOAPFault element");
/*     */       }
/*     */     
/*     */     }
/* 179 */     else if (elementLevel == 5) {
/* 180 */       if (parent.getLocalName().equals("Code")) {
/*     */         
/* 182 */         if (parser.getLocalName().equals("Value")) {
/*     */           
/* 184 */           if (!this.valuePresent) {
/*     */             
/* 186 */             SOAPFaultValue sOAPFaultValue = this.factory.createSOAPFaultValue((SOAPFaultCode)parent, (OMXMLParserWrapper)this.builder);
/*     */             
/* 188 */             this.valuePresent = true;
/* 189 */             this.codeprocessing = false;
/*     */           } else {
/* 191 */             throw new OMBuilderException("Multiple value Encountered in code element");
/*     */           }
/*     */         
/*     */         }
/* 195 */         else if (parser.getLocalName().equals("Subcode")) {
/*     */           
/* 197 */           if (!this.subcodePresent) {
/* 198 */             if (this.valuePresent) {
/*     */               
/* 200 */               SOAPFaultSubCode sOAPFaultSubCode = this.factory.createSOAPFaultSubCode((SOAPFaultCode)parent, (OMXMLParserWrapper)this.builder);
/*     */               
/* 202 */               this.subcodePresent = true;
/* 203 */               this.subCodeProcessing = true;
/*     */             } else {
/* 205 */               throw new OMBuilderException("Value should present before the subcode");
/*     */             }
/*     */           
/*     */           } else {
/*     */             
/* 210 */             throw new OMBuilderException("multiple subcode Encountered in code element");
/*     */           } 
/*     */         } else {
/*     */           
/* 214 */           throw new OMBuilderException(parser
/* 215 */               .getLocalName() + " is not supported inside the code element");
/*     */         }
/*     */       
/*     */       }
/* 219 */       else if (parent.getLocalName().equals("Reason")) {
/*     */         
/* 221 */         if (parser.getLocalName().equals("Text")) {
/*     */ 
/*     */           
/* 224 */           SOAPFaultText sOAPFaultText = this.factory.createSOAPFaultText((SOAPFaultReason)parent, (OMXMLParserWrapper)this.builder);
/*     */           
/* 226 */           ((OMNodeEx)sOAPFaultText).setComplete(false);
/* 227 */           this.reasonProcessing = false;
/*     */         } else {
/* 229 */           throw new OMBuilderException(parser
/* 230 */               .getLocalName() + " is not supported inside the reason");
/*     */         }
/*     */       
/*     */       }
/* 234 */       else if (parent.getLocalName().equalsIgnoreCase("Detail")) {
/*     */ 
/*     */ 
/*     */         
/* 238 */         element = this.factory.createOMElement(parser
/* 239 */             .getLocalName(), null, (OMContainer)parent, (OMXMLParserWrapper)this.builder);
/* 240 */         this.builder.setProcessingDetailElements(true);
/* 241 */         this.detailElementNames = new Vector();
/* 242 */         this.detailElementNames.add(parser.getLocalName());
/*     */       } else {
/*     */         
/* 245 */         throw new OMBuilderException(parent
/* 246 */             .getLocalName() + " should not have child element");
/*     */       
/*     */       }
/*     */     
/*     */     }
/* 251 */     else if (elementLevel > 5) {
/* 252 */       if (parent.getLocalName().equals("Subcode")) {
/*     */         
/* 254 */         if (parser.getLocalName().equals("Value")) {
/*     */           
/* 256 */           if (this.subcodeValuePresent) {
/* 257 */             throw new OMBuilderException("multiple subCode value encountered");
/*     */           }
/*     */ 
/*     */           
/* 261 */           SOAPFaultValue sOAPFaultValue = this.factory.createSOAPFaultValue((SOAPFaultSubCode)parent, (OMXMLParserWrapper)this.builder);
/*     */           
/* 263 */           this.subcodeValuePresent = true;
/* 264 */           this.subSubcodePresent = false;
/* 265 */           this.subCodeProcessing = false;
/*     */         }
/* 267 */         else if (parser.getLocalName().equals("Subcode")) {
/*     */           
/* 269 */           if (this.subcodeValuePresent) {
/* 270 */             if (!this.subSubcodePresent) {
/*     */               
/* 272 */               SOAPFaultSubCode sOAPFaultSubCode = this.factory.createSOAPFaultSubCode((SOAPFaultSubCode)parent, (OMXMLParserWrapper)this.builder);
/*     */ 
/*     */               
/* 275 */               this.subcodeValuePresent = false;
/* 276 */               this.subSubcodePresent = true;
/* 277 */               this.subCodeProcessing = true;
/*     */             } else {
/* 279 */               throw new OMBuilderException("multiple subcode encountered");
/*     */             } 
/*     */           } else {
/*     */             
/* 283 */             throw new OMBuilderException("Value should present before the subcode");
/*     */           } 
/*     */         } else {
/*     */           
/* 287 */           throw new OMBuilderException(parser
/* 288 */               .getLocalName() + " is not supported inside the subCode element");
/*     */         }
/*     */       
/* 291 */       } else if (this.builder.isProcessingDetailElements()) {
/* 292 */         int detailElementLevel = 0;
/* 293 */         boolean localNameExist = false;
/* 294 */         for (int i = 0; i < this.detailElementNames.size(); i++) {
/* 295 */           if (parent.getLocalName().equals(this.detailElementNames
/* 296 */               .get(i))) {
/* 297 */             localNameExist = true;
/* 298 */             detailElementLevel = i + 1;
/*     */           } 
/*     */         } 
/* 301 */         if (localNameExist) {
/* 302 */           this.detailElementNames.setSize(detailElementLevel);
/*     */           
/* 304 */           element = this.factory.createOMElement(parser
/* 305 */               .getLocalName(), null, (OMContainer)parent, (OMXMLParserWrapper)this.builder);
/*     */ 
/*     */ 
/*     */           
/* 309 */           this.detailElementNames.add(parser.getLocalName());
/*     */         } 
/*     */       } else {
/*     */         
/* 313 */         throw new OMBuilderException(parent
/* 314 */             .getLocalName() + " should not have child at element level " + elementLevel);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 320 */     processNamespaceData(element, false);
/* 321 */     processAttributes(element);
/* 322 */     return element;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\compatibility\CustomSOAP12BuilderHelper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */