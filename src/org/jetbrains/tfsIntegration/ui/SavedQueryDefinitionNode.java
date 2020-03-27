//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.util.containers.ContainerUtil;
import com.microsoft.tfs.core.clients.workitem.WorkItemQueryUtils;
import com.microsoft.tfs.core.clients.workitem.exceptions.WorkItemException;
import com.microsoft.tfs.core.clients.workitem.query.BatchReadParameter;
import com.microsoft.tfs.core.clients.workitem.query.BatchReadParameterCollection;
import com.microsoft.tfs.core.clients.workitem.query.Query;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemCollection;
import com.microsoft.tfs.core.clients.workitem.query.WorkItemLinkInfo;
import com.microsoft.tfs.core.clients.workitem.queryhierarchy.QueryDefinition;
import com.microsoft.tfs.core.clients.workitem.queryhierarchy.QueryType;
import com.microsoft.tfs.core.ws.runtime.exceptions.ProxyException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil.Process;
import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItemSerialize;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class SavedQueryDefinitionNode extends BaseQueryNode {
    private static final String[] WORK_ITEM_FIELDS;
    private static final String WORK_ITEMS_QUERY;
    @NotNull
    private final QueryDefinition myQueryDefinition;

    public SavedQueryDefinitionNode(@NotNull QueriesTreeContext context, @NotNull QueryDefinition definition) {
        super(context);
        this.myQueryDefinition = definition;
    }

    protected void doUpdate() {
        PresentationData presentation = this.getPresentation();
        presentation.addText(this.myQueryDefinition.getName(), this.getPlainAttributes());
    }

    @NotNull
    public Object[] getEqualityObjects() {
        Object[] var10000 = new Object[]{this.myQueryDefinition.getID()};
        return var10000;
    }

    @NotNull
    public SimpleNode[] getChildren() {
        SimpleNode[] var10000 = NO_CHILDREN;
        return var10000;
    }

    public boolean isAlwaysLeaf() {
        return true;
    }

    public void handleSelection(@NotNull SimpleTree tree) {

        final boolean isList = this.isListQuery();
        this.myQueriesTreeContext.queryWorkItems(new Process<WorkItemsQueryResult>() {
            @NotNull
            public WorkItemsQueryResult run() throws TfsException, VcsException {
                WorkItemsQueryResult var10000;
                try {
                    var10000 = isList ? SavedQueryDefinitionNode.this.runListQuery() : SavedQueryDefinitionNode.this.runLinkQuery();
                } catch (WorkItemException var2) {
                    throw new VcsException(var2);
                } catch (ProxyException var3) {
                    throw new VcsException(var3);
                }


                return var10000;
            }
        });
    }

    private boolean isListQuery() {
        return QueryType.LIST.equals(this.myQueryDefinition.getQueryType());
    }

    @NotNull
    private WorkItemsQueryResult runListQuery() throws WorkItemException, ProxyException {
        WorkItemCollection workItems = this.getWorkItemClient().query(this.myQueryDefinition.getQueryText(), this.buildQueryContext());
        WorkItemsQueryResult var10000 = new WorkItemsQueryResult(toList(workItems));
        return var10000;
    }

    @NotNull
    private static List<WorkItem> toList(@NotNull WorkItemCollection workItems) throws WorkItemException, ProxyException {

        List<WorkItem> result = new ArrayList();

        for(int i = 0; i < workItems.size(); ++i) {
            result.add(WorkItem.create(workItems.getWorkItem(i)));
        }

        return result;
    }

    @NotNull
    private WorkItemsQueryResult runLinkQuery() throws WorkItemException, ProxyException {
        Query linksQuery = this.getWorkItemClient().createQuery(this.myQueryDefinition.getQueryText(), this.buildQueryContext());
        List<WorkItemLinkInfo> links = ContainerUtil.newArrayList(linksQuery.runLinkQuery());
        Query workItemsQuery = this.getWorkItemClient().createQuery(WORK_ITEMS_QUERY, toBatchReadCollection(getWorkItemIds(links)));
        WorkItemsQueryResult var10000 = new WorkItemsQueryResult(toList(workItemsQuery.runQuery()), links);

        return var10000;
    }

    @NotNull
    private static BatchReadParameterCollection toBatchReadCollection(@NotNull Iterable<Integer> ids) {

        BatchReadParameterCollection result = new BatchReadParameterCollection();
        Iterator var2 = ids.iterator();

        while(var2.hasNext()) {
            Integer id = (Integer)var2.next();
            result.add(new BatchReadParameter(id));
        }


        return result;
    }

    @NotNull
    private static Set<Integer> getWorkItemIds(@NotNull Iterable<WorkItemLinkInfo> links) {

        Set<Integer> result = new HashSet();
        Iterator var2 = links.iterator();

        while(var2.hasNext()) {
            WorkItemLinkInfo link = (WorkItemLinkInfo)var2.next();
            addId(result, link.getSourceID());
            addId(result, link.getTargetID());
        }

        return result;
    }

    private static void addId(@NotNull Set<Integer> ids, int id) {

        if (id > 0) {
            ids.add(id);
        }

    }

    @NotNull
    private Map<String, Object> buildQueryContext() {
        Map var10000 = WorkItemQueryUtils.makeContext(this.myQueryDefinition.getProject(), (String)null);

        return var10000;
    }

    static {
        WORK_ITEM_FIELDS = (String[])ContainerUtil.map2Array(WorkItemSerialize.FIELDS, String.class, (field) -> {
            return field.getSerialized();
        });
        WORK_ITEMS_QUERY = "SELECT " + WorkItemQueryUtils.formatFieldList(WORK_ITEM_FIELDS) + " FROM WorkItems";
    }
}
