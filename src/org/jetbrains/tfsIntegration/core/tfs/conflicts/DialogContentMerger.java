/*    */ package org.jetbrains.tfsIntegration.core.tfs.conflicts;
/*    */ 
/*    */ import com.intellij.diff.DiffManager;
/*    */ import com.intellij.diff.DiffRequestFactory;
/*    */ import com.intellij.diff.InvalidDiffRequestException;
/*    */ import com.intellij.diff.merge.MergeRequest;
/*    */ import com.intellij.diff.merge.MergeResult;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.util.Ref;
/*    */ import com.intellij.openapi.vcs.VcsException;
/*    */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*    */ import com.intellij.openapi.vcs.merge.MergeDialogCustomizer;
/*    */ import com.intellij.openapi.vfs.VirtualFile;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ import org.jetbrains.tfsIntegration.core.TFSVcs;
/*    */ import org.jetbrains.tfsIntegration.ui.ContentTriplet;
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
/*    */ public class DialogContentMerger
/*    */   implements ContentMerger
/*    */ {
/*    */   public boolean mergeContent(Conflict conflict, ContentTriplet contentTriplet, Project project, VirtualFile localFile, String localPath, VcsRevisionNumber serverVersion) throws VcsException {
/* 40 */     TFSVcs.assertTrue(localFile.isWritable(), localFile.getPresentableUrl() + " must be writable");
/*    */     
/* 42 */     List<byte[]> contents = (List)Arrays.asList(new byte[][] { contentTriplet.localContent, contentTriplet.baseContent, contentTriplet.serverContent });
/*    */     
/* 44 */     MergeDialogCustomizer c = new MergeDialogCustomizer();
/* 45 */     String title = c.getMergeWindowTitle(localFile);
/*    */     
/* 47 */     List<String> contentTitles = Arrays.asList(new String[] { c.getLeftPanelTitle(localFile), c.getCenterPanelTitle(localFile), c.getRightPanelTitle(localFile, serverVersion) });
/*    */ 
/*    */     
/*    */     try {
/* 51 */       Ref<MergeResult> resultRef = new Ref(MergeResult.CANCEL);
/* 52 */       MergeRequest request = DiffRequestFactory.getInstance().createMergeRequest(project, localFile, contents, title, contentTitles, mergeResult -> 
/* 53 */           resultRef.set(mergeResult));
/* 54 */       DiffManager.getInstance().showMerge(project, request);
/* 55 */       return (resultRef.get() != MergeResult.CANCEL);
/*    */     }
/* 57 */     catch (InvalidDiffRequestException e) {
/* 58 */       throw new VcsException((Throwable)e);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\conflicts\DialogContentMerger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */