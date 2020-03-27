//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitExecutor;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.VcsCheckinHandlerFactory;
import com.intellij.openapi.vcs.checkin.CheckinHandler.ReturnResult;
import com.intellij.util.PairConsumer;
import com.intellij.vcsUtil.VcsUtil;
import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
import org.jetbrains.tfsIntegration.checkin.CheckinPoliciesManager;
import org.jetbrains.tfsIntegration.checkin.DuplicatePolicyIdException;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters.Severity;
import org.jetbrains.tfsIntegration.ui.OverridePolicyWarningsDialog;

public class TFSCheckinHandlerFactory extends VcsCheckinHandlerFactory {
    public TFSCheckinHandlerFactory() {
        super(TFSVcs.getKey());
    }

    @NotNull
    protected CheckinHandler createVcsHandler(final CheckinProjectPanel panel) {
        CheckinHandler var10000 = new CheckinHandler() {
            public ReturnResult beforeCheckin(@Nullable CommitExecutor executor, PairConsumer<Object, Object> additionalDataConsumer) {
                if (executor != null) {
                    return ReturnResult.COMMIT;
                } else if (!panel.vcsIsAffected("TFS")) {
                    return ReturnResult.COMMIT;
                } else {
                    boolean reallyAffected = false;
                    Iterator var4 = panel.getFiles().iterator();

                    while(var4.hasNext()) {
                        File file = (File)var4.next();
                        if (TFSVcs.isUnderTFS(VcsUtil.getFilePath(file), panel.getProject())) {
                            reallyAffected = true;
                            break;
                        }
                    }

                    if (!reallyAffected) {
                        return ReturnResult.COMMIT;
                    } else {
                        TFSVcs vcs = TFSVcs.getInstance(panel.getProject());
                        CheckinParameters parameters = vcs.getCheckinData().parameters;
                        if (parameters == null) {
                            Messages.showErrorDialog(panel.getProject(), "Validation must be performed before checking in", "Checkin");
                            return ReturnResult.CLOSE_WINDOW;
                        } else {
                            Pair<String, Severity> msg = parameters.getValidationMessage(Severity.ERROR);
                            if (msg != null) {
                                Messages.showErrorDialog(panel.getProject(), (String)msg.first, "Checkin: Validation Failed");
                                return ReturnResult.CANCEL;
                            } else {
                                try {
                                    CheckinPoliciesManager.getInstalledPolicies();
                                } catch (DuplicatePolicyIdException var9) {
                                    String message = MessageFormat.format("Found multiple checkin policies with the same id: ''{0}''.\nPlease review your extensions.", var9.getDuplicateId());
                                    Messages.showErrorDialog(panel.getProject(), message, "Checkin Policies Evaluation");
                                    vcs.getCheckinData().parameters = null;
                                    return ReturnResult.CLOSE_WINDOW;
                                }

                                boolean completed = ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
                                    ProgressIndicator pi = ProgressManager.getInstance().getProgressIndicator();
                                    pi.setIndeterminate(true);
                                    parameters.evaluatePolicies(pi);
                                }, "Evaluating Checkin Policies", true, panel.getProject());
                                if (!completed) {
                                    TFSCheckinEnvironment.updateMessage(vcs.getCheckinData());
                                    return ReturnResult.CANCEL;
                                } else {
                                    msg = parameters.getValidationMessage(Severity.WARNING);
                                    if (msg == null) {
                                        return ReturnResult.COMMIT;
                                    } else {
                                        OverridePolicyWarningsDialog d = new OverridePolicyWarningsDialog(panel.getProject(), parameters.getAllFailures());
                                        if (d.showAndGet()) {
                                            parameters.setOverrideReason(d.getReason());
                                            return ReturnResult.COMMIT;
                                        } else {
                                            return ReturnResult.CANCEL;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        };

        return var10000;
    }
}
