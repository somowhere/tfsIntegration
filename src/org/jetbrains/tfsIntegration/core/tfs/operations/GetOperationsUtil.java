/*    */ package org.jetbrains.tfsIntegration.core.tfs.operations;
/*    */ 
/*    */ import com.intellij.openapi.util.io.FileUtil;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
/*    */ import java.io.File;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Collection;
/*    */ import java.util.List;
/*    */ import org.jetbrains.tfsIntegration.core.TFSVcs;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
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
/*    */ public class GetOperationsUtil
/*    */ {
/*    */   static List<GetOperation> sortGetOperations(Collection<? extends GetOperation> getOperations) {
/* 31 */     List<GetOperation> result = new ArrayList<>(getOperations.size());
/* 32 */     for (GetOperation newOperation : getOperations) {
/* 33 */       TFSVcs.assertTrue((newOperation.getSlocal() != null || newOperation.getTlocal() != null));
/* 34 */       int positionToInsert = result.size();
/* 35 */       if (newOperation.getSlocal() != null) {
/* 36 */         File newOpPath = VersionControlPath.getFile(newOperation.getSlocal());
/* 37 */         for (int i = 0; i < result.size(); i++) {
/* 38 */           GetOperation existingOperation = result.get(i);
/* 39 */           if (existingOperation.getSlocal() == null || 
/* 40 */             FileUtil.isAncestor(newOpPath, VersionControlPath.getFile(existingOperation.getSlocal()), false)) {
/* 41 */             positionToInsert = i;
/*    */             break;
/*    */           } 
/*    */         } 
/*    */       } 
/* 46 */       result.add(positionToInsert, newOperation);
/*    */     } 
/* 48 */     return result;
/*    */   }
/*    */ 
/*    */   
/*    */   public static void updateSourcePaths(List<? extends GetOperation> sortedOperations, int index, GetOperation operation) {
/* 53 */     for (GetOperation operationToUpdate : sortedOperations.subList(index + 1, sortedOperations.size())) {
/* 54 */       if (operationToUpdate.getSlocal() != null) {
/* 55 */         String updated = operationToUpdate.getSlocal().replace(operation.getSlocal(), operation.getTlocal());
/* 56 */         operationToUpdate.setSlocal(updated);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\operations\GetOperationsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */