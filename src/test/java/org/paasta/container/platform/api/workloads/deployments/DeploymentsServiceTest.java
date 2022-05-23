package org.paasta.container.platform.api.workloads.deployments;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class DeploymentsServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String DEPLOYMENT_NAME = "test-deployment-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String FIELD_SELECTOR = "?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
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

    private static DeploymentsList gResultListModel = null;
    private static DeploymentsList gFinalResultListModel = null;
    private static DeploymentsList gFinalResultListFailModel = null;

    private static Deployments gResultModel = null;
    private static Deployments gFinalResultModel = null;

    private static DeploymentsListAdmin gResultListAdminModel = null;
    private static DeploymentsListAdmin gFinalResultListAdminModel = null;
    private static DeploymentsListAdmin gFinalResultListAdminFailModel = null;

    private static DeploymentsAdmin gResultAdminModel = null;
    private static DeploymentsAdmin gFinalResultAdminModel = null;
    private static DeploymentsAdmin gFinalResultAdminFailModel = null;

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
    DeploymentsService deploymentsService;

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_WORKLOAD_DEPLOYMENTS);

        // 리스트가져옴
        gResultListModel = new DeploymentsList();

        gFinalResultListModel = new DeploymentsList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new DeploymentsList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new Deployments();
        gFinalResultModel = new Deployments();
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
        gResultListAdminModel = new DeploymentsListAdmin();
        gFinalResultListAdminModel = new DeploymentsListAdmin();

        gFinalResultListAdminModel = new DeploymentsListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new DeploymentsListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new DeploymentsAdmin();
        gFinalResultAdminModel = new DeploymentsAdmin();
        gFinalResultAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());


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


        gFinalResultAdminFailModel = new DeploymentsAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

    }

    @Test
    public void getDeploymentList_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListDeploymentsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, DeploymentsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, DeploymentsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        DeploymentsList resultList = deploymentsService.getDeploymentsList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getDeploymentsListAdmin_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListDeploymentsListUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);


        when(commonService.setResultObject(gResultAdminMap, DeploymentsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, DeploymentsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        DeploymentsListAdmin resultList = (DeploymentsListAdmin) deploymentsService.getDeploymentsListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }


    @Test
    public void getDeployments_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListDeploymentsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments/" + DEPLOYMENT_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Deployments.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        Deployments result = deploymentsService.getDeployments(NAMESPACE, DEPLOYMENT_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());

    }

    @Test
    public void getDeploymentsAdmin_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListDeploymentsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments/" + DEPLOYMENT_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, DeploymentsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, DeploymentsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        //call method
        DeploymentsAdmin result = (DeploymentsAdmin) deploymentsService.getDeploymentsAdmin(NAMESPACE, DEPLOYMENT_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());

    }


    @Test
    public void getDeployments_Yaml_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListDeploymentsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments/" + DEPLOYMENT_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) deploymentsService.getDeploymentsYaml(NAMESPACE, DEPLOYMENT_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());

    }

    @Test
    public void getDeploymentsAdmin_Yaml_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListDeploymentsGetUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments/" + DEPLOYMENT_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) deploymentsService.getDeploymentsAdminYaml(NAMESPACE, DEPLOYMENT_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());

    }


    @Test
    public void createDeployments() {
        //when
        when(propertyService.getCpMasterApiListDeploymentsCreateUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_DEPLOYMENTS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) deploymentsService.createDeployments(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }


    @Test
    public void deleteDeployments_Admin_Valid() {
        //when
        when(propertyService.getCpMasterApiListDeploymentsDeleteUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments/" + DEPLOYMENT_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_DEPLOYMENTS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = deploymentsService.deleteDeployments(NAMESPACE, DEPLOYMENT_NAME, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }


    @Test
    public void deleteDeployments_Not_Admin_Valid() {
        //when
        when(propertyService.getCpMasterApiListDeploymentsDeleteUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments/" + DEPLOYMENT_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_DEPLOYMENTS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = deploymentsService.deleteDeployments(NAMESPACE, DEPLOYMENT_NAME, isNotAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }


    @Test
    public void updateDeployments() {
        String nextUrl = Constants.URI_WORKLOAD_DEPLOYMENTS_DETAIL.replace("{deploymentName:.+}", DEPLOYMENT_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListDeploymentsUpdateUrl()).thenReturn("/apis/apps/v1/namespaces/{namespace}/deployments/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/namespaces/" + NAMESPACE + "/deployments/" + DEPLOYMENT_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = deploymentsService.updateDeployments(NAMESPACE, DEPLOYMENT_NAME, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }


    @Test
    public void getDeploymentsListAllNamespacesAdmin() {
        //when
        when(propertyService.getCpMasterApiListDeploymentsListAllNamespacesUrl()).thenReturn("/apis/apps/v1/deployments");

        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/apps/v1/deployments?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, DeploymentsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, DeploymentsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        DeploymentsListAdmin resultList = (DeploymentsListAdmin) deploymentsService.getDeploymentsListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }
}
