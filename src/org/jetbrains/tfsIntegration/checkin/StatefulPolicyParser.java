/*     */ package org.jetbrains.tfsIntegration.checkin;
/*     */ 
/*     */ import com.intellij.openapi.util.JDOMUtil;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.jdom.Document;
/*     */ import org.jdom.Element;
/*     */ import org.jdom.JDOMException;
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
/*     */ public class StatefulPolicyParser
/*     */ {
/*     */   private static final String VERSION = "version";
/*     */   private static final String POLICY_ANNOTATION = "policy-annotation";
/*     */   private static final String CURRENT_VERSION = "1";
/*     */   private static final String POLICY_DEFINITION = "policy-definition";
/*     */   private static final String ENABLED = "enabled";
/*     */   private static final String PRIORITY = "priority";
/*     */   private static final String POLICY_TYPE = "policy-type";
/*     */   private static final String ID = "id";
/*     */   private static final String NAME = "name";
/*     */   private static final String SHORT_DESCRIPTION = "short-description";
/*     */   private static final String LONG_DESCRIPTION = "long-description";
/*     */   private static final String INSTALLATION_INSTRUCTIONS = "installation-instructions";
/*     */   private static final String CONFIGURATION_DATA = "configuration-data";
/*     */   private static final String SCOPE = "scope";
/*     */   
/*     */   public static List<StatefulPolicyDescriptor> parseDescriptors(String input) throws PolicyParseException {
/*     */     Element document;
/*     */     try {
/*  49 */       document = JDOMUtil.load(input);
/*     */     }
/*  51 */     catch (IOException e) {
/*  52 */       throw new PolicyParseException(e);
/*     */     }
/*  54 */     catch (JDOMException e) {
/*  55 */       throw new PolicyParseException((Throwable)e);
/*     */     } 
/*  57 */     if (!"policy-annotation".equals(document.getName())) {
/*  58 */       throw new PolicyParseException("Element expected: policy-annotation");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  65 */     List<StatefulPolicyDescriptor> result = new ArrayList<>();
/*  66 */     for (Object o : document.getChildren("policy-definition")) {
/*  67 */       Element definitionElement = (Element)o;
/*     */ 
/*     */ 
/*     */       
/*  71 */       String enabled = definitionElement.getAttributeValue("enabled");
/*  72 */       checkNotNull(enabled, "enabled");
/*  73 */       String priority = definitionElement.getAttributeValue("priority");
/*  74 */       checkNotNull(priority, "priority");
/*  75 */       Element typeElement = definitionElement.getChild("policy-type");
/*  76 */       checkNotNull(typeElement, "policy-type");
/*  77 */       String id = typeElement.getAttributeValue("id");
/*  78 */       checkNotNull(id, "id");
/*  79 */       String name = typeElement.getAttributeValue("name");
/*  80 */       checkNotNull(name, "name");
/*  81 */       String shortDescription = typeElement.getAttributeValue("short-description");
/*  82 */       String longDescription = typeElement.getAttributeValue("long-description");
/*  83 */       String installationInstructions = typeElement.getAttributeValue("installation-instructions");
/*  84 */       PolicyType type = new PolicyType(id, name, shortDescription, installationInstructions);
/*  85 */       Element configurationElement = definitionElement.getChild("configuration-data");
/*  86 */       checkNotNull(configurationElement, "configuration-data");
/*  87 */       List<String> scope = new ArrayList<>();
/*  88 */       for (Object o2 : definitionElement.getChildren("scope")) {
/*  89 */         Element scopeElement = (Element)o2;
/*  90 */         scope.add(scopeElement.getText());
/*     */       } 
/*  92 */       result.add(new StatefulPolicyDescriptor(type, Boolean.parseBoolean(enabled), configurationElement, scope, priority, longDescription));
/*     */     } 
/*  94 */     return result;
/*     */   }
/*     */ 
/*     */   
/*  98 */   public static Element createEmptyConfiguration() { return new Element("configuration-data"); }
/*     */ 
/*     */   
/*     */   public static String saveDescriptors(List<? extends StatefulPolicyDescriptor> value) {
/* 102 */     Element root = new Element("policy-annotation");
/* 103 */     root.setAttribute("version", "1");
/* 104 */     for (StatefulPolicyDescriptor descriptor : value) {
/* 105 */       Element descriptorElement = new Element("policy-definition");
/* 106 */       root.addContent(descriptorElement);
/*     */       
/* 108 */       descriptorElement.setAttribute("enabled", String.valueOf(descriptor.isEnabled()));
/* 109 */       descriptorElement.setAttribute("priority", descriptor.getPriority());
/* 110 */       descriptorElement.setAttribute("version", "1");
/*     */       
/* 112 */       for (String scope : descriptor.getScope()) {
/* 113 */         Element scopeElement = new Element("scope");
/* 114 */         scopeElement.setText(scope);
/* 115 */         descriptorElement.addContent(scopeElement);
/*     */       } 
/*     */       
/* 118 */       Element typeElement = new Element("policy-type");
/* 119 */       typeElement.setAttribute("id", descriptor.getType().getId());
/* 120 */       typeElement.setAttribute("installation-instructions", descriptor.getType().getInstallationInstructions());
/* 121 */       typeElement.setAttribute("long-description", descriptor.getLongDescription());
/* 122 */       typeElement.setAttribute("name", descriptor.getType().getName());
/* 123 */       typeElement.setAttribute("short-description", descriptor.getType().getDescription());
/* 124 */       descriptorElement.addContent(typeElement);
/*     */       
/* 126 */       descriptorElement.addContent(descriptor.getConfiguration().clone());
/*     */     } 
/* 128 */     Document document = new Document(root);
/* 129 */     return JDOMUtil.writeDocument(document, "");
/*     */   }
/*     */   
/*     */   private static void checkNotNull(Element element, String expectedElementName) throws PolicyParseException {
/* 133 */     if (element == null) {
/* 134 */       throw new PolicyParseException("Element expected: " + expectedElementName);
/*     */     }
/*     */   }
/*     */   
/*     */   private static void checkNotNull(String value, String expectedElementName) throws PolicyParseException {
/* 139 */     if (value == null)
/* 140 */       throw new PolicyParseException("Attribute expected: " + expectedElementName); 
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\StatefulPolicyParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */