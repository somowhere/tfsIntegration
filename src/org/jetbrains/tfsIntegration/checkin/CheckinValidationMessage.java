/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
/*    */ import org.jetbrains.annotations.NotNull;
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
/*    */ public class CheckinValidationMessage
/*    */ {
/*    */   @NotNull
/*    */   private final Severity mySeverity;
/*    */   @NotNull
/*    */   private final String myMessage;
/*    */   
/*    */   public enum Severity
/*    */   {
/* 24 */     Error, PolicyWarning;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CheckinValidationMessage(@NotNull Severity severity, @NotNull String message) {
/* 32 */     this.mySeverity = severity;
/* 33 */     this.myMessage = message;
/*    */   }
/*    */ 
/*    */   
/*    */   @NotNull
/* 38 */   public Severity getSeverity() {   return this.mySeverity; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 43 */   public String getMessage() {    return this.myMessage; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\CheckinValidationMessage.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */