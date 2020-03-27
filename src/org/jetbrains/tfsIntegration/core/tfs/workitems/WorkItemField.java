//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs.workitems;

import org.jetbrains.annotations.NonNls;

public enum WorkItemField {
    ASSIGNED_TO("System.AssignedTo"),
    STATE("System.State"),
    ID("System.Id"),
    TITLE("System.Title"),
    REVISION("System.Rev"),
    TYPE("System.WorkItemType"),
    REASON("System.Reason"),
    STATE_CHANGE_DATE("Microsoft.VSTS.Common.StateChangeDate"),
    RESOLVED_DATE("Microsoft.VSTS.Common.ResolvedDate"),
    RESOLVED_BY("Microsoft.VSTS.Common.ResolvedBy"),
    RESOLVED_REASON("Microsoft.VSTS.Common.ResolvedReason"),
    CLOSED_DATE("Microsoft.VSTS.Common.ClosedDate"),
    CLOSED_BY("Microsoft.VSTS.Common.ClosedBy"),
    REVISED_DATE("System.RevisedDate"),
    CHANGED_DATE("System.ChangedDate"),
    PERSON_ID("System.PersonId"),
    BISLINKS("System.BISLinks"),
    HISTORY("System.History");

    @NonNls
    private final String mySerialized;

    private WorkItemField(@NonNls String serialized) {
        this.mySerialized = serialized;
    }

    public String getSerialized() {
        return this.mySerialized;
    }
}
