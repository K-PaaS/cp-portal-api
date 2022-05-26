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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
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

    private static HashMap gResultMap = null;

    private static PodsList gResultListModel = null;
    private static PodsList gFinalResultListModel = null;


    private static Pods gResultModel = null;
    private static Pods gFinalResultModel = null;


    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static PodsStatus podsStatus = null;
    private static List<ContainerStatusesItem> containerStatuses = null;
    private static ContainerStatusesItem containerStatusesItem = null;

    private static List<PodsList> podsItemListAdmin = null;
    private static List<Pods> podsItemListUser = null;

    private static Params gParams = null;

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
        List<PodsListItem> items = new ArrayList<>();
        List<CommonContainer> containers = new ArrayList<>();
        CommonContainer commonContainer = new CommonContainer();

        CommonResourceRequirement resources = new CommonResourceRequirement();
        resources.setUsage(hm);
        commonContainer.setResources(resources);
        commonContainer.setName(containerUsageName);
        containers.add(commonContainer);
        CommonSpec commonSpec = new CommonSpec();
        commonSpec.setContainers(containers);
//        Pods pods = new Pods();
//        pods.setMetadata(metaData);
//        pods.setSpec(commonSpec);
//        items.add(pods);


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

        gResultModel = new Pods();
        gFinalResultModel = new Pods();
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
        gParams.setResourceName(PODS_NAME);
        gParams.setYaml(YAML_STRING);

    }


    /**
     * Pods 목록 조회(Get Pods list) Test
     */
    @Test
    public void getPodsList_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                "/api/v1/namespaces/{namespace}/pods", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, PodsList.class)).thenReturn(gResultListModel);
        when(podsService.restartCountProcessing(gResultListModel)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        PodsList resultList = podsService.getPodsList(gParams);

        // then
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * Pods 목록 조회(Get Pods selector) Test
     */
    @Test
    public void getPodListWithLabelSelector_Valid_ReturnModel() {
        // given
        gParams.setAddParam("/pods?labelSelector=" + SELECTOR);
        when(propertyService.getCpMasterApiListPodsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/pods", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, PodsList.class)).thenReturn(gResultListModel);
        when(podsService.restartCountProcessing(gResultListModel)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        PodsList resultList = podsService.getPodListWithLabelSelector(gParams);

        // then
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    /**
     * Pods 목록 조회(Get Pods node) Test
     */
    @Test
    public void getPodListByNode_Valid_ReturnModel() {
        // given
        gParams.setAddParam(",spec.nodeName=" + NODE_NAME);
        when(propertyService.getCpMasterApiListPodsListAllNamespacesUrl()).thenReturn("/api/v1/pods");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/pods", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PodsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, PodsList.class)).thenReturn(gResultListModel);
        when(podsService.restartCountProcessing(gResultListModel)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        // when
        PodsList resultList = podsService.getPodsListByNode(gParams);

        // then
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }



    /**
     * Pods 상세 조회(Get Pods detail) Test
     */

    @Test
    public void getPods_Valid_ReturnModel() {
        PodsStatus originStatus = new PodsStatus();
        originStatus.setPodIP("aaa");
        originStatus.setQosClass("aaa");
        originStatus.setContainerStatuses(null);
        originStatus.setPhase("aaa");

        gResultMap.put("status", originStatus);

        // given
        when(propertyService.getCpMasterApiListPodsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/pods/{name}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);

        PodsStatus status = new PodsStatus();

        List<ContainerStatusesItem> list = new ArrayList<>();
        ContainerStatusesItem item = new ContainerStatusesItem();
        item.setRestartCount(0);

        status.setPodIP("aaa");
        status.setQosClass("aaa");
        status.setContainerStatuses(list);
        status.setPhase("aaa");

        when(commonService.setResultObject(gResultMap.get("status"), PodsStatus.class)).thenReturn(status);
        when(commonService.setResultObject(gResultMap, Pods.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, Pods.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        // when
        Pods result = podsService.getPods(gParams);

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
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/pods/{name}", HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);

        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        // when
        CommonResourcesYaml result = podsService.getPodsYaml(gParams);

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
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/pods", HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = podsService.createPods(gParams);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }

    /**
     * Pods 삭제(Delete Pods) Test
     */
    @Test
    public void deletePods_Valid_ReturnModel() {
        // given
        when(propertyService.getCpMasterApiListPodsDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/pods/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/pods/{name}", HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = podsService.deletePods(gParams);

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
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/pods/{name}", HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        // when
        ResultStatus result = podsService.updatePods(gParams);

        // then
        assertEquals(gFinalResultStatusModel, result);
    }
//
//    /**
//     * 전체 Namespaces 의 Pods Admin 목록 조회(Get Services Admin list in all namespaces) Test
//     */
//    @Test
//    public void getPodsListAllNamespacesAdmin_Valid_ReturnModel() {
//        // given
//        when(propertyService.getCpMasterApiListPodsListAllNamespacesUrl())
//                .thenReturn("/api/v1/pods");
//        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE))
//                .thenReturn(FIELD_SELECTOR);
//        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/pods" + FIELD_SELECTOR, HttpMethod.GET, null, Map.class))
//                .thenReturn(gResultMap);
//        when(commonService.setResultObject(gResultMap, PodsListAdmin.class))
//                .thenReturn(gResultListAdminModel);
//        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PodsListAdmin.class))
//                .thenReturn(gResultListAdminModel);
//        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS))
//                .thenReturn(gResultListAdminModel);
//
//
//        // when
//        PodsListAdmin resultList = (PodsListAdmin) podsService.getPodsListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);
//
//        // then
//        assertEquals(gResultListAdminModel, resultList);
//    }
//

}


