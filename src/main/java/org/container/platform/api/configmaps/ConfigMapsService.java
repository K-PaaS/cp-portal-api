package org.container.platform.api.configmaps;

import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ConfigMaps Service 클래스
 *
 * @author hkm
 * @version 1.0
 * @since 2022.05.25
 **/
@Service
public class ConfigMapsService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;


    /**
     * Instantiates a new ConfigMaps service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public ConfigMapsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * ConfigMaps 목록 조회(Get ConfigMaps List)
     *
     * @param params the params
     * @return the ConfigMaps list
     */
    public ConfigMapsList getConfigMapsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsListUrl(), HttpMethod.GET, null, Map.class, params);
        return procConfigMapsList(responseMap, params);
    }


    /**
     * ConfigMaps 상세 조회(Get ConfigMaps Detail)
     *
     * @param params the params
     * @return the ConfigMaps detail
     */
    public ConfigMaps getConfigMaps(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsGetUrl(), HttpMethod.GET, null, Map.class, params);
        ConfigMaps configMaps = commonService.setResultObject(responseMap, ConfigMaps.class);
        configMaps = commonService.annotationsProcessing(configMaps, ConfigMaps.class);
        return (ConfigMaps) commonService.setResultModel(configMaps, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ConfigMaps YAML 조회(Get ConfigMaps Yaml)
     *
     * @param params the params
     * @return the ConfigMaps yaml
     */
    public CommonResourcesYaml getConfigMapsYaml(Params params){
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * ConfigMaps 생성(Create ConfigMaps)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createConfigMaps(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ConfigMaps 삭제(Delete ConfigMaps)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteConfigMaps(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ConfigMaps 수정(Update ConfigMaps)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateConfigMaps(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListConfigMapsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * 필터링된 ConfigMaps 목록 조회(Get filtered ConfigMaps list)
     *
     * @param responseMap the HashMap
     * @param params the params
     * @return the filtered ConfigMaps list
     */
    ConfigMapsList procConfigMapsList(HashMap responseMap, Params params){
        ConfigMapsList configMapsList = commonService.setResultObject(responseMap, ConfigMapsList.class);
        configMapsList.setItems(configMapsList.getItems().stream()
                .filter(x -> !x.getMetadata().getName().contains(Constants.DEFAULT_CONFIGMAPS))
                .collect(Collectors.toList()));
        configMapsList = commonService.resourceListProcessing(configMapsList, params, ConfigMapsList.class);
        return (ConfigMapsList) commonService.setResultModel(configMapsList, Constants.RESULT_STATUS_SUCCESS);
    }
}
