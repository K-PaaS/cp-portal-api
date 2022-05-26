package org.paasta.container.platform.api.storages.storageClasses;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.Params;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * StorageClasses Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Service
public class StorageClassesService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new StorageClasses service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public StorageClassesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * StorageClasses 목록 조회(Get StorageClasses list)
     *
     * @param params the params
     * @return the storageClasses list
     */
    public StorageClassesList getStorageClassesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesListUrl(), HttpMethod.GET, null, Map.class, params);
        StorageClassesList storageClassesList = commonService.setResultObject(responseMap, StorageClassesList.class);
        storageClassesList = commonService.resourceListProcessing(storageClassesList, params, StorageClassesList.class);
        return (StorageClassesList) commonService.setResultModel(storageClassesList, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses 상세 조회(Get StorageClasses detail)
     *
     * @param params the params
     * @return the storageClasses detail
     */
    public StorageClasses getStorageClasses(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesGetUrl(), HttpMethod.GET, null, Map.class, params);
        StorageClasses storageClasses = commonService.setResultObject(responseMap, StorageClasses.class);
        storageClasses = commonService.annotationsProcessing(storageClasses, StorageClasses.class);
        return (StorageClasses) commonService.setResultModel(storageClasses, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses YAML 조회(Get StorageClasses yaml)
     *
     * @param params the params
     * @return the storageClasses yaml
     */
    public CommonResourcesYaml getStorageClassesYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses 생성(Create StorageClasses)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus createStorageClasses(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);

        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses 삭제(Delete StorageClasses)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus deleteStorageClasses(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses 수정(Update StorageClasses)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus updateStorageClasses(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


}
