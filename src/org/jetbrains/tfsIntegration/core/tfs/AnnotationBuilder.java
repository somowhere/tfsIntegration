/*     */ package org.jetbrains.tfsIntegration.core.tfs;
/*     */ 
/*     */ import com.intellij.openapi.util.io.StreamUtil;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vcs.history.VcsFileRevision;
/*     */ import com.intellij.util.diff.Diff;
/*     */ import com.intellij.util.diff.FilesTooBigForDiffException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import org.jetbrains.tfsIntegration.core.TFSFileRevision;
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
/*     */ public class AnnotationBuilder
/*     */ {
/*     */   private final String myAnnotatedContent;
/*     */   private final VcsFileRevision[] myLineRevisions;
/*     */   private final List<Integer> myLineNumbers;
/*     */   
/*     */   public AnnotationBuilder(List<TFSFileRevision> revisions, ContentProvider contentProvider) throws VcsException {
/*  52 */     if (revisions == null || revisions.size() < 1) {
/*  53 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/*  56 */     Iterator<TFSFileRevision> iterator = revisions.iterator();
/*  57 */     TFSFileRevision revision = iterator.next();
/*  58 */     this.myAnnotatedContent = contentProvider.getContent(revision);
/*  59 */     String[] lines = splitLines(this.myAnnotatedContent);
/*     */     
/*  61 */     this.myLineRevisions = new VcsFileRevision[lines.length];
/*  62 */     this.myLineNumbers = new ArrayList<>(lines.length);
/*  63 */     for (int i = 0; i < lines.length; i++) {
/*  64 */       this.myLineNumbers.add(Integer.valueOf(i));
/*     */     }
/*     */     
/*  67 */     while (iterator.hasNext()) {
/*  68 */       Diff.Change change; TFSFileRevision previousRevision = iterator.next();
/*  69 */       String previousContent = contentProvider.getContent(previousRevision);
/*  70 */       String[] previousLines = splitLines(previousContent);
/*     */       
/*     */       try {
/*  73 */         change = Diff.buildChanges((Object[])previousLines, (Object[])lines);
/*     */       }
/*  75 */       catch (FilesTooBigForDiffException e) {
/*  76 */         throw new VcsException((Throwable)e);
/*     */       } 
/*     */       
/*  79 */       annotateAll(change, (VcsFileRevision)revision);
/*  80 */       if (allLinesAnnotated()) {
/*     */         break;
/*     */       }
/*  83 */       lines = previousLines;
/*  84 */       revision = previousRevision;
/*     */     } 
/*     */     
/*  87 */     fillAllNotAnnotated((VcsFileRevision)revisions.get(revisions.size() - 1));
/*     */   }
/*     */   
/*     */   private void annotateAll(Diff.Change changesList, VcsFileRevision revision) {
/*  91 */     Diff.Change change = changesList;
/*  92 */     while (change != null) {
/*  93 */       annotate(change, revision);
/*  94 */       change = change.link;
/*     */     } 
/*  96 */     recalculateLineNumbers(changesList);
/*     */   }
/*     */   
/*     */   private void annotate(Diff.Change change, VcsFileRevision revision) {
/* 100 */     if (change.inserted > 0) {
/* 101 */       for (int line = change.line1; line < change.line1 + change.inserted; line++) {
/* 102 */         Integer origLine = this.myLineNumbers.get(line);
/* 103 */         if (origLine != null && 
/* 104 */           this.myLineRevisions[origLine.intValue()] == null) {
/* 105 */           this.myLineRevisions[origLine.intValue()] = revision;
/*     */         }
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void recalculateLineNumbers(Diff.Change changesList) {
/* 113 */     Diff.Change change = changesList;
/* 114 */     int removedLinesCount = 0;
/* 115 */     while (change != null) {
/* 116 */       for (int i = 0; i < change.inserted; i++) {
/* 117 */         this.myLineNumbers.remove(change.line1 - removedLinesCount);
/*     */       }
/* 119 */       removedLinesCount += change.inserted;
/* 120 */       change = change.link;
/*     */     } 
/*     */     
/* 123 */     change = changesList;
/* 124 */     while (change != null) {
/* 125 */       for (int i = 0; i < change.deleted; i++) {
/* 126 */         this.myLineNumbers.add(change.line0, null);
/*     */       }
/* 128 */       change = change.link;
/*     */     } 
/*     */   }
/*     */   
/*     */   private boolean allLinesAnnotated() {
/* 133 */     for (VcsFileRevision revision : this.myLineRevisions) {
/* 134 */       if (revision == null) {
/* 135 */         return false;
/*     */       }
/*     */     } 
/* 138 */     return true;
/*     */   }
/*     */   
/*     */   private void fillAllNotAnnotated(VcsFileRevision vcsFileRevision) {
/* 142 */     for (int i = 0; i < this.myLineRevisions.length; i++) {
/* 143 */       if (this.myLineRevisions[i] == null) {
/* 144 */         this.myLineRevisions[i] = vcsFileRevision;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static String[] splitLines(String string) {
/* 151 */     string = StreamUtil.convertSeparators(string);
/*     */     
/* 153 */     boolean spaceAdded = false;
/* 154 */     if (string.endsWith("\n")) {
/* 155 */       string = string + " ";
/* 156 */       spaceAdded = true;
/*     */     } 
/*     */     
/* 159 */     String[] temp = string.split("\n");
/*     */     
/* 161 */     if (spaceAdded) {
/* 162 */       String[] result = new String[temp.length - 1];
/* 163 */       System.arraycopy(temp, 0, result, 0, result.length);
/* 164 */       return result;
/*     */     } 
/* 166 */     return temp;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 171 */   public String getAnnotatedContent() { return this.myAnnotatedContent; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 180 */   public VcsFileRevision[] getLineRevisions() { return this.myLineRevisions; }
/*     */   
/*     */   public static interface ContentProvider {
/*     */     String getContent(TFSFileRevision param1TFSFileRevision) throws VcsException;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\AnnotationBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */