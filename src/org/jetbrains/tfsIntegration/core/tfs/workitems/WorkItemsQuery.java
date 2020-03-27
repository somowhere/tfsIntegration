//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs.workitems;

import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Expression_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.GroupOperatorType;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.GroupType;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.OperatorType;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Query_type0E;
import java.util.List;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public enum WorkItemsQuery {
    AllMyActive("All My Active Work Items") {
        public List<WorkItem> queryWorkItems(ServerInfo server, Object projectOrComponent, String progressMessage) throws TfsException {
            Expression_type0 expression1 = new Expression_type0();
            expression1.setColumn(WorkItemField.ASSIGNED_TO.getSerialized());
            expression1.setOperator(OperatorType.equals);
            expression1.setString(server.getVCS().readIdentity(server.getQualifiedUsername(), projectOrComponent, progressMessage).getDisplayName());
            Expression_type0 expression2 = new Expression_type0();
            expression2.setColumn(WorkItemField.STATE.getSerialized());
            expression2.setOperator(OperatorType.equals);
            expression2.setString(WorkItemState.ACTIVE.getName());
            GroupType groupType = new GroupType();
            groupType.setGroupOperator(GroupOperatorType.And);
            groupType.setExpression(new Expression_type0[]{expression1, expression2});
            Query_type0E query_type01 = new Query_type0E();
            query_type01.setGroup(groupType);
            return queryWorkItems(server, query_type01, projectOrComponent, progressMessage);
        }
    },
    AllMy("All My Work Items") {
        public List<WorkItem> queryWorkItems(ServerInfo server, Object projectOrComponent, String progressMessage) throws TfsException {
            Expression_type0 expression1 = new Expression_type0();
            expression1.setColumn(WorkItemField.ASSIGNED_TO.getSerialized());
            expression1.setOperator(OperatorType.equals);
            expression1.setString(server.getVCS().readIdentity(server.getQualifiedUsername(), projectOrComponent, progressMessage).getDisplayName());
            Query_type0E query_type01 = new Query_type0E();
            query_type01.setExpression(expression1);
            return queryWorkItems(server, query_type01, projectOrComponent, progressMessage);
        }
    },
    AllActive("All Active Work Items") {
        public List<WorkItem> queryWorkItems(ServerInfo server, Object projectOrComponent, String progressMessage) throws TfsException {
            Expression_type0 expression1 = new Expression_type0();
            expression1.setColumn(WorkItemField.STATE.getSerialized());
            expression1.setOperator(OperatorType.equals);
            expression1.setString(WorkItemState.ACTIVE.getName());
            Query_type0E query_type01 = new Query_type0E();
            query_type01.setExpression(expression1);
            return queryWorkItems(server, query_type01, projectOrComponent, progressMessage);
        }
    },
    All("All Work Items") {
        public List<WorkItem> queryWorkItems(ServerInfo server, Object projectOrComponent, String progressMessage) throws TfsException {
            Expression_type0 expression1 = new Expression_type0();
            expression1.setColumn(WorkItemField.ID.getSerialized());
            expression1.setOperator(OperatorType.equalsGreater);
            expression1.setNumber(0);
            Query_type0E query_type01 = new Query_type0E();
            query_type01.setExpression(expression1);
            return queryWorkItems(server, query_type01, projectOrComponent, progressMessage);
        }
    };

    private final String myName;

    private WorkItemsQuery(String name) {
        this.myName = name;
    }

    public String toString() {
        return this.myName;
    }

    public abstract List<WorkItem> queryWorkItems(ServerInfo var1, Object var2, String var3) throws TfsException;

    protected static List<WorkItem> queryWorkItems(ServerInfo server, Query_type0E query_type01, Object projectOrComponent, String progressMessage) throws TfsException {
        return server.getVCS().queryWorkItems(query_type01, projectOrComponent, progressMessage);
    }
}
