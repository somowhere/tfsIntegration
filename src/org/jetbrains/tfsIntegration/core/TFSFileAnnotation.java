/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.vcs.AbstractVcsHelper;
/*     */ import com.intellij.openapi.vcs.VcsKey;
/*     */ import com.intellij.openapi.vcs.annotate.FileAnnotation;
/*     */ import com.intellij.openapi.vcs.annotate.LineAnnotationAspect;
/*     */ import com.intellij.openapi.vcs.annotate.LineAnnotationAspectAdapter;
/*     */ import com.intellij.openapi.vcs.history.VcsFileRevision;
/*     */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*     */ import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.ui.GuiUtils;
/*     */ import com.intellij.util.text.DateFormatUtil;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TFSFileAnnotation
/*     */   extends FileAnnotation
/*     */ {
/*     */   private final TFSVcs myVcs;
/*     */   private final WorkspaceInfo myWorkspace;
/*     */   private final String myAnnotatedContent;
/*     */   private final VcsFileRevision[] myLineRevisions;
/*     */   private final VirtualFile myFile;
/*     */   
/*  47 */   private final LineAnnotationAspect REVISION_ASPECT = (LineAnnotationAspect)new TFSAnnotationAspect(TFSAnnotationAspect.REVISION, false)
/*     */     {
/*     */       public String getValue(int lineNumber) {
/*  50 */         VcsFileRevision fileRevision = TFSFileAnnotation.this.getLineRevision(lineNumber);
/*  51 */         if (fileRevision == null) return "";
/*     */         
/*  53 */         return ((TfsRevisionNumber)fileRevision.getRevisionNumber()).getChangesetString();
/*     */       }
/*     */     };
/*     */   
/*  57 */   private final LineAnnotationAspect DATE_ASPECT = (LineAnnotationAspect)new TFSAnnotationAspect(TFSAnnotationAspect.DATE, true)
/*     */     {
/*     */       public String getValue(int lineNumber) {
/*  60 */         VcsFileRevision fileRevision = TFSFileAnnotation.this.getLineRevision(lineNumber);
/*  61 */         if (fileRevision == null) return "";
/*     */         
/*  63 */         return DateFormatUtil.formatPrettyDate(fileRevision.getRevisionDate());
/*     */       }
/*     */     };
/*     */   
/*  67 */   private final LineAnnotationAspect AUTHOR_ASPECT = (LineAnnotationAspect)new TFSAnnotationAspect(TFSAnnotationAspect.AUTHOR, true)
/*     */     {
/*     */       public String getValue(int lineNumber) {
/*  70 */         VcsFileRevision fileRevision = TFSFileAnnotation.this.getLineRevision(lineNumber);
/*  71 */         if (fileRevision == null) return "";
/*     */         
/*  73 */         return TfsUtil.getNameWithoutDomain(fileRevision.getAuthor());
/*     */       }
/*     */     };
/*     */   
/*  77 */   private final TFSVcs.RevisionChangedListener myListener = new TFSVcs.RevisionChangedListener()
/*     */     {
/*     */       public void revisionChanged() {
/*     */         try {
/*  81 */           GuiUtils.runOrInvokeAndWait(() -> TFSFileAnnotation.this.close());
/*     */         }
/*  83 */         catch (InvocationTargetException invocationTargetException) {
/*     */ 
/*     */         
/*  86 */         } catch (InterruptedException interruptedException) {}
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public TFSFileAnnotation(TFSVcs vcs, WorkspaceInfo workspace, String annotatedContent, VcsFileRevision[] lineRevisions, VirtualFile file) {
/*  96 */     super(vcs.getProject());
/*  97 */     this.myVcs = vcs;
/*  98 */     this.myWorkspace = workspace;
/*  99 */     this.myAnnotatedContent = annotatedContent;
/* 100 */     this.myLineRevisions = lineRevisions;
/* 101 */     this.myFile = file;
/* 102 */     this.myVcs.addRevisionChangedListener(this.myListener);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 107 */   public void dispose() { this.myVcs.removeRevisionChangedListener(this.myListener); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   public String getAnnotatedContent() { return this.myAnnotatedContent; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 117 */   public LineAnnotationAspect[] getAspects() { return new LineAnnotationAspect[] { this.REVISION_ASPECT, this.DATE_ASPECT, this.AUTHOR_ASPECT }; }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private VcsFileRevision getLineRevision(int lineNumber) {
/* 122 */     if (lineNumber < 0 || lineNumber >= this.myLineRevisions.length) return null; 
/* 123 */     return this.myLineRevisions[lineNumber];
/*     */   }
/*     */ 
/*     */   
/*     */   public String getToolTip(int lineNumber) {
/* 128 */     VcsFileRevision fileRevision = getLineRevision(lineNumber);
/* 129 */     if (fileRevision == null) return "";
/*     */     
/* 131 */     String commitMessage = (fileRevision.getCommitMessage() == null) ? "(no comment)" : fileRevision.getCommitMessage();
/* 132 */     return MessageFormat.format("Changeset {0}: {1}", new Object[] { ((TfsRevisionNumber)fileRevision
/* 133 */           .getRevisionNumber()).getChangesetString(), commitMessage });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public VcsRevisionNumber getLineRevisionNumber(int lineNumber) {
/* 139 */     VcsFileRevision fileRevision = getLineRevision(lineNumber);
/* 140 */     if (fileRevision == null) return null;
/*     */     
/* 142 */     return fileRevision.getRevisionNumber();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getLineDate(int lineNumber) {
/* 147 */     VcsFileRevision fileRevision = getLineRevision(lineNumber);
/* 148 */     if (fileRevision == null) return null;
/*     */     
/* 150 */     return fileRevision.getRevisionDate();
/*     */   }
/*     */ 
/*     */   
/*     */   public List<VcsFileRevision> getRevisions() {
/* 155 */     Set<VcsFileRevision> set = new HashSet<>(Arrays.asList(this.myLineRevisions));
/* 156 */     List<VcsFileRevision> result = new ArrayList<>(set);
/* 157 */     Collections.sort(result, REVISION_COMPARATOR);
/* 158 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 163 */   public int getLineCount() { return this.myLineRevisions.length; }
/*     */ 
/*     */ 
/*     */   
/* 167 */   private static final Comparator<VcsFileRevision> REVISION_COMPARATOR = (revision1, revision2) -> -1 * revision1.getRevisionNumber().compareTo(revision2.getRevisionNumber());
/*     */   
/*     */   private abstract class TFSAnnotationAspect
/*     */     extends LineAnnotationAspectAdapter {
/* 171 */     TFSAnnotationAspect(String id, boolean showByDefault) { super(id, showByDefault); }
/*     */ 
/*     */ 
/*     */     
/*     */     protected void showAffectedPaths(int lineNum) {
/* 176 */       VcsFileRevision revision = TFSFileAnnotation.this.getLineRevision(lineNum);
/* 177 */       if (revision == null)
/*     */         return; 
/* 179 */       int changeset = ((VcsRevisionNumber.Int)revision.getRevisionNumber()).getValue();
/*     */       
/* 181 */       CommittedChangeList changeList = new TFSChangeList(TFSFileAnnotation.this.myWorkspace, changeset, revision.getAuthor(), revision.getRevisionDate(), revision.getCommitMessage(), TFSFileAnnotation.this.myVcs);
/* 182 */       String changesetString = ((TfsRevisionNumber)revision.getRevisionNumber()).getChangesetString();
/* 183 */       String progress = MessageFormat.format("Loading changeset {0}...", new Object[] { changesetString });
/* 184 */       ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> changeList.getChanges(), progress, false, TFSFileAnnotation.this.myVcs.getProject());
/* 185 */       String title = MessageFormat.format("Changeset {0}", new Object[] { changesetString });
/* 186 */       AbstractVcsHelper.getInstance(TFSFileAnnotation.this.myVcs.getProject()).showChangesListBrowser(changeList, title);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 193 */   public VcsRevisionNumber getCurrentRevision() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 198 */   public VcsKey getVcsKey() { return TFSVcs.getKey(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 203 */   public VirtualFile getFile() { return this.myFile; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSFileAnnotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */