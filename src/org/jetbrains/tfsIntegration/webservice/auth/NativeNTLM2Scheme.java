/*    */ package org.jetbrains.tfsIntegration.webservice.auth;
/*    */ 
/*    */ import com.intellij.openapi.diagnostic.Logger;
/*    */ import com.intellij.openapi.util.Pair;
/*    */ import com.intellij.openapi.util.SystemInfo;
/*    */ import java.lang.reflect.Constructor;
/*    */ import java.lang.reflect.Method;
/*    */ import org.apache.commons.httpclient.NTCredentials;
/*    */ import org.apache.commons.httpclient.auth.AuthenticationException;
/*    */ import org.apache.commons.httpclient.params.HttpMethodParams;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.webservice.WebServiceHelper;
/*    */ 
/*    */ public class NativeNTLM2Scheme
/*    */   extends NTLM2Scheme
/*    */ {
/* 17 */   private static final Logger LOG = Logger.getInstance(NativeNTLM2Scheme.class.getName());
/*    */   
/*    */   @Nullable
/*    */   private final Object myAuthSequenceObject;
/*    */   @Nullable
/*    */   private final Method myGetAuthHeaderMethod;
/*    */   
/*    */   public NativeNTLM2Scheme() {
/* 25 */     Pair<Object, Method> pair = createNativeAuthSequence();
/* 26 */     this.myAuthSequenceObject = Pair.getFirst(pair);
/* 27 */     this.myGetAuthHeaderMethod = (Method)Pair.getSecond(pair);
/*    */   }
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   private static Pair<Object, Method> createNativeAuthSequence() {
/* 33 */     boolean java7 = SystemInfo.isJavaVersionAtLeast(7, 0, 0);
/*    */ 
/*    */     
/*    */     try {
/* 37 */       Class<?> clazz = java7 ? Class.forName("sun.net.www.protocol.http.ntlm.NTLMAuthSequence") : Class.forName("sun.net.www.protocol.http.NTLMAuthSequence");
/* 38 */       if (clazz == null) {
/* 39 */         return null;
/*    */       }
/* 41 */       Constructor<?> constructor = clazz.getDeclaredConstructor(new Class[] { String.class, String.class, String.class });
/* 42 */       constructor.setAccessible(true);
/* 43 */       Object sequence = constructor.newInstance(new Object[] { null, null, null });
/* 44 */       Method method = clazz.getMethod("getAuthHeader", new Class[] { String.class });
/* 45 */       if (method == null) {
/* 46 */         return null;
/*    */       }
/* 48 */       return Pair.create(sequence, method);
/*    */     }
/* 50 */     catch (Throwable t) {
/* 51 */       LOG.debug(t);
/* 52 */       return null;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getType1MessageResponse(NTCredentials ntcredentials, HttpMethodParams params) {
/* 58 */     if (!params.getBooleanParameter(WebServiceHelper.USE_NATIVE_CREDENTIALS, false) || this.myAuthSequenceObject == null) {
/* 59 */       return super.getType1MessageResponse(ntcredentials, params);
/*    */     }
/*    */     
/*    */     try {
/* 63 */       return (String)this.myGetAuthHeaderMethod.invoke(this.myAuthSequenceObject, new Object[] { null });
/*    */     }
/* 65 */     catch (Throwable t) {
/* 66 */       LOG.warn("Native authentication failed", t);
/* 67 */       return "";
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected String getType3MessageResponse(String type2message, NTCredentials ntcredentials, HttpMethodParams params) throws AuthenticationException {
/* 74 */     if (!params.getBooleanParameter(WebServiceHelper.USE_NATIVE_CREDENTIALS, false) || this.myAuthSequenceObject == null) {
/* 75 */       return super.getType3MessageResponse(type2message, ntcredentials, params);
/*    */     }
/*    */     
/*    */     try {
/* 79 */       return (String)this.myGetAuthHeaderMethod.invoke(this.myAuthSequenceObject, new Object[] { type2message });
/*    */     }
/* 81 */     catch (Throwable t) {
/* 82 */       LOG.warn("Native authentication failed", t);
/* 83 */       return "";
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/* 88 */   public static boolean isAvailable() { return (createNativeAuthSequence() != null); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\auth\NativeNTLM2Scheme.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */