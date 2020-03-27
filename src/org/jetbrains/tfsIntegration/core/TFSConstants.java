/*    */ package org.jetbrains.tfsIntegration.core;
/*    */ 
/*    */ import com.microsoft.wsdl.types.Guid;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class TFSConstants
/*    */ {
/*    */   @NonNls
/*    */   public static final String REGISTRATION_ASMX = "Services/v1.0/Registration.asmx";
/*    */   @NonNls
/*    */   public static final String LOCATION_SERVICE_ASMX = "TeamFoundation/Administration/v3.0/LocationService.asmx";
/*    */   @NonNls
/*    */   public static final String SERVER_STATUS_ASMX = "Services/v1.0/ServerStatus.asmx";
/*    */   @NonNls
/*    */   public static final String PROXY_DOWNLOAD_ASMX = "VersionControlProxy/v1.0/item.asmx";
/*    */   @NonNls
/*    */   public static final String TFS_CHECKIN_POLICIES_ANNOTATION = "CheckinPolicies";
/*    */   @NonNls
/*    */   public static final String STATEFUL_CHECKIN_POLICIES_ANNOTATION = "TeampriseCheckinPolicies";
/*    */   @NonNls
/*    */   public static final String OVERRRIDES_ANNOTATION = "IntellijOverrides";
/*    */   @NonNls
/*    */   public static final String TOOL_ID_TFS = "vstfs";
/*    */   @NonNls
/*    */   public static final String INSTANCE_ID_ATTRIBUTE = "InstanceId";
/*    */   @NonNls
/*    */   public static final String SINGLE_RECURSE_STAR = "*";
/* 39 */   public static final Guid PROJECT_COLLECTION_GUID = Guid.Factory.fromString("26338d9e-d437-44aa-91f2-55880a328b54", Guid.MY_QNAME.getNamespaceURI());
/*    */   
/* 41 */   public static final Guid FRAMEWORK_SERVER_DATA_PROVIDER_FILTER_GUID = Guid.Factory.fromString("567713db-d56d-4bb0-8f35-604e0e116174", Guid.MY_QNAME.getNamespaceURI());
/*    */   public static final int INCLUDE_SERVICES_CONNECTION_OPTION = 1;
/*    */   public static final String ACCOUNT = "Account";
/*    */   public static final String DOMAIN = "Domain";
/*    */   public static final String CATALOG_SERVICE_CONFIG_GUID = "c2f9106f-127a-45b7-b0a3-e0ad8239a2a7";
/*    */   public static final String VERSION_CONTROL_ENTRY_TYPE = "VersionControl";
/*    */   public static final String WORK_ITEM_TRACKING_ENTRY_TYPE = "WorkItemTracking";
/*    */   public static final String VSTFS_ENTRY_TYPE = "vstfs";
/*    */   public static final String ISCC_PROVIDER_4_SERVICE_NAME = "ISCCProvider4";
/*    */   public static final String ISCC_PROVIDER_SERVICE_NAME = "ISCCProvider";
/*    */   public static final String DOWNLOAD_SERVICE_NAME = "Download";
/*    */   public static final String UPLOAD_SERVICE_NAME = "Upload";
/*    */   public static final String WORKITEM_SERVICE_NAME = "WorkitemService";
/*    */   public static final String GROUP_SECURITY_SERVICE_NAME = "GroupSecurity";
/*    */   public static final String TFS_PATH = "/tfs";
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */