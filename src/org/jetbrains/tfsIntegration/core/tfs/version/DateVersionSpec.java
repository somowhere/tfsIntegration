/*    */ package org.jetbrains.tfsIntegration.core.tfs.version;
/*    */ 
/*    */ import java.text.DateFormat;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
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
/*    */ public class DateVersionSpec
/*    */   extends VersionSpecBase
/*    */ {
/* 29 */   static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
/* 30 */   public static SimpleDateFormat otextFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
/*    */   
/* 32 */   private static final DateFormat defaultDateFormat = new SimpleDateFormat();
/*    */   
/*    */   private final Date myDate;
/*    */ 
/*    */   
/* 37 */   public DateVersionSpec(Date date) { this.myDate = date; }
/*    */ 
/*    */ 
/*    */   
/* 41 */   public Date getMyDate() { return this.myDate; }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void writeAttributes(QName parentQName, OMFactory factory, MTOMAwareXMLStreamWriter xmlWriter) throws XMLStreamException {
/* 46 */     writeVersionAttribute("", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance", (XMLStreamWriter)xmlWriter);
/* 47 */     writeVersionAttribute("", "xsi:type", "DateVersionSpec", (XMLStreamWriter)xmlWriter);
/* 48 */     writeVersionAttribute("", "date", getDateString(), (XMLStreamWriter)xmlWriter);
/* 49 */     writeVersionAttribute("", "otext", getOTextString(), (XMLStreamWriter)xmlWriter);
/*    */   }
/*    */ 
/*    */   
/* 53 */   public String getOTextString() { return otextFormat.format(this.myDate); }
/*    */ 
/*    */   
/*    */   public String getDateString() {
/* 57 */     String dateString = dateFormat.format(this.myDate);
/*    */ 
/*    */ 
/*    */     
/* 61 */     dateString = dateString.substring(0, 22) + ":" + dateString.substring(22);
/* 62 */     return dateString;
/*    */   }
/*    */ 
/*    */   
/* 66 */   public Date getDate() { return this.myDate; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 71 */   public String getPresentableString() { return defaultDateFormat.format(this.myDate); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\version\DateVersionSpec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */