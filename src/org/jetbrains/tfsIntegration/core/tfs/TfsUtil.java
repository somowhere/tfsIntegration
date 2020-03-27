/*     */ package org.jetbrains.tfsIntegration.core.tfs;
/*     */ 
/*     */ import com.intellij.notification.NotificationGroup;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.MessageType;
/*     */ import com.intellij.openapi.util.ClassLoaderUtil;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.ThrowableComputable;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*     */ import com.intellij.openapi.wm.ToolWindowId;
/*     */ import com.intellij.util.ArrayUtilRt;
/*     */ import com.intellij.util.ThrowableConsumer;
/*     */ import com.intellij.util.ThrowableRunnable;
/*     */ import com.intellij.util.UriUtil;
/*     */ import com.intellij.util.io.URLUtil;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfString;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.DeletedState;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Failure;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.SeverityType;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collection;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.List;
/*     */ import java.util.TimeZone;
/*     */ import org.jetbrains.annotations.Contract;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.revision.TFSContentRevision;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TfsUtil
/*     */ {
/*  46 */   private static final NotificationGroup NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("TFS", ToolWindowId.VCS);
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static Pair<WorkspaceInfo, ExtendedItem> getWorkspaceAndExtendedItem(FilePath localPath, Object projectOrComponent, String progressTitle) throws TfsException {
/*  52 */     Collection<WorkspaceInfo> workspaces = Workstation.getInstance().findWorkspaces(localPath, false, projectOrComponent);
/*  53 */     if (workspaces.isEmpty()) {
/*  54 */       return null;
/*     */     }
/*  56 */     WorkspaceInfo workspace = workspaces.iterator().next();
/*     */     
/*  58 */     ExtendedItem item = workspace.getServer().getVCS().getExtendedItem(workspace.getName(), workspace.getOwnerName(), localPath, RecursionType.None, DeletedState.Any, projectOrComponent, progressTitle);
/*     */     
/*  60 */     return Pair.create(workspace, item);
/*     */   }
/*     */   
/*     */   public static VcsRevisionNumber getCurrentRevisionNumber(FilePath path, Object projectOrComponent, String progressTitle) {
/*     */     try {
/*  65 */       Pair<WorkspaceInfo, ExtendedItem> workspaceAndItem = getWorkspaceAndExtendedItem(path, projectOrComponent, progressTitle);
/*  66 */       return (workspaceAndItem != null && workspaceAndItem.second != null) ? 
/*  67 */         getCurrentRevisionNumber((ExtendedItem)workspaceAndItem.second) : VcsRevisionNumber.NULL;
/*     */     
/*     */     }
/*  70 */     catch (TfsException e) {
/*  71 */       return VcsRevisionNumber.NULL;
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public static TFSContentRevision getCurrentRevision(Project project, FilePath path, String progressTitle) throws TfsException {
/*  77 */     Pair<WorkspaceInfo, ExtendedItem> workspaceAndItem = getWorkspaceAndExtendedItem(path, project, progressTitle);
/*  78 */     if (workspaceAndItem != null && workspaceAndItem.second != null) {
/*  79 */       return 
/*  80 */         TFSContentRevision.create(project, (WorkspaceInfo)workspaceAndItem.first, ((ExtendedItem)workspaceAndItem.second).getLver(), ((ExtendedItem)workspaceAndItem.second).getItemid());
/*     */     }
/*     */     
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  88 */   public static VcsRevisionNumber getCurrentRevisionNumber(@NotNull ExtendedItem item) {    return (item.getLver() != Integer.MIN_VALUE) ? (VcsRevisionNumber)new TfsRevisionNumber(item.getLver(), item.getItemid()) : VcsRevisionNumber.NULL; }
/*     */ 
/*     */   
/*     */   public static VcsException collectExceptions(Collection<? extends VcsException> exceptions) {
/*  92 */     if (exceptions.isEmpty()) {
/*  93 */       throw new IllegalArgumentException("No exceptions to collect");
/*     */     }
/*  95 */     if (exceptions.size() == 1)
/*     */     {
/*  97 */       return exceptions.iterator().next();
/*     */     }
/*     */     
/* 100 */     StringBuilder s = new StringBuilder();
/* 101 */     for (VcsException exception : exceptions) {
/* 102 */       if (s.length() > 0) {
/* 103 */         s.append("\n");
/*     */       }
/* 105 */       s.append(exception.getMessage());
/*     */     } 
/* 107 */     return new VcsException(s.toString());
/*     */   }
/*     */ 
/*     */   
/*     */   public static List<FilePath> getLocalPaths(List<? extends ItemPath> paths) {
/* 112 */     List<FilePath> localPaths = new ArrayList<>(paths.size());
/* 113 */     for (ItemPath path : paths) {
/* 114 */       localPaths.add(path.getLocalPath());
/*     */     }
/* 116 */     return localPaths;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Calendar getZeroCalendar() {
/* 123 */     Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
/* 124 */     calendar.clear();
/* 125 */     calendar.set(1, 0, 1, 0, 0, 0);
/* 126 */     return calendar;
/*     */   }
/*     */   
/*     */   public static List<VcsException> getVcsExceptions(Collection<? extends Failure> failures) {
/* 130 */     List<VcsException> exceptions = new ArrayList<>();
/* 131 */     for (Failure failure : failures) {
/* 132 */       if (failure.getSev() != SeverityType.Warning) {
/* 133 */         exceptions.add(new VcsException(failure.getMessage()));
/*     */       }
/*     */     } 
/* 136 */     return exceptions;
/*     */   }
/*     */   
/*     */   public static String getNameWithoutDomain(String qualifiedName) {
/* 140 */     int slashIndex = qualifiedName.indexOf('\\');
/* 141 */     if (slashIndex > -1 && slashIndex < qualifiedName.length() - 1) {
/* 142 */       return qualifiedName.substring(slashIndex + 1);
/*     */     }
/*     */     
/* 145 */     return qualifiedName;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public static URI getUrl(String uriText, boolean complainOnPath, boolean trimPath) {
/* 151 */     int i = uriText.indexOf("://");
/*     */     
/*     */     try {
/*     */       URI uri;
/* 155 */       if (i == -1) {
/* 156 */         uri = (new URI("http", "//" + uriText, null)).normalize();
/*     */       } else {
/*     */         
/* 159 */         uri = (new URI(uriText.substring(0, i), "//" + uriText.substring(i + "://".length()), null)).normalize();
/*     */       } 
/* 161 */       if (StringUtil.isEmpty(uri.getHost())) {
/* 162 */         return null;
/*     */       }
/*     */       
/* 165 */       int port = uri.getPort();
/* 166 */       if (port > 65535) {
/* 167 */         return null;
/*     */       }
/*     */       
/* 170 */       if (complainOnPath && uri.getPath() != null && !uri.getPath().isEmpty() && !"/".equals(uri.getPath())) {
/* 171 */         return null;
/*     */       }
/* 173 */       if (trimPath) {
/* 174 */         return new URI(uri.getScheme(), null, uri.getHost(), uri.getPort(), "/", null, null);
/*     */       }
/*     */       
/* 177 */       return uri;
/*     */     
/*     */     }
/* 180 */     catch (URISyntaxException e) {
/* 181 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 187 */   public static void showBalloon(Project project, MessageType messageType, String messageHtml) { NOTIFICATION_GROUP.createNotification(messageHtml, messageType).notify(project); }
/*     */ 
/*     */ 
/*     */   
/* 191 */   public static String getPresentableUri(URI uri) { return URLUtil.decode(uri.toString()); }
/*     */ 
/*     */ 
/*     */   
/* 195 */   public static String getQualifiedUsername(String domain, String userName) { return domain + "\\" + userName; }
/*     */ 
/*     */   
/*     */   public static <T, E extends Throwable> void consumeInParts(List<T> items, int maxPartSize, ThrowableConsumer<? super List<T>, E> consumer) throws E {
/* 199 */     for (int group = 0; group <= items.size() / maxPartSize; group++) {
/* 200 */       List<T> subList = items.subList(group * maxPartSize, Math.min((group + 1) * maxPartSize, items.size()));
/* 201 */       if (!subList.isEmpty()) {
/* 202 */         consumer.consume(subList);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static String appendPath(URI serverUri, String path) {
/* 208 */     path = StringUtil.trimStart(path, "/");
/* 209 */     return UriUtil.trimTrailingSlashes(serverUri.toString()) + "/" + path.replace(" ", "%20");
/*     */   }
/*     */ 
/*     */   
/* 213 */   public static <T, E extends Throwable> T forcePluginClassLoader(@NotNull ThrowableComputable<T, E> computable) throws E {    return (T)ClassLoaderUtil.computeWithClassLoader(null, computable); }
/*     */ 
/*     */ 
/*     */   
/* 217 */   public static <E extends Throwable> void forcePluginClassLoader(@NotNull ThrowableRunnable<E> runnable) throws E {    ClassLoaderUtil.runWithClassLoader(null, runnable); }
/*     */ 
/*     */   
/*     */   @Contract(pure = true, value = "null -> null; !null -> !null")
/*     */   public static ArrayOfString toArrayOfString(@Nullable Collection<String> values) {
/* 222 */     ArrayOfString result = null;
/*     */     
/* 224 */     if (values != null) {
/* 225 */       result = new ArrayOfString();
/* 226 */       result.setString(ArrayUtilRt.toStringArray(values));
/*     */     } 
/*     */     
/* 229 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\TfsUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */