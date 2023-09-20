package org.container.platform.api.customServices.services;

import org.container.platform.api.common.*;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * CustomServices Service 클래스
 *
 * @author jjy
 * @version 1.0
 * @since 2022.05.24
 */
@Service
public class CustomServicesService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new CustomServices service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public CustomServicesService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * Services 목록 조회(Get Services list)
     *
     * @param params the params
     * @return the services list
     */
    public CustomServicesList getCustomServicesList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesListUrl(), HttpMethod.GET, null, Map.class, params);
        CustomServicesList customServicesList = commonService.setResultObject(responseMap, CustomServicesList.class);
        customServicesList = commonService.resourceListProcessing(customServicesList, params, CustomServicesList.class);
        return (CustomServicesList) commonService.setResultModel(customServicesList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services 상세 조회(Get Services detail)
     *
     * @param params the params
     * @return the services detail
     */
    public CustomServices getCustomServices(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesGetUrl(), HttpMethod.GET, null, Map.class, params);
        CustomServices customServices = commonService.setResultObject(responseMap, CustomServices.class);
        customServices = commonService.annotationsProcessing(customServices, CustomServices.class);
        return (CustomServices) commonService.setResultModel(customServices, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services YAML 조회(Get Services yaml)
     *
     * @param params the params
     * @return the services yaml
     */
    public CommonResourcesYaml getCustomServicesYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }

    /**
     * Services 생성(Create Services)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createServices(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services 삭제(Delete Services)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteServices(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                    propertyService.getCpMasterApiListServicesDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Services 수정(Update Services)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateServices(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListServicesUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }
}
