//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.annotate.AnnotationProvider;
import com.intellij.openapi.vcs.annotate.FileAnnotation;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsRevisionNumber.Int;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.AnnotationBuilder;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.Workstation;
import org.jetbrains.tfsIntegration.core.tfs.AnnotationBuilder.ContentProvider;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
import org.jetbrains.tfsIntegration.core.tfs.version.WorkspaceVersionSpec;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class TFSAnnotationProvider implements AnnotationProvider {
    private static final int CURRENT_CHANGESET = 0;
    @NotNull
    private final TFSVcs myVcs;

    public TFSAnnotationProvider(@NotNull TFSVcs vcs) {

        super();
        this.myVcs = vcs;
    }

    @Nullable
    public FileAnnotation annotate(VirtualFile file) throws VcsException {
        return this.annotate(file, 0);
    }

    @Nullable
    public FileAnnotation annotate(VirtualFile file, VcsFileRevision revision) throws VcsException {
        return this.annotate(file, ((Int)revision.getRevisionNumber()).getValue());
    }

    @Nullable
    private FileAnnotation annotate(VirtualFile file, int changeset) throws VcsException {
        Ref<VcsException> exception = new Ref();
        Ref<FileAnnotation> result = new Ref();
        Runnable runnable = () -> {
            try {
                ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
                TFSProgressUtil.setIndeterminate(progressIndicator, true);
                FilePath localPath = TfsFileUtil.getFilePath(file);
                Collection<WorkspaceInfo> workspaces = Workstation.getInstance().findWorkspaces(localPath, false, this.myVcs.getProject());
                TFSProgressUtil.checkCanceled(progressIndicator);
                if (workspaces.isEmpty()) {
                    exception.set(new VcsException(MessageFormat.format("Mappings not found for file ''{0}''", localPath.getPresentableUrl())));
                    return;
                }

                WorkspaceInfo workspace = (WorkspaceInfo)workspaces.iterator().next();
                Map<FilePath, ExtendedItem> path2item = workspace.getExtendedItems(Collections.singletonList(localPath), this.myVcs.getProject(), TFSBundle.message("loading.item", new Object[0]));
                if (path2item.isEmpty()) {
                    exception.set(new VcsException(MessageFormat.format("''{0}'' is unversioned", localPath.getPresentableUrl())));
                    return;
                }

                TFSProgressUtil.checkCanceled(progressIndicator);
                VersionSpecBase versionSpec = changeset == 0 ? new WorkspaceVersionSpec(workspace.getName(), workspace.getOwnerName()) : new ChangesetVersionSpec(changeset);
                List<TFSFileRevision> revisionList = TFSHistoryProvider.getRevisions(this.myVcs.getProject(), ((ExtendedItem)path2item.get(localPath)).getSitem(), false, workspace, (VersionSpecBase)versionSpec);
                TFSProgressUtil.checkCanceled(progressIndicator);
                if (revisionList.isEmpty()) {
                    return;
                }

                result.set(this.annotate(workspace, localPath, revisionList));
            } catch (TfsException var12) {
                exception.set(new VcsException(var12));
            } catch (VcsException var13) {
                exception.set(var13);
            }

        };
        if (ApplicationManager.getApplication().isDispatchThread()) {
            ProgressManager.getInstance().runProcessWithProgressSynchronously(runnable, "Computing Annotations", true, this.myVcs.getProject());
        } else {
            runnable.run();
        }

        if (!exception.isNull()) {
            throw (VcsException)exception.get();
        } else {
            return (FileAnnotation)result.get();
        }
    }

    @Nullable
    private FileAnnotation annotate(WorkspaceInfo workspace, final FilePath localPath, List<TFSFileRevision> revisions) throws VcsException {
        final ProgressIndicator progressIndicator = ProgressManager.getInstance().getProgressIndicator();
        AnnotationBuilder annotationBuilder = new AnnotationBuilder(revisions, new ContentProvider() {
            public String getContent(TFSFileRevision revision) throws VcsException {
                TFSProgressUtil.checkCanceled(progressIndicator);
                String content = revision.createContentRevision().getContent();
                if (content == null) {
                    String errorMessage = MessageFormat.format("Cannot load content for file ''{0}'', rev. {1}", localPath.getPresentableUrl(), revision.getRevisionNumber().getValue());
                    throw new VcsException(errorMessage);
                } else {
                    return content;
                }
            }
        });
        return new TFSFileAnnotation(this.myVcs, workspace, annotationBuilder.getAnnotatedContent(), annotationBuilder.getLineRevisions(), localPath.getVirtualFile());
    }
}
