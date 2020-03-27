/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import org.apache.commons.httpclient.HttpStatus;
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
/*    */ public class ConnectionFailedException
/*    */   extends TfsException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int CODE_UNDEFINED = 0;
/*    */   private final int myHttpStatusCode;
/*    */   private final String myMessage;
/*    */   
/*    */   public ConnectionFailedException(Throwable cause, int httpStatusCode) {
/* 31 */     super(cause);
/* 32 */     this.myHttpStatusCode = httpStatusCode;
/* 33 */     this.myMessage = null;
/*    */   }
/*    */ 
/*    */   
/* 37 */   public ConnectionFailedException(Throwable cause) { this(cause, 0); }
/*    */ 
/*    */   
/*    */   public ConnectionFailedException(Throwable cause, String message) {
/* 41 */     super(cause);
/* 42 */     this.myHttpStatusCode = 0;
/* 43 */     this.myMessage = message;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getMessage() {
/* 48 */     if (this.myMessage != null) {
/* 49 */       return this.myMessage;
/*    */     }
/*    */     
/* 52 */     String message = super.getMessage();
/* 53 */     if (message != null) {
/* 54 */       return message;
/*    */     }
/*    */     
/* 57 */     if (this.myHttpStatusCode != 0) {
/* 58 */       return HttpStatus.getStatusText(this.myHttpStatusCode);
/*    */     }
/* 60 */     return null;
/*    */   }
/*    */ 
/*    */   
/* 64 */   public int getHttpStatusCode() { return this.myHttpStatusCode; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\ConnectionFailedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */