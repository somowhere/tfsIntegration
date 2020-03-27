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
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.AbstractVcsHelper;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.openapi.vcs.FileStatusManager;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcsUtil.VcsUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LockLevel;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper.VoidProcessDelegate;
import org.jetbrains.tfsIntegration.core.tfs.locks.LockItemModel;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.ui.LockItemsDialog;

public class LockAction extends AnAction implements DumbAware {
    public LockAction() {
    }

    public void actionPerformed(@NotNull AnActionEvent e) {

        Project project = (Project)e.getData(CommonDataKeys.PROJECT);
        VirtualFile[] files = VcsUtil.getVirtualFiles(e);
        List<LockItemModel> items = new ArrayList();
        List<VcsException> exceptions = new ArrayList();
        Ref<Boolean> mappingFound = new Ref(false);
        ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            try {
                ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                WorkstationHelper.processByWorkspaces(TfsFileUtil.getFilePaths(files), false, project, new VoidProcessDelegate() {
                    public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
                        mappingFound.set(true);
                        Map itemsMap = workspace.getExtendedItems2(paths, project, TFSBundle.message("loading.items", new Object[0]));
                        Iterator var4 = itemsMap.values().iterator();

                        while(var4.hasNext()) {
                            ExtendedItem item = (ExtendedItem)var4.next();
                            if (item != null) {
                                items.add(new LockItemModel(item, workspace));
                            }
                        }

                    }
                });
            } catch (TfsException var7) {
                exceptions.add(new VcsException(var7));
            }

        }, "Reading existing locks...", false, project);
        if (!exceptions.isEmpty()) {
            AbstractVcsHelper.getInstance(project).showErrors(exceptions, "TFS");
        } else if (!(Boolean)mappingFound.get()) {
            Messages.showInfoMessage(project, "Team Foundation Server mappings not found.", e.getPresentation().getText());
        } else if (items.isEmpty()) {
            Messages.showInfoMessage(project, "Server item not found.", e.getPresentation().getText());
        } else {
            performInitialSelection(items);
            LockItemsDialog d = new LockItemsDialog(project, items);
            d.show();
            int exitCode = d.getExitCode();
            if (exitCode == 2 || exitCode == 3) {
                List<LockItemModel> selectedItems = d.getSelectedItems();
                String title = d.getLockLevel() == LockLevel.None ? "Unlocking..." : "Locking...";
                ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                    ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                    exceptions.addAll(lockOrUnlockItems(selectedItems, d.getLockLevel(), project));
                }, title, false, project);
                if (exceptions.isEmpty()) {
                    String message = MessageFormat.format("{0} {1} {2}", selectedItems.size(), selectedItems.size() == 1 ? "item" : "items", exitCode == 2 ? "locked" : "unlocked");
                    TfsUtil.showBalloon(project, MessageType.INFO, message);
                } else {
                    AbstractVcsHelper.getInstance(project).showErrors(exceptions, "TFS");
                }

            }
        }
    }

    private static void performInitialSelection(List<LockItemModel> items) {
        boolean unlockableExists = false;
        Iterator var2 = items.iterator();

        LockItemModel item;
        while(var2.hasNext()) {
            item = (LockItemModel)var2.next();
            if (item.canBeUnlocked()) {
                unlockableExists = true;
                item.setSelectionStatus(Boolean.TRUE);
            }
        }

        if (!unlockableExists) {
            var2 = items.iterator();

            while(var2.hasNext()) {
                item = (LockItemModel)var2.next();
                if (item.canBeLocked()) {
                    item.setSelectionStatus(Boolean.TRUE);
                }
            }
        }

    }

    private static List<VcsException> lockOrUnlockItems(List<LockItemModel> items, LockLevel lockLevel, Project project) {
        Map<WorkspaceInfo, List<ExtendedItem>> itemsByWorkspace = new HashMap();

        LockItemModel item;
        List<ExtendedItem> itemsForWorkspace;
        for(Iterator var4 = items.iterator(); var4.hasNext(); ((List)itemsForWorkspace).add(item.getExtendedItem())) {
            item = (LockItemModel)var4.next();
            itemsForWorkspace = (List)itemsByWorkspace.get(item.getWorkspace());
            if (itemsForWorkspace == null) {
                itemsForWorkspace = new ArrayList();
                itemsByWorkspace.put(item.getWorkspace(), itemsForWorkspace);
            }
        }

        List<VcsException> exceptions = new ArrayList();
        Iterator var11 = itemsByWorkspace.entrySet().iterator();

        while(var11.hasNext()) {
            Entry entry = (Entry)var11.next();

            try {
                WorkspaceInfo workspace = (WorkspaceInfo)entry.getKey();
                ResultWithFailures<GetOperation> resultWithFailures = workspace.getServer().getVCS().lockOrUnlockItems(workspace.getName(), workspace.getOwnerName(), lockLevel, (Collection)entry.getValue(), project, TFSBundle.message("applying.locks", new Object[0]));
                exceptions.addAll(TfsUtil.getVcsExceptions(resultWithFailures.getFailures()));
            } catch (TfsException var9) {
                exceptions.add(new VcsException(var9));
            }
        }

        return exceptions;
    }

    public void update(@NotNull AnActionEvent e) {

        Project project = (Project)e.getData(CommonDataKeys.PROJECT);
        VirtualFile[] files = VcsUtil.getVirtualFiles(e);
        e.getPresentation().setEnabled(isEnabled(project, files));
    }

    private static boolean isEnabled(Project project, VirtualFile[] files) {
        if (files.length == 0) {
            return false;
        } else {
            FileStatusManager fileStatusManager = FileStatusManager.getInstance(project);
            VirtualFile[] var3 = files;
            int var4 = files.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                VirtualFile file = var3[var5];
                FileStatus fileStatus = fileStatusManager.getStatus(file);
                if (fileStatus != FileStatus.NOT_CHANGED && fileStatus != FileStatus.MODIFIED && fileStatus != FileStatus.HIJACKED) {
                    return false;
                }
            }

            return true;
        }
    }
}
