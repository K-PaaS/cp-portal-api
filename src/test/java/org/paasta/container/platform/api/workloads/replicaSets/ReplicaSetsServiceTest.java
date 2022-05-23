package org.paasta.container.platform.api.workloads.replicaSets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.*;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ReplicaSetsServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String REPLICASETS_NAME = "cp-service-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final String SELECTOR = "test-selector";
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";
    private static final String UID= "";
    private static final String RESOURCE_NAME="" ;
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final String TYPE = "deployments";
    private static final boolean isAdmin = true;
    private static final boolean isNotAdmin = false;

    private static HashMap gResultMap = null;

    private static ReplicaSetsList gResultListModel = null;
    private static ReplicaSetsList gFinalResultListModel = null;

    private static ReplicaSetsListAdmin gResultListAdminModel = null;
    private static ReplicaSetsListAdmin gFinalResultListAdminModel = null;

    private static ReplicaSets gResultModel = null;
    private static ReplicaSets gFinalResultModel = null;

    private static ReplicaSetsAdmin gResultAdminModel = null;
    private static ReplicaSetsAdmin gFinalResultAdminModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    @Mock
    private RestTemplateService restTemplateService;

    @Mock
    private CommonService commonService;

    @Mock
    private PropertyService propertyService;

    @InjectMocks
    private ReplicaSetsService replicaSetsService;

    @Before
    public void setUp() {
        gResultMap = new HashMap();

        List<ReplicaSets>  replicaSetsList = new ArrayList<>();
        ReplicaSets replicaSets = new ReplicaSets();
        CommonMetaData metadata = new CommonMetaData();
        CommonSpec commonSpec = new CommonSpec();

        commonSpec.setReplicas(2);

        List<CommonOwnerReferences> commonOwnerReferencesList = new ArrayList<>();
        CommonOwnerReferences commonOwnerReferences = new CommonOwnerReferences();
        commonOwnerReferences.setUid("uid");
        commonOwnerReferences.setName("name");
        commonOwnerReferencesList.add(commonOwnerReferences);



        metadata.setOwnerReferences(commonOwnerReferencesList);
        replicaSets.setMetadata(metadata);
        replicaSets.setSpec(commonSpec);
        replicaSetsList.add(replicaSets);
        gResultListModel = new ReplicaSetsList();
        gResultListModel.setItems(replicaSetsList);

        gFinalResultListModel = new ReplicaSetsList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultListAdminModel = new ReplicaSetsListAdmin();
        gFinalResultListAdminModel = new ReplicaSetsListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultModel = new ReplicaSets();
        gFinalResultModel = new ReplicaSets();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultAdminModel = new ReplicaSetsAdmin();
        gFinalResultAdminModel = new ReplicaSetsAdmin();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

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



        gResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        gResultStatusModel = new ResultStatus();
        gFinalResultStatusModel = new ResultStatus();
        gFinalResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets list) Test
     */
    @Test
    public void getReplicaSetsList_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" +NAMESPACE+ "/replicasets", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        ReplicaSetsList resultList = replicaSetsService.getReplicaSetsList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * ReplicaSets 상세 조회(Get ReplicaSets detail) Test
     */
    @Test
    public void getReplicaSets_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets/" + REPLICASETS_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSets.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        // when
        ReplicaSets result = replicaSetsService.getReplicaSets(NAMESPACE, REPLICASETS_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * ReplicaSets 상세 조회(Get ReplicaSets detail) Test
     * (Admin Portal)
     */
    @Test
    public void getReplicaSetsAdmin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets/" + REPLICASETS_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, ReplicaSetsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        // when
        ReplicaSetsAdmin result = (ReplicaSetsAdmin) replicaSetsService.getReplicaSetsAdmin(NAMESPACE, REPLICASETS_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * ReplicaSets YAML 조회(Get ReplicaSets yaml) Test
     */
    @Test
    public void getReplicaSetsYaml_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets/" + REPLICASETS_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        // when
        CommonResourcesYaml result = (CommonResourcesYaml) replicaSetsService.getReplicaSetsYaml(NAMESPACE, REPLICASETS_NAME, gResultMap);

        // then
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * ReplicaSets Admin YAML 조회(Get ReplicaSets Admin yaml) Test
     */
    @Test
    public void getReplicaSetsAdminYaml_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets/" + REPLICASETS_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        // when
        CommonResourcesYaml result = (CommonResourcesYaml) replicaSetsService.getReplicaSetsAdminYaml(NAMESPACE, REPLICASETS_NAME, gResultMap);

        // then
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets Selector) Test
     */
    @Test
    public void getReplicaSetsListLabelSelector_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets?labelSelector=" + SELECTOR, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsList.class)).thenReturn(gResultListModel);

        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when

        ReplicaSetsList resultList = replicaSetsService.getReplicaSetsListLabelSelector(NAMESPACE, SELECTOR, TYPE, UID, RESOURCE_NAME, OFFSET,LIMIT,ORDER_BY, ORDER,SEARCH_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets Selector) Test
     */
    @Test
    public void getReplicaSetsListLabelSelectorAdmin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets?labelSelector=" + SELECTOR, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setCommonItemMetaDataBySelector(gResultListAdminModel, ReplicaSetsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        // when
        ReplicaSetsListAdmin resultList = replicaSetsService.getReplicaSetsListLabelSelectorAdmin(NAMESPACE, SELECTOR);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets list) Test
     */
    @Test
    public void getReplicaSetsListAdmin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ReplicaSetsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        // when
        ReplicaSetsListAdmin resultList = (ReplicaSetsListAdmin) replicaSetsService.getReplicaSetsListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * ReplicaSets 생성(Create ReplicaSets) Test
     */
    @Test
    public void createReplicaSets_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsCreateUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_REPLICA_SETS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = (ResultStatus) replicaSetsService.createReplicaSets(NAMESPACE, YAML_STRING, isAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * ReplicaSets 삭제(Delete ReplicaSets) Admin Test
     */
    @Test
    public void deleteReplicaSets_Admin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsDeleteUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets/" + REPLICASETS_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_REPLICA_SETS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = replicaSetsService.deleteReplicaSets(NAMESPACE, REPLICASETS_NAME, isAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * ReplicaSets 삭제(Delete ReplicaSets) Not Admin Test
     */
    @Test
    public void deleteReplicaSets_Not_Admin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsDeleteUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets/" + REPLICASETS_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_REPLICA_SETS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = replicaSetsService.deleteReplicaSets(NAMESPACE, REPLICASETS_NAME, isNotAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * ReplicaSets 수정(Update ReplicaSets) Test
     */
    @Test
    public void updateReplicaSets_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsUpdateUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/replicasets/" + REPLICASETS_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_REPLICA_SETS_DETAIL.replace("{replicaSetName:.+}", REPLICASETS_NAME))).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = replicaSetsService.updateReplicaSets(NAMESPACE, REPLICASETS_NAME, YAML_STRING, isAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * 전체 Namespaces 의 ReplicaSets Admin 목록 조회(Get ReplicaSets Admin list in all namespaces) Test
     */
    @Test
    public void getReplicaSetsListAllNamespacesAdmin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsListAllNamespacesUrl()).thenReturn("/apis/apps/v1/replicasets");
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/replicasets" + FIELD_SELECTOR, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ReplicaSetsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        // when
        ReplicaSetsListAdmin resultList =  (ReplicaSetsListAdmin) replicaSetsService.getReplicaSetsListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(gFinalResultListAdminModel, resultList);
    }
}
