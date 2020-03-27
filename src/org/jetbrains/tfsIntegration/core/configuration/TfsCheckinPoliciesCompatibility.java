/*    */ package org.jetbrains.tfsIntegration.core.configuration;
/*    */ 
/*    */ import com.intellij.openapi.util.JDOMUtil;
/*    */ import java.io.IOException;
/*    */ import org.jdom.Document;
/*    */ import org.jdom.Element;
/*    */ import org.jdom.JDOMException;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ 
/*    */ public class TfsCheckinPoliciesCompatibility {
/*    */   @NonNls
/*    */   private static final String ENFORCE_TEAM_EXPLORER_EVALUATION_ATTR = "enforceTfs";
/*    */   @NonNls
/*    */   private static final String ENFORCE_TEAMPRISE_EVALUATION_ATTR = "enforceTeamprise";
/*    */   @NonNls
/*    */   private static final String ENFORCE_NOT_INSTALLED_ATTR = "enforceNotInstalled";
/*    */   public boolean teamprise;
/*    */   public boolean teamExplorer;
/*    */   public boolean nonInstalled;
/*    */   
/*    */   public TfsCheckinPoliciesCompatibility(boolean teamprise, boolean teamExplorer, boolean nonInstalled) {
/* 22 */     this.teamprise = teamprise;
/* 23 */     this.teamExplorer = teamExplorer;
/* 24 */     this.nonInstalled = nonInstalled;
/*    */   }
/*    */   
/*    */   public static TfsCheckinPoliciesCompatibility fromOverridesAnnotationValue(String s) throws JDOMException, IOException {
/* 28 */     Element doc = JDOMUtil.load(s);
/* 29 */     boolean enforceTeamprise = Boolean.parseBoolean(doc.getAttributeValue("enforceTeamprise"));
/* 30 */     boolean enforceTeamExplorer = Boolean.parseBoolean(doc.getAttributeValue("enforceTfs"));
/* 31 */     boolean enforceNonInstalledWarning = Boolean.parseBoolean(doc.getAttributeValue("enforceNotInstalled"));
/* 32 */     return new TfsCheckinPoliciesCompatibility(enforceTeamprise, enforceTeamExplorer, enforceNonInstalledWarning);
/*    */   }
/*    */   
/*    */   public String toOverridesAnnotationValue() {
/* 36 */     Document doc = new Document();
/* 37 */     Element element = new Element("IntellijOverrides");
/* 38 */     doc.setRootElement(element);
/* 39 */     element.setAttribute("enforceTeamprise", Boolean.toString(this.teamprise));
/* 40 */     element.setAttribute("enforceTfs", Boolean.toString(this.teamExplorer));
/* 41 */     element.setAttribute("enforceNotInstalled", Boolean.toString(this.nonInstalled));
/* 42 */     return JDOMUtil.writeDocument(doc, "");
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\configuration\TfsCheckinPoliciesCompatibility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */