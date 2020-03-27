//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import java.util.Collections;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.core.tfs.RootsCollection.VirtualFileRootsCollection;

public class CheckoutAction extends AnAction implements DumbAware {
    public CheckoutAction() {
    }

    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = (Project)e.getRequiredData(CommonDataKeys.PROJECT);
        VirtualFileRootsCollection roots = new VirtualFileRootsCollection(VcsUtil.getVirtualFiles(e));
        Ref<VcsException> error = Ref.create();
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            try {
                ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                TFSVcs.getInstance(project).getEditFileProvider().editFiles(VfsUtilCore.toVirtualFileArray(roots));
            } catch (VcsException var4) {
                error.set(var4);
            }

        }, "Checking out files for edit...", false, project);
        if (!error.isNull()) {
            AbstractVcsHelper.getInstance(project).showErrors(Collections.singletonList(error.get()), "TFS");
        }

    }

    public void update(@NotNull AnActionEvent e) {

        Project project = e.getProject();
        VirtualFile[] files = VcsUtil.getVirtualFiles(e);
        e.getPresentation().setEnabled(project != null && files.length != 0 && areNotChangedOrHijacked(project, files));
    }

    private static boolean areNotChangedOrHijacked(@NotNull Project project, @NotNull VirtualFile[] files) {

        FileStatusManager fileStatusManager = FileStatusManager.getInstance(project);
        fileStatusManager.getClass();
        return Stream.of(files).map(fileStatusManager::getStatus).allMatch((status) -> {
            return status == FileStatus.NOT_CHANGED || status == FileStatus.HIJACKED;
        });
    }
}
