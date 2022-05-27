package org.paasta.container.platform.api.customServices.ingresses;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class IngressesServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String INGRESS_NAME = "test-ingress-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";

    private static HashMap gResultMap = null;

    private static IngressesList gResultListModel = null;
    private static IngressesList gFinalResultListModel = null;
    private static IngressesList gFinalResultListFailModel = null;

    private static Ingresses gResultModel = null;
    private static Ingresses gFinalResultModel = null;

    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gResultFailModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static Params gParams = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    IngressesService ingressesService;

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_INGRESSES);

        // 리스트가져옴
        gResultListModel = new IngressesList();

        gFinalResultListModel = new IngressesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new IngressesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new Ingresses();
        gFinalResultModel = new Ingresses();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gResultFailModel = new ResultStatus();
        gResultFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gResultFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gResultFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());

        gFinalResultYamlModel = new CommonResourcesYaml("");
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

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
        gParams.setResourceName(INGRESS_NAME);
        gParams.setYaml(YAML_STRING);
    }

    @Test
    public void getIngressesList_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListIngressesListUrl()).thenReturn("/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, IngressesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, IngressesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        IngressesList resultList = ingressesService.getIngressesList(gParams);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }


    @Test
    public void getIngresses_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListIngressesGetUrl()).thenReturn("/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, Ingresses.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, Ingresses.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        Ingresses result = ingressesService.getIngresses(gParams);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getIngresses_Yaml_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListIngressesGetUrl()).thenReturn("/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}", HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = ingressesService.getIngressesYaml(gParams);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createIngresses() {
        //when
        when(propertyService.getCpMasterApiListIngressesCreateUrl()).thenReturn("/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/", HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = ingressesService.createIngresses(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteServices_Valid() {
        //when
        when(propertyService.getCpMasterApiListIngressesDeleteUrl()).thenReturn("/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}", HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = ingressesService.deleteIngresses(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateServices() {
        //when
        when(propertyService.getCpMasterApiListIngressesUpdateUrl()).thenReturn("/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/networking.k8s.io/v1/namespaces/{namespace}/ingresses/{name}", HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = ingressesService.updateIngresses(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }
}
