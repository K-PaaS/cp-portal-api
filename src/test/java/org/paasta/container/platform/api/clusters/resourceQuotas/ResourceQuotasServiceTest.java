package org.paasta.container.platform.api.clusters.resourceQuotas;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.paasta.container.platform.api.clusters.resourceQuotas.support.ResourceQuotasStatus;
import org.paasta.container.platform.api.common.CommonService;
import org.paasta.container.platform.api.common.Constants;
import org.paasta.container.platform.api.common.PropertyService;
import org.paasta.container.platform.api.common.RestTemplateService;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.CommonStatusCode;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private static List<ResourceQuotas> gResultListArrayModel = null;
    private static ResourceQuotas gFinalResultModel = null;

    private static ResourceQuotasStatus gStatusModel = null;
    private static ResourceQuotasStatus gStatusAdminModel = null;

    private static ResourceQuotasListAdmin gResultListAdminModel = null;
    private static ResourceQuotasListAdminItem gResultListAdminItemModel = null;
    private static List<ResourceQuotasListAdminItem> gResultListAdminItemListModel = null;
    private static ResourceQuotasListAdmin gFinalResultListAdminModel = null;
    private static ResourceQuotasListAdmin gFinalResultListAdminFailModel = null;


    private static ResourceQuotasAdmin gResultAdminModel = null;
    private static ResourceQuotasAdmin gFinalResultAdminModel = null;
    private static ResourceQuotasAdmin gFinalResultAdminFailModel = null;

    private static CommonResourcesYaml gResultYamlModel = null;
    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;



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


        gResultListArrayModel = new ArrayList<>();
        gResultListArrayModel.add(0, gResultModel);

        gResultListModel.setItems(gResultListArrayModel);

        // 리스트가져옴
        gFinalResultListModel = new ResourceQuotasList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

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

        gResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel = new CommonResourcesYaml();
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultAdminMap = new HashMap();
        gResultListAdminModel = new ResourceQuotasListAdmin();
        gFinalResultListAdminModel = new ResourceQuotasListAdmin();

        gFinalResultListAdminModel = new ResourceQuotasListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new ResourceQuotasListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new ResourceQuotasAdmin();
        gFinalResultAdminModel = new ResourceQuotasAdmin();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultAdminFailModel = new ResourceQuotasAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
    }

    @Test
    public void getResourceQuotasList_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasListUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceQuotasList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ResourceQuotasList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        ResourceQuotasList resultList = resourceQuotasService.getResourceQuotasList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getResourceQuotasListAdmin_Valid_ReturnModel() {
        ResourceQuotasListAdmin resourceQuotasListAdmin = ResourceQuotasModel.getResourceQuotasListAdmin();
        ResourceQuotasListAdmin finalResourceQuotasListAdmin = resourceQuotasListAdmin;
        finalResourceQuotasListAdmin.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        finalResourceQuotasListAdmin.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        finalResourceQuotasListAdmin.setHttpStatusCode(CommonStatusCode.OK.getCode());
        finalResourceQuotasListAdmin.setDetailMessage(CommonStatusCode.OK.getMsg());

        //when
        when(propertyService.getCpMasterApiListResourceQuotasListUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);

        when(commonService.setResultObject(gResultAdminMap, ResourceQuotasListAdmin.class)).thenReturn(resourceQuotasListAdmin);
        when(commonService.resourceListProcessing(resourceQuotasListAdmin, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ResourceQuotasListAdmin.class)).thenReturn(resourceQuotasListAdmin);
        when(commonService.setResultModel(resourceQuotasListAdmin, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalResourceQuotasListAdmin);

        //call method
        ResourceQuotasListAdmin resultList = (ResourceQuotasListAdmin) resourceQuotasService.getResourceQuotasListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getResourceQuotas_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas/" + RESOURCE_QUOTA_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceQuotas.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        ResourceQuotas result = resourceQuotasService.getResourceQuotas(NAMESPACE, RESOURCE_QUOTA_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getResourceQuotasAdmin_Valid_ReturnModel() {
        ResourceQuotasAdmin resourceQuotasAdmin = ResourceQuotasModel.getResourceQuotasAdmin();
        ResourceQuotasAdmin finalResourceQuotasAdmin = resourceQuotasAdmin;
        finalResourceQuotasAdmin.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        finalResourceQuotasAdmin.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        finalResourceQuotasAdmin.setHttpStatusCode(CommonStatusCode.OK.getCode());
        finalResourceQuotasAdmin.setDetailMessage(CommonStatusCode.OK.getMsg());

        //when
        when(propertyService.getCpMasterApiListResourceQuotasGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas/" + RESOURCE_QUOTA_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, ResourceQuotasAdmin.class)).thenReturn(resourceQuotasAdmin);
        when(commonService.setResultModel(resourceQuotasAdmin, Constants.RESULT_STATUS_SUCCESS)).thenReturn(finalResourceQuotasAdmin);

        //call method
        ResourceQuotasAdmin result = (ResourceQuotasAdmin) resourceQuotasService.getResourceQuotasAdmin(NAMESPACE, RESOURCE_QUOTA_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getResourceQuotas_Yaml_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas/" + RESOURCE_QUOTA_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) resourceQuotasService.getResourceQuotasAdminYaml(NAMESPACE, RESOURCE_QUOTA_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createResourceQuotas() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasCreateUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_RESOURCE_QUOTAS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) resourceQuotasService.createResourceQuotas(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteResourceQuotas() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas/" + RESOURCE_QUOTA_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_RESOURCE_QUOTAS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = resourceQuotasService.deleteResourceQuotas(NAMESPACE, RESOURCE_QUOTA_NAME);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateResourceQuotas() {
        String nextUrl = Constants.URI_RESOURCE_QUOTAS_DETAIL.replace("{resourceQuotaName:.+}", RESOURCE_QUOTA_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListResourceQuotasUpdateUrl()).thenReturn("/api/v1/namespaces/{namespace}/resourcequotas/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/resourcequotas/" + RESOURCE_QUOTA_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = resourceQuotasService.updateResourceQuotas(NAMESPACE, RESOURCE_QUOTA_NAME, YAML_STRING);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void getRqDefaultList() throws JsonProcessingException {
        getResourceQuotasListAdmin_Valid_ReturnModel();
        when(restTemplateService.send(Constants.TARGET_COMMON_API, "/resourceQuotas", HttpMethod.GET, null, ResourceQuotasDefaultList.class)).thenReturn(ResourceQuotasModel.getResourceQuotasDefaultList());

        ResourceQuotasDefaultList defaultList = mock(ResourceQuotasDefaultList.class);
        when(defaultList.getItems()).thenReturn(ResourceQuotasModel.getUpdateResourceQuotasDefaultList().getItems());

        when(commonService.setResultObject(defaultList, ResourceQuotasDefaultList.class)).thenReturn(ResourceQuotasModel.getUpdateResourceQuotasDefaultList());
        when(commonService.resourceListProcessing(ResourceQuotasModel.getUpdateResourceQuotasDefaultList(), OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ResourceQuotasDefaultList.class)).thenReturn(ResourceQuotasModel.getUpdateResourceQuotasDefaultList());
        when(commonService.setResultModel(ResourceQuotasModel.getUpdateResourceQuotasDefaultList(), Constants.RESULT_STATUS_SUCCESS)).thenReturn(ResourceQuotasModel.getFinalResourceQuotasDefaultList());

        ResourceQuotasDefaultList resourceQuotasList = (ResourceQuotasDefaultList) resourceQuotasService.getRqDefaultList(NAMESPACE, 0, 0, "creationTime", "desc", "");
    }

    @Test
    public void getResourceQuotasListAllNamespacesAdmin() {
        //when
        when(propertyService.getCpMasterApiListResourceQuotasListAllNamespacesUrl()).thenReturn("/api/v1/resourcequotas");

        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/resourcequotas?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, ResourceQuotasListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, ResourceQuotasListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        ResourceQuotasListAdmin resultList = (ResourceQuotasListAdmin) resourceQuotasService.getResourceQuotasListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }
}