/*     */ package org.jetbrains.tfsIntegration.checkin;
/*     */ 
/*     */ import java.util.List;
/*     */ import org.jdom.Element;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class XMLMemento
/*     */   implements Memento
/*     */ {
/*     */   private Element myElement;
/*     */   
/*  15 */   public XMLMemento(Element element) { this.myElement = element; }
/*     */ 
/*     */ 
/*     */   
/*     */   public Memento createChild(String nodeName) {
/*  20 */     Element element = new Element(nodeName);
/*  21 */     this.myElement.addContent(element);
/*  22 */     return new XMLMemento(element);
/*     */   }
/*     */ 
/*     */   
/*     */   public Memento copyChild(Memento child) {
/*  27 */     Element element = ((XMLMemento)child).myElement.clone();
/*  28 */     this.myElement.addContent(element);
/*  29 */     return new XMLMemento(element);
/*     */   }
/*     */ 
/*     */   
/*     */   public Memento getChild(String nodeName) {
/*  34 */     Element element = this.myElement.getChild(nodeName);
/*  35 */     return (element != null) ? new XMLMemento(element) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Memento[] getChildren(String nodeName) {
/*  40 */     List<Element> elements = this.myElement.getChildren(nodeName);
/*  41 */     Memento[] result = new Memento[elements.size()];
/*  42 */     for (int i = 0; i < elements.size(); i++) {
/*  43 */       result[i] = new XMLMemento(elements.get(i));
/*     */     }
/*     */     
/*  46 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  51 */   public String getName() { return this.myElement.getName(); }
/*     */ 
/*     */ 
/*     */   
/*     */   public Double getDouble(String key) {
/*  56 */     String s = this.myElement.getAttributeValue(key);
/*  57 */     if (s != null) {
/*     */       try {
/*  59 */         return new Double(s);
/*     */       }
/*  61 */       catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/*  65 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Float getFloat(String key) {
/*  70 */     String s = this.myElement.getAttributeValue(key);
/*  71 */     if (s != null) {
/*     */       try {
/*  73 */         return new Float(s);
/*     */       }
/*  75 */       catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/*  79 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Integer getInteger(String key) {
/*  84 */     String s = this.myElement.getAttributeValue(key);
/*  85 */     if (s != null) {
/*     */       try {
/*  87 */         return new Integer(s);
/*     */       }
/*  89 */       catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/*  93 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getLong(String key) {
/*  98 */     String s = this.myElement.getAttributeValue(key);
/*  99 */     if (s != null) {
/*     */       try {
/* 101 */         return new Long(s);
/*     */       }
/* 103 */       catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */     
/* 107 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 112 */   public String getString(String key) { return this.myElement.getAttributeValue(key); }
/*     */ 
/*     */ 
/*     */   
/*     */   public Boolean getBoolean(String key) {
/* 117 */     String s = this.myElement.getAttributeValue(key);
/* 118 */     if (s != null) {
/* 119 */       return Boolean.valueOf(s);
/*     */     }
/* 121 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTextData() {
/* 126 */     String text = this.myElement.getText();
/* 127 */     if (text != null && text.length() == 0) {
/* 128 */       text = null;
/*     */     }
/* 130 */     return text;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 135 */   public void putDouble(String key, double value) { this.myElement.setAttribute(key, String.valueOf(value)); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 140 */   public void putFloat(String key, float value) { this.myElement.setAttribute(key, String.valueOf(value)); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 145 */   public void putInteger(String key, int value) { this.myElement.setAttribute(key, String.valueOf(value)); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 150 */   public void putLong(String key, long value) { this.myElement.setAttribute(key, String.valueOf(value)); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 155 */   public void putMemento(Memento memento) { this.myElement = ((XMLMemento)memento).myElement.clone(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 160 */   public void putString(String key, String value) { this.myElement.setAttribute(key, value); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 165 */   public void putBoolean(String key, boolean value) { this.myElement.setAttribute(key, String.valueOf(value)); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 170 */   public void putTextData(String data) { this.myElement.setText(data); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 175 */   public Element getElement() { return this.myElement; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\XMLMemento.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */