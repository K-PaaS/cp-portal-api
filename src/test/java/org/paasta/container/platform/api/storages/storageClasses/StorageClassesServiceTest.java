package org.paasta.container.platform.api.storages.storageClasses;

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
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.yml")
public class StorageClassesServiceTest {

    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String STORAGE_CLASS_NAME = "test-storage-class-name";
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

    private static StorageClasses gResultModel = null;
    private static StorageClasses gFinalResultModel = null;

    private static StorageClassesListAdmin gResultListAdminModel = null;
    private static StorageClassesListAdmin gFinalResultListAdminModel = null;
    private static StorageClassesListAdmin gFinalResultListAdminFailModel = null;

    private static StorageClassesAdmin gResultAdminModel = null;
    private static StorageClassesAdmin gFinalResultAdminModel = null;
    private static StorageClassesAdmin gFinalResultAdminFailModel = null;

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
    StorageClassesService storageClassesService;

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_STORAGES_STORAGE_CLASSES);

        // 하나만 가져옴
        gResultModel = new StorageClasses();
        gFinalResultModel = new StorageClasses();
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
        gResultListAdminModel = new StorageClassesListAdmin();
        gFinalResultListAdminModel = new StorageClassesListAdmin();

        gFinalResultListAdminModel = new StorageClassesListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new StorageClassesListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new StorageClassesAdmin();
        gFinalResultAdminModel = new StorageClassesAdmin();
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


        gFinalResultAdminFailModel = new StorageClassesAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
    }

    @Test
    public void getStorageClassesListAdmin_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesListUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);


        when(commonService.setResultObject(gResultAdminMap, StorageClassesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, StorageClassesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        StorageClassesListAdmin resultList = (StorageClassesListAdmin) storageClassesService.getStorageClassesListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getStorageClassesAdmin_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesGetUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/" + STORAGE_CLASS_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, StorageClassesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, StorageClassesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        //call method
        StorageClassesAdmin result = (StorageClassesAdmin) storageClassesService.getStorageClassesAdmin(NAMESPACE, STORAGE_CLASS_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getStorageClasses_Yaml_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesGetUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/{name}" + STORAGE_CLASS_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) storageClassesService.getStorageClassesAdminYaml(STORAGE_CLASS_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createStorageClasses() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesCreateUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_STORAGE_CLASSES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) storageClassesService.createStorageClasses(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteStorageClasses() {

        //when
        when(propertyService.getCpMasterApiListStorageClassesDeleteUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/" + STORAGE_CLASS_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_STORAGE_CLASSES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = storageClassesService.deleteStorageClasses(NAMESPACE, STORAGE_CLASS_NAME);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateStorageClasses() {
        String nextUrl = Constants.URI_STORAGES_STORAGE_CLASSES_DETAIL.replace("{storageClassName:.+}", STORAGE_CLASS_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListStorageClassesUpdateUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/" + STORAGE_CLASS_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) storageClassesService.updateStorageClasses(STORAGE_CLASS_NAME, YAML_STRING);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }
}