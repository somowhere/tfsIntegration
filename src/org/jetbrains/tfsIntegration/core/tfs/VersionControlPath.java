/*     */ package org.jetbrains.tfsIntegration.core.tfs;
/*     */ 
/*     */ import com.intellij.openapi.util.SystemInfo;
/*     */ import com.intellij.openapi.util.io.FileUtil;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.vcsUtil.VcsUtil;
/*     */ import java.io.File;
/*     */ import java.util.Arrays;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
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
/*     */ public class VersionControlPath
/*     */ {
/*     */   public static final String SERVER_PATH_SEPARATOR = "/";
/*     */   public static final String ROOT_FOLDER = "$/";
/*     */   private static final String WINDOWS_PATH_SEPARATOR = "\\";
/*     */   private static final String FAKE_DRIVE_PREFIX = "U:";
/*     */   
/*     */   public static String toTfsRepresentation(@Nullable String localPath) {
/*  41 */     if (localPath == null) {
/*  42 */       return null;
/*     */     }
/*  44 */     localPath = localPath.replace("/", "\\");
/*  45 */     return SystemInfo.isWindows ? localPath : ("U:" + localPath);
/*     */   }
/*     */ 
/*     */   
/*  49 */   public static String toTfsRepresentation(@NotNull FilePath localPath) {    return toTfsRepresentation(localPath.getPath()); }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static String localPathFromTfsRepresentation(@Nullable String localPath) {
/*  54 */     if (localPath == null) {
/*  55 */       return null;
/*     */     }
/*     */     
/*  58 */     String systemDependent = FileUtil.toSystemDependentName(localPath);
/*  59 */     if (!SystemInfo.isWindows && systemDependent.startsWith("U:")) {
/*  60 */       return systemDependent.substring("U:".length());
/*     */     }
/*     */     
/*  63 */     return systemDependent;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  69 */   public static FilePath getFilePath(@Nullable String localPath, boolean isDirectory) { return (localPath != null) ? VcsUtil.getFilePath(localPathFromTfsRepresentation(localPath), isDirectory) : null; }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  74 */   public static VirtualFile getVirtualFile(@NotNull String localPath) {    return VcsUtil.getVirtualFile(localPathFromTfsRepresentation(localPath)); }
/*     */ 
/*     */ 
/*     */   
/*  78 */   public static File getFile(String localPath) { return new File(localPathFromTfsRepresentation(localPath)); }
/*     */ 
/*     */   
/*     */   public static String getPathToProject(String serverPath) {
/*  82 */     int secondSlashPos = serverPath.indexOf("/", "$/".length());
/*  83 */     return (secondSlashPos == -1) ? serverPath : serverPath.substring(0, secondSlashPos);
/*     */   }
/*     */   
/*     */   public static String getTeamProject(String serverPath) {
/*  87 */     int secondSlashPos = serverPath.indexOf("/", "$/".length());
/*  88 */     return serverPath.substring("$/".length(), (secondSlashPos != -1) ? secondSlashPos : serverPath.length());
/*     */   }
/*     */   
/*     */   public static boolean isUnder(String parent, String child) {
/*  92 */     parent = StringUtil.toLowerCase(parent);
/*  93 */     return parent.equals(getCommonAncestor(parent, StringUtil.toLowerCase(child)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 102 */   public static int compareParentToChild(String path1, String path2) { return path1.compareTo(path2); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int compareParentToChild(@NotNull String path1, boolean isDirectory1, @NotNull String path2, boolean isDrectory2) {
/* 109 */           String[] pathComponents1 = getPathComponents(path1);
/* 110 */     String[] pathComponents2 = getPathComponents(path2);
/*     */     
/* 112 */     int minLength = Math.min(pathComponents1.length, pathComponents2.length);
/*     */ 
/*     */     
/* 115 */     for (int i = 0; i < minLength - 1; i++) {
/* 116 */       String s1 = pathComponents1[i];
/* 117 */       String s2 = pathComponents2[i];
/* 118 */       if (!s1.equals(s2)) {
/* 119 */         return s1.compareTo(s2);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 124 */     if (pathComponents1.length == pathComponents2.length) {
/* 125 */       if (isDirectory1 == isDrectory2) {
/* 126 */         return pathComponents1[pathComponents1.length - 1].compareTo(pathComponents2[pathComponents2.length - 1]);
/*     */       }
/*     */       
/* 129 */       return isDirectory1 ? 1 : -1;
/*     */     } 
/*     */ 
/*     */     
/* 133 */     if (pathComponents1.length == minLength && !isDirectory1) {
/* 134 */       return -1;
/*     */     }
/* 136 */     if (pathComponents2.length == minLength && !isDrectory2) {
/* 137 */       return 1;
/*     */     }
/*     */     
/* 140 */     if (pathComponents1[minLength - 1].equals(pathComponents2[minLength - 1])) {
/* 141 */       return pathComponents1.length - pathComponents2.length;
/*     */     }
/*     */     
/* 144 */     return pathComponents1[minLength - 1].compareTo(pathComponents2[minLength - 1]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getCommonAncestor(@NotNull String path1, @NotNull String path2) {
/* 151 */           String[] components1 = getPathComponents(path1);
/* 152 */     String[] components2 = getPathComponents(path2);
/*     */     
/* 154 */     int i = 0;
/* 155 */     while (i < Math.min(components1.length, components2.length) && components1[i].equals(components2[i])) {
/* 156 */       i++;
/*     */     }
/* 158 */     return (i == 1) ? "$/" : StringUtil.join(Arrays.asList(components1).subList(0, i), "/");
/*     */   }
/*     */ 
/*     */   
/* 162 */   public static String getLastComponent(@NotNull String serverPath) {    return serverPath.substring(serverPath.lastIndexOf("/") + 1); }
/*     */ 
/*     */ 
/*     */   
/* 166 */   public static String[] getPathComponents(@NotNull String serverPath) {    return serverPath.split("/"); }
/*     */ 
/*     */   
/*     */   public static String getCombinedServerPath(FilePath localPathBase, String serverPathBase, FilePath localPath) {
/* 170 */     String localPathBaseString = FileUtil.toSystemIndependentName(localPathBase.getPath());
/* 171 */     String localPathString = FileUtil.toSystemIndependentName(localPath.getPath());
/*     */     
/* 173 */     String localPathRemainder = localPathString.substring(localPathBaseString.length());
/* 174 */     if (serverPathBase.endsWith("/") && localPathRemainder.startsWith("/")) {
/* 175 */       localPathRemainder = localPathRemainder.substring(1);
/*     */     }
/* 177 */     return serverPathBase + localPathRemainder;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static FilePath getCombinedLocalPath(FilePath localPathBase, String serverPathBase, String serverPath, boolean isDirectory) {
/* 184 */     String serverPathBaseString = FileUtil.toSystemDependentName(serverPathBase);
/* 185 */     String serverPathString = FileUtil.toSystemDependentName(serverPath);
/* 186 */     File localFile = new File(localPathBase.getIOFile(), serverPathString.substring(serverPathBaseString.length()));
/* 187 */     return VcsUtil.getFilePath(localFile, isDirectory);
/*     */   }
/*     */ 
/*     */   
/* 191 */   public static String getCombinedServerPath(String path, String name) { return path + (path.endsWith("/") ? "" : "/") + name; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\VersionControlPath.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */