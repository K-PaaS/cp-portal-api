package org.container.platform.api.workloads.deployments;

import org.container.platform.api.common.CommonService;
import org.container.platform.api.common.Constants;
import org.container.platform.api.common.PropertyService;
import org.container.platform.api.common.RestTemplateService;
import org.container.platform.api.common.model.CommonResourcesYaml;
import org.container.platform.api.common.model.Params;
import org.container.platform.api.common.model.ResultStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Deployments Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.20
 */
@Service
public class DeploymentsService {
    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new Deployments service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public DeploymentsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * Deployments 목록 조회(Get Deployments List)
     *
     * @param params the params
     * @return the deployments list
     */
    public DeploymentsList getDeploymentsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsListUrl(), HttpMethod.GET, null, Map.class, params);
        DeploymentsList deploymentsList = commonService.setResultObject(responseMap, DeploymentsList.class);
        deploymentsList = commonService.resourceListProcessing(deploymentsList, params, DeploymentsList.class);
        return (DeploymentsList) commonService.setResultModel(deploymentsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Deployments 상세 조회(Get Deployments Detail)
     *
     * @param params the params
     * @return the deployments detail
     */
    public Deployments getDeployments(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl(), HttpMethod.GET, null, Map.class, params);
        Deployments deployments = commonService.setResultObject(responseMap, Deployments.class);
        deployments = commonService.annotationsProcessing(deployments, Deployments.class);
        return (Deployments) commonService.setResultModel(deployments, Constants.RESULT_STATUS_SUCCESS);

    }


    /**
     * Deployments YAML 조회(Get Deployments Yaml)
     *
     * @param params the params
     * @return the deployments yaml
     */
    public CommonResourcesYaml getDeploymentsYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Deployments 생성(Create Deployments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createDeployments(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * Deployments 수정(Update Deployments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateDeployments(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }



    /**
     * Deployments 삭제(Delete Deployments)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteDeployments(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListDeploymentsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }



}
