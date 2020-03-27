//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs.workitems;

import com.intellij.openapi.util.text.StringUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinWorkItemAction;
import com.microsoft.tfs.core.clients.workitem.fields.FieldCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkItem {
    private final int myId;
    @Nullable
    private final String myAssignedTo;
    private final WorkItemState myState;
    private final String myTitle;
    private final int myRevision;
    private final WorkItemType myType;
    private final String myReason;

    public WorkItem(int id, @Nullable String assignedTo, WorkItemState state, String title, int revision, WorkItemType type, String reason) {
        this.myAssignedTo = assignedTo;
        this.myId = id;
        this.myReason = reason;
        this.myRevision = revision;
        this.myState = state;
        this.myTitle = title;
        this.myType = type;
    }

    @NotNull
    public static WorkItem create(@NotNull com.microsoft.tfs.core.clients.workitem.WorkItem workItem) {

        int id = workItem.getID();
        String assignedTo = getStringValue(workItem, WorkItemField.ASSIGNED_TO);
        String state = getStringValue(workItem, WorkItemField.STATE);
        String title = workItem.getTitle();
        int revision = workItem.getFields().getRevision();
        String type = workItem.getType().getName();
        String reason = getStringValue(workItem, WorkItemField.REASON);
        WorkItem var10000 = new WorkItem(id, assignedTo, WorkItemState.from(state), title, revision, WorkItemType.from(type), reason);
        return var10000;
    }

    @NotNull
    private static String getStringValue(@NotNull com.microsoft.tfs.core.clients.workitem.WorkItem workItem, @NotNull WorkItemField field) {
        FieldCollection fields = workItem.getFields();
        String result = null;
        if (fields.contains(field.getSerialized())) {
            result = (String)fields.getField(field.getSerialized()).getValue();
        }

        String var10000 = StringUtil.notNullize(result);

        return var10000;
    }

    public int getId() {
        return this.myId;
    }

    @Nullable
    public String getAssignedTo() {
        return this.myAssignedTo;
    }

    public WorkItemState getState() {
        return this.myState;
    }

    public String getTitle() {
        return this.myTitle;
    }

    public int getRevision() {
        return this.myRevision;
    }

    public WorkItemType getType() {
        return this.myType;
    }

    public String getReason() {
        return this.myReason;
    }

    public boolean isActionPossible(CheckinWorkItemAction action) {
        return action == CheckinWorkItemAction.None || action == CheckinWorkItemAction.Associate || this.myState == WorkItemState.ACTIVE && (this.myType == WorkItemType.BUG || this.myType == WorkItemType.TASK);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            WorkItem workItem = (WorkItem)o;
            return this.myId == workItem.myId;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.myId;
    }

    public String toString() {
        return this.myTitle;
    }
}
