package org.paasta.container.platform.api.roles;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.clusters.namespaces.Namespaces;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.*;
import org.paasta.container.platform.api.users.Users;
import org.paasta.container.platform.api.users.UsersList;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.paasta.container.platform.api.common.Constants.URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_NAME_USER_ID;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class RolesServiceTest {
    private static final String CLUSTER = "test-cluster";
    private static final String NAMESPACE = "test-namespace";
    private static final String ROLE_NAME = "test-role-name";
    private static final String USER_ID = "test-user-id";
    private static final String USER_TYPE = "test-user-type";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final boolean isAdmin = true;
    private static final boolean isNotAdmin = false;

    private static HashMap gResultMap = null;
    private static HashMap gResultAdminMap = null;
    private static HashMap gResultAdminFailMap = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static Roles gResultModel = null;
    private static Roles gFinalResultModel = null;

    private static RolesAdmin gResultAdminModel = null;
    private static RolesAdmin gFinalResultAdminModel = null;
    private static RolesAdmin gFinalResultAdminFailModel = null;

    private static RolesList gResultListModel = null;
    private static RolesList gFinalResultListModel = null;
    private static RolesList gFinalResultListFailModel = null;

    private static RolesListAdmin gResultListAdminModel = null;
    private static RolesListAdmin gFinalResultListAdminModel = null;
    private static RolesListAdmin gFinalResultListAdminFailModel = null;

    private static RolesListAllNamespaces gResultListAllNamespacesModel = null;
    private static RolesListAllNamespaces gFinalResultListAllNamespacesModel = null;
    private static RolesListAllNamespaces gFinalResultListAllNamespacesFailModel = null;

    private static List<String> gIgnoreNamespaceList = null;

    private static UsersList gUsersList = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    RolesService rolesService;

    @Before
    public void setUp() throws Exception {
        gResultMap = new HashMap();

        gResultStatusModel = new ResultStatus();
        gResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultStatusModel = new ResultStatus();
        gFinalResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultStatusModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultStatusModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultStatusModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_ROLES);

        // 리스트가져옴
        gResultListModel = new RolesList();
        
        gFinalResultListModel = new RolesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new RolesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new Roles();
        gFinalResultModel = new Roles();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultFailModel = new ResultStatus();
        gResultFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gResultFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultAdminMap = new HashMap();
        gResultListAdminModel = new RolesListAdmin();

        List<RolesListAdminItem> rolesListAdminItems = new ArrayList<>();
        RolesListAdminItem rolesListAdminItem = new RolesListAdminItem();
        rolesListAdminItem.setCreationTimestamp("2020-11-03");
        rolesListAdminItem.setName(ROLE_NAME);
        rolesListAdminItem.setNamespace(NAMESPACE);

        CommonMetaData metaData = new CommonMetaData();
        metaData.setCreationTimestamp("2020-11-03");
        metaData.setName(ROLE_NAME);
        metaData.setNamespace(NAMESPACE);

        rolesListAdminItem.setMetadata(metaData);

        rolesListAdminItems.add(rolesListAdminItem);

        gResultListAdminModel.setItems(rolesListAdminItems);

        gFinalResultListAdminModel = new RolesListAdmin();

        gFinalResultListAdminModel = new RolesListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new RolesListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        // 하나만 가져옴
        gResultAdminModel = new RolesAdmin();
        gFinalResultAdminModel = new RolesAdmin();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());


        CommonMetaData commonMetaData = new CommonMetaData();
        Map<String, String> annotations = new HashMap<>();
        annotations.put(KUBE_ANNOTATIONS, KUBE_ANNOTATIONS);
        commonMetaData.setAnnotations(annotations);

        CommonAnnotations commonAnnotations = new CommonAnnotations();
        commonAnnotations.setCheckYn("Y");
        commonAnnotations.setKey(KUBE_ANNOTATIONS);
        commonAnnotations.setValue(KUBE_ANNOTATIONS);

        List<CommonAnnotations> commonAnnotationsList = new ArrayList<>();
        commonAnnotationsList.add(commonAnnotations);
        gResultAdminModel.setAnnotations(commonAnnotationsList);



        gFinalResultAdminFailModel = new RolesAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        // 리스트가져옴
        gResultListAllNamespacesModel = new RolesListAllNamespaces();

        CommonMetaData metadata = new CommonMetaData();
        metadata.setName(ROLE_NAME);
        metadata.setNamespace(NAMESPACE);

        RolesListAllNamespaces.RolesListAllNamespacesItem rolesListAllNamespacesItem = new RolesListAllNamespaces.RolesListAllNamespacesItem();
        rolesListAllNamespacesItem.setMetadata(metadata);
        rolesListAllNamespacesItem.setName(ROLE_NAME);
        rolesListAllNamespacesItem.setNamespace(NAMESPACE);
        rolesListAllNamespacesItem.setCheckYn(Constants.CHECK_Y);
        rolesListAllNamespacesItem.setUserType(USER_TYPE);

        List<RolesListAllNamespaces.RolesListAllNamespacesItem> items = new ArrayList<RolesListAllNamespaces.RolesListAllNamespacesItem>();
        items.add(rolesListAllNamespacesItem);

        gResultListAllNamespacesModel.setItems(items);

        gFinalResultListAllNamespacesModel = new RolesListAllNamespaces();
        gFinalResultListAllNamespacesModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListAllNamespacesFailModel = new RolesListAllNamespaces();
        gFinalResultListAllNamespacesFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        gIgnoreNamespaceList = new ArrayList<String>();
        gIgnoreNamespaceList.add("default");
        gIgnoreNamespaceList.add("kubernetes-dashboard");
        gIgnoreNamespaceList.add("kube-node-lease");
        gIgnoreNamespaceList.add("kube-public");
        gIgnoreNamespaceList.add("kube-system");
        gIgnoreNamespaceList.add("paas-ta-container-platform-temp-namespace");

        gUsersList = new UsersList();

        gUsersList.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gUsersList.setResultMessage(Constants.RESULT_STATUS_SUCCESS);

        Users users = new Users();

        users.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        users.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        users.setHttpStatusCode(CommonStatusCode.OK.getCode());
        users.setDetailMessage(CommonStatusCode.OK.getMsg());

        users.setCpNamespace(NAMESPACE);
        users.setRoleSetCode(ROLE_NAME);
        users.setUserType(USER_TYPE);

        List<Users> usersList = new ArrayList<Users>();
        usersList.add(users);

        gUsersList.setItems(usersList);
    }

    @Test
    public void getRolesList() {
        //when
        when(propertyService.getCpMasterApiListRolesListUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, RolesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, RolesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        RolesList resultList = rolesService.getRolesList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getRolesListAdmin() {
        //when
        when(propertyService.getCpMasterApiListRolesListUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);

        when(commonService.setResultObject(gResultAdminMap, RolesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, RolesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        RolesListAdmin resultList = (RolesListAdmin) rolesService.getRolesListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getRoles() {
        //when
        when(propertyService.getCpMasterApiListRolesGetUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Roles.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        Roles result = rolesService.getRoles(NAMESPACE, ROLE_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getRolesAdmin() {
        //when
        when(propertyService.getCpMasterApiListRolesGetUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, RolesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, RolesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        //call method
        RolesAdmin result = (RolesAdmin) rolesService.getRolesAdmin(NAMESPACE, ROLE_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getRolesYaml() {
        //when
        when(propertyService.getCpMasterApiListRolesGetUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) rolesService.getRolesYaml(NAMESPACE, ROLE_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getRolesAdminYaml(){
        //when
        when(propertyService.getCpMasterApiListRolesGetUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel,Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) rolesService.getRolesAdminYaml(NAMESPACE, ROLE_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createRoles() {
        //when
        when(propertyService.getCpMasterApiListRolesCreateUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_ROLES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) rolesService.createRoles(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteRoles() {
        //when
        when(propertyService.getCpMasterApiListRolesDeleteUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_ROLES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = rolesService.deleteRoles(NAMESPACE, ROLE_NAME);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateRoles() {
        String nextUrl = Constants.URI_ROLES_DETAIL.replace("{roleName:.+}", ROLE_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListRolesUpdateUrl()).thenReturn("/apis/rbac.authorization.k8s.io/v1/namespaces/{namespace}/roles/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/namespaces/" + NAMESPACE + "/roles/" + ROLE_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = rolesService.updateRoles(NAMESPACE, ROLE_NAME, YAML_STRING);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }


    @Test
    public void getRolesListAllNamespacesAdmin() {
        //when
        when(propertyService.getCpMasterApiListRolesListAllNamespacesUrl())
                .thenReturn("/apis/rbac.authorization.k8s.io/v1/roles");

        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE))
                .thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/roles" + FIELD_SELECTOR, HttpMethod.GET, null, Map.class))
                .thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, RolesListAdmin.class))
                .thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, RolesListAdmin.class))
                .thenReturn(gResultListAdminModel);
        when(propertyService.getDefaultNamespace())
                .thenReturn("default");
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        RolesListAdmin resultList = (RolesListAdmin) rolesService.getRolesListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getNamespacesRolesTemplateList() {
        //when
        when(propertyService.getAdminRole())
                .thenReturn("paas-ta-container-platform-admin-role");
        when(propertyService.getCpMasterApiListRolesListAllNamespacesUrl())
                .thenReturn("/apis/rbac.authorization.k8s.io/v1/roles");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/rbac.authorization.k8s.io/v1/roles" + "?fieldSelector=metadata.name!=paas-ta-container-platform-admin-role", HttpMethod.GET, null, Map.class))
                .thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, RolesListAllNamespaces.class))
                .thenReturn(gResultListAllNamespacesModel);
        when(propertyService.getIgnoreNamespaceList())
                .thenReturn(gIgnoreNamespaceList);
        when(restTemplateService.sendAdmin(Constants.TARGET_COMMON_API, URI_COMMON_API_NAMESPACES_ROLE_BY_CLUSTER_NAME_USER_ID.replace("{cluster:.+}", CLUSTER).replace("{userId:.+}", USER_ID), HttpMethod.GET, null, UsersList.class))
                .thenReturn(gUsersList);
        when(commonService.resourceListProcessing(gResultListAllNamespacesModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, RolesListAllNamespaces.class))
                .thenReturn(gResultListAllNamespacesModel);
        when(commonService.setResultModel(gResultListAllNamespacesModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gFinalResultListAllNamespacesModel);

    }
}