package org.paasta.container.platform.api.common;
import org.springframework.http.MediaType;
import java.util.*;

/**
 * Constants 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2020.08.26
 */
public class Constants {

    public static final String RESULT_STATUS_SUCCESS = "SUCCESS";
    public static final String RESULT_STATUS_FAIL = "FAIL";

    public static final String CHECK_Y = "Y";
    public static final String CHECK_N = "N";

    public static final String CHECK_TRUE = "true";
    public static final String CHECK_FALSE = "false";

    public static final String EMPTY_STRING ="";

    // for kubernetes cli user
    public static final String CHECK_K8S = "K";
    public static final String TYPE = "type" ;
    public static final String PATH= "path" ;

    public static final String TARGET_CP_MASTER_API = "cpMasterApi/{cluster}";
    public static final String TARGET_COMMON_API = "commonApi";
    //FIXME!!
    public static final String TARGET_TERRAMAN_API = "terramanApi";
    public static final String TARGET_METRIC_COLLECTOR_API = "metricCollectorApi";

    public static final String CLUSTER_TYPE_HOST = "host";
    public static final String CLUSTER_TYPE_SUB = "sub";
    public static final String ACCEPT_TYPE_YAML = "application/yaml";

    public static final String TOKEN_KEY = "cp_admin";

    public static final String SELECTED_ADMINISTRATOR = "administrator";
    public static final String SELECTED_USER = "user";

    public static final String AUTH_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String AUTH_CLUSTER_ADMIN = "CLUSTER_ADMIN";
    public static final String AUTH_NAMESPACE_ADMIN = "NAMESPACE_ADMIN";
    public static final String AUTH_USER = "USER";
    public static final List<String> AUTH_ADMIN_LIST =   Arrays.asList(new String[]{AUTH_SUPER_ADMIN, AUTH_CLUSTER_ADMIN});

    public static final String AUTH_CLUSTER_ADMIN_CG = "Cluster Admin";
    public static final String AUTH_NAMESPACE_ADMIN_CG = "Namespace Admin";
    public static final String AUTH_USER_CG = "User";

    public static final String ALL_NAMESPACES = "all";
    public static final String ALL_USER_ID = "all";
    public static final String USERS = "users";
    public static final String DEFAULT_SERVICE_ACCOUNT = "default";

    public static final String NOT_ASSIGNED_ROLE = "NOT_ASSIGNED_ROLE";
    public static final String DEFAULT_SUPER_ADMIN_ROLE = "cluster-admin"; // k8s default cluster role's name
    public static final String DEFAULT_CLUSTER_ADMIN_ROLE = "cluster-admin"; // k8s default cluster role's name
    public static final String DEFAULT_CONFIGMAPS = "kube-root-ca.crt"; // k8s default configMaps name

    public static final String NOT_ALLOWED_POD_NAME_NODES = "nodes";
    public static final String NOT_ALLOWED_POD_NAME_RESOURCES= "resources";

    public static final String LIMIT_RANGE_TYPE_POD = "Pod";
    public static final String LIMIT_RANGE_TYPE_CONTAINER = "Container";
    public static final String LIMIT_RANGE_TYPE_PVC = "PersistentVolumeClaim";

    public static final String SUPPORTED_RESOURCE_CPU = "cpu";
    public static final String SUPPORTED_RESOURCE_MEMORY = "memory";
    public static final String SUPPORTED_RESOURCE_STORAGE = "storage";
    public static final String SUPPORTED_RESOURCE_PORT = "port";

    public static final String INGRESS_CONTROLLER_NAMESPACE= "ingress-nginx";
    public static final String INGRESS_CONTROLLER_RESOURCE_NAME= "ingress-nginx-controller";

    public static final List<String> LIMIT_RANGE_TYPE_LIST = Collections.unmodifiableList(new ArrayList<String>(){
        {
            add(LIMIT_RANGE_TYPE_POD);
            add(LIMIT_RANGE_TYPE_CONTAINER);
            add(LIMIT_RANGE_TYPE_PVC);
        }
    });

    public static final List<String> SUPPORTED_RESOURCE_LIST = Collections.unmodifiableList(new ArrayList<String>(){
        {
            add(SUPPORTED_RESOURCE_CPU);
            add(SUPPORTED_RESOURCE_MEMORY);
        }
    });

    public static final List<String> NOT_ALLOWED_POD_NAME_LIST = Collections.unmodifiableList(new ArrayList<String>(){
        {
            add(NOT_ALLOWED_POD_NAME_NODES);
            add(NOT_ALLOWED_POD_NAME_RESOURCES);
        }
    });


    public static final List<String> USER_AUTH_LIST = Collections.unmodifiableList(new ArrayList<String>(){
        {
            add(AUTH_SUPER_ADMIN);
            add(AUTH_CLUSTER_ADMIN);
            add(AUTH_USER);
        }
    });

    static final String STRING_DATE_TYPE = "yyyy-MM-dd HH:mm:ss";
    static final String STRING_ORIGINAL_DATE_TYPE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    static final String STRING_TIME_ZONE_ID = "Asia/Seoul";

    static final String ACCEPT_TYPE_JSON = MediaType.APPLICATION_JSON_VALUE;

    public static final String URI_SIGN_UP = "/signUp";
    public static final String URI_LOGIN = "/login";
    public static final String CLUSTER_ROLE_URI = "users/resources";


    public static final String URI_CHECK_REGISTERED_USER = "/check/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userId:.+}";

    public static final String[] PERMIT_PATH_LIST = new String[]{ URI_SIGN_UP, URI_LOGIN, URI_CHECK_REGISTERED_USER};

    public static final String ENDS_WITH_SES = "ses";
    public static final String ENDS_WITH_S = "s";

    // KUBERNETES METRIC API URI
    public static final String URI_METRIC_API_BASIC = "/apis/metrics.k8s.io/v1beta1/namespaces/{namespace}/pods";

    // COMMON API CALL URI
    public static final String URI_COMMON_API_ADMIN_TOKEN_DETAIL = "/adminToken/{tokenName:.+}";
    public static final String URI_COMMON_API_USERS = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userAuthId:.+}";
    public static final String URI_COMMON_API_USERS_DETAIL =  "/users/{userId:.+}/{userAuthId:.+}";
    public static final String URI_COMMON_API_USERS_LIST =  "/users";
    public static final String URI_COMMON_API_USERS_NAMES =  "/users/names";
    public static final String URI_COMMON_API_USERS_LIST_BY_CLUSTER = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/usersList";
    public static final String URI_COMMON_API_USERS_LIST_BY_CLUSTER_TEMPNAMESPACE = "/clusters/{cluster:.+}/users/tempNamespace";



    public static final String URI_COMMON_API_USER_DETAIL_LOGIN =  "/login/users/{userId:.+}";
    public static final String URI_COMMON_API_USERS_LIST_BY_NAMESPACE = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users";
    public static final String URI_COMMON_API_USERS_NAMES_LIST = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/names";
    public static final String URI_COMMON_API_USER_DELETE = "/users/";
    public static final String URI_COMMON_API_USERS_BY_NAMESPACE_NS_ADMIN = "/clusters/{cluster:.+}/namespaces/{namespace:.+}";
    public static final String URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_USER_AUTH_ID = "/clusters/{cluster:.+}/users/{userAuthId:.+}";

    public static final String URI_COMMON_API_PRIVATE_REGISTRY = "/privateRegistry/{imageName:.+}";
    public static final String URI_COMMON_API_CLUSTER_ADMIN_ROLE_BY_CLUSTER_NAME_USER_ID = "/clusters/{cluster:.+}/users/{userId:.+}/userType";

    public static final String URI_COMMON_API_USER_SIGNUP = "/user/signUp";
    public static final String URI_COMMON_API_CHECK_USER_REGISTER =  "/userRegisterCheck?userId={userId:.+}&userAuthId={userAuthId:.+}&userType={userType:.+}";

    public static final String URI_COMMON_API_CLUSTER_ADMIN_LIST = "/cluster/{cluster:.+}/admin?searchName={searchName:.+}";
    public static final String URI_COMMON_API_CLUSTER_USER_DETAILS = "/clusters/{cluster:.+}/users/{userAuthId:.+}/details";
    public static final String URI_COMMON_API_CLUSTER_INFO_USER_DETAILS = "/cluster/info/all/user/details?userAuthId={userAuthId:.+}&cluster={cluster:.+}&namespace={namespace:.+}";
    public static final String URI_COMMON_API_NAMESPACE_OR_NOT_CHECK = "/clusters/all/namespaces/{namespace:.+}/adminCheck";
    public static final String URI_COMMON_API_DELETE_USER_BY_ID_AND_AUTHID = "/cluster/all/user/delete?userId={userId:.+}&userAuthId={userAuthId:.+}&namespace={namespace:.+}";
    public static final String URI_COMMON_API_CLUSTER_LIST_BY_USER = "/users/{userAuthId:.+}/clustersList?userType={userType:.+}";
    public static final String URI_COMMON_API_DELETE_USER = "/clusters/{cluster:.+}/namespaces/{namespace:.+}/users/{userAuthId:.+}/{userType:.+}";
    public static final String URI_COMMON_API_DELETE_USER_BY_IDS = "/users/ids?ids=";
    public static final String URI_COMMON_API_CLUSTER_AND_NAMESPACE_LIST_BY_USER = "/users/{userAuthId:.+}/clustersAndNamespacesList?userType={userType:.+}";
    public static final String URI_COMMON_API_CREATE_CLUSTER_ADMIN = "/users/create/clusterAdmin";



    // NEXT ACTION MOVEMENT DASHBOARD URI
    public static final String URI_CLUSTER_NODES = "/container-platform/clusters/nodes";
    public static final String URI_CLUSTER_NAMESPACES = "/container-platform/clusters/namespaces";
    public static final String URI_INTRO_OVERVIEW = "/container-platform/intro/overview";

    public static final String URI_WORKLOAD_DEPLOYMENTS = "/container-platform/workloads/deployments";
    public static final String URI_WORKLOAD_DEPLOYMENTS_DETAIL = "/container-platform/workloads/deployments/{deploymentName:.+}";
    public static final String URI_WORKLOAD_PODS = "/container-platform/workloads/pods";
    public static final String URI_WORKLOAD_PODS_DETAIL = "/container-platform/workloads/pods/{podName:.+}";
    public static final String URI_WORKLOAD_REPLICA_SETS = "/container-platform/workloads/replicaSets";
    public static final String URI_WORKLOAD_REPLICA_SETS_DETAIL = "/container-platform/workloads/replicaSets/{replicaSetName:.+}";

    public static final String URI_SERVICES = "/container-platform/services";
    public static final String URI_SERVICES_DETAIL = "/container-platform/services/{serviceName:.+}";

    public static final String URI_INGRESSES = "/container-platform/ingresses";
    public static final String URI_INGRESSES_DETAIL = "/container-platform/ingresses/{serviceName:.+}";

    public static final String URI_STORAGES = "/container-platform/storages";
    public static final String URI_STORAGES_DETAIL = "/container-platform/storages/{persistentVolumeClaimName:.+}";

    public static final String URI_STORAGES_PERSISTENT_VOLUMES = "/container-platform/storages/persistentVolumes";
    public static final String URI_STORAGES_PERSISTENT_VOLUMES_DETAIL = "/container-platform/storages/persistentVolumes/{persistentVolumeName:.+}";
    public static final String URI_STORAGES_STORAGE_CLASSES = "/container-platform/storages/storageClasses";
    public static final String URI_STORAGES_STORAGE_CLASSES_DETAIL = "/container-platform/storages/storageClasses/{storageClassName:.+}";

    public static final String URI_USERS = "/container-platform/users";
    public static final String URI_USERS_DETAIL = "/container-platform/users/{userId:.+}";
    public static final String URI_USERS_CONFIG = "/container-platform/users/config";

    public static final String URI_ROLES = "/container-platform/roles";
    public static final String URI_ROLES_DETAIL = "/container-platform/roles/{roleName:.+}";

    public static final String URI_RESOURCE_QUOTAS = "/container-platform";
    public static final String URI_RESOURCE_QUOTAS_DETAIL = "/container-platform/resourceQuotas/{resourceQuotaName:.+}";

    public static final String URI_LIMIT_RANGES = "/container-platform";
    public static final String URI_LIMIT_RANGES_DETAIL = "/container-platform/limitRanges/{limitRangeName:.+}";
    public static final String URI_SERVICEINSTANCE_DETAIL =  "/serviceInstance/{serviceInstanceId:.+}";

    public static final String URI_CONFIGMAPS = "/container-platform";
    public static final String URI_CONFIGMAPS_DETAIL = "/container-platform/configMaps/{configMapsName:.+}";



    /** 서비스 요청시 처리 메소드 kind 매핑 정보 */
    public static final String RESOURCE_ENDPOINTS = "Endpoints";
    public static final String RESOURCE_EVENTS = "Events";

    //cluster
    public static final String RESOURCE_CLUSTER = "Cluster";
    public static final String RESOURCE_NAMESPACE = "Namespace";
    public static final String RESOURCE_NODE = "Node";

    //workload
    public static final String RESOURCE_DEPLOYMENT = "Deployment";
    public static final String RESOURCE_POD = "Pod";
    public static final String RESOURCE_REPLICASET = "ReplicaSet";

    //service
    public static final String RESOURCE_SERVICE = "Service";
    public static final String RESOURCE_INGRESS = "Ingress";

    //storage
    public static final String RESOURCE_PERSISTENTVOLUMECLAIM = "PersistentVolumeClaim";
    public static final String RESOURCE_PERSISTENTVOLUME = "PersistentVolume";
    public static final String RESOURCE_STORAGECLASS = "StorageClass";

    //management
    public static final String RESOURCE_LIMITRANGE = "LimitRange";
    public static final String RESOURCE_RESOURCEQUOTA = "ResourceQuota";
    public static final String RESOURCE_ROLE = "Role";

    public static final String RESOURCE_NAME = "name";
    public static final String RESOURCE_CREATIONTIMESTAMP = "creationTimestamp";
    public static final String RESOURCE_CREATED = "created";
    public static final String RESOURCE_METADATA = "metadata";
    public static final String RESOURCE_NS = "namespace";
    public static final String RESOURCE_ANNOTATIONS = "annotations";




    public static final String noName = "[-]";
    public static final String separatorString =  "," ;

    public static final Integer EVENT_DEFAULT_LIMIT  = 5;
    public static final String PERSISTENT_HOST_PATH_FIELD = "hostPath";

    public static final String REPLICASETS_FOR_SELECTOR = "replicaSets";
    public static final String DEPLOYMENTS_FOR_SELECTOR = "deployments";
    public static final String NULL_REPLACE_TEXT = "-";



    public static final String PARAM_QUERY_FIRST ="?" ;
    public static final String PARAM_QUERY_AND = "&";

    public static final String U_LANG_KO = "ko";
    public static final String U_LANG_KO_START_WITH = "ko_";
    public static final String U_LANG_ENG = "en";


    /** 서비스 클래스의 Package */
    public static final String SERVICE_PACKAGE = "org.paasta.container.platform.api.";

    public static final Map<String, String> RESOURCE_SERVICE_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put(RESOURCE_ENDPOINTS, SERVICE_PACKAGE + "endpoints:EndpointsService");     // Endpoints 서비스
            put(RESOURCE_EVENTS, SERVICE_PACKAGE + "events:EventsService");     // Endpoints 서비스
            put(RESOURCE_NAMESPACE, SERVICE_PACKAGE + "clusters.namespaces:NamespacesService");     // Namespace 서비스
            put(RESOURCE_NODE, SERVICE_PACKAGE + "clusters.nodes:NodesService");     // Node 서비스
            put(RESOURCE_DEPLOYMENT, SERVICE_PACKAGE + "workloads.deployments:DeploymentsService");     // Deployment 서비스
            put(RESOURCE_POD, SERVICE_PACKAGE + "workloads.pods:PodsService");     // Pod 서비스
            put(RESOURCE_REPLICASET, SERVICE_PACKAGE + "workloads.pods:ReplicaSetsService");     // ReplicaSet 서비스
            put(RESOURCE_SERVICE, SERVICE_PACKAGE + "customServices:CustomServicesService");     // Service 서비스
            put(RESOURCE_PERSISTENTVOLUMECLAIM, SERVICE_PACKAGE + "storages.persistentVolumeClaims:PersistentVolumeClaimsService");     // PersistentVolumeClaim 서비스
            put(RESOURCE_PERSISTENTVOLUME, SERVICE_PACKAGE + "storages.persistentVolumes:PersistentVolumesService");     // PersistentVolume 서비스
            put(RESOURCE_STORAGECLASS, SERVICE_PACKAGE + "storages.storageClasses:StorageClassesService");     // StorageClass 서비스
            put(RESOURCE_RESOURCEQUOTA, SERVICE_PACKAGE + "clusters.resourceQuotas:ResourceQuotasService");     // ResourceQuota 서비스
            put(RESOURCE_LIMITRANGE, SERVICE_PACKAGE + "clusters.limitRanges:LimitRangesService");     // LimitRange 서비스
            put(RESOURCE_ROLE, SERVICE_PACKAGE + "roles:RolesService"); // Role 서비스
        }

    });


    public Constants() {
        throw new IllegalStateException();
    }

    /**
     * The enum List object type
     */
    public enum ListObjectType {
        LIMIT_RANGES_ITEM,
        COMMON_OWNER_REFERENCES,
        STRING
    }

    public enum ProviderType {
        AWS("AWSInfo"),
        GCP("GCPInfo"),
        OPENSTACK("OpenstackInfo");

        private final String classType;

        ProviderType(String classType) {
            this.classType = classType;
        }

        public String getClassType() {
            return classType;
        }
    }

    public enum ContextType {
        CLUSTER,
        NAMESPACE
    }

    public enum ClusterStatus {
        ACTIVE("A"),
        CREATING("C"),
        DISABLED("D");

        private final String initial;
        ClusterStatus(String initial) {
            this.initial = initial;
        }
        public String getInitial() {
            return initial;
        }

    }

    public static final String STRING_CONDITION_READY = "Ready";


    public static final String CPU = "cpu";
    public static final String MEMORY = "memory";
    public static final String CPU_UNIT = "m";
    public static final String MEMORY_UNIT = "Mi";


    public static final String CONTAINER_STATE_RUNNING = "running";
    public static final String CONTAINER_STATE_TERMINATED = "terminated";
    public static final String CONTAINER_STATE_WAITING = "waiting";

    public static final String STATUS_FAILED = "Failed";
    public static final String STATUS_RUNNING = "Running";

    public static final String CLUSTER_ADMIN_SERVICE_ACCOUNT = "cp-cluster-admin-{userAuthId}";
    public static final String CLUSTER_ROLE_BINDING_NAME = "-cluster-admin-binding";
}