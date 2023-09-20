package org.container.platform.api.storages.persistentVolumeClaims;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.*;
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
public class PersistentVolumeClaimsServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String PERSISTENT_VOLUME_CLAIM_NAME = "test-persistent-volume-claim-name";
    private static final String YAML_STRING = "test-yaml-string";
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";

    private static HashMap gResultMap = null;

    private static PersistentVolumeClaimsList gResultListModel = null;
    private static PersistentVolumeClaimsList gFinalResultListModel = null;
    private static PersistentVolumeClaimsList gFinalResultListFailModel = null;

    private static PersistentVolumeClaims gResultModel = null;
    private static PersistentVolumeClaims gFinalResultModel = null;

    private static CommonResourcesYaml gFinalResultYamlModel = null;

    private static ResultStatus gResultStatusModel = null;
    private static ResultStatus gFinalResultStatusModel = null;

    private static Params gParams = null;

    @Mock
    RestTemplateService restTemplateService;

    @Mock
    CommonService commonService;

    @Mock
    PropertyService propertyService;

    @InjectMocks
    PersistentVolumeClaimsService persistentVolumeClaimsService;

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_STORAGES);

        // 리스트가져옴
        gResultListModel = new PersistentVolumeClaimsList();

        gFinalResultListModel = new PersistentVolumeClaimsList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new PersistentVolumeClaimsList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new PersistentVolumeClaims();
        gFinalResultModel = new PersistentVolumeClaims();
        gFinalResultModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

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

        gParams = new Params();

        //Params setting
        gParams.setCluster(CLUSTER);
        gParams.setNamespace(NAMESPACE);
        gParams.setOffset(OFFSET);
        gParams.setLimit(LIMIT);
        gParams.setOrderBy(ORDER_BY);
        gParams.setOrder(ORDER);
        gParams.setSearchName(SEARCH_NAME);
        gParams.setResourceName(PERSISTENT_VOLUME_CLAIM_NAME);
        gParams.setYaml(YAML_STRING);
    }

    @Test
    public void getPersistentVolumeClaimsList_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/persistentvolumeclaims", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PersistentVolumeClaimsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, PersistentVolumeClaimsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        PersistentVolumeClaimsList resultList = persistentVolumeClaimsService.getPersistentVolumeClaimsList(gParams);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }


    @Test
    public void getPersistentVolumeClaims_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PersistentVolumeClaims.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, PersistentVolumeClaims.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        PersistentVolumeClaims result = persistentVolumeClaimsService.getPersistentVolumeClaims(gParams);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }


    @Test
    public void getPersistentVolumeClaims_Yaml_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}", HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = persistentVolumeClaimsService.getPersistentVolumeClaimsYaml(gParams);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createPersistentVolumeClaims() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsCreateUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/persistentvolumeclaims", HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = persistentVolumeClaimsService.createPersistentVolumeClaims(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deletePersistentVolumeClaims_Valid() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}", HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = persistentVolumeClaimsService.deletePersistentVolumeClaims(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updatePersistentVolumeClaims() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsUpdateUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}", HttpMethod.PUT, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = persistentVolumeClaimsService.updatePersistentVolumeClaims(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }
}