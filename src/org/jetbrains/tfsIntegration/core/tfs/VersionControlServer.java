//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.util.ArrayUtilRt;
import com.intellij.util.containers.ContainerUtil;
import com.microsoft.schemas.teamfoundation._2005._06.services.authorization._03.Identity;
import com.microsoft.schemas.teamfoundation._2005._06.services.authorization._03.QueryMembership;
import com.microsoft.schemas.teamfoundation._2005._06.services.authorization._03.SearchFactor;
import com.microsoft.schemas.teamfoundation._2005._06.services.groupsecurity._03.ReadIdentity;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.AddConflict;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.AddConflictResponse;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Annotation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfAnnotation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfArrayOfBranchRelative;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfArrayOfGetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfChangeRequest;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfCheckinNoteFieldDefinition;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfCheckinNoteFieldValue;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfCheckinNotificationWorkItemInfo;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfFailure;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfGetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfGetRequest;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfInt;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfItemSet;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfItemSpec;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfLabelItemSpec;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfLabelResult;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfLocalVersionUpdate;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfMergeCandidate;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ArrayOfPolicyFailureInfo;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.BranchRelative;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeRequest;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Changeset;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckIn;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckInResponse;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinNote;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinNoteFieldDefinition;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinNoteFieldValue;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinNotificationInfo;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinNotificationWorkItemInfo;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinOptions;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinOptions_type0;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinResult;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinWorkItemAction;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ConflictType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CreateAnnotation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CreateWorkspace;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.DeleteAnnotation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.DeleteWorkspace;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.DeletedState;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Failure;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Get;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetOperation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.GetRequest;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemSet;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemSpec;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelChildOption;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelItemResponse;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelItemSpec;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelResult;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LocalVersionUpdate;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LockLevel;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Merge;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.MergeCandidate;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.MergeOptions;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.MergeOptions_type0;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.MergeResponse;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendChanges;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendChangesResponse;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingChange;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PendingSet;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PolicyFailureInfo;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.PolicyOverrideInfo;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryAnnotation;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryBranches;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryChangeset;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryCheckinNoteDefinition;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryConflicts;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryHistory;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryItems;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryItemsById;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryItemsExtended;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryLabels;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryMergeCandidates;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryPendingSets;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryWorkspace;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.QueryWorkspaces;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RequestType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Resolution;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Resolve;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ResolveResponse;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.UndoPendingChanges;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.UndoPendingChangesResponse;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.UpdateLocalVersion;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.UpdateWorkspace;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionControlLabel;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Workspace;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ArrayOfMetadataTableHaveEntry;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ArrayOfString;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Id_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Package_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Package_type0E;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.PageWorkitemsByIds;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.PageWorkitemsByIdsResponse;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.PsQuery_type1;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.QueryWorkitems;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.QueryWorkitemsResponse;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Query_type0E;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.R_type0;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.RequestHeader;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.RequestHeaderE;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.Update;
import com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.UpdateWorkItem_type0;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.core.TfsBeansHolder;
import org.jetbrains.tfsIntegration.core.configuration.Credentials;
import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItemField;
import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItemSerialize;
import org.jetbrains.tfsIntegration.exceptions.HostNotApplicableException;
import org.jetbrains.tfsIntegration.exceptions.TfsException;
import org.jetbrains.tfsIntegration.webservice.TfsRequestManager;
import org.jetbrains.tfsIntegration.webservice.WebServiceHelper;
import org.jetbrains.tfsIntegration.webservice.TfsRequestManager.Request;

public class VersionControlServer {
    @NonNls
    public static final String WORKSPACE_NAME_FIELD = "wsname";
    @NonNls
    public static final String WORKSPACE_OWNER_FIELD = "wsowner";
    @NonNls
    public static final String RANGE_FIELD = "range";
    @NonNls
    public static final String LENGTH_FIELD = "filelength";
    @NonNls
    public static final String HASH_FIELD = "hash";
    @NonNls
    public static final String SERVER_ITEM_FIELD = "item";
    @NonNls
    public static final String CONTENT_FIELD = "content";
    public static final int LOCAL_CONFLICT_REASON_SOURCE = 1;
    public static final int LOCAL_CONFLICT_REASON_TARGET = 3;
    private static final int ITEMS_IN_GROUP = Integer.getInteger("org.jetbrains.tfsIntegration.requestGroupSize", 200);
    private final URI myServerUri;
    private final String myInstanceId;
    @NotNull
    private final TfsBeansHolder myBeans;
    private static final Logger LOG = Logger.getInstance(VersionControlServer.class.getName());

    private <T, U> U execute(final VersionControlServer.OperationOnCollection<T, U> operation, Object projectOrComponent, Collection<T> items, String progressTitle) throws TfsException {
        OperationOnList<T, U> operationOnList = new OperationOnList<T, U>() {
            public U execute(List<T> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                return operation.execute(items, credentials, pi);
            }

            public U merge(Collection<U> results) {
                return operation.merge(results);
            }
        };
        return this.execute(operationOnList, projectOrComponent, (List<T>) new ArrayList(items), progressTitle);
    }

    private <T, U> U execute(VersionControlServer.OperationOnList<T, U> operation, Object projectOrComponent, List<T> items, String progressTitle) throws TfsException {
        if (items.isEmpty()) {
            return operation.merge(Collections.emptyList());
        } else {
            Collection<U> results = new ArrayList();
            TfsUtil.consumeInParts(items, ITEMS_IN_GROUP, (ts) -> {
                U result = TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<U>(progressTitle) {
                    public U execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                        return operation.execute(ts, credentials, pi);
                    }
                });
                results.add(result);
            });
            return operation.merge(results);
        }
    }

    public VersionControlServer(URI uri, @NotNull TfsBeansHolder beans, String instanceId) {
        super();
        this.myServerUri = uri;
        this.myBeans = beans;
        this.myInstanceId = instanceId;
    }

    public static ItemSpec createItemSpec(String string, RecursionType recursionType) {
        return createItemSpec(string, -2147483648, recursionType);
    }

    public static ItemSpec createItemSpec(FilePath localPath, RecursionType recursionType) {
        return createItemSpec(VersionControlPath.toTfsRepresentation(localPath), -2147483648, recursionType);
    }

    public static ItemSpec createItemSpec(String string, int deletionId, RecursionType recursionType) {
        ItemSpec itemSpec = new ItemSpec();
        itemSpec.setItem(string);
        itemSpec.setDid(deletionId);
        itemSpec.setRecurse(recursionType);
        return itemSpec;
    }

    private List<Item> queryItemsById(int[] itemIds, final int changeSet, final boolean generateDownloadUrl, Object projectOrComponent, String progressTitle) throws TfsException {
        final ArrayOfInt arrayOfInt = new ArrayOfInt();
        arrayOfInt.set_int(itemIds);
        ArrayOfItem arrayOfItems = (ArrayOfItem)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ArrayOfItem>(progressTitle) {
            public ArrayOfItem execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryItemsById param = new QueryItemsById();
                param.setChangeSet(changeSet);
                param.setItemIds(arrayOfInt);
                param.setGenerateDownloadUrls(generateDownloadUrl);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryItemsById(param).getQueryItemsByIdResult();
            }
        });
        ArrayList<Item> result = new ArrayList();
        ContainerUtil.addAll(result, arrayOfItems.getItem());
        return result;
    }

    @Nullable
    public Item queryItemById(int itemId, int changeSet, boolean generateDownloadUrl, Object projectOrComponent, String progressTitle) throws TfsException {
        List<Item> items = this.queryItemsById(new int[]{itemId}, changeSet, generateDownloadUrl, projectOrComponent, progressTitle);
        if (items.isEmpty()) {
            return null;
        } else {
            TFSVcs.assertTrue(items.size() == 1);
            return (Item)items.get(0);
        }
    }

    private static ChangeRequest createChangeRequestTemplate() {
        ItemSpec itemSpec = createItemSpec((String)((String)null), (RecursionType)null);
        ChangeRequest changeRequest = new ChangeRequest();
        changeRequest.setDid(-2147483648);
        changeRequest.setEnc(-2147483648);
        changeRequest.setItem(itemSpec);
        changeRequest.setLock((LockLevel)null);
        changeRequest.setTarget((String)null);
        changeRequest.setTargettype((ItemType)null);
        changeRequest.setVspec((VersionSpec)null);
        return changeRequest;
    }

    public ResultWithFailures<GetOperation> checkoutForEdit(String workspaceName, String workspaceOwner, List<ItemPath> paths, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.pendChanges(workspaceName, workspaceOwner, paths, false, new VersionControlServer.ChangeRequestProvider<ItemPath>() {
            public ChangeRequest createChangeRequest(ItemPath itemPath) {
                ChangeRequest changeRequest = VersionControlServer.createChangeRequestTemplate();
                changeRequest.getItem().setItem(itemPath.getServerPath());
                if (itemPath.getLocalPath().isDirectory()) {
                    changeRequest.getItem().setRecurse(RecursionType.Full);
                }

                changeRequest.setReq(RequestType.Edit);
                return changeRequest;
            }
        }, projectOrComponent, progressTitle);
    }

    public ResultWithFailures<GetOperation> createBranch(String workspaceName, String workspaceOwner, String sourceServerPath, final VersionSpecBase versionSpec, final String targetServerPath, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.pendChanges(workspaceName, workspaceOwner, Collections.singletonList(sourceServerPath), false, new VersionControlServer.ChangeRequestProvider<String>() {
            public ChangeRequest createChangeRequest(String serverPath) {
                ChangeRequest changeRequest = VersionControlServer.createChangeRequestTemplate();
                changeRequest.getItem().setItem(serverPath);
                changeRequest.getItem().setRecurse(RecursionType.Full);
                changeRequest.setReq(RequestType.Branch);
                changeRequest.setTarget(targetServerPath);
                changeRequest.setVspec(versionSpec);
                return changeRequest;
            }
        }, projectOrComponent, progressTitle);
    }

    public ResultWithFailures<GetOperation> scheduleForAddition(String workspaceName, String workspaceOwner, List<ItemPath> paths, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.pendChanges(workspaceName, workspaceOwner, paths, false, new VersionControlServer.ChangeRequestProvider<ItemPath>() {
            public ChangeRequest createChangeRequest(ItemPath itemPath) {
                ChangeRequest changeRequest = VersionControlServer.createChangeRequestTemplate();
                changeRequest.getItem().setItem(VersionControlPath.toTfsRepresentation(itemPath.getLocalPath()));
                changeRequest.setReq(RequestType.Add);
                File file = itemPath.getLocalPath().getIOFile();
                changeRequest.setType(file.isFile() ? ItemType.File : ItemType.Folder);
                changeRequest.setEnc(1251);
                return changeRequest;
            }
        }, projectOrComponent, progressTitle);
    }

    public ResultWithFailures<GetOperation> scheduleForDeletionAndUpateLocalVersion(String workspaceName, String workspaceOwner, Collection<FilePath> localPaths, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.pendChanges(workspaceName, workspaceOwner, localPaths, true, new VersionControlServer.ChangeRequestProvider<FilePath>() {
            public ChangeRequest createChangeRequest(FilePath localPath) {
                ChangeRequest changeRequest = VersionControlServer.createChangeRequestTemplate();
                changeRequest.getItem().setItem(VersionControlPath.toTfsRepresentation(localPath));
                changeRequest.setReq(RequestType.Delete);
                return changeRequest;
            }
        }, projectOrComponent, progressTitle);
    }

    public ResultWithFailures<GetOperation> renameAndUpdateLocalVersion(String workspaceName, String workspaceOwner, final Map<FilePath, FilePath> movedPaths, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.pendChanges(workspaceName, workspaceOwner, movedPaths.keySet(), true, new VersionControlServer.ChangeRequestProvider<FilePath>() {
            public ChangeRequest createChangeRequest(FilePath localPath) {
                ChangeRequest changeRequest = VersionControlServer.createChangeRequestTemplate();
                changeRequest.getItem().setItem(VersionControlPath.toTfsRepresentation(localPath));
                changeRequest.setReq(RequestType.Rename);
                changeRequest.setTarget(VersionControlPath.toTfsRepresentation((FilePath)movedPaths.get(localPath)));
                return changeRequest;
            }
        }, projectOrComponent, progressTitle);
    }

    public ResultWithFailures<GetOperation> lockOrUnlockItems(String workspaceName, String workspaceOwner, final LockLevel lockLevel, Collection<ExtendedItem> items, Object projectOrComponent, String progressTitle) throws TfsException {
        return this.pendChanges(workspaceName, workspaceOwner, items, false, new VersionControlServer.ChangeRequestProvider<ExtendedItem>() {
            public ChangeRequest createChangeRequest(ExtendedItem item) {
                ChangeRequest changeRequest = VersionControlServer.createChangeRequestTemplate();
                changeRequest.getItem().setItem(item.getSitem());
                changeRequest.setReq(RequestType.Lock);
                changeRequest.setLock(lockLevel);
                return changeRequest;
            }
        }, projectOrComponent, progressTitle);
    }

    private <T> ResultWithFailures<GetOperation> pendChanges(final String workspaceName, final String workspaceOwner, Collection<T> paths, final boolean updateLocalVersion, final VersionControlServer.ChangeRequestProvider<T> changeRequestProvider, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnCollection<T, ResultWithFailures<GetOperation>> operation = new VersionControlServer.OperationOnCollection<T, ResultWithFailures<GetOperation>>() {
            public ResultWithFailures<GetOperation> execute(Collection<T> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                ResultWithFailures<GetOperation> result = new ResultWithFailures();
                List<ChangeRequest> changeRequests = new ArrayList(items.size());
                Iterator var6 = items.iterator();

                while(var6.hasNext()) {
                    changeRequests.add(changeRequestProvider.createChangeRequest((T) var6.next()));
                }

                ArrayOfChangeRequest arrayOfChangeRequest = new ArrayOfChangeRequest();
                arrayOfChangeRequest.setChangeRequest((ChangeRequest[])changeRequests.toArray(new ChangeRequest[0]));
                PendChanges param = new PendChanges();
                param.setOwnerName(workspaceOwner);
                param.setWorkspaceName(workspaceName);
                param.setChanges(arrayOfChangeRequest);
                PendChangesResponse response = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).pendChanges(param);
                if (updateLocalVersion && response.getPendChangesResult().getGetOperation() != null) {
                    ArrayOfLocalVersionUpdate arrayOfLocalVersionUpdate = new ArrayOfLocalVersionUpdate();
                    List<LocalVersionUpdate> localVersionUpdates = new ArrayList(response.getPendChangesResult().getGetOperation().length);
                    GetOperation[] var11 = response.getPendChangesResult().getGetOperation();
                    int var12 = var11.length;

                    for(int var13 = 0; var13 < var12; ++var13) {
                        GetOperation getOperation = var11[var13];
                        localVersionUpdates.add(VersionControlServer.getLocalVersionUpdate(getOperation));
                    }

                    arrayOfLocalVersionUpdate.setLocalVersionUpdate((LocalVersionUpdate[])localVersionUpdates.toArray(new LocalVersionUpdate[0]));
                    UpdateLocalVersion param2 = new UpdateLocalVersion();
                    param2.setOwnerName(workspaceOwner);
                    param2.setWorkspaceName(workspaceName);
                    param2.setUpdates(arrayOfLocalVersionUpdate);
                    VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).updateLocalVersion(param2);
                }

                if (response.getPendChangesResult().getGetOperation() != null) {
                    ContainerUtil.addAll(result.getResult(), response.getPendChangesResult().getGetOperation());
                }

                if (response.getFailures().getFailure() != null) {
                    ContainerUtil.addAll(result.getFailures(), response.getFailures().getFailure());
                }

                return result;
            }

            public ResultWithFailures<GetOperation> merge(Collection<ResultWithFailures<GetOperation>> results) {
                return ResultWithFailures.merge(results);
            }
        };
        return (ResultWithFailures)this.execute(operation, projectOrComponent, paths, progressTitle);
    }

    public Workspace loadWorkspace(final String workspaceName, final String workspaceOwner, Object projectOrComponent, boolean force) throws TfsException {
        return (Workspace)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, force, new Request<Workspace>(TFSBundle.message("load.workspace.0", new Object[]{workspaceName})) {
            public Workspace execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryWorkspace param = new QueryWorkspace();
                param.setOwnerName(workspaceOwner);
                param.setWorkspaceName(workspaceName);
                return VersionControlServer.this.myBeans.getRepository4Stub(credentials, pi).queryWorkspace(param).getQueryWorkspaceResult();
            }
        });
    }

    public void updateWorkspace(final String oldWorkspaceName, final Workspace newWorkspaceDataBean, Object projectOrComponent, boolean force) throws TfsException {
        TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, force, new Request<Void>(TFSBundle.message("save.workspace.0", new Object[]{newWorkspaceDataBean.getName()})) {
            public Void execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                UpdateWorkspace param = new UpdateWorkspace();
                param.setNewWorkspace(newWorkspaceDataBean);
                param.setOldWorkspaceName(oldWorkspaceName);
                param.setOwnerName(credentials.getQualifiedUsername());
                VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).updateWorkspace(param).getUpdateWorkspaceResult();
                return null;
            }
        });
    }

    public Workspace createWorkspace(final Workspace workspaceBean, Object projectOrComponent) throws TfsException {
        return (Workspace)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Workspace>(TFSBundle.message("create.workspace.0", new Object[]{workspaceBean.getName()})) {
            public Workspace execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                CreateWorkspace param = new CreateWorkspace();
                param.setWorkspace(workspaceBean);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).createWorkspace(param).getCreateWorkspaceResult();
            }
        });
    }

    public void deleteWorkspace(final String workspaceName, final String workspaceOwner, Object projectOrComponent, boolean force) throws TfsException {
        TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, force, new Request<Void>(TFSBundle.message("delete.workspace.0", new Object[]{workspaceName})) {
            public Void execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                DeleteWorkspace param = new DeleteWorkspace();
                param.setOwnerName(workspaceOwner);
                param.setWorkspaceName(workspaceName);
                VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).deleteWorkspace(param);
                return null;
            }
        });
    }

    public List<Item> getChildItems(String parentServerItem, final boolean foldersOnly, Object projectOrComponent, String progressTitle) throws TfsException {
        final ArrayOfItemSpec itemSpecs = new ArrayOfItemSpec();
        itemSpecs.setItemSpec(new ItemSpec[]{createItemSpec(parentServerItem, RecursionType.OneLevel)});
        ArrayOfItemSet arrayOfItemSet = (ArrayOfItemSet)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ArrayOfItemSet>(progressTitle) {
            public ArrayOfItemSet execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryItems param = new QueryItems();
                param.setWorkspaceName((String)null);
                param.setWorkspaceOwner((String)null);
                param.setItems(itemSpecs);
                param.setVersion(LatestVersionSpec.INSTANCE);
                param.setDeletedState(DeletedState.NonDeleted);
                param.setItemType(foldersOnly ? ItemType.Folder : ItemType.Any);
                param.setGenerateDownloadUrls(false);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryItems(param).getQueryItemsResult();
            }
        });
        TFSVcs.assertTrue(arrayOfItemSet.getItemSet() != null && arrayOfItemSet.getItemSet().length == 1);
        ItemSet itemSet = arrayOfItemSet.getItemSet()[0];
        if (itemSet.getItems() != null && itemSet.getItems().getItem() != null) {
            List<Item> result = new ArrayList(itemSet.getItems().getItem().length);
            Item[] var9 = itemSet.getItems().getItem();
            int var10 = var9.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                Item item = var9[var11];
                if (!item.getItem().equals(parentServerItem)) {
                    result.add(item);
                }
            }

            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public VersionControlServer.ExtendedItemsAndPendingChanges getExtendedItemsAndPendingChanges(final String workspaceName, final String ownerName, List<ItemSpec> itemsSpecs, final ItemType itemType, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnCollection<ItemSpec, VersionControlServer.ExtendedItemsAndPendingChanges> operation = new VersionControlServer.OperationOnCollection<ItemSpec, VersionControlServer.ExtendedItemsAndPendingChanges>() {
            public VersionControlServer.ExtendedItemsAndPendingChanges execute(Collection<ItemSpec> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
                arrayOfItemSpec.setItemSpec((ItemSpec[])items.toArray(new ItemSpec[0]));
                QueryItemsExtended param = new QueryItemsExtended();
                param.setWorkspaceName(workspaceName);
                param.setWorkspaceOwner(ownerName);
                param.setItems(arrayOfItemSpec);
                param.setDeletedState(DeletedState.NonDeleted);
                param.setItemType(itemType);
                ArrayOfExtendedItem[] extendedItemsArray = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryItemsExtended(param).getQueryItemsExtendedResult().getArrayOfExtendedItem();
                TFSVcs.assertTrue(extendedItemsArray != null && extendedItemsArray.length == items.size());
                List<ExtendedItem> extendedItems = new ArrayList();
                ArrayOfExtendedItem[] var8 = extendedItemsArray;
                int var9 = extendedItemsArray.length;

                for(int var10 = 0; var10 < var9; ++var10) {
                    ArrayOfExtendedItem extendedItem = var8[var10];
                    if (extendedItem.getExtendedItem() != null) {
                        ContainerUtil.addAll(extendedItems, extendedItem.getExtendedItem());
                    }
                }

                QueryPendingSets param2 = new QueryPendingSets();
                param2.setLocalWorkspaceName(workspaceName);
                param2.setLocalWorkspaceOwner(ownerName);
                param2.setQueryWorkspaceName(workspaceName);
                param2.setOwnerName(ownerName);
                param2.setItemSpecs(arrayOfItemSpec);
                param2.setGenerateDownloadUrls(false);
                PendingSet[] pendingSets = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryPendingSets(param2).getQueryPendingSetsResult().getPendingSet();
                List pendingChanges;
                if (pendingSets != null) {
                    TFSVcs.assertTrue(pendingSets.length == 1);
                    pendingChanges = Arrays.asList(pendingSets[0].getPendingChanges().getPendingChange());
                } else {
                    pendingChanges = Collections.emptyList();
                }

                return new VersionControlServer.ExtendedItemsAndPendingChanges(pendingChanges, extendedItems);
            }

            public VersionControlServer.ExtendedItemsAndPendingChanges merge(Collection<VersionControlServer.ExtendedItemsAndPendingChanges> results) {
                List<ExtendedItem> mergedItems = new ArrayList();
                List<PendingChange> mergedPendingChanges = new ArrayList();
                Iterator var4 = results.iterator();

                while(var4.hasNext()) {
                    VersionControlServer.ExtendedItemsAndPendingChanges r = (VersionControlServer.ExtendedItemsAndPendingChanges)var4.next();
                    mergedItems.addAll(r.extendedItems);
                    mergedPendingChanges.addAll(r.pendingChanges);
                }

                return new VersionControlServer.ExtendedItemsAndPendingChanges(mergedPendingChanges, mergedItems);
            }
        };
        return (VersionControlServer.ExtendedItemsAndPendingChanges)this.execute((VersionControlServer.OperationOnCollection)operation, projectOrComponent, (Collection)itemsSpecs, progressTitle);
    }

    @Nullable
    public ExtendedItem getExtendedItem(final String workspaceName, final String ownerName, FilePath localPath, RecursionType recursionType, final DeletedState deletedState, Object projectOrComponent, String progressTitle) throws TfsException {
        final ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
        arrayOfItemSpec.setItemSpec(new ItemSpec[]{createItemSpec(localPath, recursionType)});
        ArrayOfExtendedItem[] extendedItems = (ArrayOfExtendedItem[])TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ArrayOfExtendedItem[]>(progressTitle) {
            public ArrayOfExtendedItem[] execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryItemsExtended param = new QueryItemsExtended();
                param.setDeletedState(deletedState);
                param.setItems(arrayOfItemSpec);
                param.setItemType(ItemType.Any);
                param.setWorkspaceName(workspaceName);
                param.setWorkspaceOwner(ownerName);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryItemsExtended(param).getQueryItemsExtendedResult().getArrayOfExtendedItem();
            }
        });
        TFSVcs.assertTrue(extendedItems != null && extendedItems.length == 1);
        ExtendedItem[] resultItems = extendedItems[0].getExtendedItem();
        return resultItems != null ? chooseExtendedItem(resultItems) : null;
    }

    private static ExtendedItem chooseExtendedItem(ExtendedItem[] extendedItems) {
        TFSVcs.assertTrue(extendedItems.length > 0);
        if (extendedItems.length > 1) {
            ExtendedItem[] var1 = extendedItems;
            int var2 = extendedItems.length;

            int var3;
            for(var3 = 0; var3 < var2; ++var3) {
                ExtendedItem candidate = var1[var3];
                if (candidate.getLocal() != null) {
                    return candidate;
                }
            }

            ExtendedItem latest = extendedItems[0];
            ExtendedItem[] var7 = extendedItems;
            var3 = extendedItems.length;

            for(int var8 = 0; var8 < var3; ++var8) {
                ExtendedItem candidate = var7[var8];
                if (candidate.getLocal() != null && candidate.getLatest() > latest.getLatest()) {
                    latest = candidate;
                }
            }

            return latest;
        } else {
            return extendedItems[0];
        }
    }

    public Map<FilePath, ExtendedItem> getExtendedItems(final String workspaceName, final String ownerName, List<FilePath> paths, final DeletedState deletedState, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnList<FilePath, Map<FilePath, ExtendedItem>> operation = new VersionControlServer.OperationOnList<FilePath, Map<FilePath, ExtendedItem>>() {
            public Map<FilePath, ExtendedItem> execute(List<FilePath> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                List<ItemSpec> itemSpecs = new ArrayList();
                Iterator var5 = items.iterator();

                while(var5.hasNext()) {
                    FilePath path = (FilePath)var5.next();
                    itemSpecs.add(VersionControlServer.createItemSpec(path, RecursionType.None));
                }

                ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
                arrayOfItemSpec.setItemSpec((ItemSpec[])itemSpecs.toArray(new ItemSpec[0]));
                QueryItemsExtended param = new QueryItemsExtended();
                param.setWorkspaceName(workspaceName);
                param.setWorkspaceOwner(ownerName);
                param.setItems(arrayOfItemSpec);
                param.setDeletedState(deletedState);
                param.setItemType(ItemType.Any);
                ArrayOfExtendedItem[] extendedItems = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryItemsExtended(param).getQueryItemsExtendedResult().getArrayOfExtendedItem();
                TFSVcs.assertTrue(extendedItems != null && extendedItems.length == items.size());
                Map<FilePath, ExtendedItem> result = new HashMap();

                for(int i = 0; i < extendedItems.length; ++i) {
                    ExtendedItem[] resultItems = extendedItems[i].getExtendedItem();
                    ExtendedItem item = null;
                    if (resultItems != null) {
                        item = VersionControlServer.chooseExtendedItem(resultItems);
                    }

                    result.put(items.get(i), item);
                }

                return result;
            }

            public Map<FilePath, ExtendedItem> merge(Collection<Map<FilePath, ExtendedItem>> results) {
                Map<FilePath, ExtendedItem> merged = new HashMap();
                Iterator var3 = results.iterator();

                while(var3.hasNext()) {
                    Map<FilePath, ExtendedItem> r = (Map)var3.next();
                    merged.putAll(r);
                }

                return merged;
            }
        };
        return (Map)this.execute(operation, projectOrComponent, paths, progressTitle);
    }

    public void downloadItem(Project project, final String downloadKey, final OutputStream outputStream, String progressTitle) throws TfsException {
        final boolean tryProxy = TFSConfigurationManager.getInstance().shouldTryProxy(this.myServerUri);

        try {
            TfsRequestManager.executeRequest(this.myServerUri, project, new Request<Void>(progressTitle) {
                public Void execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                    String downloadUrl;
                    if (tryProxy) {
                        downloadUrl = TfsUtil.appendPath(TFSConfigurationManager.getInstance().getProxyUri(VersionControlServer.this.myServerUri), "VersionControlProxy/v1.0/item.asmx?" + downloadKey + "&rid=" + VersionControlServer.this.myInstanceId);
                    } else {
                        downloadUrl = TfsUtil.appendPath(serverUri, VersionControlServer.this.myBeans.getDownloadUrl(credentials, pi) + "?" + downloadKey);
                    }

                    VersionControlServer.LOG.debug((tryProxy ? "Downloading via proxy: " : "Downloading: ") + downloadUrl);
                    WebServiceHelper.httpGet(VersionControlServer.this.myServerUri, downloadUrl, outputStream, credentials, VersionControlServer.this.myBeans.getUploadDownloadClient(tryProxy));
                    return null;
                }
            });
        } catch (TfsException var8) {
            LOG.warn("Download failed", var8);
            if (!tryProxy) {
                throw var8;
            }

            TFSVcs.LOG.warn("Disabling proxy");
            String messageHtml = TFSBundle.message("proxy.failed", new Object[]{TfsUtil.getPresentableUri(this.myServerUri), TFSConfigurationManager.getInstance().getProxyUri(this.myServerUri), StringUtil.trimEnd(var8.getMessage(), "."), ApplicationNamesInfo.getInstance().getFullProductName()});
            TfsUtil.showBalloon(project, MessageType.WARNING, messageHtml);
            TFSConfigurationManager.getInstance().setProxyInaccessible(this.myServerUri);
            this.downloadItem(project, downloadKey, outputStream, progressTitle);
        }

    }

    public List<Changeset> queryHistory(WorkspaceInfo workspace, String serverPath, boolean recursive, String user, VersionSpec versionFrom, VersionSpec versionTo, Object projectOrComponent, String progressTitle, int maxCount) throws TfsException {
        VersionSpec itemVersion = LatestVersionSpec.INSTANCE;
        ItemSpec itemSpec = createItemSpec(serverPath, recursive ? RecursionType.Full : null);
        return this.queryHistory(workspace.getName(), workspace.getOwnerName(), itemSpec, user, itemVersion, versionFrom, versionTo, maxCount, projectOrComponent, progressTitle);
    }

    public List<Changeset> queryHistory(final String workspaceName, final String workspaceOwner, final ItemSpec itemSpec, final String user, final VersionSpec itemVersion, final VersionSpec versionFrom, VersionSpec versionTo, int maxCount, Object projectOrComponent, String progressTitle) throws TfsException {
        List<Changeset> allChangeSets = new ArrayList();
        int total = maxCount > 0 ? maxCount : 2147483647;
        final Ref versionToCurrent = new Ref(versionTo);

        while(total > 0) {
            final int batchMax = Math.min(256, total);
            Changeset[] currentChangeSets = (Changeset[])TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Changeset[]>(progressTitle) {
                public Changeset[] execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                    QueryHistory param = new QueryHistory();
                    param.setWorkspaceName(workspaceName);
                    param.setWorkspaceOwner(workspaceOwner);
                    param.setItemSpec(itemSpec);
                    param.setVersionItem(itemVersion);
                    param.setUser(user);
                    param.setVersionFrom(versionFrom);
                    param.setVersionTo((VersionSpec)versionToCurrent.get());
                    param.setMaxCount(batchMax);
                    param.setIncludeFiles(true);
                    param.setGenerateDownloadUrls(false);
                    param.setSlotMode(false);
                    return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryHistory(param).getQueryHistoryResult().getChangeset();
                }
            });
            if (currentChangeSets != null) {
                ContainerUtil.addAll(allChangeSets, currentChangeSets);
            }

            if (currentChangeSets == null || currentChangeSets.length < batchMax) {
                break;
            }

            total -= currentChangeSets.length;
            Changeset lastChangeSet = currentChangeSets[currentChangeSets.length - 1];
            versionToCurrent.set(new ChangesetVersionSpec(lastChangeSet.getCset()));
        }

        return allChangeSets;
    }

    public Workspace[] queryWorkspaces(final String computer, Object projectOrComponent, boolean force) throws TfsException {
        Workspace[] workspaces = (Workspace[])TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, force, new Request<Workspace[]>(TFSBundle.message("reload.workspaces", new Object[0])) {
            public Workspace[] execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryWorkspaces param = new QueryWorkspaces();
                param.setComputer(computer);
                param.setOwnerName(credentials.getQualifiedUsername());
                return VersionControlServer.this.myBeans.getRepository4Stub(credentials, pi).queryWorkspaces(param).getQueryWorkspacesResult().getWorkspace();
            }
        });
        return workspaces != null ? workspaces : new Workspace[0];
    }

    @Nullable
    public GetOperation get(String workspaceName, String ownerName, String path, VersionSpec versionSpec, Object projectOrComponent, String progressTitle) throws TfsException {
        List<GetOperation> operations = this.get(workspaceName, ownerName, path, versionSpec, RecursionType.None, projectOrComponent, progressTitle);
        TFSVcs.assertTrue(operations.size() == 1);
        return (GetOperation)operations.get(0);
    }

    public List<GetOperation> get(String workspaceName, String ownerName, String path, VersionSpec versionSpec, RecursionType recursionType, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.GetRequestParams getRequest = new VersionControlServer.GetRequestParams(path, recursionType, versionSpec);
        return this.get(workspaceName, ownerName, Collections.singletonList(getRequest), projectOrComponent, progressTitle);
    }

    public static LocalVersionUpdate getLocalVersionUpdate(GetOperation operation) {
        LocalVersionUpdate localVersionUpdate = new LocalVersionUpdate();
        localVersionUpdate.setItemid(operation.getItemid());
        localVersionUpdate.setTlocal(operation.getTlocal());
        localVersionUpdate.setLver(operation.getSver() != -2147483648 ? operation.getSver() : 0);
        return localVersionUpdate;
    }

    public void updateLocalVersions(final String workspaceName, final String workspaceOwnerName, Collection<LocalVersionUpdate> updates, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnCollection<LocalVersionUpdate, Void> operation = new VersionControlServer.OperationOnCollection<LocalVersionUpdate, Void>() {
            public Void execute(Collection<LocalVersionUpdate> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                ArrayOfLocalVersionUpdate arrayOfLocalVersionUpdate = new ArrayOfLocalVersionUpdate();
                arrayOfLocalVersionUpdate.setLocalVersionUpdate((LocalVersionUpdate[])items.toArray(new LocalVersionUpdate[0]));
                UpdateLocalVersion param = new UpdateLocalVersion();
                param.setOwnerName(workspaceOwnerName);
                param.setWorkspaceName(workspaceName);
                param.setUpdates(arrayOfLocalVersionUpdate);
                VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).updateLocalVersion(param);
                return null;
            }

            public Void merge(Collection<Void> results) {
                return null;
            }
        };
        this.execute(operation, projectOrComponent, updates, progressTitle);
    }

    public ResultWithFailures<GetOperation> undoPendingChanges(final String workspaceName, final String workspaceOwner, Collection<String> serverPaths, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnCollection<String, ResultWithFailures<GetOperation>> operation = new VersionControlServer.OperationOnCollection<String, ResultWithFailures<GetOperation>>() {
            public ResultWithFailures<GetOperation> execute(Collection<String> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                List<ItemSpec> itemSpecs = new ArrayList(items.size());
                Iterator var5 = items.iterator();

                while(var5.hasNext()) {
                    String serverPath = (String)var5.next();
                    itemSpecs.add(VersionControlServer.createItemSpec((String)serverPath, (RecursionType)null));
                }

                ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
                arrayOfItemSpec.setItemSpec((ItemSpec[])itemSpecs.toArray(new ItemSpec[0]));
                UndoPendingChanges param = new UndoPendingChanges();
                param.setOwnerName(workspaceOwner);
                param.setWorkspaceName(workspaceName);
                param.setItems(arrayOfItemSpec);
                UndoPendingChangesResponse response = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).undoPendingChanges(param);
                GetOperation[] getOperations = response.getUndoPendingChangesResult() != null ? response.getUndoPendingChangesResult().getGetOperation() : null;
                Failure[] failures = response.getFailures() != null ? response.getFailures().getFailure() : null;
                return new ResultWithFailures(getOperations, failures);
            }

            public ResultWithFailures<GetOperation> merge(Collection<ResultWithFailures<GetOperation>> results) {
                return ResultWithFailures.merge(results);
            }
        };
        return (ResultWithFailures)this.execute(operation, projectOrComponent, serverPaths, progressTitle);
    }

    public List<GetOperation> get(final String workspaceName, final String workspaceOwner, List<VersionControlServer.GetRequestParams> requests, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnList<VersionControlServer.GetRequestParams, List<GetOperation>> operation = new VersionControlServer.OperationOnList<VersionControlServer.GetRequestParams, List<GetOperation>>() {
            public List<GetOperation> execute(List<VersionControlServer.GetRequestParams> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                List<GetRequest> getRequests = new ArrayList(items.size());
                Iterator var5 = items.iterator();

                while(var5.hasNext()) {
                    VersionControlServer.GetRequestParams getRequestParams = (VersionControlServer.GetRequestParams)var5.next();
                    GetRequest getRequest = new GetRequest();
                    getRequest.setItemSpec(VersionControlServer.createItemSpec(getRequestParams.serverPath, getRequestParams.recursionType));
                    getRequest.setVersionSpec(getRequestParams.version);
                    getRequests.add(getRequest);
                }

                ArrayOfGetRequest arrayOfGetRequests = new ArrayOfGetRequest();
                arrayOfGetRequests.setGetRequest((GetRequest[])getRequests.toArray(new GetRequest[0]));
                Get param = new Get();
                param.setWorkspaceName(workspaceName);
                param.setOwnerName(workspaceOwner);
                param.setRequests(arrayOfGetRequests);
                param.setForce(true);
                param.setNoGet(false);
                ArrayOfArrayOfGetOperation response = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).get(param).getGetResult();
                TFSVcs.assertTrue(response.getArrayOfGetOperation() != null && response.getArrayOfGetOperation().length >= items.size());
                List<GetOperation> results = new ArrayList();
                ArrayOfGetOperation[] var9 = response.getArrayOfGetOperation();
                int var10 = var9.length;

                for(int var11 = 0; var11 < var10; ++var11) {
                    ArrayOfGetOperation arrayOfGetOperation = var9[var11];
                    if (arrayOfGetOperation.getGetOperation() != null) {
                        ContainerUtil.addAll(results, arrayOfGetOperation.getGetOperation());
                    }
                }

                return results;
            }

            public List<GetOperation> merge(Collection<List<GetOperation>> results) {
                List<GetOperation> merged = new ArrayList();
                Iterator var3 = results.iterator();

                while(var3.hasNext()) {
                    List<GetOperation> r = (List)var3.next();
                    merged.addAll(r);
                }

                return merged;
            }
        };
        return (List)this.execute(operation, projectOrComponent, requests, progressTitle);
    }

    public void addLocalConflict(final String workspaceName, final String workspaceOwner, final int itemId, final int versionFrom, final int pendingChangeId, final String sourceLocal, final String targetLocal, final int reason, Object projectOrComponent, String progressTitle) throws TfsException {
        TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<AddConflictResponse>(progressTitle) {
            public AddConflictResponse execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                AddConflict param = new AddConflict();
                param.setWorkspaceName(workspaceName);
                param.setOwnerName(workspaceOwner);
                param.setConflictType(ConflictType.Local);
                param.setItemId(itemId);
                param.setVersionFrom(versionFrom);
                param.setPendingChangeId(pendingChangeId);
                param.setSourceLocalItem(sourceLocal);
                param.setTargetLocalItem(targetLocal);
                param.setReason(reason);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).addConflict(param);
            }
        });
    }

    public Collection<Conflict> queryConflicts(final String workspaceName, final String ownerName, List<ItemPath> paths, final RecursionType recursionType, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnCollection<ItemPath, Collection<Conflict>> operation = new VersionControlServer.OperationOnCollection<ItemPath, Collection<Conflict>>() {
            public Collection<Conflict> execute(Collection<ItemPath> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                List<ItemSpec> itemSpecList = new ArrayList();
                Iterator var5 = items.iterator();

                while(var5.hasNext()) {
                    ItemPath path = (ItemPath)var5.next();
                    itemSpecList.add(VersionControlServer.createItemSpec(path.getServerPath(), recursionType));
                }

                ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
                arrayOfItemSpec.setItemSpec((ItemSpec[])itemSpecList.toArray(new ItemSpec[0]));
                QueryConflicts param = new QueryConflicts();
                param.setWorkspaceName(workspaceName);
                param.setOwnerName(ownerName);
                param.setItems(arrayOfItemSpec);
                Conflict[] conflicts = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryConflicts(param).getQueryConflictsResult().getConflict();
                return conflicts != null ? Arrays.asList(conflicts) : Collections.emptyList();
            }

            public Collection<Conflict> merge(Collection<Collection<Conflict>> results) {
                return VersionControlServer.mergeStatic(results);
            }
        };
        return (Collection)this.execute((VersionControlServer.OperationOnCollection)operation, projectOrComponent, (Collection)paths, progressTitle);
    }

    private static <T> Collection<T> mergeStatic(Collection<Collection<T>> results) {
        Collection<T> merged = new ArrayList();
        Iterator var2 = results.iterator();

        while(var2.hasNext()) {
            Collection<T> r = (Collection)var2.next();
            merged.addAll(r);
        }

        return merged;
    }

    public ResolveResponse resolveConflict(final String workspaceName, final String workspasceOwnerName, final VersionControlServer.ResolveConflictParams params, Object projectOrComponent, String progressTitle) throws TfsException {
        return (ResolveResponse)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ResolveResponse>(progressTitle) {
            public ResolveResponse execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                Resolve param = new Resolve();
                param.setWorkspaceName(workspaceName);
                param.setOwnerName(workspasceOwnerName);
                param.setConflictId(params.conflictId);
                param.setResolution(params.resolution);
                param.setNewPath(params.newPath);
                param.setEncoding(params.encoding);
                param.setLockLevel(params.lockLevel);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).resolve(param);
            }
        });
    }

    public void uploadItem(final WorkspaceInfo workspaceInfo, final PendingChange change, Object projectOrComponent, String progressTitle) throws TfsException, IOException {
        TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Void>(progressTitle) {
            public Void execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                String uploadUrl = TfsUtil.appendPath(VersionControlServer.this.myServerUri, VersionControlServer.this.myBeans.getUploadUrl(credentials, pi));
                File file = VersionControlPath.getFile(change.getLocal());
                long fileLength = file.length();
                ArrayList<Part> parts = new ArrayList();
                parts.add(new StringPart("item", change.getItem(), "UTF-8"));
                parts.add(new StringPart("wsname", workspaceInfo.getName()));
                parts.add(new StringPart("wsowner", workspaceInfo.getOwnerName()));
                parts.add(new StringPart("filelength", Long.toString(fileLength)));
                byte[] hash = TfsFileUtil.calculateMD5(file);
                parts.add(new StringPart("hash", Base64.getEncoder().encodeToString(hash)));
                parts.add(new StringPart("range", String.format("bytes=0-%d/%d", fileLength - 1L, fileLength)));
                FilePart filePart = new FilePart("content", "item", file);
                parts.add(filePart);
                filePart.setCharSet((String)null);
                WebServiceHelper.httpPost(uploadUrl, (Part[])parts.toArray(new Part[0]), (OutputStream)null, credentials, serverUri, VersionControlServer.this.myBeans.getUploadDownloadClient(false));
                return null;
            }
        });
    }

    public Collection<PendingChange> queryPendingSetsByLocalPaths(String workspaceName, String workspaceOwnerName, Collection<ItemPath> paths, RecursionType recursionType, Object projectOrComponent, String progressTitle) throws TfsException {
        Collection<ItemSpec> itemSpecs = new ArrayList(paths.size());
        Iterator var8 = paths.iterator();

        while(var8.hasNext()) {
            ItemPath path = (ItemPath)var8.next();
            itemSpecs.add(createItemSpec(VersionControlPath.toTfsRepresentation(path.getLocalPath()), recursionType));
        }

        return this.doQueryPendingSets(workspaceName, workspaceOwnerName, itemSpecs, projectOrComponent, progressTitle);
    }

    public Collection<PendingChange> queryPendingSetsByServerItems(String workspaceName, String workspaceOwnerName, Collection<String> serverItems, RecursionType recursionType, Object projectOrComponent, String progressTitle) throws TfsException {
        List<ItemSpec> itemSpecs = new ArrayList(serverItems.size());
        Iterator var8 = serverItems.iterator();

        while(var8.hasNext()) {
            String serverItem = (String)var8.next();
            itemSpecs.add(createItemSpec(serverItem, recursionType));
        }

        return this.doQueryPendingSets(workspaceName, workspaceOwnerName, itemSpecs, projectOrComponent, progressTitle);
    }

    private Collection<PendingChange> doQueryPendingSets(final String workspaceName, final String workspaceOwnerName, Collection<ItemSpec> itemSpecs, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlServer.OperationOnCollection<ItemSpec, Collection<PendingChange>> operation = new VersionControlServer.OperationOnCollection<ItemSpec, Collection<PendingChange>>() {
            public Collection<PendingChange> execute(Collection<ItemSpec> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
                arrayOfItemSpec.setItemSpec((ItemSpec[])items.toArray(new ItemSpec[0]));
                QueryPendingSets param = new QueryPendingSets();
                param.setLocalWorkspaceName(workspaceName);
                param.setLocalWorkspaceOwner(workspaceOwnerName);
                param.setQueryWorkspaceName(workspaceName);
                param.setOwnerName(workspaceOwnerName);
                param.setItemSpecs(arrayOfItemSpec);
                param.setGenerateDownloadUrls(false);
                PendingSet[] pendingSets = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryPendingSets(param).getQueryPendingSetsResult().getPendingSet();
                return pendingSets != null ? Arrays.asList(pendingSets[0].getPendingChanges().getPendingChange()) : Collections.emptyList();
            }

            public Collection<PendingChange> merge(Collection<Collection<PendingChange>> results) {
                return VersionControlServer.mergeStatic(results);
            }
        };
        return (Collection)this.execute(operation, projectOrComponent, itemSpecs, progressTitle);
    }

    public ResultWithFailures<CheckinResult> checkIn(final String workspaceName, final String workspaceOwnerName, Collection<String> serverItems, String comment, @NotNull Map<WorkItem, CheckinWorkItemAction> workItemsActions, List<Pair<String, String>> checkinNotes, @Nullable Pair<String, Map<String, String>> policyOverride, Object projectOrComponent, String progressTitle) throws TfsException {

        ArrayOfCheckinNoteFieldValue fieldValues = new ArrayOfCheckinNoteFieldValue();
        Iterator var11 = checkinNotes.iterator();

        while(var11.hasNext()) {
            Pair<String, String> checkinNote = (Pair)var11.next();
            CheckinNoteFieldValue fieldValue = new CheckinNoteFieldValue();
            fieldValue.setName((String)checkinNote.first);
            fieldValue.setVal((String)checkinNote.second);
            fieldValues.addCheckinNoteFieldValue(fieldValue);
        }

        CheckinNote checkinNote = new CheckinNote();
        checkinNote.setValues(fieldValues);
        PolicyOverrideInfo policyOverrideInfo = new PolicyOverrideInfo();
        if (policyOverride != null) {
            policyOverrideInfo.setComment((String)policyOverride.first);
            ArrayOfPolicyFailureInfo policyFailures = new ArrayOfPolicyFailureInfo();
            Iterator var14 = ((Map)policyOverride.second).entrySet().iterator();

            while(var14.hasNext()) {
                Entry<String, String> entry = (Entry)var14.next();
                PolicyFailureInfo policyFailureInfo = new PolicyFailureInfo();
                policyFailureInfo.setPolicyName((String)entry.getKey());
                policyFailureInfo.setMessage((String)entry.getValue());
                policyFailures.addPolicyFailureInfo(policyFailureInfo);
            }

            policyOverrideInfo.setPolicyFailures(policyFailures);
        }

        final Changeset changeset = new Changeset();
        changeset.setCset(0);
        changeset.setDate(TfsUtil.getZeroCalendar());
        changeset.setOwner(workspaceOwnerName);
        changeset.setComment(comment);
        changeset.setCheckinNote(checkinNote);
        changeset.setPolicyOverride(policyOverrideInfo);
        final CheckinNotificationInfo checkinNotificationInfo = new CheckinNotificationInfo();
        checkinNotificationInfo.setWorkItemInfo(toArrayOfCheckinNotificationWorkItemInfo(workItemsActions));
        final CheckinOptions checkinOptions = new CheckinOptions();
        checkinOptions.setCheckinOptions_type0(new CheckinOptions_type0[]{CheckinOptions_type0.ValidateCheckinOwner});
        VersionControlServer.OperationOnCollection<String, ResultWithFailures<CheckinResult>> operation = new VersionControlServer.OperationOnCollection<String, ResultWithFailures<CheckinResult>>() {
            public ResultWithFailures<CheckinResult> execute(Collection<String> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                CheckIn param = new CheckIn();
                param.setWorkspaceName(workspaceName);
                param.setOwnerName(workspaceOwnerName);
                param.setServerItems(TfsUtil.toArrayOfString(items));
                param.setInfo(changeset);
                param.setCheckinNotificationInfo(checkinNotificationInfo);
                param.setCheckinOptions(checkinOptions);
                CheckInResponse response = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).checkIn(param);
                ResultWithFailures<CheckinResult> result = new ResultWithFailures();
                if (response.getCheckInResult() != null) {
                    result.getResult().add(response.getCheckInResult());
                }

                if (response.getFailures().getFailure() != null) {
                    ContainerUtil.addAll(result.getFailures(), response.getFailures().getFailure());
                }

                return result;
            }

            public ResultWithFailures<CheckinResult> merge(Collection<ResultWithFailures<CheckinResult>> results) {
                return ResultWithFailures.merge(results);
            }
        };
        return (ResultWithFailures)this.execute(operation, projectOrComponent, serverItems, progressTitle);
    }

    @Nullable
    private static ArrayOfCheckinNotificationWorkItemInfo toArrayOfCheckinNotificationWorkItemInfo(@NotNull Map<WorkItem, CheckinWorkItemAction> workItemsActions) {

        if (workItemsActions.size() == 0) {
            return null;
        } else {
            List<CheckinNotificationWorkItemInfo> checkinNotificationWorkItemInfoArray = new ArrayList(workItemsActions.size());
            Iterator var2 = workItemsActions.entrySet().iterator();

            while(var2.hasNext()) {
                Entry<WorkItem, CheckinWorkItemAction> e = (Entry)var2.next();
                if (e.getValue() != CheckinWorkItemAction.None) {
                    CheckinNotificationWorkItemInfo checkinNotificationWorkItemInfo = new CheckinNotificationWorkItemInfo();
                    checkinNotificationWorkItemInfo.setId(((WorkItem)e.getKey()).getId());
                    checkinNotificationWorkItemInfo.setCheckinAction((CheckinWorkItemAction)e.getValue());
                    checkinNotificationWorkItemInfoArray.add(checkinNotificationWorkItemInfo);
                }
            }

            ArrayOfCheckinNotificationWorkItemInfo arrayOfCheckinNotificationWorkItemInfo = new ArrayOfCheckinNotificationWorkItemInfo();
            arrayOfCheckinNotificationWorkItemInfo.setCheckinNotificationWorkItemInfo((CheckinNotificationWorkItemInfo[])checkinNotificationWorkItemInfoArray.toArray(new CheckinNotificationWorkItemInfo[0]));
            return arrayOfCheckinNotificationWorkItemInfo;
        }
    }

    public MergeResponse merge(final String workspaceName, final String ownerName, String sourceServerPath, String targetServerPath, final VersionSpecBase fromVersion, final VersionSpecBase toVersion, Object projectOrComponent, String progressTitle) throws TfsException {
        final ItemSpec source = createItemSpec(sourceServerPath, RecursionType.Full);
        final ItemSpec target = createItemSpec((String)targetServerPath, (RecursionType)null);
        return (MergeResponse)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<MergeResponse>(progressTitle) {
            public MergeResponse execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                Merge param = new Merge();
                param.setWorkspaceName(workspaceName);
                param.setWorkspaceOwner(ownerName);
                param.setSource(source);
                param.setTarget(target);
                param.setFrom(fromVersion);
                param.setTo(toVersion);
                MergeOptions mergeOptions = new MergeOptions();
                mergeOptions.setMergeOptions_type0(new MergeOptions_type0[]{MergeOptions_type0.None});
                param.setOptions(mergeOptions);
                param.setLockLevel(LockLevel.Unchanged);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).merge(param);
            }
        });
    }

    public List<CheckinNoteFieldDefinition> queryCheckinNoteDefinition(final Collection<String> teamProjects, Object projectOrComponent, String progressTitle) throws TfsException {
        ArrayOfCheckinNoteFieldDefinition result = (ArrayOfCheckinNoteFieldDefinition)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ArrayOfCheckinNoteFieldDefinition>(progressTitle) {
            public ArrayOfCheckinNoteFieldDefinition execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryCheckinNoteDefinition param = new QueryCheckinNoteDefinition();
                param.setAssociatedServerItem(TfsUtil.toArrayOfString(teamProjects));
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryCheckinNoteDefinition(param).getQueryCheckinNoteDefinitionResult();
            }
        });
        CheckinNoteFieldDefinition[] definitions = result.getCheckinNoteFieldDefinition();
        return definitions == null ? Collections.emptyList() : Arrays.asList(definitions);
    }

    public Collection<Annotation> queryAnnotations(final String annotationName, final String serverItem, Object projectOrComponent, String progressTitle, boolean force) throws TfsException {
        ArrayOfAnnotation arrayOfAnnotation = (ArrayOfAnnotation)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, force, new Request<ArrayOfAnnotation>(progressTitle) {
            public ArrayOfAnnotation execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryAnnotation param = new QueryAnnotation();
                param.setAnnotationName(annotationName);
                param.setAnnotatedItem(serverItem);
                param.setVersion(0);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryAnnotation(param).getQueryAnnotationResult();
            }
        });
        if (arrayOfAnnotation != null && arrayOfAnnotation.getAnnotation() != null) {
            Collection<Annotation> result = new ArrayList();
            Annotation[] var8 = arrayOfAnnotation.getAnnotation();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
                Annotation annotation = var8[var10];
                if (annotationName.equals(annotation.getName())) {
                    result.add(annotation);
                }
            }

            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public void createAnnotation(final String serverItem, final String annotationName, final String annotationValue, Object projectOrComponent, String progressTitle) throws TfsException {
        TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Void>(progressTitle) {
            public Void execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                CreateAnnotation param = new CreateAnnotation();
                param.setAnnotationName(annotationName);
                param.setAnnotationValue(annotationValue);
                param.setAnnotatedItem(serverItem);
                param.setVersion(0);
                param.setOverwrite(true);
                VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).createAnnotation(param);
                return null;
            }
        });
    }

    public void deleteAnnotation(final String serverItem, final String annotationName, Object projectOrComponent, String progressTitle) throws TfsException {
        TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Void>(progressTitle) {
            public Void execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                DeleteAnnotation param = new DeleteAnnotation();
                param.setAnnotationName(annotationName);
                param.setAnnotatedItem(serverItem);
                param.setVersion(0);
                VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).deleteAnnotation(param);
                return null;
            }
        });
    }

    @Nullable
    public Item queryItem(final String workspaceName, final String ownerName, String itemServerPath, final VersionSpec versionSpec, final DeletedState deletedState, final boolean generateDownloadUrl, Object projectOrComponent, String progressTitle) throws TfsException {
        final ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
        arrayOfItemSpec.setItemSpec(new ItemSpec[]{createItemSpec(itemServerPath, RecursionType.None)});
        ItemSet[] items = (ItemSet[])TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ItemSet[]>(progressTitle) {
            public ItemSet[] execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryItems param = new QueryItems();
                param.setWorkspaceName(workspaceName);
                param.setWorkspaceOwner(ownerName);
                param.setItems(arrayOfItemSpec);
                param.setVersion(versionSpec);
                param.setItemType(ItemType.Any);
                param.setDeletedState(deletedState);
                param.setGenerateDownloadUrls(generateDownloadUrl);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryItems(param).getQueryItemsResult().getItemSet();
            }
        });
        TFSVcs.assertTrue(items != null && items.length == 1);
        Item[] resultItems = items[0].getItems().getItem();
        if (resultItems != null) {
            TFSVcs.assertTrue(resultItems.length == 1);
            return resultItems[0];
        } else {
            return null;
        }
    }

    public List<Item> queryItems(ItemSpec itemSpec, final VersionSpec version, Object projectOrComponent, String progressTitle) throws TfsException {
        final ArrayOfItemSpec itemSpecs = new ArrayOfItemSpec();
        itemSpecs.setItemSpec(new ItemSpec[]{itemSpec});
        ArrayOfItemSet arrayOfItemSet = (ArrayOfItemSet)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ArrayOfItemSet>(progressTitle) {
            public ArrayOfItemSet execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryItems param = new QueryItems();
                param.setWorkspaceName((String)null);
                param.setWorkspaceOwner((String)null);
                param.setItems(itemSpecs);
                param.setVersion(version);
                param.setDeletedState(DeletedState.NonDeleted);
                param.setItemType(ItemType.Any);
                param.setGenerateDownloadUrls(false);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryItems(param).getQueryItemsResult();
            }
        });
        TFSVcs.assertTrue(arrayOfItemSet.getItemSet() != null && arrayOfItemSet.getItemSet().length == 1);
        ItemSet itemSet = arrayOfItemSet.getItemSet()[0];
        if (itemSet.getItems() != null && itemSet.getItems().getItem() != null) {
            List<Item> result = new ArrayList(itemSet.getItems().getItem().length);
            ContainerUtil.addAll(result, itemSet.getItems().getItem());
            return result;
        } else {
            return Collections.emptyList();
        }
    }

    public Changeset queryChangeset(final int changesetId, Object projectOrComponent, String progressTitle) throws TfsException {
        return (Changeset)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Changeset>(progressTitle) {
            public Changeset execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryChangeset param = new QueryChangeset();
                param.setChangesetId(changesetId);
                param.setIncludeChanges(true);
                param.setGenerateDownloadUrls(false);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryChangeset(param).getQueryChangesetResult();
            }
        });
    }

    public List<VersionControlLabel> queryLabels(final String labelName, final String labelScope, final String owner, final boolean includeItems, final String filterItem, final VersionSpec versionFilterItem, final boolean generateDownloadUrls, Object projectOrComponent, String progressTitle) throws TfsException {
        VersionControlLabel[] labels = (VersionControlLabel[])TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<VersionControlLabel[]>(progressTitle) {
            public VersionControlLabel[] execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryLabels param = new QueryLabels();
                param.setWorkspaceName((String)null);
                param.setWorkspaceOwner((String)null);
                param.setLabelName(labelName);
                param.setLabelScope(labelScope);
                param.setOwner(owner);
                param.setFilterItem(filterItem);
                param.setVersionFilterItem(versionFilterItem);
                param.setIncludeItems(includeItems);
                param.setGenerateDownloadUrls(generateDownloadUrls);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryLabels(param).getQueryLabelsResult().getVersionControlLabel();
            }
        });
        ArrayList<VersionControlLabel> result = new ArrayList();
        if (labels != null) {
            ContainerUtil.addAll(result, labels);
        }

        return result;
    }

    public ResultWithFailures<LabelResult> labelItem(String labelName, String labelComment, List<LabelItemSpec> labelItemSpecs, Object projectOrComponent, String progressTitle) throws TfsException {
        final VersionControlLabel versionControlLabel = new VersionControlLabel();
        versionControlLabel.setName(labelName);
        versionControlLabel.setComment(labelComment);
        versionControlLabel.setDate(TfsUtil.getZeroCalendar());
        VersionControlServer.OperationOnCollection<LabelItemSpec, ResultWithFailures<LabelResult>> operation = new VersionControlServer.OperationOnCollection<LabelItemSpec, ResultWithFailures<LabelResult>>() {
            public ResultWithFailures<LabelResult> execute(Collection<LabelItemSpec> items, Credentials credentials, ProgressIndicator pi) throws RemoteException, HostNotApplicableException {
                ArrayOfLabelItemSpec arrayOfLabelItemSpec = new ArrayOfLabelItemSpec();
                arrayOfLabelItemSpec.setLabelItemSpec((LabelItemSpec[])items.toArray(new LabelItemSpec[0]));
                LabelItem param = new LabelItem();
                param.setWorkspaceName((String)null);
                param.setWorkspaceOwner((String)null);
                param.setLabel(versionControlLabel);
                param.setLabelSpecs(arrayOfLabelItemSpec);
                param.setChildren(LabelChildOption.Fail);
                LabelItemResponse labelItemResponse = VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).labelItem(param);
                ArrayOfLabelResult results = labelItemResponse.getLabelItemResult();
                ArrayOfFailure failures = labelItemResponse.getFailures();
                return new ResultWithFailures(results == null ? null : results.getLabelResult(), failures == null ? null : failures.getFailure());
            }

            public ResultWithFailures<LabelResult> merge(Collection<ResultWithFailures<LabelResult>> results) {
                return ResultWithFailures.merge(results);
            }
        };
        return (ResultWithFailures)this.execute((VersionControlServer.OperationOnCollection)operation, projectOrComponent, (Collection)labelItemSpecs, progressTitle);
    }

    public Collection<BranchRelative> queryBranches(String itemServerPath, final VersionSpec versionSpec, Object projectOrComponent, String progressTitle) throws TfsException {
        final ArrayOfItemSpec arrayOfItemSpec = new ArrayOfItemSpec();
        arrayOfItemSpec.setItemSpec(new ItemSpec[]{createItemSpec((String)itemServerPath, (RecursionType)null)});
        ArrayOfArrayOfBranchRelative result = (ArrayOfArrayOfBranchRelative)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ArrayOfArrayOfBranchRelative>(progressTitle) {
            public ArrayOfArrayOfBranchRelative execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryBranches param = new QueryBranches();
                param.setWorkspaceName((String)null);
                param.setWorkspaceOwner((String)null);
                param.setItems(arrayOfItemSpec);
                param.setVersion(versionSpec);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryBranches(param).getQueryBranchesResult();
            }
        });
        TFSVcs.assertTrue(result.getArrayOfBranchRelative().length == 1);
        BranchRelative[] branches = result.getArrayOfBranchRelative()[0].getBranchRelative();
        return branches != null ? Arrays.asList(branches) : Collections.emptyList();
    }

    public Collection<MergeCandidate> queryMergeCandidates(final String workspaceName, final String ownerName, String sourceServerPath, String targetServerPath, Object projectOrComponent, String progressTitle) throws TfsException {
        final ItemSpec source = createItemSpec(sourceServerPath, RecursionType.Full);
        final ItemSpec target = createItemSpec(targetServerPath, RecursionType.Full);
        ArrayOfMergeCandidate result = (ArrayOfMergeCandidate)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<ArrayOfMergeCandidate>(progressTitle) {
            public ArrayOfMergeCandidate execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryMergeCandidates param = new QueryMergeCandidates();
                param.setWorkspaceName(workspaceName);
                param.setWorkspaceOwner(ownerName);
                param.setSource(source);
                param.setTarget(target);
                return VersionControlServer.this.myBeans.getRepositoryStub(credentials, pi).queryMergeCandidates(param).getQueryMergeCandidatesResult();
            }
        });
        return result.getMergeCandidate() != null ? Arrays.asList(result.getMergeCandidate()) : Collections.emptyList();
    }

    public Identity readIdentity(final String qualifiedUsername, Object projectOrComponent, String progressTitle) throws TfsException {
        final SearchFactor searchFactor = SearchFactor.AccountName;
        final QueryMembership queryMembership = QueryMembership.None;
        return (Identity)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Identity>(progressTitle) {
            public Identity execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                ReadIdentity param = new ReadIdentity();
                param.setFactor(searchFactor);
                param.setFactorValue(qualifiedUsername);
                param.setQueryMembership(queryMembership);
                return VersionControlServer.this.myBeans.getGroupSecurityServiceStub(credentials, pi).readIdentity(param).getReadIdentityResult();
            }
        });
    }

    private static RequestHeaderE generateRequestHeader() {
        RequestHeader requestHeader = new RequestHeader();
        requestHeader.setId("uuid:" + UUID.randomUUID().toString());
        RequestHeaderE requestHeader3 = new RequestHeaderE();
        requestHeader3.setRequestHeader(requestHeader);
        return requestHeader3;
    }

    public List<WorkItem> queryWorkItems(Query_type0E query, Object projectOrComponent, String progressTitle) throws TfsException {
        final PsQuery_type1 psQuery_type1 = new PsQuery_type1();
        psQuery_type1.setQuery(query);
        QueryWorkitemsResponse queryWorkitemsResponse = (QueryWorkitemsResponse)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<QueryWorkitemsResponse>(progressTitle) {
            public QueryWorkitemsResponse execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                QueryWorkitems param = new QueryWorkitems();
                param.setPsQuery(psQuery_type1);
                return VersionControlServer.this.myBeans.getWorkItemServiceStub(credentials, pi).queryWorkitems(param, VersionControlServer.generateRequestHeader());
            }
        });
        List<Integer> ids = parseWorkItemsIds(queryWorkitemsResponse);
        Collections.sort(ids);
        return this.pageWorkitemsByIds(ids, projectOrComponent, progressTitle);
    }

    private static List<Integer> parseWorkItemsIds(QueryWorkitemsResponse queryWorkitemsResponse) {
        Id_type0[] ids_type0 = queryWorkitemsResponse.getResultIds().getQueryIds().getId();
        List<Integer> workItemsIdSet = new ArrayList();
        if (ids_type0 != null) {
            Id_type0[] var3 = ids_type0;
            int var4 = ids_type0.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                Id_type0 id_type0 = var3[var5];
                int startIndex = id_type0.getS();
                int endIndex = id_type0.getE();
                if (endIndex > startIndex) {
                    for(int i = startIndex; i <= endIndex; ++i) {
                        workItemsIdSet.add(i);
                    }
                } else {
                    workItemsIdSet.add(startIndex);
                }
            }
        }

        return workItemsIdSet;
    }

    private List<WorkItem> pageWorkitemsByIds(Collection<Integer> workItemsIds, Object projectOrComponent, String progressTitle) throws TfsException {
        if (workItemsIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            int[] idsAsArray = new int[workItemsIds.size()];
            int i = 0;

            Integer id;
            for(Iterator var6 = workItemsIds.iterator(); var6.hasNext(); idsAsArray[i++] = id) {
                id = (Integer)var6.next();
            }

            final com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ArrayOfInt workitemIds = new com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ArrayOfInt();
            workitemIds.set_int(idsAsArray);
            final ArrayOfString workItemFields = new ArrayOfString();
            List<String> serializedFields = new ArrayList();
            Iterator var9 = WorkItemSerialize.FIELDS.iterator();

            while(var9.hasNext()) {
                WorkItemField field = (WorkItemField)var9.next();
                serializedFields.add(field.getSerialized());
            }

            workItemFields.setString(ArrayUtilRt.toStringArray(serializedFields));
            PageWorkitemsByIdsResponse pageWorkitemsByIdsResponse = (PageWorkitemsByIdsResponse)TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<PageWorkitemsByIdsResponse>(progressTitle) {
                public PageWorkitemsByIdsResponse execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                    PageWorkitemsByIds param = new PageWorkitemsByIds();
                    param.setIds(workitemIds);
                    param.setColumns(workItemFields);
                    param.setLongTextColumns((com.microsoft.schemas.teamfoundation._2005._06.workitemtracking.clientservices._03.ArrayOfInt)null);
                    param.setAsOfDate(new GregorianCalendar());
                    param.setUseMaster(false);
                    param.setMetadataHave((ArrayOfMetadataTableHaveEntry)null);
                    return VersionControlServer.this.myBeans.getWorkItemServiceStub(credentials, pi).pageWorkitemsByIds(param, VersionControlServer.generateRequestHeader());
                }
            });
            List<WorkItem> workItems = new ArrayList();
            R_type0[] var11 = pageWorkitemsByIdsResponse.getItems().getTable().getRows().getR();
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
                R_type0 row = var11[var13];
                workItems.add(WorkItemSerialize.createFromFields(row.getF()));
            }

            return workItems;
        }
    }

    public void updateWorkItemsAfterCheckin(String workspaceOwnerName, Map<WorkItem, CheckinWorkItemAction> workItems, int changeSet, Object projectOrComponent, String progressTitle) throws TfsException {
        if (!workItems.isEmpty()) {
            String identity = this.readIdentity(workspaceOwnerName, projectOrComponent, progressTitle).getDisplayName();
            Iterator var7 = workItems.keySet().iterator();

            while(var7.hasNext()) {
                WorkItem workItem = (WorkItem)var7.next();
                CheckinWorkItemAction checkinWorkItemAction = (CheckinWorkItemAction)workItems.get(workItem);
                if (checkinWorkItemAction != CheckinWorkItemAction.None) {
                    this.updateWorkItem(workItem, checkinWorkItemAction, changeSet, identity, projectOrComponent, progressTitle);
                }
            }

        }
    }

    private void updateWorkItem(WorkItem workItem, CheckinWorkItemAction action, int changeSet, String identity, Object projectOrComponent, String progressTitle) throws TfsException {
        UpdateWorkItem_type0 updateWorkItem_type0 = new UpdateWorkItem_type0();
        updateWorkItem_type0.setWorkItemID(workItem.getId());
        updateWorkItem_type0.setRevision(workItem.getRevision());
        updateWorkItem_type0.setObjectType("WorkItem");
        updateWorkItem_type0.setComputedColumns(WorkItemSerialize.generateComputedColumnsForUpdateRequest(workItem.getType(), action));
        updateWorkItem_type0.setColumns(WorkItemSerialize.generateColumnsForUpdateRequest(workItem.getType(), workItem.getReason(), action, identity));
        updateWorkItem_type0.setInsertText(WorkItemSerialize.generateInsertTextForUpdateRequest(action, changeSet));
        updateWorkItem_type0.setInsertResourceLink(WorkItemSerialize.generateInsertResourceLinkforUpdateRequest(changeSet));
        Package_type0 package_type00 = new Package_type0();
        package_type00.setXmlns("");
        package_type00.setUpdateWorkItem(updateWorkItem_type0);
        final Package_type0E package_type_0 = new Package_type0E();
        package_type_0.setPackage(package_type00);
        TfsRequestManager.executeRequest(this.myServerUri, projectOrComponent, new Request<Void>(progressTitle) {
            public Void execute(Credentials credentials, URI serverUri, @Nullable ProgressIndicator pi) throws Exception {
                Update param = new Update();
                param.set_package(package_type_0);
                param.setMetadataHave((ArrayOfMetadataTableHaveEntry)null);
                VersionControlServer.this.myBeans.getWorkItemServiceStub(credentials, pi).update(param, VersionControlServer.generateRequestHeader());
                return null;
            }
        });
    }

    public static class ResolveConflictParams {
        public final int conflictId;
        public final Resolution resolution;
        public final LockLevel lockLevel;
        public final int encoding;
        public final String newPath;

        public ResolveConflictParams(int conflictId, Resolution resolution, LockLevel lockLevel, int encoding, String newPath) {
            this.conflictId = conflictId;
            this.resolution = resolution;
            this.lockLevel = lockLevel;
            this.encoding = encoding;
            this.newPath = newPath;
        }
    }

    public static class ExtendedItemsAndPendingChanges {
        public final List<ExtendedItem> extendedItems;
        public final Collection<PendingChange> pendingChanges;

        public ExtendedItemsAndPendingChanges(Collection<PendingChange> pendingChanges, List<ExtendedItem> extendedItems) {
            this.pendingChanges = pendingChanges;
            this.extendedItems = extendedItems;
        }
    }

    private interface ChangeRequestProvider<T> {
        ChangeRequest createChangeRequest(T var1);
    }

    public static class GetRequestParams {
        public final String serverPath;
        public final RecursionType recursionType;
        public final VersionSpec version;

        public GetRequestParams(String serverPath, RecursionType recursionType, VersionSpec version) {
            this.serverPath = serverPath;
            this.recursionType = recursionType;
            this.version = version;
        }
    }

    private interface OperationOnList<T, U> {
        U execute(List<T> var1, Credentials var2, ProgressIndicator var3) throws RemoteException, HostNotApplicableException;

        U merge(Collection<U> var1);
    }

    private interface OperationOnCollection<T, U> {
        U execute(Collection<T> var1, Credentials var2, ProgressIndicator var3) throws RemoteException, HostNotApplicableException;

        U merge(Collection<U> var1);
    }
}
