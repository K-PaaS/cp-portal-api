package org.container.platform.api.clusters.limitRanges;

import com.google.gson.internal.LinkedTreeMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.clusters.limitRanges.support.LimitRangesItem;
import org.container.platform.api.common.*;
import org.container.platform.api.common.model.*;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class LimitRangesServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String LIMIT_RANGE_NAME = "test-limit-range-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String LOW_LIMIT_NAME = "container-platform-low-limit-range";
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

    private static LimitRangesTemplateList gResultTemplateList = null;
    private static LimitRangesTemplateList gFinalResultTemplateList = null;

    private static Params gParams = null;
    private static CommonSpec gCommonSpec = null;

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


        gResultTemplateList = new LimitRangesTemplateList();

        gFinalResultTemplateList = new LimitRangesTemplateList();
        gFinalResultTemplateList.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        // 리스트가져옴
        gResultListModel = new LimitRangesList();

        gFinalResultListModel = new LimitRangesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListModel.setItems(new ArrayList<>());

        gFinalResultListFailModel = new LimitRangesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new LimitRanges();

        gCommonSpec = new CommonSpec();
        gCommonSpec.setLimits(new ArrayList<>());
        gResultModel.setSpec(gCommonSpec);



        gFinalResultModel = new LimitRanges();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultModel.setItems(new ArrayList<>());

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

    @Test
    public void getLimitRangesList() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);
        LimitRangesList result = limitRangesService.getLimitRangesList(gParams);
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getLimitRangesListAllNamespaces() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesListAllNamespacesUrl()
//                        + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
                , HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        LimitRangesList result = limitRangesService.getLimitRangesListAllNamespaces(gParams);
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getLimitRanges() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);

        when(commonService.setResultObject(gResultMap, LimitRanges.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(commonService.setResultObject(gResultModel, LimitRanges.class), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        List<LimitRangesItem> limits = gResultModel.getSpec().getLimits();
        List<LimitRangesItem> serversItemList = new ArrayList<>();

        for (LimitRangesItem item:limits) {
            List<String> typeList = Constants.LIMIT_RANGE_TYPE_LIST;

            for (String type : typeList) {
                if(type.equals(item.getType())) {
                    if(Constants.LIMIT_RANGE_TYPE_CONTAINER.equals(type) || Constants.LIMIT_RANGE_TYPE_POD.equals(type)) {
                        for (String resourceType : Constants.SUPPORTED_RESOURCE_LIST) {
                            LimitRangesItem serversItem = new LimitRangesItem();
//                            serversItem = (LimitRangesItem) getLimitRangesTemplateItem(limitRanges.getName(), limitRanges.getCreationTimestamp(), type, resourceType, item, serversItem);

                            if(!serversItem.getDefaultLimit().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getDefaultRequest().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMax().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMin().equals(Constants.NULL_REPLACE_TEXT)) {
                                serversItemList.add(serversItem);
                            }
                        }
                    } else {
                        String resourceType = Constants.SUPPORTED_RESOURCE_STORAGE;
                        LimitRangesItem serversItem = new LimitRangesItem();
//                        serversItem = (LimitRangesItem) getLimitRangesTemplateItem(limitRanges.getName(), limitRanges.getCreationTimestamp(), type, resourceType, item, serversItem);

                        if(!serversItem.getDefaultLimit().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getDefaultRequest().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMax().equals(Constants.NULL_REPLACE_TEXT) || !serversItem.getMin().equals(Constants.NULL_REPLACE_TEXT)) {
                            serversItemList.add(serversItem);
                        }
                    }
                }
            }
        }

        LimitRanges result = limitRangesService.getLimitRanges(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getLimitRanges_items() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);

        CommonSpec commonSpec = new CommonSpec();
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("test");
        List<LimitRangesItem> limits = new ArrayList<>();
        LimitRangesItem limit = new LimitRangesItem();
        limit.setType(Constants.LIMIT_RANGE_TYPE_CONTAINER);
        limits.add(limit);
        commonSpec.setLimits(limits);
        gResultModel.setSpec(commonSpec);
        gResultModel.setMetadata(commonMetaData);

        when(commonService.setResultObject(gResultMap, LimitRanges.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(commonService.setResultObject(gResultModel, LimitRanges.class), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        LimitRanges result = limitRangesService.getLimitRanges(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getLimitRanges_items2() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);

        CommonSpec commonSpec = new CommonSpec();
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("test");
        List<LimitRangesItem> limits = new ArrayList<>();
        LimitRangesItem limit = new LimitRangesItem();
        limit.setType("te");
        limits.add(limit);
        commonSpec.setLimits(limits);
        gResultModel.setSpec(commonSpec);
        gResultModel.setMetadata(commonMetaData);

        when(commonService.setResultObject(gResultMap, LimitRanges.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(commonService.setResultObject(gResultModel, LimitRanges.class), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        LimitRanges result = limitRangesService.getLimitRanges(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }


    @Test
    public void getLimitRangesYaml() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);

        when(commonService.setResultModel(commonService.setResultObject(gResultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS)).thenReturn(YAML_STRING);

        Object result = limitRangesService.getLimitRangesYaml(gParams, gResultMap);

        assertEquals(result.toString(), YAML_STRING);
    }

    @Test
    public void createLimitRanges() {
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.createLimitRanges(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void deleteLimitRanges() {
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.deleteLimitRanges(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void updateLimitRanges() {
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesUpdateUrl(), HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = limitRangesService.updateLimitRanges(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getLimitRangesDefaultTemplateList() {
        LimitRangesDefaultList gFinalDefaultList = new LimitRangesDefaultList();
        gFinalDefaultList.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/limitRanges", HttpMethod.GET, null, LimitRangesDefaultList.class, gParams)).thenReturn(gFinalDefaultList);

        LimitRangesDefaultList result = limitRangesService.getLimitRangesDefaultTemplateList(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getLimitRangesTemplateList() {

        List<LimitRangesListItem> list = new ArrayList<>();
        LimitRangesListItem item = new LimitRangesListItem();
        CommonMetaData metaData = new CommonMetaData();
        metaData.setName("test");
        item.setMetadata(metaData);
        CommonSpec commonSpec = new CommonSpec();
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("test");
        List<LimitRangesItem> limits = new ArrayList<>();
        LimitRangesItem limit = new LimitRangesItem();
        limit.setType(Constants.LIMIT_RANGE_TYPE_CONTAINER);
        limits.add(limit);
        commonSpec.setLimits(limits);
        item.setSpec(commonSpec);
        list.add(item);
        gResultListModel.setItems(list);
        gFinalResultListModel.setItems(list);





        LimitRangesDefaultList gFinalDefaultList = new LimitRangesDefaultList();
        gFinalDefaultList.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalDefaultList.setItems(new ArrayList<>());
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/limitRanges", HttpMethod.GET, null, LimitRangesDefaultList.class, gParams)).thenReturn(gFinalDefaultList);
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListLimitRangesListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, LimitRangesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        when(commonService.setResultObject(gResultTemplateList, LimitRangesTemplateList.class)).thenReturn(gResultTemplateList);
        when(commonService.resourceListProcessing(gResultTemplateList, gParams, LimitRangesTemplateList.class)).thenReturn(gResultTemplateList);

        when(commonService.setResultModel(gResultTemplateList, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultTemplateList);

        Object result = limitRangesService.getLimitRangesTemplateList(gParams);
        assertNull(result);
    }

    @Test
    public void getLimitRangesDb() {
        LimitRangesTemplateItem item = new LimitRangesTemplateItem();
        CommonMetaData metadata = new CommonMetaData();
        LimitRangesDefault limitRangesDefault = new LimitRangesDefault();

        item.setName(limitRangesDefault.getName());
        item.setType(limitRangesDefault.getType());
        item.setResource(limitRangesDefault.getResource());
        item.setMin(limitRangesDefault.getMin());
        item.setMax(limitRangesDefault.getMax());
        item.setDefaultRequest(limitRangesDefault.getDefaultRequest());
        item.setDefaultLimit(limitRangesDefault.getDefaultLimit());
        item.setCheckYn("y");
        metadata.setCreationTimestamp(limitRangesDefault.getCreationTimestamp());
        item.setMetadata(metadata);
        item.setCreationTimestamp(limitRangesDefault.getCreationTimestamp());
    }

    @Test
    public void getLimitRangesTemplateItem() {
        LinkedTreeMap<String, String> defaultLimit = null;
        LinkedTreeMap<String, String> defaultRequest = null;
        LinkedTreeMap<String, String> max = null;
        LinkedTreeMap<String, String> min = null;
        LimitRangesItem item = new LimitRangesItem();
        Object object = new LimitRangesTemplateItem();
        String name = "";
        String creationTimestamp = "";
        String type = "";
        String resourceType = "";

        Object result = limitRangesService.getLimitRangesTemplateItem(name, creationTimestamp, type, resourceType, item, object);
        assertNotNull(result);
    }

    @Test
    public void commonSetResourceValue() {
        String resourceType = "key";
        LinkedTreeMap<String, String> defaultLimit = new LinkedTreeMap<>();
        defaultLimit.put("key", "test");
        LinkedTreeMap<String, String> defaultRequest = new LinkedTreeMap<>();
        defaultRequest.put("key", "test");
        LinkedTreeMap<String, String> max = new LinkedTreeMap<>();
        max.put("key", "test");
        LinkedTreeMap<String, String> min = new LinkedTreeMap<>();
        min.put("key", "test");
        Object item = new LimitRangesTemplateItem();

        Object result = limitRangesService.commonSetResourceValue(resourceType, defaultLimit, defaultRequest, max, min, item);

        assertNotNull(result);
    }

    @Test
    public void commonSetResourceValue_2() {
        String resourceType = "key";
        LinkedTreeMap<String, String> defaultLimit = new LinkedTreeMap<>();
        defaultLimit.put("key", "test");
        LinkedTreeMap<String, String> defaultRequest = new LinkedTreeMap<>();
        defaultRequest.put("key", "test");
        LinkedTreeMap<String, String> max = new LinkedTreeMap<>();
        max.put("key", "test");
        LinkedTreeMap<String, String> min = new LinkedTreeMap<>();
        min.put("key", "test");
        Object item = new LimitRangesItem();

        Object result = limitRangesService.commonSetResourceValue(resourceType, defaultLimit, defaultRequest, max, min, item);

        assertNotNull(result);
    }

    @Test
    public void commonSetResourceValue_defaultLimit_null() {
        String resourceType = "";
        LinkedTreeMap<String, String> defaultLimit = new LinkedTreeMap<>();
        LinkedTreeMap<String, String> defaultRequest = new LinkedTreeMap<>();
        LinkedTreeMap<String, String> max = new LinkedTreeMap<>();
        LinkedTreeMap<String, String> min = new LinkedTreeMap<>();
        Object item = new LimitRangesTemplateItem();

        Object result = limitRangesService.commonSetResourceValue(resourceType, defaultLimit, defaultRequest, max, min, item);

        assertNotNull(result);
    }




}
