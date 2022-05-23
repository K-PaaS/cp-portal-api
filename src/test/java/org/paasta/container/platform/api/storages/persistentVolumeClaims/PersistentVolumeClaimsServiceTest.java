package org.paasta.container.platform.api.storages.persistentVolumeClaims;

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
public class PersistentVolumeClaimsServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String PERSISTENT_VOLUME_CLAIM_NAME = "test-persistent-volume-claim-name";
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

    private static PersistentVolumeClaimsList gResultListModel = null;
    private static PersistentVolumeClaimsList gFinalResultListModel = null;
    private static PersistentVolumeClaimsList gFinalResultListFailModel = null;

    private static PersistentVolumeClaims gResultModel = null;
    private static PersistentVolumeClaims gFinalResultModel = null;

    private static PersistentVolumeClaimsListAdmin gResultListAdminModel = null;
    private static PersistentVolumeClaimsListAdmin gFinalResultListAdminModel = null;
    private static PersistentVolumeClaimsListAdmin gFinalResultListAdminFailModel = null;

    private static PersistentVolumeClaimsAdmin gResultAdminModel = null;
    private static PersistentVolumeClaimsAdmin gFinalResultAdminModel = null;
    private static PersistentVolumeClaimsAdmin gFinalResultAdminFailModel = null;

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
        gResultListAdminModel = new PersistentVolumeClaimsListAdmin();
        gFinalResultListAdminModel = new PersistentVolumeClaimsListAdmin();

        gFinalResultListAdminModel = new PersistentVolumeClaimsListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new PersistentVolumeClaimsListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new PersistentVolumeClaimsAdmin();
        gFinalResultAdminModel = new PersistentVolumeClaimsAdmin();
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

        gFinalResultAdminFailModel = new PersistentVolumeClaimsAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
    }

    @Test
    public void getPersistentVolumeClaimsList_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims", HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PersistentVolumeClaimsList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PersistentVolumeClaimsList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        PersistentVolumeClaimsList resultList = persistentVolumeClaimsService.getPersistentVolumeClaimsList(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getPersistentVolumeClaimsListAdmin_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsListUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);


        when(commonService.setResultObject(gResultAdminMap, PersistentVolumeClaimsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PersistentVolumeClaimsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        PersistentVolumeClaimsListAdmin resultList = (PersistentVolumeClaimsListAdmin) persistentVolumeClaimsService.getPersistentVolumeClaimsListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getPersistentVolumeClaims_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims/" + PERSISTENT_VOLUME_CLAIM_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PersistentVolumeClaims.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        PersistentVolumeClaims result = persistentVolumeClaimsService.getPersistentVolumeClaims(NAMESPACE, PERSISTENT_VOLUME_CLAIM_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getPersistentVolumeClaimsAdmin_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims/" + PERSISTENT_VOLUME_CLAIM_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PersistentVolumeClaimsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, PersistentVolumeClaimsAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        //call method
        PersistentVolumeClaimsAdmin result = (PersistentVolumeClaimsAdmin) persistentVolumeClaimsService.getPersistentVolumeClaimsAdmin(NAMESPACE, PERSISTENT_VOLUME_CLAIM_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getPersistentVolumeClaims_Yaml_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsGetUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims/" + PERSISTENT_VOLUME_CLAIM_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) persistentVolumeClaimsService.getPersistentVolumeClaimsYaml(NAMESPACE, PERSISTENT_VOLUME_CLAIM_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createPersistentVolumeClaims() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsCreateUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) persistentVolumeClaimsService.createPersistentVolumeClaims(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deletePersistentVolumeClaims_Admin_Valid() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims/" + PERSISTENT_VOLUME_CLAIM_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = persistentVolumeClaimsService.deletePersistentVolumeClaims(NAMESPACE, PERSISTENT_VOLUME_CLAIM_NAME, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deletePersistentVolumeClaims_Not_Admin_Valid() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsDeleteUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims/" + PERSISTENT_VOLUME_CLAIM_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = persistentVolumeClaimsService.deletePersistentVolumeClaims(NAMESPACE, PERSISTENT_VOLUME_CLAIM_NAME, isNotAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updatePersistentVolumeClaims() {
        String nextUrl = Constants.URI_STORAGES_DETAIL.replace("{persistentVolumeClaimName:.+}", PERSISTENT_VOLUME_CLAIM_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsUpdateUrl()).thenReturn("/api/v1/namespaces/{namespace}/persistentvolumeclaims/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/namespaces/" + NAMESPACE + "/persistentvolumeclaims/" + PERSISTENT_VOLUME_CLAIM_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) persistentVolumeClaimsService.updatePersistentVolumeClaims(NAMESPACE, PERSISTENT_VOLUME_CLAIM_NAME, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void getPersistentVolumeClaimsListAllNamespacesAdmin() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumeClaimsListAllNamespacesUrl()).thenReturn("/api/v1/persistentvolumeclaims");

        // ?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace
        when(commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)).thenReturn(FIELD_SELECTOR);
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/persistentvolumeclaims?fieldSelector=metadata.namespace!=kubernetes-dashboard,metadata.namespace!=kube-node-lease,metadata.namespace!=kube-public,metadata.namespace!=kube-system,metadata.namespace!=temp-namespace", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);
        when(commonService.setResultObject(gResultAdminMap, PersistentVolumeClaimsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PersistentVolumeClaimsListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        PersistentVolumeClaimsListAdmin resultList = (PersistentVolumeClaimsListAdmin) persistentVolumeClaimsService.getPersistentVolumeClaimsListAllNamespacesAdmin(OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }
}