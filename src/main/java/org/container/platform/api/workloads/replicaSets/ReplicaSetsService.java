package org.container.platform.api.workloads.replicaSets;

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
 * ReplicaSets Service 클래스
 *
 * @author kjhoon
 * @version 1.0
 * @since 2022.05.23
 */
@Service
public class ReplicaSetsService {

    private final RestTemplateService restTemplateService;
    private final CommonService commonService;
    private final PropertyService propertyService;

    /**
     * Instantiates a new ReplicaSet service
     *
     * @param restTemplateService the rest template service
     * @param commonService       the common service
     * @param propertyService     the property service
     */
    @Autowired
    public ReplicaSetsService(RestTemplateService restTemplateService, CommonService commonService, PropertyService propertyService) {
        this.restTemplateService = restTemplateService;
        this.commonService = commonService;
        this.propertyService = propertyService;
    }


    /**
     * ReplicaSets 목록 조회(Get ReplicaSets List)
     *
     * @param params the params
     * @return the replicaSets list
     */
    public ReplicaSetsList getReplicaSetsList(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListUrl(), HttpMethod.GET, null, Map.class, params);
        ReplicaSetsList replicaSetsList = commonService.setResultObject(responseMap, ReplicaSetsList.class);
        replicaSetsList = commonService.resourceListProcessing(replicaSetsList, params, ReplicaSetsList.class);
        return (ReplicaSetsList) commonService.setResultModel(replicaSetsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * Selector 값에 따른 ReplicaSets 목록 조회(Get ReplicaSets By Selector)
     *
     * @param params the params
     * @return the replicaSets list
     */
    public ReplicaSetsList getReplicaSetsListLabelSelector(Params params) {
        String labelSelector = "?labelSelector=" + params.getSelector();
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsListUrl() + labelSelector,
                HttpMethod.GET, null, Map.class, params);
        ReplicaSetsList replicaSetsList = commonService.setResultObject(responseMap, ReplicaSetsList.class);
        replicaSetsList = commonService.setCommonItemMetaDataBySelector(replicaSetsList, ReplicaSetsList.class);
        return (ReplicaSetsList) commonService.setResultModel(replicaSetsList, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets 상세 조회(Get ReplicaSets Detail)
     *
     * @param params the params
     * @return the replicaSets detail
     */
    public ReplicaSets getReplicaSets(Params params) {
        HashMap responseMap = (HashMap) restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsGetUrl(), HttpMethod.GET, null, Map.class, params);
        ReplicaSets replicaSets = commonService.setResultObject(responseMap, ReplicaSets.class);
        replicaSets = commonService.annotationsProcessing(replicaSets, ReplicaSets.class);
        return (ReplicaSets) commonService.setResultModel(replicaSets, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets Admin YAML 조회(Get ReplicaSets Yaml)
     *
     * @param params the params
     * @return the replicaSets yaml
     */
    public CommonResourcesYaml getReplicaSetsYaml(Params params) {
        String resourceYaml = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsGetUrl(), HttpMethod.GET, null, String.class, Constants.ACCEPT_TYPE_YAML, params);
        return (CommonResourcesYaml) commonService.setResultModel(new CommonResourcesYaml(resourceYaml), Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets 생성(Create ReplicaSets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus createReplicaSets(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsCreateUrl(), HttpMethod.POST, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets 수정(Update ReplicaSets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus updateReplicaSets(Params params) {
        ResultStatus resultStatus = restTemplateService.sendYaml(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsUpdateUrl(), HttpMethod.PUT, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


    /**
     * ReplicaSets 삭제(Delete ReplicaSets)
     *
     * @param params the params
     * @return the resultStatus
     */
    public ResultStatus deleteReplicaSets(Params params) {
        ResultStatus resultStatus = restTemplateService.send(Constants.TARGET_CP_MASTER_API,
                propertyService.getCpMasterApiListReplicaSetsDeleteUrl(), HttpMethod.DELETE, null, ResultStatus.class, params);
        return (ResultStatus) commonService.setResultModel(resultStatus, Constants.RESULT_STATUS_SUCCESS);
    }


}
