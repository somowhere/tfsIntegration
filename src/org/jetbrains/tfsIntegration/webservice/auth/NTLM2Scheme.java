/*     */ package org.jetbrains.tfsIntegration.webservice.auth;
/*     */ 
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import java.io.IOException;
/*     */ import jcifs.ntlmssp.Type1Message;
/*     */ import jcifs.ntlmssp.Type2Message;
/*     */ import jcifs.ntlmssp.Type3Message;
/*     */ import jcifs.util.Base64;
/*     */ import org.apache.commons.httpclient.Credentials;
/*     */ import org.apache.commons.httpclient.HttpMethod;
/*     */ import org.apache.commons.httpclient.NTCredentials;
/*     */ import org.apache.commons.httpclient.auth.AuthChallengeParser;
/*     */ import org.apache.commons.httpclient.auth.AuthenticationException;
/*     */ import org.apache.commons.httpclient.auth.InvalidCredentialsException;
/*     */ import org.apache.commons.httpclient.auth.MalformedChallengeException;
/*     */ import org.apache.commons.httpclient.auth.NTLMScheme;
/*     */ import org.apache.commons.httpclient.params.HttpMethodParams;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.Workstation;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NTLM2Scheme
/*     */   extends NTLMScheme
/*     */ {
/*  52 */   private static final Logger LOG = Logger.getInstance(NTLM2Scheme.class.getName());
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   private static final int MESSAGE_1_DEFAULT_FLAGS = Type1Message.getDefaultFlags() | readUserFlags("org.jetbrains.tfsIntegration.webservice.auth.ntlm.message1flags");
            private static final int MESSAGE_3_DEFAULT_FLAGS = Type3Message.getDefaultFlags() | readUserFlags("org.jetbrains.tfsIntegration.webservice.auth.ntlm.message3flags");
    /*     */
    static  {
/*  60 */     LOG.info("Message 1 flags: 0x" + Integer.toHexString(MESSAGE_1_DEFAULT_FLAGS));
/*     */     
/*  62 */
/*  63 */     LOG.info("Message 3 flags: 0x" + Integer.toHexString(MESSAGE_3_DEFAULT_FLAGS));
/*     */   }
/*     */   private static int readUserFlags(String key) {
/*  67 */     String flagsStr = System.getProperty(key);
/*  68 */     if (flagsStr != null && flagsStr.startsWith("0x")) {
/*     */       try {
/*  70 */         return (int)Long.parseLong(flagsStr.substring("0x".length()), 16);
/*     */       }
/*  72 */       catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */     
/*  75 */     return 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  82 */   private String ntlmchallenge = null;
/*     */ 
/*     */   
/*     */   private static final int UNINITIATED = 0;
/*     */ 
/*     */   
/*     */   private static final int INITIATED = 1;
/*     */   
/*     */   private static final int TYPE1_MSG_GENERATED = 2;
/*     */   
/*     */   private static final int TYPE2_MSG_RECEIVED = 3;
/*     */   
/*     */   private static final int TYPE3_MSG_GENERATED = 4;
/*     */   
/*     */   private static final int FAILED = 2147483647;
/*     */   
/*     */   private int state;
/*     */ 
/*     */   
/* 101 */   public NTLM2Scheme() { this.state = 0; }
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
/* 114 */   public NTLM2Scheme(String challenge) throws MalformedChallengeException { processChallenge(challenge); }
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
/*     */   public void processChallenge(String challenge) throws MalformedChallengeException {
/* 129 */     String s = AuthChallengeParser.extractScheme(challenge);
/* 130 */     if (!s.equalsIgnoreCase(getSchemeName())) {
/* 131 */       throw new MalformedChallengeException("Invalid NTLM challenge: " + challenge);
/*     */     }
/* 133 */     int i = challenge.indexOf(' ');
/* 134 */     if (i != -1) {
/* 135 */       s = challenge.substring(i);
/* 136 */       this.ntlmchallenge = s.trim();
/* 137 */       this.state = 3;
/*     */     } else {
/* 139 */       this.ntlmchallenge = "";
/* 140 */       if (this.state == 0) {
/* 141 */         this.state = 1;
/*     */       } else {
/* 143 */         this.state = Integer.MAX_VALUE;
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
/*     */ 
/*     */   
/* 158 */   public boolean isComplete() { return (this.state == 4 || this.state == Integer.MAX_VALUE); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 168 */   public String getSchemeName() { return "ntlm"; }
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
/* 179 */   public String getRealm() { return null; }
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
/*     */   @Deprecated
/* 203 */   public String getID() { return this.ntlmchallenge; }
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
/*     */   public String getParameter(String name) {
/* 218 */     if (name == null) {
/* 219 */       throw new IllegalArgumentException("Parameter name may not be null");
/*     */     }
/* 221 */     return null;
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
/* 233 */   public boolean isConnectionBased() { return true; }
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
/*     */   public String authenticate(Credentials credentials, HttpMethod method) throws AuthenticationException {
/*     */     Object response;
/* 352 */     if (this.state == 0) {
/* 353 */       throw new IllegalStateException("NTLM authentication process was not initiated");
/*     */     }
/*     */     
/* 356 */     NTCredentials ntcredentials = null;
/*     */     try {
/* 358 */       ntcredentials = (NTCredentials)credentials;
/*     */     }
/* 360 */     catch (ClassCastException e) {
/* 361 */       throw new InvalidCredentialsException("Credentials cannot be used for NTLM authentication: " + credentials.getClass().getName());
/*     */     } 
/*     */ 
/*     */     
/* 365 */     if (this.state == 1 || this.state == Integer.MAX_VALUE) {
/* 366 */       response = getType1MessageResponse(ntcredentials, method.getParams());
/* 367 */       this.state = 2;
/*     */     } else {
/*     */       
/* 370 */       response = getType3MessageResponse(this.ntlmchallenge, ntcredentials, method.getParams());
/* 371 */       this.state = 4;
/*     */     } 
/* 373 */     return "NTLM " + response;
/*     */   }
/*     */ 
/*     */   
/*     */   protected String getType1MessageResponse(NTCredentials ntcredentials, HttpMethodParams params) {
/* 378 */     Type1Message t1m = new Type1Message(MESSAGE_1_DEFAULT_FLAGS, ntcredentials.getDomain(), Workstation.getComputerName());
/* 379 */     return Base64.encode(t1m.toByteArray());
/*     */   }
/*     */   
/*     */   protected String getType3MessageResponse(String type2message, NTCredentials ntcredentials, HttpMethodParams params) throws AuthenticationException {
/*     */     Type2Message t2m;
/*     */     try {
/* 385 */       t2m = new Type2Message(Base64.decode(type2message));
/*     */     }
/* 387 */     catch (IOException ex) {
/* 388 */       throw new AuthenticationException("Invalid Type2 message", ex);
/*     */     } 
/*     */ 
/*     */     
/* 392 */     Type3Message t3m = new Type3Message(t2m, ntcredentials.getPassword(), ntcredentials.getDomain(), ntcredentials.getUserName(), Workstation.getComputerName(), MESSAGE_3_DEFAULT_FLAGS);
/* 393 */     return Base64.encode(t3m.toByteArray());
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\auth\NTLM2Scheme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */