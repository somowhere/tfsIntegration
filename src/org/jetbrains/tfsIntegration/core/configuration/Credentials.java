/*     */ package org.jetbrains.tfsIntegration.core.configuration;
/*     */ 
/*     */ import com.intellij.openapi.util.PasswordUtil;
/*     */ import com.intellij.util.xmlb.annotations.Attribute;
/*     */ import com.intellij.util.xmlb.annotations.Tag;
/*     */ import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
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
/*     */ @Tag("credentials")
/*     */ public class Credentials
/*     */ {
/*     */   @NotNull
/*     */   private String myUserName;
/*     */   @NotNull
/*     */   private String myDomain;
/*     */   @Nullable
/*     */   private String myPassword;
/*     */   private boolean myStorePassword;
/*     */   private Type myType;
/*     */   
/*     */   private enum UseNative
/*     */   {
/*  37 */     Yes, No, Reset;
/*     */   }
/*     */   
/*  40 */   public static Credentials createNative() { return new Credentials("", "", "", true, Type.NtlmNative); }
/*     */   
/*     */   public enum Type {
/*  43 */     NtlmExplicit, NtlmNative, Alternate;
/*     */ 
/*     */     
/*  46 */     public String getPresentableText() { return TFSBundle.message("credentials.type." + name(), new Object[0]); }
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
/*     */ 
/*     */   
/*  61 */   public Credentials() { this("", "", null, false, Type.NtlmExplicit); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Credentials(@NotNull String userName, @NotNull String domain, @Nullable String password, boolean storePassword, Type type) {
/*  69 */     this.myUserName = userName;
/*  70 */     this.myDomain = domain;
/*  71 */     this.myPassword = password;
/*  72 */     this.myStorePassword = storePassword;
/*  73 */     this.myType = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Credentials(@NotNull String credentials, @Nullable String password, boolean storePassword, Type type) {
/*  80 */     int i = credentials.indexOf('\\');
/*  81 */     this.myDomain = (i != -1) ? credentials.substring(0, i) : "";
/*  82 */     this.myUserName = (i != -1) ? credentials.substring(i + 1) : credentials;
/*  83 */     this.myPassword = password;
/*  84 */     this.myStorePassword = storePassword;
/*  85 */     this.myType = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  93 */   public String getPassword() { return this.myPassword; }
/*     */ 
/*     */   
/*     */   public void resetPassword() {
/*  97 */     this.myPassword = null;
/*  98 */     this.myStorePassword = false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Tag("password")
/*     */   @Nullable
/* 104 */   public String getEncodedPassword() { return (this.myStorePassword && this.myPassword != null) ? PasswordUtil.encodePassword(this.myPassword) : null; }
/*     */ 
/*     */   
/*     */   public void setEncodedPassword(String encodedPassword) {
/* 108 */     this.myPassword = (encodedPassword != null) ? PasswordUtil.decodePassword(encodedPassword) : null;
/* 109 */     this.myStorePassword = true;
/*     */   }
/*     */ 
/*     */   
/* 113 */   public boolean isStorePassword() { return this.myStorePassword; }
/*     */ 
/*     */ 
/*     */   
/*     */   @Tag("domain")
/*     */   @NotNull
/* 119 */   public String getDomain() {    return this.myDomain; }
/*     */ 
/*     */ 
/*     */   
/* 123 */   public void setDomain(@NotNull String domain) {    this.myDomain = domain; }
/*     */ 
/*     */ 
/*     */   
/*     */   @Tag("username")
/*     */   @NotNull
/* 129 */   public String getUserName() {    return this.myUserName; }
/*     */ 
/*     */ 
/*     */   
/* 133 */   public void setUserName(@NotNull String userName) {    this.myUserName = userName; }
/*     */ 
/*     */ 
/*     */   
/* 137 */   public Type getType() { return this.myType; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Attribute("nativeAuth")
/* 143 */   public String getUseNativeSerialized() { return null; }
/*     */ 
/*     */   
/*     */   public void setUseNativeSerialized(String useNative) {
/* 147 */     if (UseNative.Yes.name().equals(useNative)) {
/* 148 */       this.myType = Type.NtlmNative;
/* 149 */       this.myPassword = "";
/*     */     }
/* 151 */     else if (UseNative.Reset.name().equals(useNative)) {
/* 152 */       this.myType = Type.NtlmNative;
/* 153 */       this.myPassword = null;
/*     */     } else {
/*     */       
/* 156 */       this.myType = Type.NtlmExplicit;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Attribute("type")
/* 162 */   public String getTypeSerialized() { return this.myType.name(); }
/*     */ 
/*     */   
/*     */   public void setTypeSerialized(String s) {
/*     */     try {
/* 167 */       this.myType = Type.valueOf(s);
/*     */     }
/* 169 */     catch (IllegalArgumentException e) {
/* 170 */       this.myType = Type.NtlmExplicit;
/*     */     } 
/*     */   }
/*     */   
/*     */   @NotNull
/*     */   public String getQualifiedUsername() {
/* 176 */     if (getDomain().length() > 0) {
/* 177 */          return TfsUtil.getQualifiedUsername(getDomain(), getUserName());
/*     */     } 
/*     */     
/* 180 */          return getUserName();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NonNls
/*     */   public String toString() {
/* 187 */     return this.myType.name() + ": " + 
/*     */       
/* 189 */       getQualifiedUsername() + "," + (
/*     */       
/* 191 */       (getPassword() != null) ? getPassword().replaceAll(".", "x") : "(no password)");
/*     */   }
/*     */ 
/*     */   
/* 195 */   public boolean shouldShowLoginDialog() { return (this.myPassword == null); }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\configuration\Credentials.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */