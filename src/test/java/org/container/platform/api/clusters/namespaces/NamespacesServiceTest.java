package org.container.platform.api.clusters.namespaces;

import org.apache.catalina.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.accessInfo.AccessToken;
import org.container.platform.api.accessInfo.AccessTokenService;
import org.container.platform.api.clusters.limitRanges.LimitRanges;
import org.container.platform.api.clusters.limitRanges.LimitRangesList;
import org.container.platform.api.clusters.limitRanges.LimitRangesListItem;
import org.container.platform.api.clusters.limitRanges.LimitRangesService;
import org.container.platform.api.clusters.namespaces.support.NamespacesListItem;
import org.container.platform.api.clusters.namespaces.support.NamespacesListSupport;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotas;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasList;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasListItem;
import org.container.platform.api.clusters.resourceQuotas.ResourceQuotasService;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.*;
import org.container.platform.api.users.Users;
import org.container.platform.api.users.UsersList;
import org.container.platform.api.users.UsersService;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class NamespacesServiceTest {

    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "test-namespace";
    private static final String USER_ID = "test-user";
    private static final String YAML_STRING = "test-yaml-string";

    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";

    public static final String NAMESPACE_ADMIN_USER_ID = "";

    private static HashMap gResultMap = null;
    private static HashMap gResultAdminMap = null;
    private static Namespaces gResultModel = null;
    private static Namespaces gFinalResultModel = null;

    private static NamespacesList gResultListModel = null;
    private static NamespacesList gFinalResultListModel = null;
    private static NamespacesList gFinalResultListFailModel = null;

    private static Namespaces gResultAdminModel = null;
    private static Namespaces gFinalResultAdminModel = null;
    private static Namespaces gFinalResultAdminFailModel = null;

    private static NamespacesList gResultListAdminModel = null;
    private static NamespacesList gFinalResultListAdminModel = null;
    private static NamespacesList gFinalResultListAdminFailModel = null;


    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static Params gParams = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @Mock
    UsersService usersService;

    @Mock
    AccessTokenService accessTokenService;

    @Mock
    ResourceQuotasService resourceQuotasService;

    @Mock
    ResourceYamlService resourceYamlService;

    @Mock
    LimitRangesService limitRangesService;

    @Mock
    NamespacesService namespacesServiceMock;

    @Mock
    ResultStatusService resultStatusService;

    @InjectMocks
    NamespacesService namespacesService;

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_WORKLOAD_DEPLOYMENTS);

        // 리스트가져옴
        gResultListModel = new NamespacesList();

        gFinalResultListModel = new NamespacesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new NamespacesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new Namespaces();
        gFinalResultModel = new Namespaces();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultFailModel = new ResultStatus();
        gResultFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gResultFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gResultYamlModel = new CommonResourcesYaml("");
        gFinalResultYamlModel = new CommonResourcesYaml("");
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultAdminMap = new HashMap();
        gResultListAdminModel = new NamespacesList();
        gFinalResultListAdminModel = new NamespacesList();

        gFinalResultListAdminModel = new NamespacesList();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());



        gFinalResultListAdminFailModel = new NamespacesList();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new Namespaces();
        gFinalResultAdminModel = new Namespaces();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        CommonMetaData commonMetaData = new CommonMetaData();
        Map<String, String> annotations = new HashMap<>();
        annotations.put(KUBE_ANNOTATIONS, KUBE_ANNOTATIONS);
        commonMetaData.setAnnotations(annotations);
        commonMetaData.setName("test");

        CommonAnnotations commonAnnotations = new CommonAnnotations();
        commonAnnotations.setCheckYn("Y");
        commonAnnotations.setKey(KUBE_ANNOTATIONS);
        commonAnnotations.setValue(KUBE_ANNOTATIONS);

        List<CommonAnnotations> commonAnnotationsList = new ArrayList<>();
        commonAnnotationsList.add(commonAnnotations);
        gResultAdminModel.setAnnotations(commonAnnotationsList);

        List<NamespacesListItem> itemList = new ArrayList<>();
        NamespacesListItem item = new NamespacesListItem();
        item.setName("test");
        itemList.add(item);
        item.setMetadata(commonMetaData);

        gFinalResultListAdminModel.setItems(itemList);

        gFinalResultAdminFailModel = new Namespaces();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gParams = new Params();
        gParams.setNamespace(NAMESPACE);
        gParams.setSelectorType("Cluster");
    }

    /**
     * Namespaces 상세 조회(Get Namespaces detail) Test
     */
    @Test
    public void getNamespaces_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListNamespacesGetUrl()).thenReturn("/api/v1/namespaces/{namespace}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Namespaces.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, Namespaces.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        Namespaces result = namespacesService.getNamespaces(gParams);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());

    }

    /**
     * NameSpaces Admin 목록 조회(Get NameSpaces Admin list) Test
     */
    @Test
    public void getNamespacesList_Valid_ReturnModel() {

        gParams.setNamespace(Constants.ALL_NAMESPACES);
        //when
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultAdminMap);

        when(commonService.setResultObject(gResultAdminMap, NamespacesList.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, gParams, NamespacesList.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        NamespacesList resultList = namespacesService.getNamespacesList(gParams);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());

    }

    /**
     * NameSpaces Admin YAML 조회(Get NameSpaces yaml) Test
     */
    @Test
    public void getNamespacesYaml_Valid_ReturnModel() {

        //when

        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = namespacesService.getNamespacesYaml(gParams);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * NameSpaces 삭제(Delete NameSpaces) Test
     */
    @Test
    public void deleteNamespaces_Valid_ReturnModel() {

        Users users = new Users();
        users.setId(0);
        users.setUserId("kpaas");
        users.setServiceAccountName("kpaas");
        users.setRoleSetCode("container-platform-init-role");
        users.setIsActive("Y");
        users.setDescription("aaaa");
        users.setEmail("kpaas@gmail.com");
        users.setPassword("kpaas");
        users.setCpNamespace("cp-namespace");
        users.setSaSecret("kpaas-token-jqrx4");
        users.setSaToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJ0ZW1wLW5hbWVzcGFjZSIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VjcmV0Lm5hbWUiOiJ0ZXN0LXRva2VuLWpxcng0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InRlc3QiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC51aWQiOiI3Y2Q0Nzk4OC01YWViLTQ1ODQtYmNmOS04OTkwZTUzNWEzZGIiLCJzdWIiOiJzeXN0ZW06c2VydmljZWFjY291bnQ6dGVtcC1uYW1lc3BhY2U6dGVzdCJ9.ZEwhnscTtPW6WrQ5I7fFWcsLWEqnilw7I8i7C4aSXElFHd583OQqTYGk8RUJU7UM6b2T8oKstejkLWE9xP3TchYyG5T-omZBCMe00JZIof4tp0MRZLgBhXizYXGvLb2bcMdlcWg2bCCVRO92Hjik-r-vqfaGbsRGx4dT2dk1sI4RA-XDnMsVFJS94V9P58cBupT1gRMrwWStrqlXrbiwgfIlGbU9GXnA07JUCMy-1wUYdMmRaICdj-Q7eNZ5BmKCNsFBcJKaDl5diNw-gSka2F61sywpezU-30sWAtRHYIYZt6PaAaZ4caAdR8f43Yq1m142RWsr3tunLgQ768UNtQ");
        users.setUserType("CLUSTER_ADMIN");
        users.setCreated("2020-10-13");
        users.setLastModified("2020-10-13");
        users.setClusterApiUrl("111.111.111.111:6443");
        users.setClusterToken("eyJhbGciOiJSUzI1NiIsImtpZCI6IktNWmgxVXB3ajgwS0NxZjFWaVZJVGVvTXJoWnZ5dG0tMGExdzNGZjBKX00ifQ.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJwYWFzLWYxMGU3ZTg4LTQ4YTUtNGUyYy04Yjk5LTZhYmIzY2ZjN2Y2Zi1jYWFzIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6InN1cGVyLWFkbWluLXRva2VuLWtzbXo1Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQubmFtZSI6InN1cGVyLWFkbWluIiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZXJ2aWNlLWFjY291bnQudWlkIjoiMjMwZWQ1OGQtNzc0MC00MDI4LTk0MTEtYTM1MzVhMWM0NjU4Iiwic3ViIjoic3lzdGVtOnNlcnZpY2VhY2NvdW50OnBhYXMtZjEwZTdlODgtNDhhNS00ZTJjLThiOTktNmFiYjNjZmM3ZjZmLWNhYXM6c3VwZXItYWRtaW4ifQ.nxnIJCOH_XVMK71s0gF8bgzSxA7g6_y7hGdboLvSqIAGf9J9AgG1DouP29uShK19fMsl9IdbGODPvtuiBz4QyGLPARZldmlzEyFG3k08UMNay1xX_oK-Fe7atMlYgvoGzyM_5-Zp5dyvnxE2skk524htMGHqW1ZwnHLVxtBg8AuGfMwLW1xahmktsNZDG7pRMasPsj73E85lfavMobBlcs4hwVcZU82gAg0SK1QVe7-Uc2ip_9doNo6_9rGW3FwHdVgUNAeCvPRGV0W1dKJv0IX5e_7fIPIznj2xXcZoHf3BnKfDayDIKJOCdsEsy_2NGi1tiD3UvzDDzZpz02T2sg");
        users.setClusterName("cp-cluster");

        when(propertyService.getExceptNamespaceList()).thenReturn(new ArrayList<>());
        when(propertyService.getCpMasterApiListNamespacesDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListNamespacesDeleteUrl().replace("{namespace:.+}", gParams.getNamespace()),
                HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);

        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = namespacesService.deleteNamespaces(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);



    }

    /**
     * Namespaces 생성(Create Namespaces) Test
     */
    @Test
    public void createInitNamespaces_Valid_ReturnModel() {
        // given
        List<String> resourceQuotasList = new ArrayList<>();
        resourceQuotasList.add("cp-resource-quotas-low");

        List<String> limitRangesList = new ArrayList<>();
        limitRangesList.add("cp-limit-ranges-low");

        NamespacesInitTemplate nsInitTemp = new NamespacesInitTemplate();
        nsInitTemp.setName(NAMESPACE);
        nsInitTemp.setNsAdminUserId(NAMESPACE_ADMIN_USER_ID);
        nsInitTemp.setResourceQuotasList(resourceQuotasList);
        nsInitTemp.setLimitRangesList(limitRangesList);

        when(resourceYamlService.createNamespace(gParams)).thenReturn(gFinalResultStatusModel);
        resourceYamlService.createInitRole(gParams);
        resourceYamlService.createAdminRole(gParams);
        when(resourceYamlService.createServiceAccount(gParams)).thenReturn(gFinalResultStatusModel);
        when(propertyService.getAdminRole()).thenReturn("k-paas-container-platform-admin-role");
        when(resourceYamlService.createRoleBinding(gParams)).thenReturn(gFinalResultStatusModel);

        // for - if
        String rq = nsInitTemp.getResourceQuotasList().get(0);
        resourceYamlService.createDefaultResourceQuota(gParams);

        // for - if
        String lr = nsInitTemp.getLimitRangesList().get(0);
        resourceYamlService.createDefaultLimitRanges(gParams);

        String saSecretName = "cp-secret";

//        when(restTemplateService.getSecretName(NAMESPACE, NAMESPACE_ADMIN_USER_ID)).thenReturn(saSecretName);

        Users newNsUser = new Users();
        when(propertyService.getDefaultNamespace()).thenReturn("k-paas-container-platform-temp-namespace");
//        when(usersService.getUsers(CLUSTER, "k-paas-container-platform-temp-namespace", NAMESPACE_ADMIN_USER_ID)).thenReturn(newNsUser);
        newNsUser.setId(0);
        newNsUser.setCpNamespace(NAMESPACE);
        when(propertyService.getAdminRole()).thenReturn("k-paas-container-platform-admin-role");
        newNsUser.setRoleSetCode("k-paas-container-platform-admin-role");
        newNsUser.setSaSecret(saSecretName);
        AccessToken accessToken = new AccessToken();
        accessToken.setUserAccessToken("");
//        when(accessTokenService.getSecrets(NAMESPACE, saSecretName)).thenReturn(accessToken);
        newNsUser.setSaToken(accessToken.getUserAccessToken());
        newNsUser.setUserType(Constants.AUTH_NAMESPACE_ADMIN);
        newNsUser.setIsActive(Constants.CHECK_Y);

//        when(usersService.createUsers(usersService.commonSaveClusterInfo(CLUSTER, newNsUser))).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, "YOUR_NAMESPACES_LIST_PAGE")).thenReturn(gFinalResultStatusModel);

        ResultStatus result = namespacesService.createInitNamespaces(gParams, nsInitTemp);


    }

    /**
     * Namespaces 수정(modify Namespaces) Test
     */
    @Test
    public void modifyInitNamespaces_Valid_ReturnModel() throws Exception {
        // given
        List<String> resourceQuotasList = new ArrayList<>();
        resourceQuotasList.add("cp-resource-quotas-low");

        List<String> limitRangesList = new ArrayList<>();
        limitRangesList.add("cp-limit-ranges-low");

        NamespacesInitTemplate nsInitTemp = new NamespacesInitTemplate();
        nsInitTemp.setName(NAMESPACE);
        nsInitTemp.setNsAdminUserId(NAMESPACE_ADMIN_USER_ID);
        nsInitTemp.setResourceQuotasList(resourceQuotasList);
        nsInitTemp.setLimitRangesList(limitRangesList);

        modifyResourceQuotas_Valid_ReturnModel();
        modifyLimitRanges_Valid_ReturnModel();

        String nsAdminUserId = nsInitTemp.getNsAdminUserId();
        Users nsAdminUser = new Users();
        nsAdminUser.setUserId("cp-user-id");
//        when(usersService.getUsersByNamespaceAndNsAdmin(gParams)).thenReturn(nsAdminUser);
        when(usersService.deleteUsers(gParams)).thenReturn(gFinalResultStatusModel);
        when(resourceYamlService.createServiceAccount(gParams)).thenReturn(gFinalResultStatusModel);
        ResultStatus saResult = gFinalResultStatusModel;
        when(propertyService.getAdminRole()).thenReturn("k-paas-container-platform-admin-role");
        when(resourceYamlService.createRoleBinding(gParams)).thenReturn(gFinalResultStatusModel);
        ResultStatus rbResult = gFinalResultStatusModel;
        String saSecretName = "";
//        when(restTemplateService.getSecretName(NAMESPACE, nsAdminUserId)).thenReturn(saSecretName);
        when(propertyService.getDefaultNamespace()).thenReturn("k-paas-container-platform-temp-namespace");
        Users newNsUser = new Users();
//        when(usersService.getUsersDetailByCluster(gParams)).thenReturn(newNsUser);
        newNsUser.setId(0);
        newNsUser.setCpNamespace(NAMESPACE);
        when(propertyService.getAdminRole()).thenReturn("k-paas-container-platform-admin-role");
        newNsUser.setRoleSetCode("k-paas-container-platform-admin-role");
        newNsUser.setSaSecret(saSecretName);
        AccessToken accessToken = new AccessToken();
        accessToken.setUserAccessToken("");
//        when(accessTokenService.getVaultSecrets(gParams)).thenReturn(accessToken);
        newNsUser.setSaToken(accessToken.getUserAccessToken());
        newNsUser.setUserType(Constants.AUTH_NAMESPACE_ADMIN);
        newNsUser.setIsActive(Constants.CHECK_Y);
//        when(usersService.commonSaveClusterInfo(CLUSTER, newNsUser)).thenReturn(newNsUser);
//        when(usersService.(newNsUser)).thenReturn(gFinalResultStatusModel);
        when(commonService.setResultModelWithNextUrl(resultStatusService.SUCCESS_RESULT_STATUS(), Constants.RESULT_STATUS_SUCCESS, "YOUR_NAMESPACES_DETAIL_PAGE")).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = namespacesService.modifyInitNamespaces(gParams, nsInitTemp);

    }

    /**
     * ResourceQuotas 변경 Test
     */
    @Test
    public void modifyResourceQuotas_Valid_ReturnModel() throws Exception{
        // given
        List<String> requestUpdatedRqList = new ArrayList<>();
        requestUpdatedRqList.add("k-paas-container-platform-low-rq");

        CommonMetaData metaData = new CommonMetaData();
        metaData.setName("cp-name");

        ResourceQuotasListItem resourceQuotas = new ResourceQuotasListItem();
        resourceQuotas.setMetadata(metaData);

        List<ResourceQuotasListItem> listResourceQuotas = new ArrayList<>();
        listResourceQuotas.add(resourceQuotas);

        ResourceQuotasList resourceQuotasListMetadata = new ResourceQuotasList();
        resourceQuotasListMetadata.setItems(listResourceQuotas);

        when(propertyService.getCpMasterApiListResourceQuotasListUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas", HttpMethod.GET, null, ResourceQuotasList.class, gParams)).thenReturn(resourceQuotasListMetadata);

        List<String> k8sResourceQuotasList = resourceQuotasListMetadata.getItems().stream().map(a -> a.getMetadata().getName()).collect(Collectors.toList());

        ArrayList<String> toBeDelete = new ArrayList<>();
        toBeDelete.add("k-paas-container-platform-low-rq");
        when(commonService.compareArrayList(k8sResourceQuotasList, requestUpdatedRqList)).thenReturn(toBeDelete);

        ArrayList<String> toBeAdd = new ArrayList<>();
        toBeAdd.add("k-paas-container-platform-low-rq");
        when(commonService.compareArrayList(requestUpdatedRqList, k8sResourceQuotasList)).thenReturn(toBeAdd);

        // for
        String deleteRqName = toBeDelete.get(0);

        when(resourceQuotasService.deleteResourceQuotas(gParams)).thenReturn(gResultStatusModel);
        when(resourceQuotasService.getResourceQuotasList(gParams)).thenReturn(resourceQuotasListMetadata);

        // for
        String rqName = toBeAdd.get(0);

        resourceYamlService.createDefaultResourceQuota(gParams);

        // when
        Method method = namespacesService.getClass().getDeclaredMethod("modifyResourceQuotas", Params.class, List.class);
        method.setAccessible(true);
//        method.invoke(namespacesService, gParams, requestUpdatedRqList);
        namespacesService.modifyResourceQuotas(gParams, requestUpdatedRqList);

        // then

    }

    /**
     * LimitRanges 변경 Test
     */
    @Test
    public void modifyLimitRanges_Valid_ReturnModel() throws Exception{
        List<String> requestUpdatedLrList = new ArrayList<>();
        requestUpdatedLrList.add("k-paas-container-platform-low-limit-range");

        CommonMetaData metaData = new CommonMetaData();
        metaData.setName("cp-name");

        LimitRangesListItem limitRanges = new LimitRangesListItem();
        limitRanges.setMetadata(metaData);

        List<LimitRangesListItem> listLimitRanges = new ArrayList<>();
        listLimitRanges.add(limitRanges);

        LimitRangesList limitRangesListMetadata = new LimitRangesList();
        limitRangesListMetadata.setItems(listLimitRanges);

        when(propertyService.getCpMasterApiListLimitRangesListUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges", HttpMethod.GET, null, LimitRangesList.class, gParams)).thenReturn(limitRangesListMetadata);

        List<String> k8sLimitRangesList = limitRangesListMetadata.getItems().stream().map(a -> a.getMetadata().getName()).collect(Collectors.toList());
        ArrayList<String> toBeDelete = new ArrayList<>();
        toBeDelete.add("k-paas-container-platform-low-limit-range");
        when(commonService.compareArrayList(k8sLimitRangesList, requestUpdatedLrList)).thenReturn(toBeDelete);

        ArrayList<String> toBeAdd = new ArrayList<>();
        toBeAdd.add("k-paas-container-platform-low-limit-range");
        when(commonService.compareArrayList(requestUpdatedLrList, k8sLimitRangesList)).thenReturn(toBeAdd);

        // for
        String lrName = toBeAdd.get(0);
        resourceYamlService.createDefaultLimitRanges(gParams);

        // for
        String deleteLrName = toBeDelete.get(0);
        when(limitRangesService.deleteLimitRanges(gParams)).thenReturn(gResultStatusModel);
        when(limitRangesService.getLimitRangesList(gParams)).thenReturn(limitRangesListMetadata);

        // when
//        Method method = namespacesService.getClass().getDeclaredMethod("modifyLimitRanges", String.class, List.class);
//        method.setAccessible(true);
//        method.invoke(namespacesService, NAMESPACE, requestUpdatedLrList);

        namespacesService.modifyLimitRanges(gParams, requestUpdatedLrList);

        // then

    }

    /**
     * Namespace Selectbox를 위한 전체 목록 조회 Test
     */
    @Test
    public void getNamespacesListForSelectbox_Valid_ReturnModel() {
        // given
        NamespacesListItem item = new NamespacesListItem();
        item.setName("cp-namespace");

        CommonMetaData metadata = new CommonMetaData();
        metadata.setName("cp-namespace");

        item.setMetadata(metadata);

        List<NamespacesListItem> items = new ArrayList<>();
        items.add(item);

        NamespacesList namespacesListAdmin = new NamespacesList();
        namespacesListAdmin.setItems(items);

        when(namespacesService.getNamespacesList(gParams)).thenReturn(namespacesListAdmin);
        List<NamespacesListItem> namespaceItem = namespacesListAdmin.getItems();

        List<String> returnNamespaceList = new ArrayList<>();
        NamespacesListSupport namespacesListSupport = new NamespacesListSupport();

        returnNamespaceList.add(Constants.ALL_NAMESPACES);
        NamespacesListItem n = namespaceItem.get(0);
        returnNamespaceList.add(n.getName());
        namespacesListSupport.setItems(returnNamespaceList);

        when(commonService.setResultModel(namespacesListSupport, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = (ResultStatus) namespacesService.getNamespacesListForSelectbox(gParams);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getMappingNamespacesListByAdmin() {
//        getNamespacesList_Valid_ReturnModel();

        UsersList gFinalResultModel = new UsersList();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        UsersList gResultModel = new UsersList();
        List<Users> usersList = new ArrayList<>();
        Users users = new Users("test");
        usersList.add(users);
        gResultModel.setItems(usersList);

        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListNamespacesGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultAdminMap);

        when(commonService.setResultObject(gResultAdminMap, NamespacesList.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, gParams, NamespacesList.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);


//        List<Users> items = gResultListModel.getItems().stream().map(x -> new Users(x.getName())).collect(Collectors.toList());
//        items.add(0, new Users(Constants.ALL_NAMESPACES.toUpperCase()));
//        UsersList usersList = new UsersList(items);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        namespacesService.getMappingNamespacesListByAdmin(gParams);
    }
}