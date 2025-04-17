package org.container.platform.api.storages.persistentVolumes;

import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.container.platform.api.storages.persistentVolumes.support.PersistentVolumeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * PersistentVolumes Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.23
 */
@Service
public class PersistentVolumesService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new PersistentVolumes service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public PersistentVolumesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * PersistentVolumes 목록 조회(Get PersistentVolumes list)
     *
     * @param params the params
     * @return the persistentVolumes list
     */
    public PersistentVolumesList getPersistentVolumesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesListUrl()
                , HttpMethod.GET, null, Map.class, params);

        PersistentVolumesList persistentVolumesList = commonService.setResultObject(responseMap, PersistentVolumesList.class);
        persistentVolumesList = commonService.resourceListProcessing(persistentVolumesList, params, PersistentVolumesList.class);
        return (PersistentVolumesList) commonService.setResultModel(persistentVolumesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * PersistentVolumes YAML 조회(Get PersistentVolumes yaml)
     *
     * @param params the params
     * @return the persistentVolumes yaml
     */
    public CommonResourcesYaml getPersistentVolumesYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);

        return (CommonResourcesYaml)commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * PersistentVolumes 생성(Create PersistentVolumes)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus createPersistentVolumes(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * PersistentVolumes 삭제(Delete PersistentVolumes)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus deletePersistentVolumes(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * PersistentVolumes 수정(Update PersistentVolumes)
     *
     * @param params the params
     * @return return is succeeded
     */
    public ResultStatus updatePersistentVolumes(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * PersistentVolumes 상세 조회(Get PersistentVolumes detail)
     *
     * @param params the params
     * @return the persistentVolumes detail
     */
    public PersistentVolumes getPersistentVolumes(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesGetUrl(), HttpMethod.GET, null, Map.class, params);

        PersistentVolumes persistentVolumes = commonService.setResultObject(responseMap, PersistentVolumes.class);
        persistentVolumes = commonService.annotationsProcessing(persistentVolumes, PersistentVolumes.class);

        List<Map> pvSource = new ArrayList<>();

        for(PersistentVolumeType pvType : PersistentVolumeType.class.getEnumConstants() ) {
            String type = pvType.name();
            Map volume = commonService.getField(type, persistentVolumes.getSpec());
            if(volume != null) {
                LinkedHashMap volumeLinkedMap = new LinkedHashMap<>();
                if(type.equals(Constants.PERSISTENT_HOST_PATH_FIELD)) {
                    String path = Constants.NULL_REPLACE_TEXT;
                    if(volume.get(Constants.PATH) != null) {
                        path = volume.get(Constants.PATH).toString();
                    }
                    volumeLinkedMap.put(Constants.TYPE, pvType.getName());
                    volumeLinkedMap.put(Constants.PATH, path);
                }
                else {
                    volumeLinkedMap.put(Constants.TYPE, pvType.getName());

                    for( Object key : volume.keySet()){
                        String value = Constants.NULL_REPLACE_TEXT;

                        if(volume.get(key) != null) {
                            value = volume.get(key).toString();
                        }
                        volumeLinkedMap.put(key.toString(), value);
                    }
                }
                pvSource.add(volumeLinkedMap);
            }
        }

        if (pvSource.size() == 0 ) {
            LinkedHashMap volumeLinkedMap = new LinkedHashMap<>();
            volumeLinkedMap.put(Constants.TYPE, Constants.NULL_REPLACE_TEXT);
            pvSource.add(volumeLinkedMap);
        }
        persistentVolumes.setSource(pvSource);

        return (PersistentVolumes) commonService.setResultModel(persistentVolumes, Constants.RESULT_STATUS_SUCCESS);
    }



}
