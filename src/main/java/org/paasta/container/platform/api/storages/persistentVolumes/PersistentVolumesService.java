package org.paasta.container.platform.api.storages.persistentVolumes;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.paasta.container.platform.api.storages.persistentVolumes.support.PersistentVolumeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * PersistentVolumes Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.19
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
     * (Admin Portal)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the persistentVolumes list
     */
    public Object getPersistentVolumesListAdmin(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        String param = "";
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesListUrl().replace("{namespace}", namespace)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        PersistentVolumesListAdmin persistentVolumesListAdmin = commonService.setResultObject(responseMap, PersistentVolumesListAdmin.class);
        persistentVolumesListAdmin = commonService.resourceListProcessing(persistentVolumesListAdmin, offset, limit, orderBy, order, searchName, PersistentVolumesListAdmin.class);


        return commonService.setResultModel(persistentVolumesListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * PersistentVolumes YAML 조회(Get PersistentVolumes yaml)
     *
     * @param resourceName the resource name
     * @param resultMap    the result map
     * @return the persistentVolumes yaml
     */
    public Object getPersistentVolumesAdminYaml(String resourceName, HashMap resultMap) {
        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesGetUrl()
                        .replace("{name}", resourceName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }
        //noinspection unchecked
        resultMap.put("sourceTypeYaml", response);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * PersistentVolumes 생성(Create PersistentVolumes)
     *
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin the isAdmin
     * @return return is succeeded
     */
    public Object createPersistentVolumes(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_PERSISTENT_VOLUMES);
    }

    /**
     * PersistentVolumes 삭제(Delete PersistentVolumes)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return return is succeeded
     */
    public ResultStatus deletePersistentVolumes(String namespace, String resourceName) {
        ResultStatus resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesDeleteUrl()
                        .replace("{namespace}", namespace).replace("{name}", resourceName),
                HttpMethod.DELETE, null, ResultStatus.class);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_PERSISTENT_VOLUMES);
    }

    /**
     * PersistentVolumes 수정(Update PersistentVolumes)
     *
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @return return is succeeded
     */
    public ResultStatus updatePersistentVolumes(String resourceName, String yaml) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesUpdateUrl()
                        .replace("{name}", resourceName), HttpMethod.PUT, yaml, ResultStatus.class, true);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_PERSISTENT_VOLUMES_DETAIL.replace("{persistentVolumeName:.+}", resourceName));
    }


    /**
     * PersistentVolumes 상세 조회(Get PersistentVolumes detail)
     * (Admin Portal)
     *
     * @param namespace             the namespace
     * @param persistentVolumesName the persistentVolumes name
     * @return the persistentVolumes detail
     */
    public Object getPersistentVolumesAdmin(String namespace, String persistentVolumesName) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListPersistentVolumesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", persistentVolumesName)
                , HttpMethod.GET, null, Map.class);
        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e) {
            return obj;
        }

        PersistentVolumesAdmin persistentVolumesAdmin = commonService.setResultObject(responseMap, PersistentVolumesAdmin.class);
        persistentVolumesAdmin = commonService.annotationsProcessing(persistentVolumesAdmin, PersistentVolumesAdmin.class);

        List<Map> pvSource = new ArrayList<>();

        for(PersistentVolumeType pvType : PersistentVolumeType.class.getEnumConstants() ) {

            String type = pvType.name();

            Map volume = commonService.getField(type, persistentVolumesAdmin.getSpec());

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


        persistentVolumesAdmin.setSource(pvSource);

        return commonService.setResultModel(persistentVolumesAdmin, Constants.RESULT_STATUS_SUCCESS);
    }



}
