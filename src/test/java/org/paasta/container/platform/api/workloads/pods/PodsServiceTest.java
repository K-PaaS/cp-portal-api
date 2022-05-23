package org.paasta.container.platform.api.workloads.pods;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.*;
import org.paasta.container.platform.api.workloads.pods.support.ContainerStatusesItem;
import org.paasta.container.platform.api.workloads.pods.support.PodsStatus;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class PodsServiceTest {


    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String ALL_NAMESPACE = "all";
    private static final String PODS_NAME = "test-pod";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final String TYPE = "replicaSets";
    private static final String OWNER_REFERENCES_UID = "";
    private static final String SELECTOR = "app=nginx";
    private static final String NODE_NAME = "paasta-cp-k8s-worker-002";
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";
    private static final String containerUsageName = "cp-container";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final String UID = "81f2c76c-4d39-40d7-a4e5-3f7a99ae9c63";
    private static final boolean isAdmin = true;
    private static final boolean isNotAdmin = false;

    private static HashMap gResultMap = null;

    private static PodsList gResultListModel = null;
    private static PodsList gFinalResultListModel = null;

    private static PodsListAdmin gResultListAdminModel = null;
    private static PodsListAdmin gFinalResultListAdminModel = null;

    private static PodsListAdminList gResultListAdminListModel = null;
    private static PodsListAdminList gFinalResultListAdminListModel = null;

    private static Pods gResultModel = null;
    private static Pods gFinalResultModel = null;

    private static PodsAdmin gResultAdminModel = null;
    private static PodsAdmin gFinalResultAdminModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static PodsStatus podsStatus = null;
    private static List<ContainerStatusesItem> containerStatuses = null;
    private static ContainerStatusesItem containerStatusesItem = null;

    private static List<PodsListAdminList> podsItemListAdmin = null;
    private static List<Pods> podsItemListUser = null;
    private static PodsListAdminList podsListAdminList = null;

    private static  PodsMetric podsMetric = null;
    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @Spy
    @InjectMocks
    PodsService podsService;

    @Before
    public void setUp() {
        gResultMap = new HashMap();

        podsListAdminList = new PodsListAdminList();
        podsStatus = new PodsStatus();

        gFinalResultListModel = new PodsList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);





        CommonMetaData metaData = new CommonMetaData();
        List<CommonOwnerReferences> ownerReferences = new ArrayList<>();
        CommonOwnerReferences commonOwnerReferences = new CommonOwnerReferences();
        commonOwnerReferences.setUid(UID);

        ownerReferences.add(commonOwnerReferences);
        metaData.setOwnerReferences(ownerReferences);

        HashMap hm = new HashMap();
        hm.put("cpu", "");
        hm.put("memory", "");

        //PodsList.item
        List<Pods> items = new ArrayList<>();
        List<CommonContainer> containers = new ArrayList<>();
        CommonContainer commonContainer = new CommonContainer();

        CommonResourceRequirement resources = new CommonResourceRequirement();
        resources.setUsage(hm);
        commonContainer.setResources(resources);
        commonContainer.setName(containerUsageName);
        containers.add(commonContainer);
        CommonSpec commonSpec = new CommonSpec();
        commonSpec.setContainers(containers);
        Pods pods = new Pods();
        pods.setMetadata(metaData);
        pods.setSpec(commonSpec);
        items.add(pods);


        PodsUsage podsUsage = new PodsUsage();
        List<Containers> containersListForUsage = new ArrayList<>();
        Containers containersforUsage = new Containers();
        ContainerUsage  containerUsage = new ContainerUsage();
        containerUsage.setCpu("");
        containerUsage.setMemory("");
        containersforUsage.setName(containerUsageName);
        containersforUsage.setUsage(containerUsage);
        containersListForUsage.add(containersforUsage);
        podsUsage.setContainers(containersListForUsage);





       podsMetric = new PodsMetric();

        //PodsMetrics.item
        List<PodsUsage>  podsUsagesItem = new ArrayList<>();
        metaData.setName(containerUsageName);
        podsUsage.setMetadata(metaData);
        podsUsagesItem.add(podsUsage);
        podsMetric.setItems(podsUsagesItem);


        gResultListModel = new PodsList();
        gResultListModel.setItems(items);
        gFinalResultListModel.setItems(items);


        //admin
        podsItemListAdmin = new ArrayList<>();
        PodsListAdminList podsitemAdmin = new PodsListAdminList();
        PodsStatus podsStatus = new PodsStatus();
        podsitemAdmin.setStatus(podsStatus);
        podsitemAdmin.setMetadata(metaData);

        podsItemListAdmin.add(podsitemAdmin);
        gResultListAdminModel = new PodsListAdmin();
        gResultListAdminModel.setItems(podsItemListAdmin);


        gFinalResultListAdminModel = new PodsListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultListAdminListModel = new PodsListAdminList();
        gFinalResultListAdminListModel = new PodsListAdminList();

        gResultModel = new Pods();
        gFinalResultModel = new Pods();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultAdminModel = new PodsAdmin();
        gFinalResultAdminModel = new PodsAdmin();
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

        ContainerStatusesItem containerStatusesItem = new ContainerStatusesItem();


    }


    /**
     * Pods 목록 조회(Get Pods list) Test
     * (Admin Portal)
     */
    @Test
    public void getPodsListAdmin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods", HttpMethod.GET, null, Map.class))
                .thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gResultListAdminModel);

        // when
        PodsListAdmin resultList = (PodsListAdmin) podsService.getPodsListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(gResultListAdminModel, resultList);
    }

    /**
     * Pods 목록 조회(Get Pods selector) Test
     */
    @Test
    public void getPodListWithLabelSelector_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods?labelSelector=" + SELECTOR, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gResultListModel);
    }

    /**
     * Pods 목록 조회(Get Pods selector) Test
     */
    @Test
    public void getPodListWithLabelSelectorAdmin_Valid_ReturnModel() {
        String type = "replicaSets";
        // given
        when(propertyService.getCpMasterApiListPodsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods?labelSelector=" + SELECTOR, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).
                thenReturn(gResultListAdminModel);

        // when
        PodsListAdmin resultList = (PodsListAdmin) podsService.getPodListWithLabelSelectorAdmin(NAMESPACE, SELECTOR, "replicaSets", UID, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(gResultListAdminModel, resultList);
    }

    /**
     * Pods 목록 조회(Get Pods node) Test
     */
    @Test
    public void getPodListByNode_Valid_ReturnModel() {
        // given

        String requestURL = "/api/v1/namespaces/" + NAMESPACE + "/pods?fieldSelector=spec.nodeName=" + NODE_NAME;

        when(propertyService.getCpMasterApiListPodsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, requestURL, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);
    }

    /**
     * Pods 목록 조회(Get Pods node) Test
     * (Admin portal)
     */
    @Test
    public void getPodsListByNodeAdmin_Valid_ReturnModel() {
        // given

        String requestURL = "/api/v1/namespaces/" + NAMESPACE + "/pods?fieldSelector=spec.nodeName=" + NODE_NAME;

        when(propertyService.getCpMasterApiListPodsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(commonService.generateFieldSelectorForPodsByNode(Constants.PARAM_QUERY_FIRST, NODE_NAME)).thenReturn("?fieldSelector=spec.nodeName=" + NODE_NAME);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, requestURL, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gResultListAdminModel);

        // when
        PodsListAdmin resultList = (PodsListAdmin) podsService.getPodsListByNodeAdmin(NAMESPACE, NODE_NAME, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(gResultListAdminModel, resultList);
    }


    /**
     * Pods 목록 조회(Get Pods node) Test
     * (Admin portal)
     */
    @Test
    public void getPodsListByNodeAdmin_allNamespace_Valid_ReturnModel() {
        // given
        String ignoreNamesapceQuery = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=default,metadata.namespace!=paas-ta-container-platform-temp-namespace,metadata.namespace!=temp-namespace";
        String nodeNameQuery =",spec.nodeName="+NODE_NAME;
        String requestURL = "/api/v1/pods" + ignoreNamesapceQuery + nodeNameQuery;

        when(propertyService.getCpMasterApiListPodsListAllNamespacesUrl()).thenReturn("/api/v1/pods");
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(ignoreNamesapceQuery);
        when(commonService.generateFieldSelectorForPodsByNode(Constants.PARAM_QUERY_AND, NODE_NAME)).thenReturn(nodeNameQuery);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, requestURL, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gResultListAdminModel);

        // when
        PodsListAdmin resultList = (PodsListAdmin) podsService.getPodsListByNodeAdmin("all", NODE_NAME, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(gResultListAdminModel, resultList);
    }

    /**
     * Pods 상세 조회(Get Pods detail) Test
     */
    @Test
    public void getPods_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods/" + PODS_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Pods.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        // when
        Pods result = podsService.getPods(NAMESPACE, PODS_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Pods 상세 조회(Get Pods detail) Test
     * (Admin Portal)
     */
    @Test
    public void getPodsAdmin_Valid_ReturnModel() {
        PodsStatus originStatus = new PodsStatus();
        originStatus.setPodIP("aaa");
        originStatus.setQosClass("aaa");
        originStatus.setContainerStatuses(null);
        originStatus.setPhase("aaa");

        gResultMap.put("status", originStatus);

        // given
        when(propertyService.getCpMasterApiListPodsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods/" + PODS_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);

        PodsStatus status = new PodsStatus();

        List<ContainerStatusesItem> list = new ArrayList<>();
        ContainerStatusesItem item = new ContainerStatusesItem();
        item.setRestartCount(0);

        status.setPodIP("aaa");
        status.setQosClass("aaa");
        status.setContainerStatuses(list);
        status.setPhase("aaa");

        when(commonService.setResultObject(gResultMap.get("status"), PodsStatus.class)).thenReturn(status);


        // try catch
        when(commonService.setResultObject(gResultMap, PodsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, PodsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        // when
        PodsAdmin result = (PodsAdmin) podsService.getPodsAdmin(NAMESPACE, PODS_NAME);

        // then
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Pods YAML 조회(Get Pods yaml) Test
     */
    @Test
    public void getPodsYaml_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods/" + PODS_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        // when
        CommonResourcesYaml result = (CommonResourcesYaml) podsService.getPodsYaml(NAMESPACE, PODS_NAME, gResultMap);

        // then
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Pods Admin YAML 조회(Get Pods Admin yaml) Test
     */
    @Test
    public void getPodsAdminYaml_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods/" + PODS_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);

        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        // when
        CommonResourcesYaml result = (CommonResourcesYaml) podsService.getPodsAdminYaml(NAMESPACE, PODS_NAME, gResultMap);

        // then
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    /**
     * Pods 생성(Create Pods) Test
     */
    @Test
    public void createPods_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsCreateUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_PODS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = (ResultStatus) podsService.createPods(NAMESPACE, YAML_STRING, isAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * Pods 삭제(Delete Pods) Admin Test
     */
    @Test
    public void deletePods_Admin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods/" + PODS_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_PODS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = podsService.deletePods(NAMESPACE, PODS_NAME, isAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * Pods 삭제(Delete Pods) Not Admin Test
     */
    @Test
    public void deletePods_Not_Admin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods/" + PODS_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_PODS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = podsService.deletePods(NAMESPACE, PODS_NAME, isNotAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * Pods 수정(Update Pods) Test
     */
    @Test
    public void updatePods_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsUpdateUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/pods/" + PODS_NAME, HttpMethod.PUT, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_PODS_DETAIL.replace("{podName:.+}", PODS_NAME))).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = (ResultStatus) podsService.updatePods(NAMESPACE, PODS_NAME, YAML_STRING, isAdmin);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * 전체 Namespaces 의 Pods Admin 목록 조회(Get Services Admin list in all namespaces) Test
     */
    @Test
    public void getPodsListAllNamespacesAdmin_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsListAllNamespacesUrl())
                .thenReturn("/api/v1/pods");
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE))
                .thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/pods" + FIELD_SELECTOR, HttpMethod.GET, null, Map.class))
                .thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsListAdmin.class))
                .thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsListAdmin.class))
                .thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS))
                .thenReturn(gResultListAdminModel);


        // when
        PodsListAdmin resultList = (PodsListAdmin) podsService.getPodsListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        // then
        assertEquals(gResultListAdminModel, resultList);
    }

    /**
     * 참조자 UID에 의한 Pods 목록 필터 (Get Services Admin list in all namespaces) Test
     */
    @Test
    public void podsFIlterWithOwnerReferences_Valid_ReturnModel() {

        PodsListAdmin resultList =  podsService.podsFIlterWithOwnerReferences(gResultListAdminModel, UID);
        assertEquals(gResultListAdminModel, resultList);

    }



    @Test
    public void getMergeMetric_Valid_ReturnModel() {
        podsService.getMergeMetric(gResultListModel, podsMetric);
    }

    @Test
    public void getPodsMetricList_Valid_ReturnModel() {
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/metrics.k8s.io/v1beta1/namespaces/"+ NAMESPACE + "/pods", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsMetric.class)).thenReturn(podsMetric);
    }



}


