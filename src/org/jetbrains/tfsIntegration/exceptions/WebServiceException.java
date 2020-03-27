/*    */ package org.jetbrains.tfsIntegration.exceptions;
/*    */ 
/*    */ import java.net.UnknownHostException;
/*    */ import java.rmi.RemoteException;
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
/*    */ public class WebServiceException
/*    */   extends Exception
/*    */ {
/* 27 */   public WebServiceException(String message) { super(message); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 32 */   public WebServiceException(Exception exception) { super(exception); }
/*    */ 
/*    */ 
/*    */   
/* 36 */   public WebServiceException(RemoteException e) { super(e); }
/*    */ 
/*    */ 
/*    */   
/* 40 */   public WebServiceException(String message, UnknownHostException cause) { super(message, cause); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\exceptions\WebServiceException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */