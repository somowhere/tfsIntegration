package org.jetbrains.tfsIntegration.checkin;

public interface Memento {
  Memento createChild(String paramString);
  
  Memento copyChild(Memento paramMemento);
  
  Memento getChild(String paramString);
  
  Memento[] getChildren(String paramString);
  
  String getName();
  
  Double getDouble(String paramString);
  
  Float getFloat(String paramString);
  
  Integer getInteger(String paramString);
  
  Long getLong(String paramString);
  
  String getString(String paramString);
  
  Boolean getBoolean(String paramString);
  
  String getTextData();
  
  void putDouble(String paramString, double paramDouble);
  
  void putFloat(String paramString, float paramFloat);
  
  void putInteger(String paramString, int paramInt);
  
  void putLong(String paramString, long paramLong);
  
  void putMemento(Memento paramMemento);
  
  void putString(String paramString1, String paramString2);
  
  void putBoolean(String paramString, boolean paramBoolean);
  
  void putTextData(String paramString);
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\Memento.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */