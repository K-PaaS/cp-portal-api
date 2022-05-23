package org.paasta.container.platform.api.storages.storageClasses;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * StorageClasses Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2020.10.13
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
     * (Admin Portal)
     *
     * @param namespace  the namespace
     * @param offset     the offset
     * @param limit      the limit
     * @param orderBy    the orderBy
     * @param order      the order
     * @param searchName the searchName
     * @return the storageClasses list
     */
    public Object getStorageClassesListAdmin(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesListUrl().replace("{namespace}", namespace),
                HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        StorageClassesListAdmin storageClassesListAdmin = commonService.setResultObject(responseMap, StorageClassesListAdmin.class);
        storageClassesListAdmin = commonService.resourceListProcessing(storageClassesListAdmin, offset, limit, orderBy, order, searchName, StorageClassesListAdmin.class);

        return commonService.setResultModel(storageClassesListAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses 상세 조회(Get StorageClasses detail)
     * (Admin Portal)
     *
     * @param namespace          the namespace
     * @param storageClassesName the storageClasses name
     * @return the storageClasses detail
     */
    public Object getStorageClassesAdmin(String namespace, String storageClassesName) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesGetUrl()
                        .replace("{namespace}", namespace)
                        .replace("{name}", storageClassesName)
                , HttpMethod.GET, null, Map.class);
        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e) {
            return obj;
        }

        StorageClassesAdmin storageClassesAdmin = commonService.setResultObject(responseMap, StorageClassesAdmin.class);
        storageClassesAdmin = commonService.annotationsProcessing(storageClassesAdmin, StorageClassesAdmin.class);

        return commonService.setResultModel(storageClassesAdmin, Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses YAML 조회(Get StorageClasses yaml)
     *
     * @param resourceName the resource name
     * @param resultMap    the result map
     * @return the storageClasses yaml
     */
    public Object getStorageClassesAdminYaml(String resourceName, HashMap resultMap) {
        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesGetUrl().replace("{name}", resourceName), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        if (CommonUtils.isResultStatusInstanceCheck(response)) {
            return response;
        }

        resultMap.put("sourceTypeYaml", response);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * StorageClasses 생성(Create StorageClasses)
     *
     * @param namespace the namespace
     * @param yaml      the yaml
     * @param isAdmin the isAmdin
     * @return return is succeeded
     */
    public Object createStorageClasses(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_STORAGE_CLASSES);
    }

    /**
     * StorageClasses 삭제(Delete StorageClasses)
     *
     * @param namespace    the namespace
     * @param resourceName the resource name
     * @return return is succeeded
     */
    public ResultStatus deleteStorageClasses(String namespace, String resourceName) {
        ResultStatus resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesDeleteUrl()
                        .replace("{namespace}", namespace).replace("{name}", resourceName), HttpMethod.DELETE, null, ResultStatus.class);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_STORAGE_CLASSES);
    }

    /**
     * StorageClasses 수정(Update StorageClasses)
     *
     * @param resourceName the resource name
     * @param yaml         the yaml
     * @return return is succeeded
     */
    public ResultStatus updateStorageClasses(String resourceName, String yaml) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListStorageClassesUpdateUrl()
                        .replace("{name}", resourceName), HttpMethod.PUT, yaml, ResultStatus.class, true);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_STORAGES_STORAGE_CLASSES_DETAIL.replace("{storageClassName:.+}", resourceName));
    }


}
