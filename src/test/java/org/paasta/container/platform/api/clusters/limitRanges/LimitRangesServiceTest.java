package org.paasta.container.platform.api.clusters.limitRanges;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.*;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class LimitRangesServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String LIMIT_RANGE_NAME = "test-limit-range-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String LOW_LIMIT_NAME = "paas-ta-container-platform-low-limit-range";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    public static final String CHECK_Y = "Y";
    public static final String CHECK_N = "N";

    private static final String LIMIT_RANGE_RESOURCE = "cpu";
    private static final String LIMIT_RANGE_TYPE = "Container";
    private static final String LIMIT_RANGE_DEFAULT_LIMIT = "100m";
    private static final String LIMIT_RANGE_DEFAULT_REQUEST = "-";
    private static final String MAX = "-";
    private static final String MIN = "-";
    private static final String CREATION_TIME = "2020-11-17T09:31:37Z";

    private static HashMap gResultMap = null;

    private static LimitRangesList gResultListModel = null;
    private static LimitRangesList gFinalResultListModel = null;
    private static LimitRangesList gFinalResultListFailModel = null;

    private static LimitRanges gResultModel = null;
    private static LimitRanges gFinalResultModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static LimitRangesTemplateItem gFinalLimitRangesTemplateItem = null;

    private static Params gParams = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    LimitRangesService limitRangesService;

    @Before
    public void setUp() {
        gResultMap = new HashMap();
        gParams = new Params();

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_LIMIT_RANGES);

        // 리스트가져옴
        gResultListModel = new LimitRangesList();

        gFinalResultListModel = new LimitRangesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new LimitRangesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new LimitRanges();
        gFinalResultModel = new LimitRanges();
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

        gFinalLimitRangesTemplateItem = LimitRangesModel.getLimitRangesTemplateItem();

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

        //Params setting
        gParams.setCluster(CLUSTER);
        gParams.setNamespace(NAMESPACE);
        gParams.setOffset(OFFSET);
        gParams.setLimit(LIMIT);
        gParams.setOrderBy(ORDER_BY);
        gParams.setOrder(ORDER);
        gParams.setSearchName(SEARCH_NAME);
        gParams.setResourceName(LIMIT_RANGE_NAME);
        gParams.setYaml(YAML_STRING);

    }

    @Test
    public void getLimitRangesList_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesListUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/limitranges", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        LimitRangesList resultList = limitRangesService.getLimitRangesList(gParams);

        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

//    @Test
//    public void getLimitRanges_Yaml_Valid_ReturnModel() {
//
//        //when
//        when(propertyService.getCpMasterApiListLimitRangesGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
//        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/limitranges/{name}", HttpMethod.GET, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
//        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);
//
//        //call method
//        CommonResourcesYaml result = (CommonResourcesYaml) limitRangesService.getLimitRangesYaml(gParams);
//
//        //compare result
//        //assertThat(result).isNotNull();
//        assertEquals(YAML_STRING, result.getSourceTypeYaml());
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
//    }

    @Test
    public void createLimitRanges_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListLimitRangesCreateUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/limitranges", HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.createLimitRanges(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteLimitRanges_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/limitranges/{name}", HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.deleteLimitRanges(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateLimitRanges_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesUpdateUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/limitranges/{name}", HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.updateLimitRanges(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void getLimitRangesDb_Valid_ReturnModel() {
        LimitRangesTemplateItem templateItem = limitRangesService.getLimitRangesDb(LimitRangesModel.getLimitRangesDefault(), CHECK_Y);
        assertNotNull(templateItem);
    }




/*    @Test
    public void getLimitRangesListAllNamespacesAdmin_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesListAllNamespacesUrl()).thenReturn("/api/v1/limitranges");

        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/limitranges?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, LimitRangesList.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, LimitRangesList.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        LimitRangesList resultList = (LimitRangesList) limitRangesService.getLimitRangesListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getLimitRangesAdmin_Type_Container_Valid_ReturnModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LimitRanges limitRanges = new LimitRanges();
        List<LimitRangesItem> limitsList = new ArrayList<>();

        LimitRangesItem item = new LimitRangesItem();
        LinkedTreeMap defaultLimitMap = new LinkedTreeMap();
        defaultLimitMap.put(Constants.SUPPORTED_RESOURCE_MEMORY, "500Mi");

        LinkedTreeMap defaultRequestMap = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_MEMORY, "100Mi");

        LinkedTreeMap max = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_MEMORY, "100Mi");

        LinkedTreeMap min = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_MEMORY, "100Mi");

        item.setType(Constants.LIMIT_RANGE_TYPE_CONTAINER);
        item.setResource(Constants.SUPPORTED_RESOURCE_MEMORY);
        item.setDefaultLimit(defaultLimitMap);
        item.setDefaultRequest(defaultRequestMap);
        item.setMax(max);
        item.setMin(min);

        limitsList.add(item);
        CommonSpec spec = new CommonSpec();
        spec.setLimits(limitsList);

        CommonMetaData metaData = new CommonMetaData();
        metaData.setName(LOW_LIMIT_NAME);
        metaData.setNamespace(NAMESPACE);
        metaData.setCreationTimestamp("2020-11-17T09:31:37Z");

        limitRanges.setName(metaData.getName());
        limitRanges.setCreationTimestamp("2020-11-17T09:31:37Z");
        limitRanges.setMetadata(metaData);
        limitRanges.setSpec(spec);

        LimitRanges finalLimitRanges = limitRanges;
        finalLimitRanges.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        LimitRangesItem serversItem = new LimitRangesItem();

        LimitRangesItem finalServersItem = new LimitRangesItem();
        finalServersItem.setType(Constants.LIMIT_RANGE_TYPE_CONTAINER);
        finalServersItem.setResource(Constants.SUPPORTED_RESOURCE_MEMORY);

        LinkedHashMap map = new LinkedHashMap();
        map.put("metadata", metaData);
        map.put("spec", spec);

        //when
        when(propertyService.getCpMasterApiListLimitRangesGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges/" + LIMIT_RANGE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(map);
        when(commonService.setResultObject(map, LimitRanges.class)).thenReturn(limitRanges);

        String type = Constants.LIMIT_RANGE_TYPE_CONTAINER;
        String resourceType = Constants.SUPPORTED_RESOURCE_MEMORY;

        Method getLimitRangesTemplateItem = limitRangesService.getClass().getDeclaredMethod("getLimitRangesTemplateItem", String.class, String.class, String.class, String.class, LimitRangesItem.class, Object.class);
        getLimitRangesTemplateItem.setAccessible(true);

        Method commonSetResourceValue = limitRangesService.getClass().getDeclaredMethod("commonSetResourceValue", String.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, Object.class);
        commonSetResourceValue.setAccessible(true);

        getLimitRangesTemplateItem.invoke(limitRangesService, metaData.getName(), metaData.getCreationTimestamp(), type, resourceType, item, serversItem);

        when(commonService.setResultObject(limitRanges, LimitRanges.class)).thenReturn(limitRanges);
        when(commonService.setResultModel(limitRanges, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalLimitRanges);

        //call method
        LimitRanges result = (LimitRanges) limitRangesService.getLimitRangesAdmin(NAMESPACE, LIMIT_RANGE_NAME);

        //compare result
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }


    @Test
    public void getLimitRangesAdmin_Type_Storage_Valid_ReturnModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LimitRanges limitRanges = new LimitRanges();
        List<LimitRangesItem> limitsList = new ArrayList<>();

        LimitRangesItem item = new LimitRangesItem();
        LinkedTreeMap defaultLimitMap = new LinkedTreeMap();
        defaultLimitMap.put(Constants.SUPPORTED_RESOURCE_STORAGE, "500Mi");

        LinkedTreeMap defaultRequestMap = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_STORAGE, "100Mi");

        LinkedTreeMap max = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_STORAGE, "100Mi");

        LinkedTreeMap min = new LinkedTreeMap();
        defaultRequestMap.put(Constants.SUPPORTED_RESOURCE_STORAGE, "100Mi");

        item.setType(Constants.LIMIT_RANGE_TYPE_PVC);
        item.setResource(Constants.SUPPORTED_RESOURCE_STORAGE);
        item.setDefaultLimit(defaultLimitMap);
        item.setDefaultRequest(defaultRequestMap);
        item.setMax(max);
        item.setMin(min);

        limitsList.add(item);
        CommonSpec spec = new CommonSpec();
        spec.setLimits(limitsList);

        CommonMetaData metaData = new CommonMetaData();
        metaData.setName(LOW_LIMIT_NAME);
        metaData.setNamespace(NAMESPACE);
        metaData.setCreationTimestamp("2020-11-17T09:31:37Z");

        limitRanges.setName(metaData.getName());
        limitRanges.setCreationTimestamp("2020-11-17T09:31:37Z");
        limitRanges.setMetadata(metaData);
        limitRanges.setSpec(spec);

        LimitRanges finalLimitRanges = limitRanges;
        finalLimitRanges.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        LimitRangesItem serversItem = new LimitRangesItem();

        LimitRangesItem finalServersItem = new LimitRangesItem();
        finalServersItem.setType(Constants.LIMIT_RANGE_TYPE_PVC);
        finalServersItem.setResource(Constants.SUPPORTED_RESOURCE_STORAGE);

        LinkedHashMap map = new LinkedHashMap();
        map.put("metadata", metaData);
        map.put("spec", spec);

        //when
        when(propertyService.getCpMasterApiListLimitRangesGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges/" + LIMIT_RANGE_NAME, HttpMethod.GET, null, Map.class)).thenReturn(map);
        when(commonService.setResultObject(map, LimitRanges.class)).thenReturn(limitRanges);

        String type = Constants.LIMIT_RANGE_TYPE_PVC;
        String resourceType = Constants.SUPPORTED_RESOURCE_STORAGE;

        Method getLimitRangesTemplateItem = limitRangesService.getClass().getDeclaredMethod("getLimitRangesTemplateItem", String.class, String.class, String.class, String.class, LimitRangesItem.class, Object.class);
        getLimitRangesTemplateItem.setAccessible(true);

        Method commonSetResourceValue = limitRangesService.getClass().getDeclaredMethod("commonSetResourceValue", String.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, Object.class);
        commonSetResourceValue.setAccessible(true);

        getLimitRangesTemplateItem.invoke(limitRangesService, metaData.getName(), metaData.getCreationTimestamp(), type, resourceType, item, serversItem);

        when(commonService.setResultObject(limitRanges, LimitRanges.class)).thenReturn(limitRanges);
        when(commonService.setResultModel(limitRanges, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalLimitRanges);

        //call method
        LimitRanges result = (LimitRanges) limitRangesService.getLimitRangesAdmin(NAMESPACE, LIMIT_RANGE_NAME);

        //compare result
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getLimitRangesTemplateList_Valid_ReturnModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LimitRanges limitRanges = LimitRangesModel.getLimitRangesAdminContainerMemory();

        LimitRanges finalLimitRanges = limitRanges;
        finalLimitRanges.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        LimitRangesTemplateItem serversItem = new LimitRangesTemplateItem();

        LimitRangesTemplateItem finalServersItem = new LimitRangesTemplateItem();
        finalServersItem.setType(Constants.LIMIT_RANGE_TYPE_CONTAINER);
        finalServersItem.setResource(Constants.SUPPORTED_RESOURCE_MEMORY);

        LinkedHashMap map = new LinkedHashMap();
        map.put("metadata", LimitRangesModel.getMetadata());
        map.put("spec", LimitRangesModel.getSpec());

        LimitRangesDefaultList defaultList = new LimitRangesDefaultList();
        LimitRangesDefault limitRangesDefault = new LimitRangesDefault();
        limitRangesDefault.setName(LIMIT_RANGE_NAME);
        limitRangesDefault.setType(LIMIT_RANGE_TYPE);
        limitRangesDefault.setResource(LIMIT_RANGE_RESOURCE);
        limitRangesDefault.setDefaultLimit(LIMIT_RANGE_DEFAULT_LIMIT);
        limitRangesDefault.setDefaultRequest(LIMIT_RANGE_DEFAULT_REQUEST);
        limitRangesDefault.setMax(MAX);
        limitRangesDefault.setMin(MIN);
        limitRangesDefault.setCreationTimestamp(CREATION_TIME);

        List<LimitRangesDefault> limitRangesDefaultLists = new ArrayList<>();
        limitRangesDefaultLists.add(limitRangesDefault);

        defaultList.setItems(limitRangesDefaultLists);

        getLimitRangesListAdmin_Valid_ReturnModel();
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/limitRanges", HttpMethod.GET, null, LimitRangesDefaultList.class)).thenReturn(defaultList);

        getLimitRangesDb_Valid_ReturnModel();

        String type = Constants.LIMIT_RANGE_TYPE_CONTAINER;
        String resourceType = Constants.SUPPORTED_RESOURCE_MEMORY;

        Method getLimitRangesTemplateItem = limitRangesService.getClass().getDeclaredMethod("getLimitRangesTemplateItem", String.class, String.class, String.class, String.class, LimitRangesItem.class, Object.class);
        getLimitRangesTemplateItem.setAccessible(true);

        Method commonSetResourceValue = limitRangesService.getClass().getDeclaredMethod("commonSetResourceValue", String.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, Object.class);
        commonSetResourceValue.setAccessible(true);

        getLimitRangesTemplateItem.invoke(limitRangesService, limitRanges.getName(), limitRanges.getCreationTimestamp(), type, resourceType, LimitRangesModel.getLimitRangesItem(), serversItem);

        when(commonService.setResultObject(LimitRangesModel.getLimitRangesTemplateList(), LimitRangesTemplateList.class)).thenReturn(LimitRangesModel.getLimitRangesTemplateList());
        when(commonService.setResultModel(LimitRangesModel.getLimitRangesTemplateList(), Constants.RESULT_STATUS_SUCCESS)).thenReturn(LimitRangesModel.getFinalLimitRangesTemplateList());

        //call method
        LimitRanges result = (LimitRanges) limitRangesService.getLimitRangesTemplateList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

    }*/

}
