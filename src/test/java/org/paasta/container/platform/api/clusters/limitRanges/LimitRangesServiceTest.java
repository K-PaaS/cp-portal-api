package org.paasta.container.platform.api.clusters.limitRanges;

import com.google.gson.internal.LinkedTreeMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.paasta.container.platform.api.clusters.limitRanges.support.LimitRangesItem;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.*;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class LimitRangesServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";
    private static final String LOW_LIMIT_NAME = "paas-ta-container-platform-low-limit-range";

    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";
    private static final boolean isAdmin = true;
    private static final boolean isNotAdmin = false;
    public static final String CHECK_Y = "Y";
    public static final String CHECK_N = "N";

    private static final String LIMIT_RANGE_NAME = "paas-ta-container-platform-low-limit-range";
    private static final String LIMIT_RANGE_RESOURCE = "cpu";
    private static final String LIMIT_RANGE_TYPE = "Container";
    private static final String LIMIT_RANGE_DEFAULT_LIMIT = "100m";
    private static final String LIMIT_RANGE_DEFAULT_REQUEST = "-";
    private static final String MAX = "-";
    private static final String MIN = "-";
    private static final String CREATION_TIME = "2020-11-17T09:31:37Z";


    private static HashMap gResultMap = null;
    private static HashMap gResultAdminMap = null;
    private static HashMap gResultAdminFailMap = null;

    private static LimitRangesList gResultListModel = null;
    private static LimitRangesList gFinalResultListModel = null;
    private static LimitRangesList gFinalResultListFailModel = null;

    private static LimitRanges gResultModel = null;
    private static LimitRanges gFinalResultModel = null;

    private static LimitRangesListAdmin gResultListAdminModel = null;
    private static LimitRangesListAdmin gFinalResultListAdminModel = null;
    private static LimitRangesListAdmin gFinalResultListAdminFailModel = null;

    private static LimitRangesAdmin gResultAdminModel = null;
    private static LimitRangesAdmin gFinalResultAdminModel = null;
    private static LimitRangesAdmin gFinalResultAdminFailModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static LimitRangesTemplateItem gFinalLimitRangesTemplateItem = null;

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

        gResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultAdminMap = new HashMap();
        gResultListAdminModel = LimitRangesModel.getLimitRangesListAdmin();

        gFinalResultListAdminModel = LimitRangesModel.getLimitRangesListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new LimitRangesListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new LimitRangesAdmin();
        gFinalResultAdminModel = new LimitRangesAdmin();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultAdminFailModel = new LimitRangesAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gFinalLimitRangesTemplateItem = LimitRangesModel.getLimitRangesTemplateItem();

    }

    @Test
    public void getLimitRangesListAdmin_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesListUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);

        when(commonService.setResultObject(gResultAdminMap, LimitRangesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, LimitRangesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        LimitRangesListAdmin resultList = (LimitRangesListAdmin) limitRangesService.getLimitRangesListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getLimitRangesList_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesListUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);

        when(commonService.setResultObject(gResultMap, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        LimitRangesList limitRangesList = limitRangesService.getLimitRangesList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);
        assertEquals(Constants.RESULT_STATUS_SUCCESS, limitRangesList.getResultCode());
    }

    @Test
    public void getLimitRangesListAllNamespacesAdmin_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesListAllNamespacesUrl()).thenReturn("/api/v1/limitranges");

        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/limitranges?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, LimitRangesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, LimitRangesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        LimitRangesListAdmin resultList = (LimitRangesListAdmin) limitRangesService.getLimitRangesListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getLimitRangesAdmin_Type_Container_Valid_ReturnModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LimitRangesAdmin limitRangesAdmin = new LimitRangesAdmin();
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

        limitRangesAdmin.setName(metaData.getName());
        limitRangesAdmin.setCreationTimestamp("2020-11-17T09:31:37Z");
        limitRangesAdmin.setMetadata(metaData);
        limitRangesAdmin.setSpec(spec);

        LimitRangesAdmin finalLimitRangesAdmin = limitRangesAdmin;
        finalLimitRangesAdmin.setResultCode(Constants.RESULT_STATUS_SUCCESS);

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
        when(commonService.setResultObject(map, LimitRangesAdmin.class)).thenReturn(limitRangesAdmin);

        String type = Constants.LIMIT_RANGE_TYPE_CONTAINER;
        String resourceType = Constants.SUPPORTED_RESOURCE_MEMORY;

        Method getLimitRangesTemplateItem = limitRangesService.getClass().getDeclaredMethod("getLimitRangesTemplateItem", String.class, String.class, String.class, String.class, LimitRangesItem.class, Object.class);
        getLimitRangesTemplateItem.setAccessible(true);

        Method commonSetResourceValue = limitRangesService.getClass().getDeclaredMethod("commonSetResourceValue", String.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, Object.class);
        commonSetResourceValue.setAccessible(true);

        getLimitRangesTemplateItem.invoke(limitRangesService, metaData.getName(), metaData.getCreationTimestamp(), type, resourceType, item, serversItem);

        when(commonService.setResultObject(limitRangesAdmin,LimitRangesAdmin.class)).thenReturn(limitRangesAdmin);
        when(commonService.setResultModel(limitRangesAdmin, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalLimitRangesAdmin);

        //call method
        LimitRangesAdmin result = (LimitRangesAdmin) limitRangesService.getLimitRangesAdmin(NAMESPACE, LIMIT_RANGE_NAME);

        //compare result
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }


    @Test
    public void getLimitRangesAdmin_Type_Storage_Valid_ReturnModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LimitRangesAdmin limitRangesAdmin = new LimitRangesAdmin();
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

        limitRangesAdmin.setName(metaData.getName());
        limitRangesAdmin.setCreationTimestamp("2020-11-17T09:31:37Z");
        limitRangesAdmin.setMetadata(metaData);
        limitRangesAdmin.setSpec(spec);

        LimitRangesAdmin finalLimitRangesAdmin = limitRangesAdmin;
        finalLimitRangesAdmin.setResultCode(Constants.RESULT_STATUS_SUCCESS);

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
        when(commonService.setResultObject(map, LimitRangesAdmin.class)).thenReturn(limitRangesAdmin);

        String type = Constants.LIMIT_RANGE_TYPE_PVC;
        String resourceType = Constants.SUPPORTED_RESOURCE_STORAGE;

        Method getLimitRangesTemplateItem = limitRangesService.getClass().getDeclaredMethod("getLimitRangesTemplateItem", String.class, String.class, String.class, String.class, LimitRangesItem.class, Object.class);
        getLimitRangesTemplateItem.setAccessible(true);

        Method commonSetResourceValue = limitRangesService.getClass().getDeclaredMethod("commonSetResourceValue", String.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, LinkedTreeMap.class, Object.class);
        commonSetResourceValue.setAccessible(true);

        getLimitRangesTemplateItem.invoke(limitRangesService, metaData.getName(), metaData.getCreationTimestamp(), type, resourceType, item, serversItem);

        when(commonService.setResultObject(limitRangesAdmin,LimitRangesAdmin.class)).thenReturn(limitRangesAdmin);
        when(commonService.setResultModel(limitRangesAdmin, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalLimitRangesAdmin);

        //call method
        LimitRangesAdmin result = (LimitRangesAdmin) limitRangesService.getLimitRangesAdmin(NAMESPACE, LIMIT_RANGE_NAME);

        //compare result
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getLimitRanges_Yaml_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListLimitRangesGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges/" + LIMIT_RANGE_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) limitRangesService.getLimitRangesAdminYaml(NAMESPACE, LIMIT_RANGE_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createLimitRanges_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListLimitRangesCreateUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_LIMIT_RANGES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) limitRangesService.createLimitRanges(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteLimitRanges_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListLimitRangesDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges/" + LIMIT_RANGE_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_LIMIT_RANGES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.deleteLimitRanges(NAMESPACE, LIMIT_RANGE_NAME);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateLimitRanges_Valid_ReturnModel() {
        String nextUrl = Constants.URI_LIMIT_RANGES_DETAIL.replace("{limitRangeName:.+}", LIMIT_RANGE_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListLimitRangesUpdateUrl()).thenReturn("/api/v1/namespaces/{namespace}/limitranges/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/limitranges/" + LIMIT_RANGE_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.updateLimitRanges(NAMESPACE, LIMIT_RANGE_NAME, YAML_STRING);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void getLimitRangesTemplateList_Valid_ReturnModel() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LimitRangesAdmin limitRangesAdmin = LimitRangesModel.getLimitRangesAdminContainerMemory();

        LimitRangesAdmin finalLimitRangesAdmin = limitRangesAdmin;
        finalLimitRangesAdmin.setResultCode(Constants.RESULT_STATUS_SUCCESS);

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

        getLimitRangesTemplateItem.invoke(limitRangesService, limitRangesAdmin.getName(), limitRangesAdmin.getCreationTimestamp(), type, resourceType, LimitRangesModel.getLimitRangesItem(), serversItem);

        when(commonService.setResultObject(LimitRangesModel.getLimitRangesTemplateList(), LimitRangesTemplateList.class)).thenReturn(LimitRangesModel.getLimitRangesTemplateList());
        when(commonService.setResultModel(LimitRangesModel.getLimitRangesTemplateList(), Constants.RESULT_STATUS_SUCCESS)).thenReturn(LimitRangesModel.getFinalLimitRangesTemplateList());

        //call method
        LimitRangesAdmin result = (LimitRangesAdmin) limitRangesService.getLimitRangesTemplateList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

    }

    @Test
    public void getLimitRangesDb_Valid_ReturnModel() {
        LimitRangesTemplateItem templateItem = limitRangesService.getLimitRangesDb(LimitRangesModel.getLimitRangesDefault(), CHECK_Y);
        assertNotNull(templateItem);
    }
}