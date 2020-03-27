/*     */ package org.jetbrains.tfsIntegration.checkin;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import jcifs.util.Base64;
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
/*     */ public class StatelessPolicyParser
/*     */ {
/*     */   public static List<PolicyDescriptor> parseDescriptors(String input) throws PolicyParseException {
/*  31 */     byte[] data = Base64.decode(input);
/*     */     
/*  33 */     ByteArrayInputStream is = new ByteArrayInputStream(data);
/*  34 */     List<PolicyDescriptor> descriptors = new ArrayList<>();
/*  35 */     while (is.available() > 0) {
/*     */       try {
/*  37 */         String name = readString(is);
/*  38 */         String className = readString(is);
/*  39 */         readString(is);
/*  40 */         String installationInstructions = readString(is);
/*  41 */         boolean enabled = readBoolean(is);
/*  42 */         int length = readInt32(is);
/*  43 */         readBytes(is, length);
/*     */         
/*  45 */         PolicyType policyType = new PolicyType(className, name, "", installationInstructions);
/*  46 */         descriptors.add(new PolicyDescriptor(policyType, enabled));
/*     */       }
/*  48 */       catch (IOException e) {
/*  49 */         throw new PolicyParseException("Unexpected end of data stream");
/*     */       } 
/*     */     } 
/*  52 */     return descriptors;
/*     */   }
/*     */   
/*     */   private static boolean readBoolean(InputStream is) throws PolicyParseException, IOException {
/*  56 */     int i = is.read();
/*  57 */     if (i == -1) {
/*  58 */       throw new PolicyParseException("Unexpected end of data stream");
/*     */     }
/*  60 */     return (i != 0);
/*     */   }
/*     */   
/*     */   private static String readString(InputStream is) throws PolicyParseException, IOException {
/*  64 */     int length = read7BitEncodedInt(is);
/*  65 */     byte[] buf = readBytes(is, length);
/*  66 */     return new String(buf, 0, length, StandardCharsets.UTF_8);
/*     */   }
/*     */   
/*     */   private static byte[] readBytes(InputStream is, int len) throws PolicyParseException, IOException {
/*  70 */     byte[] buf = new byte[len];
/*  71 */     if (is.read(buf, 0, len) != len) {
/*  72 */       throw new PolicyParseException("Unexpected end of data stream");
/*     */     }
/*     */     
/*  75 */     return buf;
/*     */   }
/*     */ 
/*     */   
/*     */   private static int read7BitEncodedInt(InputStream is) throws PolicyParseException, IOException {
/*  80 */     int num3, num = 0;
/*  81 */     int num2 = 0;
/*     */     do {
/*  83 */       if (num2 == 35) {
/*  84 */         throw new PolicyParseException("Unexpected end of data stream");
/*     */       }
/*  86 */       num3 = is.read();
/*  87 */       if (num3 == -1) {
/*  88 */         throw new PolicyParseException("Unexpected end of data stream");
/*     */       }
/*  90 */       num |= (num3 & 0x7F) << num2;
/*  91 */       num2 += 7;
/*     */     }
/*  93 */     while ((num3 & 0x80) != 0);
/*  94 */     return num;
/*     */   }
/*     */   
/*     */   private static int readInt32(InputStream is) throws PolicyParseException, IOException {
/*  98 */     byte[] buf = new byte[4];
/*  99 */     if (is.read(buf, 0, 4) != 4) {
/* 100 */       throw new PolicyParseException("Unexpected end of data stream");
/*     */     }
/*     */     
/* 103 */     int i = buf[0] & 0xFF;
/* 104 */     i |= (buf[1] & 0xFF) << 8;
/* 105 */     i |= (buf[2] & 0xFF) << 16;
/* 106 */     i |= (buf[3] & 0xFF) << 24;
/*     */     
/* 108 */     return i;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\StatelessPolicyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */