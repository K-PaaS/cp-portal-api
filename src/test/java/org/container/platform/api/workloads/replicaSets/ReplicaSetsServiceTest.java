package org.container.platform.api.workloads.replicaSets;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.*;
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
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";

    private static HashMap gResultMap = null;

    private static ReplicaSetsList gResultListModel = null;
    private static ReplicaSetsList gFinalResultListModel = null;

    private static ReplicaSets gResultModel = null;
    private static ReplicaSets gFinalResultModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static Params gParams = null;

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

        List<ReplicaSetsListItem>  replicaSetsList = new ArrayList<>();
        ReplicaSetsListItem replicaSets = new ReplicaSetsListItem();
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

        gResultModel = new ReplicaSets();
        gFinalResultModel = new ReplicaSets();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

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

        gResultYamlModel = new CommonResourcesYaml("");
        gFinalResultYamlModel = new CommonResourcesYaml("");
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        gResultStatusModel = new ResultStatus();
        gFinalResultStatusModel = new ResultStatus();
        gFinalResultStatusModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gParams = new Params();

        //Params setting
        gParams.setCluster(CLUSTER);
        gParams.setNamespace(NAMESPACE);
        gParams.setOffset(OFFSET);
        gParams.setLimit(LIMIT);
        gParams.setOrderBy(ORDER_BY);
        gParams.setOrder(ORDER);
        gParams.setSearchName(SEARCH_NAME);
        gParams.setResourceName(REPLICASETS_NAME);
        gParams.setYaml(YAML_STRING);
    }

    /**
     * ReplicaSets 목록 조회(Get ReplicaSets list) Test
     */
    @Test
    public void getReplicaSetsList_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/{namespace}/replicasets", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        ReplicaSetsList resultList = replicaSetsService.getReplicaSetsList(gParams);

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
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/{namespace}/replicasets/{name}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSets.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, ReplicaSets.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        // when
        ReplicaSets result = replicaSetsService.getReplicaSets(gParams);

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
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/{namespace}/replicasets/{name}", HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        // when
        CommonResourcesYaml result = replicaSetsService.getReplicaSetsYaml(gParams);

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
        gParams.setSelector(SELECTOR);
        when(propertyService.getCpMasterApiListReplicaSetsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListUrl() + "?labelSelector=" + gParams.getSelector(),
                HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsList.class)).thenReturn(gResultListModel);

        when(commonService.setCommonItemMetaDataBySelector(gResultListModel, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when

        ReplicaSetsList resultList = replicaSetsService.getReplicaSetsListLabelSelector(gParams);

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
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/{namespace}/replicasets", HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = replicaSetsService.createReplicaSets(gParams);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * ReplicaSets 삭제(Delete ReplicaSets) Test
     */
    @Test
    public void deleteReplicaSets_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListReplicaSetsDeleteUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/{namespace}/replicasets/{name}", HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = replicaSetsService.deleteReplicaSets(gParams);

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
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/{namespace}/replicasets/{name}", HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = replicaSetsService.updateReplicaSets(gParams);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * 전체 Namespaces 의 ReplicaSets 목록 조회(Get ReplicaSets Admin list in all namespaces) Test
     */
    @Test
    public void getReplicaSetsListAllNamespaces_Valid_ReturnModel() {
        // given
        gParams.setNamespace("all");
        when(propertyService.getCpMasterApiListReplicaSetsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/replicasets");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/{namespace}/replicasets", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, ReplicaSetsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        ReplicaSetsList resultList = replicaSetsService.getReplicaSetsList(gParams);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }
}
