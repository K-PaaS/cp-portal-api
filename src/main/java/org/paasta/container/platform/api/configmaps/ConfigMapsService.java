package org.paasta.container.platform.api.configmaps;

import org.paasta.container.platform.api.common.*;
import org.paasta.container.platform.api.common.model.CommonResourcesYaml;
import org.paasta.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigMapsService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    @Autowired
    public ConfigMapsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }

    /**
     * Configmap 목록 조회
     */
    public Object getConfigMapsList(String namespace, int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap = null;
        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsListUrl()
                    .replace("{namespace}", namespace),
                HttpMethod.GET, null, Map.class);

//        return commonService.procResultList(response, offset, limit, orderBy, order, searchName, ConfigMapsList.class);

        try {
            responseMap = (HashMap) response;

        } catch (Exception e) {
            return response;
        }

        ConfigMapsList configMapsList = commonService.setResultObject(responseMap, ConfigMapsList.class);
        configMapsList = commonService.resourceListProcessing(configMapsList, offset, limit, orderBy, order, searchName, ConfigMapsList.class);
        return commonService.setResultModel(configMapsList, Constants.RESULT_STATUS_SUCCESS);
    }

    public Object getConfigMaps(String namespace, String configMapName) {
        Object obj = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsGetUrl()
                .replace("{namespace}", namespace)
                .replace("{name}", configMapName),
                HttpMethod.GET, null, Map.class);
        HashMap responseMap;

        try {
            responseMap = (HashMap) obj;
        } catch (Exception e){
            return obj;
        }

        ConfigMaps configMaps = commonService.setResultObject(responseMap, ConfigMaps.class);

        return commonService.setResultModel(configMaps, Constants.RESULT_STATUS_SUCCESS);
    }

    public Object getConfigMapsYaml(String namespace, String configmapName, HashMap resultMap){
        String resultString = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsGetUrl()
                .replace("{namespace}", namespace)
                .replace("{name}", configmapName),
                HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML);

        if (CommonUtils.isResultStatusInstanceCheck(resultString)) {
            return resultString;
        }

        resultMap.put("sourceTypeYaml", resultString);

        return commonService.setResultModel(commonService.setResultObject(resultMap, CommonResourcesYaml.class), Constants.RESULT_STATUS_SUCCESS);

    }

    public Object createConfigMaps(String namespace, String yaml, boolean isAdmin) {
        Object map = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsCreateUrl()
                        .replace("{namespace}", namespace), HttpMethod.POST, yaml, Object.class, isAdmin);

        return commonService.setResultModelWithNextUrl(commonService.setResultObject(map, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_WORKLOAD_DEPLOYMENTS);
    }

    public ResultStatus deleteConfigMaps(String namespace, String name, boolean isAdmin) {
        ResultStatus resultStatus;

        if (isAdmin) {
            resultStatus = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListConfigMapsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.DELETE, null, ResultStatus.class);
        } else {
            resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListConfigMapsDeleteUrl()
                            .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.DELETE, null, ResultStatus.class);
        }

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class), Constants.RESULT_STATUS_SUCCESS, Constants.URI_CONFIGMAPS);
    }

    public ResultStatus updateConfigMaps(String namespace, String name, String yaml, boolean isAdmin) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsUpdateUrl()
                        .replace("{namespace}", namespace).replace("{name}", name), HttpMethod.PUT, yaml, ResultStatus.class, isAdmin);

        return (ResultStatus) commonService.setResultModelWithNextUrl(commonService.setResultObject(resultStatus, ResultStatus.class),
                Constants.RESULT_STATUS_SUCCESS, Constants.URI_CONFIGMAPS_DETAIL.replace("{configmapName:.+}", name));
    }

    public Object getConfigMapsListAllNamespacesAdmin(int offset, int limit, String orderBy, String order, String searchName) {
        HashMap responseMap;

        Object response = restTemplateService.sendAdmin(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsListAllNamespacesUrl() + commonService.generateFieldSelectorForExceptNamespace(Constants.RESOURCE_NAMESPACE)
                , HttpMethod.GET, null, Map.class);

        try {
            responseMap = (HashMap) response;
        } catch (Exception e) {
            return response;
        }

        ConfigMapsList configMapsList = commonService.setResultObject(responseMap, ConfigMapsList.class);
        configMapsList = commonService.resourceListProcessing(configMapsList, offset, limit, orderBy, order, searchName, ConfigMapsList.class);

        return commonService.setResultModel(configMapsList, Constants.RESULT_STATUS_SUCCESS);
    }
}
