/*    */ package org.jetbrains.tfsIntegration.checkin;
/*    */ 
/*    */ import org.jdom.Element;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class MementoStoredPolicyBase
/*    */   extends PolicyBase
/*    */ {
/*    */   protected abstract void loadConfiguration(Memento paramMemento);
/*    */   
/*    */   protected abstract void saveConfiguration(Memento paramMemento);
/*    */   
/* 29 */   public final void loadState(@NotNull Element element) {  loadConfiguration(new XMLMemento(element)); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 34 */   public final void saveState(@NotNull Element element) { saveConfiguration(new XMLMemento(element)); }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\MementoStoredPolicyBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */