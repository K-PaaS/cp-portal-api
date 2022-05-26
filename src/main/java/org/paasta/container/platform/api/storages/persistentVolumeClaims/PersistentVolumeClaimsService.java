package org.paasta.container.platform.api.storages.persistentVolumeClaims;

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
 * PersistentVolumeClaims Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class PersistentVolumeClaimsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new PersistentVolumeClaims service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public PersistentVolumeClaimsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * PersistentVolumeClaims 목록 조회(Get PersistentVolumeClaims list)
     *
     * @param params the params
     * @return the persistentVolumeClaims list
     */
    public PersistentVolumeClaimsList getPersistentVolumeClaimsList(Params params) {

        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumeClaimsListUrl(), HttpMethod.GET, null, Map.class, params);
        PersistentVolumeClaimsList persistentVolumeClaimsList = commonService.setResultObject(responseMap, PersistentVolumeClaimsList.class);
        persistentVolumeClaimsList = commonService.resourceListProcessing(persistentVolumeClaimsList, params, PersistentVolumeClaimsList.class);
        return (PersistentVolumeClaimsList) commonService.setResultModel(persistentVolumeClaimsList, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * PersistentVolumeClaims 상세 조회(Get PersistentVolumeClaims detail)
     *
     * @param params the params
     * @return the persistentVolumeClaims detail
     */
    public PersistentVolumeClaims getPersistentVolumeClaims(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumeClaimsGetUrl(), HttpMethod.GET, null, Map.class, params);
        PersistentVolumeClaims persistentVolumeClaims = commonService.setResultObject(responseMap, PersistentVolumeClaims.class);
        persistentVolumeClaims = commonService.annotationsProcessing(persistentVolumeClaims, PersistentVolumeClaims.class);
        return (PersistentVolumeClaims) commonService.setResultModel(persistentVolumeClaims, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * PersistentVolumeClaims YAML 조회(Get PersistentVolumeClaims yaml)
     *
     * @param params the params
     * @return the persistentVolumeClaims yaml
     */
    public CommonResourcesYaml getPersistentVolumeClaimsYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumeClaimsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * PersistentVolumeClaims 생성(Create PersistentVolumeClaims)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus createPersistentVolumeClaims(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumeClaimsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * PersistentVolumeClaims 삭제(Delete PersistentVolumeClaims)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus deletePersistentVolumeClaims(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumeClaimsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * PersistentVolumeClaims 수정(Update PersistentVolumeClaims)
     *
     * @param params the params
     * @return return is succeeded
     */

    public ResultStatus updatePersistentVolumeClaims(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumeClaimsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

}
