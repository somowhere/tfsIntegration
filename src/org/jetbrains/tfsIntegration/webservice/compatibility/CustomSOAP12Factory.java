/*    */ package org.jetbrains.tfsIntegration.webservice.compatibility;
/*    */ import javax.xml.namespace.QName;
/*    */ import org.apache.axiom.om.OMNode;
/*    */ import org.apache.axiom.om.OMXMLParserWrapper;
/*    */ import org.apache.axiom.soap.SOAPBody;
/*    */ import org.apache.axiom.soap.SOAPFactory;
/*    */ import org.apache.axiom.soap.SOAPFault;
/*    */ import org.apache.axiom.soap.SOAPFaultDetail;
/*    */ import org.apache.axiom.soap.SOAPProcessingException;
/*    */ import org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory;
/*    */ import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;
/*    */ 
/*    */ public class CustomSOAP12Factory extends SOAP12Factory {
/* 14 */   public SOAPFault createSOAPFault(SOAPBody parent, Exception e) throws SOAPProcessingException { return (SOAPFault)new CustomSOAP12FaultImpl(parent, e, (SOAPFactory)this); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 19 */   public SOAPFault createSOAPFault(SOAPBody parent) throws SOAPProcessingException { return (SOAPFault)new CustomSOAP12FaultImpl(parent, (SOAPFactory)this); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 24 */   public SOAPFault createSOAPFault() throws SOAPProcessingException { return (SOAPFault)new CustomSOAP12FaultImpl((SOAPFactory)this); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 29 */   public SOAPFault createSOAPFault(SOAPBody parent, OMXMLParserWrapper builder) { return (SOAPFault)new CustomSOAP12FaultImpl(parent, builder, (SOAPFactory)this); }
/*    */ 
/*    */   
/*    */   private static class CustomSOAP12FaultImpl
/*    */     extends SOAP12FaultImpl
/*    */   {
/* 35 */     CustomSOAP12FaultImpl(SOAPFactory factory) { super(factory); }
/*    */ 
/*    */ 
/*    */     
/* 39 */     CustomSOAP12FaultImpl(SOAPBody parent, Exception e, SOAPFactory factory) throws SOAPProcessingException { super(parent, e, factory); }
/*    */ 
/*    */ 
/*    */     
/* 43 */     CustomSOAP12FaultImpl(SOAPBody parent, OMXMLParserWrapper builder, SOAPFactory factory) { super(parent, builder, factory); }
/*    */ 
/*    */ 
/*    */     
/* 47 */     CustomSOAP12FaultImpl(SOAPBody parent, SOAPFactory factory) throws SOAPProcessingException { super(parent, factory); }
/*    */ 
/*    */ 
/*    */     
/*    */     public SOAPFaultDetail getDetail() {
/* 52 */       SOAPFaultDetail detail = super.getDetail();
/*    */       
/* 54 */       if (detail == null)
/*    */       {
/* 56 */         detail = (SOAPFaultDetail)getFirstChildWithName(new QName("Detail"));
/*    */       }
/*    */ 
/*    */       
/* 60 */       if (detail != null)
/*    */       {
/*    */         
/* 63 */         detail.addChild((OMNode)detail.cloneOMElement());
/*    */       }
/*    */       
/* 66 */       return detail;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\compatibility\CustomSOAP12Factory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */