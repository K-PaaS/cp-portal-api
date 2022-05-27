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
    private static final String KUBE_ANNOTATIONS = "kubectl.kubernetes.io/";
    private static final int OFFSET = 0;
    private static final int LIMIT = 0;
    private static final String ORDER_BY = "creationTime";
    private static final String ORDER = "desc";
    private static final String SEARCH_NAME = "";

    private static HashMap gResultMap = null;

    private static StorageClasses gResultModel = null;
    private static StorageClasses gFinalResultModel = null;
    private static StorageClassesList gResultListModel = null;
    private static StorageClassesList gFinalResultListModel = null;

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

        gFinalResultYamlModel = new CommonResourcesYaml("");
        gFinalResultYamlModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultYamlModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultYamlModel.setDetailMessage(CommonStatusCode.OK.getMsg());
        gFinalResultYamlModel.setSourceTypeYaml(YAML_STRING);

        // 리스트가져옴
        gResultListModel = new StorageClassesList();
        gFinalResultListModel = new StorageClassesList();

        gFinalResultListModel = new StorageClassesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListModel.setDetailMessage(CommonStatusCode.OK.getMsg());
//
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
        gParams.setResourceName(STORAGE_CLASS_NAME);
        gParams.setYaml(YAML_STRING);
    }

    @Test
    public void getStorageClassesList_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesListUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);


        when(commonService.setResultObject(gResultMap, StorageClassesList.class)).thenReturn(gResultListModel);
        when(commonService.resourceListProcessing(gResultListModel, gParams, StorageClassesList.class)).thenReturn(gResultListModel);
        when(commonService.setResultModel(gResultListModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListModel);

        //call method
        StorageClassesList resultList = storageClassesService.getStorageClassesList(gParams);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getStorageClasses_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesGetUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/{name}", HttpMethod.GET, null, Map.class, gParams)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, StorageClasses.class)).thenReturn(gResultModel);
        when(commonService.annotationsProcessing(gResultModel, StorageClasses.class)).thenReturn(gResultModel);
        when(commonService.setResultModel(gResultModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultModel);

        //call method
        StorageClasses result = storageClassesService.getStorageClasses(gParams);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void getStorageClasses_Yaml_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesGetUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/{name}", HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, gParams)).thenReturn(YAML_STRING);
        when(commonService.setResultModel(new CommonResourcesYaml(YAML_STRING), Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = storageClassesService.getStorageClassesYaml(gParams);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createStorageClasses() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesCreateUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses", HttpMethod.POST, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = storageClassesService.createStorageClasses(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deleteStorageClasses() {

        //when
        when(propertyService.getCpMasterApiListStorageClassesDeleteUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/{name}", HttpMethod.DELETE, null, ResultStatus.class, gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = storageClassesService.deleteStorageClasses(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void updateStorageClasses() {
        //when
        when(propertyService.getCpMasterApiListStorageClassesUpdateUrl()).thenReturn("/apis/storage.k8s.io/v1/storageclasses/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/apis/storage.k8s.io/v1/storageclasses/{name}", HttpMethod.PUT, ResultStatus.class,gParams)).thenReturn(gResultStatusModel);
        when(commonService.setResultModel(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = storageClassesService.updateStorageClasses(gParams);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }
}