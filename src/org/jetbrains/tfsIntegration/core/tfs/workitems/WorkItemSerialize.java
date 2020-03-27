//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs.workitems;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.containers.ContainerUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinWorkItemAction;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Column_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Columns_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ComputedColumn_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ComputedColumns_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.InsertResourceLink_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.InsertText_type0;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;

public class WorkItemSerialize {
    public static final List<WorkItemField> FIELDS;
    private static final String SERVER_DATE_TIME = "ServerDateTime";
    private static final Logger LOG;

    public WorkItemSerialize() {
    }

    public static WorkItem createFromFields(String[] workItemFieldsValues) throws OperationFailedException {
        try {
            int id = Integer.parseInt(workItemFieldsValues[FIELDS.indexOf(WorkItemField.ID)]);
            WorkItemState state = WorkItemState.from(workItemFieldsValues[FIELDS.indexOf(WorkItemField.STATE)]);
            String title = workItemFieldsValues[FIELDS.indexOf(WorkItemField.TITLE)];
            int revision = Integer.parseInt(workItemFieldsValues[FIELDS.indexOf(WorkItemField.REVISION)]);
            WorkItemType type = WorkItemType.from(workItemFieldsValues[FIELDS.indexOf(WorkItemField.TYPE)]);
            String reason = workItemFieldsValues[FIELDS.indexOf(WorkItemField.REASON)];
            String assignedTo;
            if (workItemFieldsValues.length > FIELDS.indexOf(WorkItemField.ASSIGNED_TO)) {
                assignedTo = workItemFieldsValues[FIELDS.indexOf(WorkItemField.ASSIGNED_TO)];
            } else {
                assignedTo = null;
            }

            return new WorkItem(id, assignedTo, state, title, revision, type, reason);
        } catch (Exception var8) {
            LOG.warn("Couldn't parse work items: " + Arrays.toString(workItemFieldsValues), var8);
            throw new OperationFailedException("Cannot load work items: unexpected properties encountered", var8);
        }
    }

    @Nullable
    public static Columns_type0 generateColumnsForUpdateRequest(WorkItemType type, String reason, CheckinWorkItemAction action, String identity) {
        if (action != CheckinWorkItemAction.Resolve) {
            return null;
        } else {
            List<Column_type0> columns = new ArrayList();
            if (type == WorkItemType.BUG) {
                columns.add(createColumn(WorkItemField.STATE, (String)null, WorkItemState.RESOLVED.getName()));
                columns.add(createColumn(WorkItemField.REASON, (String)null, WorkItemSerialize.Reason.Fixed.name()));
                columns.add(createColumn(WorkItemField.STATE_CHANGE_DATE, "ServerDateTime", ""));
                columns.add(createColumn(WorkItemField.RESOLVED_DATE, "ServerDateTime", ""));
                columns.add(createColumn(WorkItemField.RESOLVED_BY, (String)null, identity));
                columns.add(createColumn(WorkItemField.RESOLVED_REASON, (String)null, reason));
            } else {
                if (type != WorkItemType.TASK) {
                    throw new IllegalArgumentException("Unexpected work item type " + type);
                }

                columns.add(createColumn(WorkItemField.STATE, (String)null, WorkItemState.CLOSED.getName()));
                columns.add(createColumn(WorkItemField.REASON, (String)null, WorkItemSerialize.Reason.Completed.name()));
                columns.add(createColumn(WorkItemField.STATE_CHANGE_DATE, "ServerDateTime", ""));
                columns.add(createColumn(WorkItemField.CLOSED_DATE, "ServerDateTime", ""));
                columns.add(createColumn(WorkItemField.CLOSED_BY, (String)null, identity));
            }

            Columns_type0 columnsArray = new Columns_type0();
            columnsArray.setColumn((Column_type0[])columns.toArray(new Column_type0[0]));
            return columnsArray;
        }
    }

    public static InsertText_type0 generateInsertTextForUpdateRequest(CheckinWorkItemAction action, int changeSetId) {
        if (action == CheckinWorkItemAction.None) {
            throw new IllegalArgumentException("Unexpected action type " + action);
        } else {
            InsertText_type0 insertText = new InsertText_type0();
            insertText.setFieldDisplayName("History");
            insertText.setFieldName(WorkItemField.HISTORY.getSerialized());
            String text = MessageFormat.format("{0} with changeset {1}.", action == CheckinWorkItemAction.Resolve ? "Resolved" : "Associated", String.valueOf(changeSetId));
            insertText.setString(text);
            return insertText;
        }
    }

    public static InsertResourceLink_type0 generateInsertResourceLinkforUpdateRequest(int changeSetId) {
        InsertResourceLink_type0 insertResourceLink = new InsertResourceLink_type0();
        insertResourceLink.setFieldName(WorkItemField.BISLINKS.getSerialized());
        insertResourceLink.setLinkType("Fixed in Changeset");
        insertResourceLink.setComment("Source control changeset " + changeSetId);
        insertResourceLink.setLocation("vstfs:///VersionControl/Changeset/" + changeSetId);
        return insertResourceLink;
    }

    public static ComputedColumns_type0 generateComputedColumnsForUpdateRequest(WorkItemType type, CheckinWorkItemAction action) {
        if (action == CheckinWorkItemAction.None) {
            throw new IllegalArgumentException("Unexpected action type " + action);
        } else {
            List<ComputedColumn_type0> computedColumns = new ArrayList();
            computedColumns.add(createComputedColumn(WorkItemField.REVISED_DATE));
            computedColumns.add(createComputedColumn(WorkItemField.CHANGED_DATE));
            computedColumns.add(createComputedColumn(WorkItemField.PERSON_ID));
            if (CheckinWorkItemAction.Resolve.equals(action)) {
                computedColumns.add(createComputedColumn(WorkItemField.STATE_CHANGE_DATE));
                if (type == WorkItemType.BUG) {
                    computedColumns.add(createComputedColumn(WorkItemField.RESOLVED_DATE));
                } else {
                    if (type != WorkItemType.TASK) {
                        throw new IllegalArgumentException("Unexpected work item type " + type);
                    }

                    computedColumns.add(createComputedColumn(WorkItemField.CLOSED_DATE));
                }
            }

            ComputedColumns_type0 computedColumnsArray = new ComputedColumns_type0();
            computedColumnsArray.setComputedColumn((ComputedColumn_type0[])computedColumns.toArray(new ComputedColumn_type0[0]));
            return computedColumnsArray;
        }
    }

    private static Column_type0 createColumn(WorkItemField field, String type, String value) {
        Column_type0 column = new Column_type0();
        column.setColumn(field.getSerialized());
        column.setType(type);
        column.setValue(value);
        return column;
    }

    private static ComputedColumn_type0 createComputedColumn(WorkItemField field) {
        ComputedColumn_type0 column = new ComputedColumn_type0();
        column.setColumn(field.getSerialized());
        return column;
    }

    static {
        FIELDS = ContainerUtil.immutableList(new WorkItemField[]{WorkItemField.ID, WorkItemField.STATE, WorkItemField.TITLE, WorkItemField.REVISION, WorkItemField.TYPE, WorkItemField.REASON, WorkItemField.ASSIGNED_TO});
        LOG = Logger.getInstance(WorkItemSerialize.class);
    }

    private static enum Reason {
        Fixed,
        Completed;

        private Reason() {
        }
    }
}
