package org.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatus;
import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.*;
import org.container.platform.api.overview.support.Status;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class ResourceQuotasServiceTest {

    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String RESOURCE_QUOTA_NAME = "test-resource-quota-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";

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

    private static ResourceQuotasList gResultListModel = null;
    private static ResourceQuotasList gFinalResultListModel = null;
    private static ResourceQuotasList gFinalResultListFailModel = null;

    private static ResourceQuotas gResultModel = null;
    private static List<ResourceQuotasListItem> gResultListArrayModel = null;
    private static ResourceQuotas gFinalResultModel = null;

    private static ResourceQuotasStatus gStatusModel = null;
    private static ResourceQuotasStatus gStatusAdminModel = null;

    private static ResourceQuotasList gResultListAdminModel = null;
    private static ResourceQuotasListItem gResultListAdminItemModel = null;
    private static List<ResourceQuotasListItem> gResultListAdminItemListModel = null;
    private static ResourceQuotasList gFinalResultListAdminModel = null;
    private static ResourceQuotasList gFinalResultListAdminFailModel = null;


    private static ResourceQuotas gResultAdminModel = null;
    private static ResourceQuotas gFinalResultAdminModel = null;
    private static ResourceQuotas gFinalResultAdminFailModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static ResourceQuotasListItem gResultListItem = null;
    private static Params gParams = null;
    private static ResourceQuotasDefaultList gFinalResultDefaultListModel = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    @Spy
    ResourceQuotasService resourceQuotasService;

    @Before
    public void setUp() {
        gResultMap = new HashMap();

        gStatusModel = new ResourceQuotasStatus();

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_RESOURCE_QUOTAS);

        gResultModel = new ResourceQuotas();
        gResultListModel = new ResourceQuotasList();


        // list
        Map<String, String> map = new HashMap<>();
        gStatusModel = new ResourceQuotasStatus();
        gStatusModel.setUsed(map);
        gStatusModel.setHard(map);

        gResultModel.setStatus(gStatusModel);

        gResultListItem = new ResourceQuotasListItem();
        gResultListItem.setStatus(gStatusModel);

        gResultListArrayModel = new ArrayList<>();
        gResultListArrayModel.add(0, gResultListItem);

//        gResultListModel.setItems(gResultListArrayModel);

        List<ResourceQuotasListItem> listItems = new ArrayList<>();
        ResourceQuotasListItem rq = new ResourceQuotasListItem();
        ResourceQuotasStatus status = new ResourceQuotasStatus();
        status.setHard(new HashMap<>());
        status.setUsed(new HashMap<>());
        CommonMetaData commonMetaData = new CommonMetaData();
        commonMetaData.setName("test");
        rq.setStatus(status);
        rq.setName("test");
        rq.setMetadata(commonMetaData);
        listItems.add(rq);

        List<ResourceQuotasDefault> rqDefaultItemList = new ArrayList<>();
        ResourceQuotasDefault rqDefault = new ResourceQuotasDefault();
        rqDefault.setName("test");
        rqDefaultItemList.add(rqDefault);

        gFinalResultDefaultListModel = new ResourceQuotasDefaultList();
        gFinalResultDefaultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultDefaultListModel.setItems(rqDefaultItemList);


        // 리스트가져옴
        gResultListModel.setItems(listItems);
        gResultModel.setStatus(status);

        gFinalResultListModel = new ResourceQuotasList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListModel.setItems(listItems);

        gFinalResultListFailModel = new ResourceQuotasList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gFinalResultModel = new ResourceQuotas();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultFailModel = new ResultStatus();
        gResultFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gResultFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gResultYamlModel = new CommonResourcesYaml(YAML_STRING);
        gFinalResultYamlModel = new CommonResourcesYaml(YAML_STRING);
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultAdminMap = new HashMap();
        gResultListAdminModel = new ResourceQuotasList();
        gFinalResultListAdminModel = new ResourceQuotasList();

        gFinalResultListAdminModel = new ResourceQuotasList();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new ResourceQuotasList();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new ResourceQuotas();
        gFinalResultAdminModel = new ResourceQuotas();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultAdminFailModel = new ResourceQuotas();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gParams = new Params();
        gParams.setNamespace(NAMESPACE);
    }

    @Test
    public void getResourceQuotasList_Valid_ReturnModel() {
        //when
//        when(propertyService.getCpMasterApiListResourceQuotasListUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListResourceQuotasListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceQuotasList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, ResourceQuotasList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        ResourceQuotasList resultList = resourceQuotasService.getResourceQuotasList(gParams);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getResourceQuotas_Valid_ReturnModel() {
        //when
//        when(propertyService.getCpMasterApiListResourceQuotasGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListResourceQuotasGetUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceQuotas.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        ResourceQuotas result = resourceQuotasService.getResourceQuotas(gParams);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

//    @Test
//    public void getResourceQuotas_Valid_ReturnModel() {
//        ResourceQuotas resourceQuotasAdmin = ResourceQuotasModel.getResourceQuotas();
//        ResourceQuotas finalResourceQuotas = resourceQuotasAdmin;
//        finalResourceQuotas.setResultCode(Constants.RESULT_STATUS_SUCCESS);
//        finalResourceQuotas.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
//        finalResourceQuotas.setHttpStatusCode(CommonStatusCode.OK.getCode());
//        finalResourceQuotas.setDetailMessage(CommonStatusCode.OK.getMsg());
//
//        //when
//        when(propertyService.getCpMasterApiListResourceQuotasGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
//        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas/" + RESOURCE_QUOTA_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
//        when(commonService.setResultObject(gResultMap, ResourceQuotas.class)).thenReturn(resourceQuotasAdmin);
//        when(commonService.setResultModel(resourceQuotasAdmin, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalResourceQuotas);
//
//        //call method
//        ResourceQuotas result = (ResourceQuotas) resourceQuotasService.getResourceQuotas(NAMESPACE, RESOURCE_QUOTA_NAME);
//
//        //compare result
//        assertThat(result).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
//    }

    @Test
    public void getResourceQuotas_Yaml_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) resourceQuotasService.getResourceQuotasYaml(gParams);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createResourceQuotas() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasCreateUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasCreateUrl(), HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = resourceQuotasService.createResourceQuotas(gParams);

        //compare result
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void deleteResourceQuotas() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = resourceQuotasService.deleteResourceQuotas(gParams);

        //compare result
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void updateResourceQuotas() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasUpdateUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListResourceQuotasUpdateUrl(), HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = resourceQuotasService.updateResourceQuotas(gParams);

        //compare result
        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

    @Test
    public void getResourceQuotasTemplateList() {
        List<String> stringList = new ArrayList<>();
        stringList.add("test");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, propertyService.getCpMasterApiListResourceQuotasListUrl(), HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceQuotasList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, ResourceQuotasList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/resourceQuotas", HttpMethod.GET, null, ResourceQuotasDefaultList.class, gParams)).thenReturn(gFinalResultDefaultListModel);

        try {
            Object result = resourceQuotasService.getResourceQuotasTemplateList(gParams);
        } catch (Exception e) {
        }
    }

    @Test
    public void getResourceQuotasDefaultTemplateList() {
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/resourceQuotas", HttpMethod.GET, null, ResourceQuotasDefaultList.class, gParams)).thenReturn(gFinalResultDefaultListModel);

        ResourceQuotasDefaultList result = resourceQuotasService.getResourceQuotasDefaultTemplateList(gParams);

        assertEquals(result.getResultCode(), Constants.RESULT_STATUS_SUCCESS);
    }

//    @TestZ
//    public void getRqDefaultList() throws JsonProcessingException {
//        getResourceQuotasList_Valid_ReturnModel();
//        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/resourceQuotas", HttpMethod.GET, null, ResourceQuotasDefaultList.class, gParams)).thenReturn(ResourceQuotasModel.getResourceQuotasDefaultList());
//
//        ResourceQuotasDefaultList defaultList = mock(ResourceQuotasDefaultList.class);
//        when(defaultList.getItems()).thenReturn(ResourceQuotasModel.getUpdateResourceQuotasDefaultList().getItems());
//
//        when(commonService.setResultObject(defaultList, ResourceQuotasDefaultList.class)).thenReturn(ResourceQuotasModel.getUpdateResourceQuotasDefaultList());
//        when(commonService.resourceListProcessing(ResourceQuotasModel.getUpdateResourceQuotasDefaultList(), OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ResourceQuotasDefaultList.class)).thenReturn(ResourceQuotasModel.getUpdateResourceQuotasDefaultList());
//        when(commonService.setResultModel(ResourceQuotasModel.getUpdateResourceQuotasDefaultList(), Constants.RESULT_STATUS_SUCCESS)).thenReturn(ResourceQuotasModel.getFinalResourceQuotasDefaultList());
//
//        ResourceQuotasDefaultList resourceQuotasList = (ResourceQuotasDefaultList) resourceQuotasService.getRqDefaultList(NAMESPACE, 0, 0, "creationTime", "desc", "");
//    }

//    @Test
//    public void getResourceQuotasListAllNamespacesAdmin() {
//        //when
//        when(propertyService.getCpMasterApiListResourceQuotasListAllNamespacesUrl()).thenReturn("/api/v1/resourcequotas");
//
//        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
////        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
//        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/resourcequotas?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultAdminMap);
//        when(commonService.setResultObject(gResultAdminMap, ResourceQuotasList.class)).thenReturn(gResultListAdminModel);
//        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ResourceQuotasList.class)).thenReturn(gResultListAdminModel);
//        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);
//
//        //call method
//        ResourceQuotasList resultList = (ResourceQuotasList) resourceQuotasService.getResourceQuotasListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);
//
//        //compare result
//        assertThat(resultList).isNotNull();
//        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
//    }
}