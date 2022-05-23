package org.paasta.container.platform.api.storages.persistentVolumes;

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
public class PersistentVolumesServiceTest {
    private static final String CLUSTER = "cp-cluster";
    private static final String NAMESPACE = "cp-namespace";
    private static final String PERSISTENT_VOLUME_NAME = "test-persistent-volume-name";
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

    private static PersistentVolumesList gResultListModel = null;
    private static PersistentVolumesList gFinalResultListModel = null;
    private static PersistentVolumesList gFinalResultListFailModel = null;

    private static PersistentVolumes gResultModel = null;
    private static PersistentVolumes gFinalResultModel = null;

    private static PersistentVolumesListAdmin gResultListAdminModel = null;
    private static PersistentVolumesListAdmin gFinalResultListAdminModel = null;
    private static PersistentVolumesListAdmin gFinalResultListAdminFailModel = null;

    private static PersistentVolumesAdmin gResultAdminModel = null;
    private static PersistentVolumesAdmin gFinalResultAdminModel = null;
    private static PersistentVolumesAdmin gFinalResultAdminFailModel = null;

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
    PersistentVolumesService persistentVolumesService;

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
        gFinalResultStatusModel.setNextActionUrl(Constants.URI_STORAGES_PERSISTENT_VOLUMES);

        // 리스트가져옴
        gResultListModel = new PersistentVolumesList();

        gFinalResultListModel = new PersistentVolumesList();
        gFinalResultListModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);

        gFinalResultListFailModel = new PersistentVolumesList();
        gFinalResultListFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);

        // 하나만 가져옴
        gResultModel = new PersistentVolumes();
        gFinalResultModel = new PersistentVolumes();
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
        gResultListAdminModel = new PersistentVolumesListAdmin();
        gFinalResultListAdminModel = new PersistentVolumesListAdmin();

        gFinalResultListAdminModel = new PersistentVolumesListAdmin();
        gFinalResultListAdminModel.setResultCode(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setResultMessage(Constants.RESULT_STATUS_SUCCESS);
        gFinalResultListAdminModel.setHttpStatusCode(CommonStatusCode.OK.getCode());
        gFinalResultListAdminModel.setDetailMessage(CommonStatusCode.OK.getMsg());

        gFinalResultListAdminFailModel = new PersistentVolumesListAdmin();
        gFinalResultListAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultListAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultListAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());


        // 하나만 가져옴
        gResultAdminModel = new PersistentVolumesAdmin();
        gFinalResultAdminModel = new PersistentVolumesAdmin();
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

        gFinalResultAdminFailModel = new PersistentVolumesAdmin();
        gFinalResultAdminFailModel.setResultCode(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setResultMessage(Constants.RESULT_STATUS_FAIL);
        gFinalResultAdminFailModel.setHttpStatusCode(CommonStatusCode.NOT_FOUND.getCode());
        gFinalResultAdminFailModel.setDetailMessage(CommonStatusCode.NOT_FOUND.getMsg());
    }

    @Test
    public void getPersistentVolumesListAdmin_Valid_ReturnModel() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumesListUrl()).thenReturn("/api/v1/persistentvolumes");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/persistentvolumes", HttpMethod.GET, null, Map.class)).thenReturn(gResultAdminMap);


        when(commonService.setResultObject(gResultAdminMap, PersistentVolumesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.resourceListProcessing(gResultListAdminModel, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME, PersistentVolumesListAdmin.class)).thenReturn(gResultListAdminModel);
        when(commonService.setResultModel(gResultListAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultListAdminModel);

        //call method
        PersistentVolumesListAdmin resultList = (PersistentVolumesListAdmin) persistentVolumesService.getPersistentVolumesListAdmin(NAMESPACE, OFFSET, LIMIT, ORDER_BY, ORDER, SEARCH_NAME);

        //compare result
        assertThat(resultList).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, resultList.getResultCode());
    }

    @Test
    public void getPersistentVolumes_Yaml_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumesGetUrl()).thenReturn("/api/v1/persistentvolumes/{name}");
        when(restTemplateService.send(Constants.TARGET_CP_MASTER_API, "/api/v1/persistentvolumes/" + PERSISTENT_VOLUME_NAME, HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML)).thenReturn(YAML_STRING);
        when(commonService.setResultObject(gResultMap, CommonResourcesYaml.class)).thenReturn(gResultYamlModel);
        when(commonService.setResultModel(gResultYamlModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultYamlModel);

        //call method
        CommonResourcesYaml result = (CommonResourcesYaml) persistentVolumesService.getPersistentVolumesAdminYaml(PERSISTENT_VOLUME_NAME, gResultMap);

        //compare result
        assertEquals(YAML_STRING, result.getSourceTypeYaml());
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());
    }

    @Test
    public void createPersistentVolumes() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumesCreateUrl()).thenReturn("/api/v1/persistentvolumes");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/persistentvolumes", HttpMethod.POST, YAML_STRING, Object.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_PERSISTENT_VOLUMES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = (ResultStatus) persistentVolumesService.createPersistentVolumes(NAMESPACE, YAML_STRING, isAdmin);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void deletePersistentVolumes() {
        //when
        when(propertyService.getCpMasterApiListPersistentVolumesDeleteUrl()).thenReturn("/api/v1/persistentvolumes/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/persistentvolumes/" + PERSISTENT_VOLUME_NAME, HttpMethod.DELETE, null, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_PERSISTENT_VOLUMES)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = persistentVolumesService.deletePersistentVolumes(NAMESPACE, PERSISTENT_VOLUME_NAME);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }


    @Test
    public void updatePersistentVolumes() {
        String nextUrl = Constants.URI_STORAGES_PERSISTENT_VOLUMES_DETAIL.replace("{persistentVolumeName:.+}", PERSISTENT_VOLUME_NAME);
        gFinalResultStatusModel.setNextActionUrl(nextUrl);

        //when
        when(propertyService.getCpMasterApiListPersistentVolumesUpdateUrl()).thenReturn("/api/v1/persistentvolumes/{name}");
        when(restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API, "/api/v1/persistentvolumes/" + PERSISTENT_VOLUME_NAME, HttpMethod.PUT, YAML_STRING, ResultStatus.class, isAdmin)).thenReturn(gResultStatusModel);
        when(commonService.setResultObject(gResultStatusModel, ResultStatus.class)).thenReturn(gResultStatusModel);
        when(commonService.setResultModelWithNextUrl(gResultStatusModel, Constants.RESULT_STATUS_SUCCESS, nextUrl)).thenReturn(gFinalResultStatusModel);

        ResultStatus result = persistentVolumesService.updatePersistentVolumes(PERSISTENT_VOLUME_NAME, YAML_STRING);

        //compare result
        assertEquals(gFinalResultStatusModel, result);
    }

    @Test
    public void getPersistentVolumesAdmin_Valid_ReturnModel() {

        //when
        when(propertyService.getCpMasterApiListPersistentVolumesGetUrl()).thenReturn("/api/v1/persistentvolumes/{name}");
        when(restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API, "/api/v1/persistentvolumes/" + PERSISTENT_VOLUME_NAME, HttpMethod.GET, null, Map.class)).thenReturn(gResultMap);
        when(commonService.setResultObject(gResultMap, PersistentVolumesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.annotationsProcessing(gResultAdminModel, PersistentVolumesAdmin.class)).thenReturn(gResultAdminModel);
        when(commonService.setResultModel(gResultAdminModel, Constants.RESULT_STATUS_SUCCESS)).thenReturn(gFinalResultAdminModel);

        //call method
        PersistentVolumesAdmin result = (PersistentVolumesAdmin) persistentVolumesService.getPersistentVolumesAdmin(NAMESPACE, PERSISTENT_VOLUME_NAME);

        //compare result
        assertThat(result).isNotNull();
        assertEquals(Constants.RESULT_STATUS_SUCCESS, result.getResultCode());

    }

}