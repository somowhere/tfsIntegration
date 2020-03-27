/*    */ package org.jetbrains.tfsIntegration.webservice.compatibility;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.PushbackInputStream;
/*    */ import javax.xml.stream.XMLStreamException;
/*    */ import javax.xml.stream.XMLStreamReader;
/*    */ import org.apache.axiom.om.OMElement;
/*    */ import org.apache.axiom.om.util.DetachableInputStream;
/*    */ import org.apache.axiom.om.util.StAXUtils;
/*    */ import org.apache.axiom.soap.SOAPEnvelope;
/*    */ import org.apache.axis2.AxisFault;
/*    */ import org.apache.axis2.builder.Builder;
/*    */ import org.apache.axis2.builder.BuilderUtil;
/*    */ import org.apache.axis2.context.MessageContext;
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
/*    */ public class CustomSOAPBuilder
/*    */   implements Builder
/*    */ {
/*    */   public OMElement processDocument(InputStream inputStream, String contentType, MessageContext messageContext) throws AxisFault {
/*    */     try {
/* 49 */       String charSetEncoding = (String)messageContext.getProperty("CHARACTER_SET_ENCODING");
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 54 */       DetachableInputStream is = new DetachableInputStream(inputStream);
/* 55 */       messageContext.setProperty("org.apache.axiom.om.util.DetachableInputStream", is);
/*    */ 
/*    */       
/* 58 */       PushbackInputStream pis = BuilderUtil.getPushbackInputStream((InputStream)is);
/* 59 */       String actualCharSetEncoding = BuilderUtil.getCharSetEncoding(pis, charSetEncoding);
/*    */ 
/*    */       
/* 62 */       XMLStreamReader streamReader = StAXUtils.createXMLStreamReader(pis, actualCharSetEncoding);
/*    */ 
/*    */       
/* 65 */       CustomStAXSOAPModelBuilder customStAXSOAPModelBuilder = new CustomStAXSOAPModelBuilder(streamReader);
/*    */       
/* 67 */       SOAPEnvelope envelope = (SOAPEnvelope)customStAXSOAPModelBuilder.getDocumentElement();
/*    */       
/* 69 */       BuilderUtil.validateSOAPVersion(BuilderUtil.getEnvelopeNamespace(contentType), envelope);
/* 70 */       BuilderUtil.validateCharSetEncoding(charSetEncoding, customStAXSOAPModelBuilder.getDocument()
/* 71 */           .getCharsetEncoding(), envelope.getNamespace().getNamespaceURI());
/* 72 */       return (OMElement)envelope;
/* 73 */     } catch (IOException e) {
/* 74 */       throw AxisFault.makeFault(e);
/* 75 */     } catch (XMLStreamException e) {
/* 76 */       throw AxisFault.makeFault(e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\compatibility\CustomSOAPBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */